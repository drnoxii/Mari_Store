package Controllers;

import Dao.PersonaDaoImpl;
import Dao.UsuarioDaoImpl;
import Interface.IPersona;
import Interface.IUsuario;
import Model.Persona;
import Model.Usuario;
import Model.Rol; // Si te da error, cambia Rol.CLIENTE por Usuario.Rol.CLIENTE
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AuthController", urlPatterns = {"/AuthController"})
public class AuthController extends HttpServlet {

    private final IUsuario uDao = new UsuarioDaoImpl();
    private final IPersona pDao = new PersonaDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        responderError(response, 405, "Método GET no permitido");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try (PrintWriter out = response.getWriter()) {

            if (action == null || action.trim().isEmpty()) {
                JsonObject json = new JsonObject();
                json.addProperty("success", false);
                json.addProperty("message", "Acción no enviada");
                out.print(json.toString());
                return;
            }

            switch (action) {

                case "validar":
                    validarLogin(request, out);
                    break;

                case "register":
                    registrarCliente(request, out);
                    break;

                case "registerAdmin":
                    registrarAdmin(request, out);
                    break;

                case "Salir":
                    cerrarSesion(request, out);
                    break;

                default:
                    JsonObject json = new JsonObject();
                    json.addProperty("success", false);
                    json.addProperty("message", "Acción no encontrada");
                    out.print(json.toString());
                    break;
            }

        } catch (Exception e) {
            responderError(response, 500, "Error: " + e.getMessage());
        }
    }

    private void validarLogin(HttpServletRequest request, PrintWriter out) {
        JsonObject jsonResponse = new JsonObject();

        String email = request.getParameter("email");

        /*
         * Acepta ambos nombres:
         * - contraseña
         * - password
         * Así no se rompe si tu formulario usa uno u otro.
         */
        String contraseña = request.getParameter("contraseña");

        if (contraseña == null || contraseña.trim().isEmpty()) {
            contraseña = request.getParameter("password");
        }

        Usuario us = uDao.validate(email, contraseña);

        if (us != null && us.getEmail() != null) {

            HttpSession session = request.getSession();
            session.setAttribute("usuario", us);

            JsonObject userData = new JsonObject();
            userData.addProperty("idUsuario", us.getIdUsuario());
            userData.addProperty("email", us.getEmail());

            if (us.getRol() != null) {
                userData.addProperty("rol", us.getRol().toString());
            } else {
                userData.addProperty("rol", "CLIENTE");
            }

            /*
             * Te mando el nombre de las dos formas:
             * 1. userData.nombre
             * 2. userData.persona.nombre
             *
             * Así funciona aunque tu JS use cualquiera de las dos.
             */
            if (us.getPersona() != null) {
                userData.addProperty("nombre", us.getPersona().getNombre());

                JsonObject personaJson = new JsonObject();
                personaJson.addProperty("idPersona", us.getPersona().getIdPersona());
                personaJson.addProperty("nombre", us.getPersona().getNombre());
                personaJson.addProperty("apellidos", us.getPersona().getApellidos());

                userData.add("persona", personaJson);
            }

            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Login correcto");
            jsonResponse.add("userData", userData);

        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Correo o contraseña incorrecta");
        }

        out.print(jsonResponse.toString());
    }

    private void registrarCliente(HttpServletRequest request, PrintWriter out) {
        JsonObject jsonResponse = new JsonObject();

        Persona p = new Persona();
        Usuario u = new Usuario();

        u.setEmail(request.getParameter("email"));

        p.setNombre(request.getParameter("nombre"));
        p.setApellidos(request.getParameter("apellidos"));
        p.setDNI(Integer.parseInt(request.getParameter("DNI")));
        p.setTelefono(Integer.parseInt(request.getParameter("telefono")));

        String contraseña = request.getParameter("contraseña");

        if (contraseña == null || contraseña.trim().isEmpty()) {
            contraseña = request.getParameter("password");
        }

        u.setContraseña(contraseña);

        /*
         * Registro público siempre CLIENTE.
         * No dejes que el usuario elija ADMIN desde el formulario normal.
         */
        u.setRol(Rol.CLIENTE);

        int resultado = pDao.insert(p, u);

        jsonResponse.addProperty("success", resultado != 0);
        jsonResponse.addProperty(
                "message",
                resultado != 0 ? "Registro correcto" : "Error de registro"
        );

        out.print(jsonResponse.toString());
    }

    private void registrarAdmin(HttpServletRequest request, PrintWriter out) {
        JsonObject jsonResponse = new JsonObject();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Debes iniciar sesión como administrador");
            out.print(jsonResponse.toString());
            return;
        }

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion.getRol() == null || !usuarioSesion.getRol().toString().equals("ADMIN")) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "No tienes permiso para crear administradores");
            out.print(jsonResponse.toString());
            return;
        }

        Persona p = new Persona();
        Usuario u = new Usuario();

        u.setEmail(request.getParameter("email"));

        p.setNombre(request.getParameter("nombre"));
        p.setApellidos(request.getParameter("apellidos"));
        p.setDNI(Integer.parseInt(request.getParameter("DNI")));
        p.setTelefono(Integer.parseInt(request.getParameter("telefono")));

        String contraseña = request.getParameter("contraseña");

        if (contraseña == null || contraseña.trim().isEmpty()) {
            contraseña = request.getParameter("password");
        }

        u.setContraseña(contraseña);
        u.setRol(Rol.ADMIN);

        int resultado = pDao.insert(p, u);

        jsonResponse.addProperty("success", resultado != 0);
        jsonResponse.addProperty(
                "message",
                resultado != 0 ? "Administrador registrado correctamente" : "Error al registrar administrador"
        );

        out.print(jsonResponse.toString());
    }

    private void cerrarSesion(HttpServletRequest request, PrintWriter out) {
        JsonObject jsonResponse = new JsonObject();

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "Sesión cerrada");

        out.print(jsonResponse.toString());
    }

    private void responderError(HttpServletResponse response, int status, String mensaje)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", mensaje);

        response.getWriter().print(json.toString());
    }

    @Override
    public String getServletInfo() {
        return "AuthController";
    }
}