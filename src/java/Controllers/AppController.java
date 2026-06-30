/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import Dao.PagoDaoImpl;
import Dao.PedidoDaoImpl;
import Dao.ProductoDaoImpl;
import Interface.IPago;
import Interface.IPedido;
import Interface.IProducto;
import Model.Carrito;
import Model.Detalle_Producto;
import Model.EstadoPedido;
import Model.MetodoPago;
import Model.Pago;
import Model.Pedido;
import Model.Productos;
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
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import Util.ConexionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author spide
 */
@WebServlet(name = "AppController", urlPatterns = {"/AppController"})
@MultipartConfig
public class AppController extends HttpServlet {

    private IProducto pDao = new ProductoDaoImpl();
    private IPedido IDao = new PedidoDaoImpl();
    private IPago pagoDao = new PagoDaoImpl();
    private Gson gson = new Gson();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        HttpSession session = request.getSession();

        List<Carrito> listCarrito = (List<Carrito>) session.getAttribute("carrito");

        if (listCarrito == null) {
            listCarrito = new ArrayList<>();
            session.setAttribute("carrito", listCarrito);
        }
        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "listarProductos":
                    List<Productos> productos = pDao.lista();
                    out.print(gson.toJson(productos));
                    break;
                case "addcarrito": {
                    int idProducto = Integer.parseInt(request.getParameter("id"));

                    Productos p = pDao.SearchbyId(idProducto);

                    if (p == null) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Producto no encontrado");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    int idDetalle = 0;
                    String talla = "";
                    int stock = 0;

                    String idDetalleParam = request.getParameter("idDetalle");

                    Detalle_Producto varianteSeleccionada = null;

                    if (idDetalleParam != null && !idDetalleParam.trim().isEmpty()) {
                        idDetalle = Integer.parseInt(idDetalleParam);

                        if (p.getVariantes() != null) {
                            for (Detalle_Producto d : p.getVariantes()) {
                                if (d.getIdDetalle() == idDetalle) {
                                    varianteSeleccionada = d;
                                    break;
                                }
                            }
                        }

                    } else {

                        if (p.getVariantes() != null && !p.getVariantes().isEmpty()) {
                            varianteSeleccionada = p.getVariantes().get(0);
                        }
                    }

                    if (varianteSeleccionada == null) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "No se encontró la variante del producto");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    idDetalle = varianteSeleccionada.getIdDetalle();
                    talla = varianteSeleccionada.getTalla();
                    stock = varianteSeleccionada.getStock();

                    if (stock <= 0) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "La variante seleccionada no tiene stock");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    int pos = -1;

                    for (int i = 0; i < listCarrito.size(); i++) {
                        if (listCarrito.get(i).getIdDetalle() == idDetalle) {
                            pos = i;
                            break;
                        }
                    }

                    if (pos != -1) {
                        int nuevaCant = listCarrito.get(pos).getCantidad() + 1;

                        if (nuevaCant > listCarrito.get(pos).getStock()) {
                            jsonResponse.addProperty("success", false);
                            jsonResponse.addProperty("message", "Stock insuficiente para esta talla");
                            out.print(jsonResponse.toString());
                            return;
                        }

                        listCarrito.get(pos).setCantidad(nuevaCant);
                        listCarrito.get(pos).setSubTotal(nuevaCant * p.getPrecio());

                    } else {
                        Carrito car = new Carrito();

                        car.setIdProducto(p.getIdProducto());
                        car.setIdDetalle(idDetalle);
                        car.setNombre(p.getNombre());
                        car.setDescripcion(p.getDescripcion());
                        car.setImagen(p.getImagen());
                        car.setTalla(talla);
                        car.setStock(stock);
                        car.setPrecioCompra(p.getPrecio());
                        car.setCantidad(1);
                        car.setSubTotal(p.getPrecio());

                        listCarrito.add(car);
                    }

                    session.setAttribute("carrito", listCarrito);

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Producto agregado al carrito");
                    jsonResponse.addProperty("cartCount", listCarrito.size());

                    out.print(jsonResponse.toString());
                    break;
                }

                case "listarCarrito":
                    double total = listCarrito.stream().mapToDouble(Carrito::getSubTotal).sum();
                    session.setAttribute("total", total);
                    JsonObject cartData = new JsonObject();
                    cartData.add("items", gson.toJsonTree(listCarrito));
                    cartData.addProperty("total", total);
                    out.print(cartData.toString());
                    break;

                case "delete":

                    try {
                        int idProducto = Integer.parseInt(request.getParameter("id"));

                        boolean eliminado = listCarrito.removeIf(c -> c.getIdProducto() == idProducto);

                        session.setAttribute("carrito", listCarrito);

                        jsonResponse.addProperty("success", eliminado);
                        jsonResponse.addProperty("message", eliminado ? "Producto eliminado" : "No se encontró el producto");
                        jsonResponse.addProperty("cartCount", listCarrito.size());

                    } catch (Exception e) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Error: " + e.getMessage());
                    }

                    out.print(jsonResponse.toString());
                    break;

                case "generarcompra": {
                    Usuario user = (Usuario) session.getAttribute("usuario");

                    if (user == null) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Inicie sesión para continuar");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    if (listCarrito == null || listCarrito.isEmpty()) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "El carrito está vacío");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    String metodoPagoParam = request.getParameter("metodo_pago");
                    String comprobante = "";

                    Part partComprobante = request.getPart("comprobante");

                    if (partComprobante == null || partComprobante.getSize() <= 0) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Debe subir la captura del comprobante");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    String fileName = System.currentTimeMillis() + "_" + partComprobante.getSubmittedFileName();

                    String uploadDir = "assets/img/comprobantes";
                    String pathBuild = getServletContext().getRealPath("/" + uploadDir + File.separator);
                    String pathSource = pathBuild.replace("build" + File.separator + "web", "web");

                    new File(pathSource).mkdirs();
                    new File(pathBuild).mkdirs();

                    File fileSource = new File(pathSource + File.separator + fileName);

                    try (InputStream input = partComprobante.getInputStream()) {
                        java.nio.file.Files.copy(
                                input,
                                fileSource.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );
                    }

                    partComprobante.write(pathBuild + File.separator + fileName);

                    comprobante = uploadDir + "/" + fileName;

                    if (metodoPagoParam == null || metodoPagoParam.trim().isEmpty()) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Debe ingresar el método de pago");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    if (comprobante == null || comprobante.trim().isEmpty()) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Debe ingresar el comprobante de pago");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    MetodoPago metodoPago;

                    try {
                        metodoPago = MetodoPago.valueOf(metodoPagoParam.trim().toUpperCase());
                    } catch (Exception e) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Método de pago no válido");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    boolean stockDisponible = true;
                    String productoSinStock = "";

                    for (Carrito c : listCarrito) {
                        Productos prodBD = pDao.SearchbyId(c.getIdProducto());

                        if (c.getStock() < c.getCantidad()) {
                            stockDisponible = false;
                            productoSinStock = c.getNombre();
                            break;
                        }

                        if (c.getIdDetalle() <= 0) {
                            jsonResponse.addProperty("success", false);
                            jsonResponse.addProperty("message", "El producto " + c.getNombre() + " no tiene detalle/variante válida");
                            out.print(jsonResponse.toString());
                            return;
                        }
                    }

                    if (!stockDisponible) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Stock insuficiente para: " + productoSinStock);
                        out.print(jsonResponse.toString());
                        return;
                    }

                    double totalPagar = listCarrito.stream()
                            .mapToDouble(Carrito::getSubTotal)
                            .sum();

                    Pedido pedido = new Pedido();

                    pedido.setUsuario(user);
                    pedido.setTotal(totalPagar);

                    pedido.setEstadopedido(EstadoPedido.PENDIENTE);
                    pedido.setDetallePedido(listCarrito);

                    int idPedidoGenerado = IDao.generarPedido(pedido);

                    if (idPedidoGenerado <= 0) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Error al procesar el pedido");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    Pedido pedidoPago = new Pedido();
                    pedidoPago.setIdPedido(idPedidoGenerado);

                    Pago pago = new Pago();
                    pago.setPedido(pedidoPago);
                    pago.setMetodopago(metodoPago);
                    pago.setMonto(totalPagar);
                    pago.setComprobante(comprobante);

                    boolean pagoRegistrado = pagoDao.registrarPago(pago);

                    if (!pagoRegistrado) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Pedido creado, pero no se pudo registrar el pago");
                        jsonResponse.addProperty("idPedido", idPedidoGenerado);
                        out.print(jsonResponse.toString());
                        return;
                    }

                    for (Carrito c : listCarrito) {
                        Productos prodBD = pDao.SearchbyId(c.getIdProducto());

                        int nuevoStock = c.getStock() - c.getCantidad();
                        pDao.updateStock(c.getIdDetalle(), nuevoStock);
                    }

                    listCarrito.clear();
                    session.setAttribute("carrito", listCarrito);
                    session.setAttribute("total", 0.0);

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Pedido registrado. Pago pendiente de verificación.");
                    jsonResponse.addProperty("idPedido", idPedidoGenerado);
                    jsonResponse.addProperty("cartCount", 0);

                    out.print(jsonResponse.toString());
                    break;
                }
                case "perfilUsuario": {
                    Usuario user = (Usuario) session.getAttribute("usuario");

                    if (user == null) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Debe iniciar sesión");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    JsonObject usuarioJson = new JsonObject();

                    usuarioJson.addProperty("idUsuario", user.getIdUsuario());
                    usuarioJson.addProperty("email", user.getEmail());

                    if (user.getRol() != null) {
                        usuarioJson.addProperty("rol", user.getRol().toString());
                    } else {
                        usuarioJson.addProperty("rol", "CLIENTE");
                    }

                    if (user.getPersona() != null) {
                        usuarioJson.addProperty("idPersona", user.getPersona().getIdPersona());
                        usuarioJson.addProperty("nombre", user.getPersona().getNombre());
                        usuarioJson.addProperty("apellidos", user.getPersona().getApellidos());
                        usuarioJson.addProperty("dni", user.getPersona().getDNI());
                        usuarioJson.addProperty("telefono", user.getPersona().getTelefono());

                        try {
                            if (user.getPersona().getFecha_nacimiento() != null) {
                                usuarioJson.addProperty("fechaNacimiento", user.getPersona().getFecha_nacimiento().toString());
                            } else {
                                usuarioJson.addProperty("fechaNacimiento", "");
                            }
                        } catch (Exception e) {
                            usuarioJson.addProperty("fechaNacimiento", "");
                        }
                    }

                    jsonResponse.addProperty("success", true);
                    jsonResponse.add("usuario", usuarioJson);

                    out.print(jsonResponse.toString());
                    break;
                }
                case "misCompras": {
                    Usuario user = (Usuario) session.getAttribute("usuario");

                    if (user == null) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Debe iniciar sesión");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    JsonArray compras = new JsonArray();

                    String sql = "SELECT "
                            + "p.idPedido, p.estado, p.total, "
                            + "pa.metodo_pago, pa.monto, pa.comprobante, pa.fecha_pago, "
                            + "prod.nombre AS nombreProducto, "
                            + "dprod.talla, dprod.color, "
                            + "dp.cantidad, dp.precio_unitario, dp.subtotal "
                            + "FROM pedido p "
                            + "INNER JOIN detalle_pedido dp ON p.idPedido = dp.idPedido "
                            + "INNER JOIN detalle_producto dprod ON dp.idDetalle = dprod.idDetalle "
                            + "INNER JOIN producto prod ON dprod.idProducto = prod.idProducto "
                            + "LEFT JOIN pago pa ON p.idPedido = pa.idPedido "
                            + "WHERE p.idUsuario = ? "
                            + "ORDER BY p.idPedido DESC";

                    try {
                        Connection cn = ConexionSingleton.getConnection();
                        PreparedStatement ps = cn.prepareStatement(sql);
                        ps.setInt(1, user.getIdUsuario());

                        ResultSet rs = ps.executeQuery();

                        Map<Integer, JsonObject> mapaPedidos = new LinkedHashMap<>();

                        while (rs.next()) {
                            int idPedido = rs.getInt("idPedido");

                            JsonObject pedidoJson;

                            if (!mapaPedidos.containsKey(idPedido)) {
                                pedidoJson = new JsonObject();

                                pedidoJson.addProperty("idPedido", idPedido);
                                pedidoJson.addProperty("estado", rs.getString("estado"));
                                pedidoJson.addProperty("total", rs.getDouble("total"));

                                if (rs.getTimestamp("fecha_pago") != null) {
                                    pedidoJson.addProperty("fechaPago", rs.getTimestamp("fecha_pago").toString());
                                } else {
                                    pedidoJson.addProperty("fechaPago", "");
                                }

                                pedidoJson.addProperty("metodoPago", rs.getString("metodo_pago"));
                                pedidoJson.addProperty("monto", rs.getDouble("monto"));
                                pedidoJson.addProperty("comprobante", rs.getString("comprobante"));

                                pedidoJson.add("detalle", new JsonArray());

                                mapaPedidos.put(idPedido, pedidoJson);
                            }

                            pedidoJson = mapaPedidos.get(idPedido);

                            JsonObject item = new JsonObject();

                            item.addProperty("nombreProducto", rs.getString("nombreProducto"));
                            item.addProperty("talla", rs.getString("talla"));
                            item.addProperty("color", rs.getString("color"));
                            item.addProperty("cantidad", rs.getInt("cantidad"));
                            item.addProperty("precioUnitario", rs.getDouble("precio_unitario"));
                            item.addProperty("subtotal", rs.getDouble("subtotal"));

                            pedidoJson.getAsJsonArray("detalle").add(item);
                        }

                        for (JsonObject pedido : mapaPedidos.values()) {
                            compras.add(pedido);
                        }

                        jsonResponse.addProperty("success", true);
                        jsonResponse.add("compras", compras);

                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Error al listar mis compras: " + e.getMessage());
                    }

                    out.print(jsonResponse.toString());
                    break;
                }

                default:
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("messagge", "accion no encontrada");
                    out.print(jsonResponse.toString());
            }
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
