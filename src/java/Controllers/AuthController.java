/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import Dao.PersonaDaoImpl;
import Dao.UsuarioDaoImpl;
import Interface.IPersona;
import Interface.IUsuario;
import Model.Persona;
import Model.Usuario;
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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author spide
 */
@WebServlet(name = "AuthController", urlPatterns = {"/AuthController"})
public class AuthController extends HttpServlet {

    private final IUsuario uDao = new UsuarioDaoImpl();
    private final IPersona pDao = new PersonaDaoImpl();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AuthController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AuthController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //forma de recoger datos de las vista
        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {
            if (action.equals("validar")) {

                String user = request.getParameter("email");
                String pasw = request.getParameter("contraseña");

                Usuario us = uDao.validate(user, pasw);

                if (us != null && us.getEmail() != null) {

                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", us);

                    JsonObject userData = new JsonObject();
                    userData.addProperty("idUsuario", us.getIdUsuario());
                    userData.addProperty("email", us.getEmail());
                    userData.addProperty("rol", us.getRol().toString());

                    if (us.getPersona() != null) {
                        userData.addProperty(
                                "nombre",
                                us.getPersona().getNombre()
                        );
                    }

                    
                    jsonResponse.add("userData", userData);

                } else {

                    jsonResponse.addProperty("sucess", false);
                    jsonResponse.addProperty("message",
                            "Correo o Contraseña incorrecta");
                }

                out.print(jsonResponse.toString());
            } else if (action.equals("register")) {
                Persona p = new Persona();
                Usuario u = new Usuario();
                u.setEmail(request.getParameter("email"));
                p.setNombre(request.getParameter("nombre"));
                p.setApellidos(request.getParameter("apellidos"));
                p.setDNI(Integer.parseInt(request.getParameter("DNI")));
                p.setTelefono(Integer.parseInt(request.getParameter("telefono")));
                u.setContraseña(request.getParameter("contraseña"));

                int resultado = pDao.insert(p, u);

                jsonResponse.addProperty("sucess", resultado != 0);
                jsonResponse.addProperty("message", resultado != 0 ? "Registro Correcto" : "Error de Registro");
                out.print(jsonResponse.toString());

            } else if (action.equals("Salir")) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                jsonResponse.addProperty("sucess", true);
                jsonResponse.addProperty("message", "Sesion Cerrada");
                out.print(jsonResponse.toString());
            }

        } catch (Exception e) {
            response.setStatus(500);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error" + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
