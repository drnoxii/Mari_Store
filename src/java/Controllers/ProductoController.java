/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import Dao.ProductoDaoImpl;
import Interface.IProducto;
import Model.Categoria;
import Model.Detalle_Producto;
import Model.Productos;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author spide
 */
@WebServlet(name = "ProductoController", urlPatterns = {"/ProductoController"})
@MultipartConfig
public class ProductoController extends HttpServlet {

    private final IProducto pDao = new ProductoDaoImpl();

    private final Gson gson = new Gson();

    private static final String UPLOAD_DIR = "assets/img/productos";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            action = "listar";
        }
        switch (action) {
            case "guardar":
                guardarProductos(request, response);
                break;
            case "editar":
                editarProductos(request, response);
                break;
            case "eliminar":
                eliminarProductos(request, response);
                break;
            case "buscar":
                buscarProductos(request, response);
                break;

            default:
                listarProductos(request, response);
                break;
        }

    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        List<Productos> productos = pDao.lista();
        response.getWriter().print(gson.toJson(productos));
    }

    private void guardarProductos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Productos p = new Productos();

            p.setNombre(request.getParameter("nombre"));
            p.setDescripcion(request.getParameter("descripcion"));
            p.setPrecio(Double.parseDouble(request.getParameter("precio")));

            Categoria cat = new Categoria();
            cat.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
            p.setCategoria(cat);

            Part part = request.getPart("imagen");

            if (part != null && part.getSize() > 0) {
                String fileName = part.getSubmittedFileName();

                String pathBuild = getServletContext().getRealPath("/" + UPLOAD_DIR + File.separator);
                String pathSource = pathBuild.replace("build" + File.separator + "web", "web");

                new File(pathSource).mkdirs();
                new File(pathBuild).mkdirs();

                File fileSource = new File(pathSource + File.separator + fileName);

                try (InputStream input = part.getInputStream()) {
                    java.nio.file.Files.copy(
                            input,
                            fileSource.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                }

                part.write(pathBuild + File.separator + fileName);

                p.setImagen(UPLOAD_DIR + "/" + fileName);
            } else {
                p.setImagen("");
            }

            String[] tallas = request.getParameterValues("talla");
            String[] colores = request.getParameterValues("color");
            String[] stocks = request.getParameterValues("stock");

            List<Detalle_Producto> variantes = new ArrayList<>();

            if (tallas != null && colores != null && stocks != null) {
                for (int i = 0; i < tallas.length; i++) {
                    Detalle_Producto detalle = new Detalle_Producto();

                    detalle.setTalla(tallas[i]);
                    detalle.setColor(colores[i]);
                    detalle.setStock(Integer.parseInt(stocks[i]));

                    variantes.add(detalle);
                }
            }

            p.setVariantes(variantes);

            boolean res = pDao.insert(p);
            response.getWriter().print(gson.toJson(res));

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void editarProductos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Productos p = new Productos();

            p.setIdProducto(Integer.parseInt(request.getParameter("id_producto")));
            p.setNombre(request.getParameter("nombre"));
            p.setDescripcion(request.getParameter("descripcion"));
            p.setPrecio(Double.parseDouble(request.getParameter("precio")));

            Categoria cat = new Categoria();
            cat.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
            p.setCategoria(cat);

            Part part = request.getPart("imagen");

            if (part != null && part.getSize() > 0) {
                String fileName = part.getSubmittedFileName();

                String pathBuild = getServletContext().getRealPath("/" + UPLOAD_DIR + File.separator);
                String pathSource = pathBuild.replace("build" + File.separator + "web", "web");

                new File(pathSource).mkdirs();
                new File(pathBuild).mkdirs();

                File fileSource = new File(pathSource + File.separator + fileName);

                try (InputStream input = part.getInputStream()) {
                    java.nio.file.Files.copy(
                            input,
                            fileSource.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                }

                part.write(pathBuild + File.separator + fileName);

                p.setImagen(UPLOAD_DIR + "/" + fileName);

            } else {
                String imagenActual = request.getParameter("imagenActual");

                if (imagenActual == null || imagenActual.trim().isEmpty()) {
                    imagenActual = request.getParameter("imagen_actual");
                }

                p.setImagen(imagenActual);
            }

            String[] tallas = request.getParameterValues("talla");
            String[] colores = request.getParameterValues("color");
            String[] stocks = request.getParameterValues("stock");

            List<Detalle_Producto> variantes = new ArrayList<>();

            if (tallas != null && colores != null && stocks != null) {
                for (int i = 0; i < tallas.length; i++) {
                    Detalle_Producto detalle = new Detalle_Producto();

                    detalle.setTalla(tallas[i]);
                    detalle.setColor(colores[i]);
                    detalle.setStock(Integer.parseInt(stocks[i]));

                    variantes.add(detalle);
                }
            }

            p.setVariantes(variantes);

            boolean res = pDao.update(p);

            response.getWriter().print(gson.toJson(res));

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void eliminarProductos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        boolean res = pDao.delete(id);
        response.getWriter().print(gson.toJson(res));

    }

    private void buscarProductos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Productos p = pDao.SearchbyId(id);
        response.getWriter().print(gson.toJson(p));

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
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
