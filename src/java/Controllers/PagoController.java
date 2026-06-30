package Controllers;

import Dao.PagoDaoImpl;
import Dao.PedidoDaoImpl;
import Interface.IPago;
import Interface.IPedido;
import Model.Pago;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Util.ConexionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "PagoController", urlPatterns = {"/PagoController"})
public class PagoController extends HttpServlet {

    private IPago pagoDao = new PagoDaoImpl();
    private IPedido pedidoDao = new PedidoDaoImpl();
    private Gson gson = new Gson();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        try (PrintWriter out = response.getWriter()) {

            if (action == null || action.trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "No se envió ninguna acción");
                out.print(jsonResponse.toString());
                return;
            }

            switch (action) {

                case "listarPagosPendientes": {
                    try {
                        List<Pago> pagosPendientes = pagoDao.listarPendientes();

                        JsonArray data = new JsonArray();

                        for (Pago p : pagosPendientes) {
                            JsonObject item = new JsonObject();

                            item.addProperty("idPago", p.getIdPago());

                            if (p.getPedido() != null) {
                                item.addProperty("idPedido", p.getPedido().getIdPedido());
                                item.addProperty("totalPedido", p.getPedido().getTotal());
                            } else {
                                item.addProperty("idPedido", 0);
                                item.addProperty("totalPedido", 0);
                            }

                            item.addProperty("metodoPago", p.getMetodopago() != null ? p.getMetodopago().name() : "");
                            item.addProperty("monto", p.getMonto());
                            item.addProperty("comprobante", p.getComprobante());

                            data.add(item);
                        }

                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("cantidad", pagosPendientes.size());
                        jsonResponse.add("data", data);

                        out.print(jsonResponse.toString());
                        return;

                    } catch (Exception e) {
                        e.printStackTrace();

                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Error al listar pagos pendientes: " + e.getMessage());

                        out.print(jsonResponse.toString());
                        return;
                    }
                }

                case "buscarPagoPorPedido": {
                    try {
                        int idPedido = Integer.parseInt(request.getParameter("idPedido"));

                        Pago pago = pagoDao.buscarPorPedido(idPedido);

                        if (pago != null) {
                            JsonObject data = new JsonObject();

                            data.addProperty("idPago", pago.getIdPago());

                            if (pago.getPedido() != null) {
                                data.addProperty("idPedido", pago.getPedido().getIdPedido());
                            } else {
                                data.addProperty("idPedido", 0);
                            }

                            data.addProperty("metodoPago", pago.getMetodopago() != null ? pago.getMetodopago().name() : "");
                            data.addProperty("monto", pago.getMonto());
                            data.addProperty("comprobante", pago.getComprobante());

                            jsonResponse.addProperty("success", true);
                            jsonResponse.add("data", data);

                        } else {
                            jsonResponse.addProperty("success", false);
                            jsonResponse.addProperty("message", "No se encontró pago para ese pedido");
                        }

                        out.print(jsonResponse.toString());
                        return;

                    } catch (Exception e) {
                        e.printStackTrace();

                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Error al buscar pago: " + e.getMessage());

                        out.print(jsonResponse.toString());
                        return;
                    }
                }

                case "aprobarPago": {
                    try {
                        int idPedido = Integer.parseInt(request.getParameter("idPedido"));

                        boolean actualizado = pedidoDao.actualizarEstado(idPedido, "PROCESADO");

                        jsonResponse.addProperty("success", actualizado);
                        jsonResponse.addProperty(
                                "message",
                                actualizado ? "Pago aprobado. Pedido procesado." : "No se pudo aprobar el pago"
                        );

                        out.print(jsonResponse.toString());
                        return;

                    } catch (Exception e) {
                        e.printStackTrace();

                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Error al aprobar pago: " + e.getMessage());

                        out.print(jsonResponse.toString());
                        return;
                    }
                }
                case "historialVentas": {
                    listarHistorialVentas(out);
                    break;
                }

                default: {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Acción no encontrada");
                    out.print(jsonResponse.toString());
                    return;
                }
            }
        }
    }

    private void listarHistorialVentas(PrintWriter out) {
        JsonObject respuesta = new JsonObject();
        JsonArray data = new JsonArray();

        String sql = "SELECT "
                + "p.idPedido, p.estado, p.total, "
                + "u.email, per.nombre, per.apellidos, "
                + "pa.idPago, pa.metodo_pago, pa.monto, pa.comprobante, pa.fecha_pago AS fecha, "
                + "GROUP_CONCAT(CONCAT(prod.nombre, ' (', dprod.talla, ' / ', dprod.color, ') x', dp.cantidad) SEPARATOR ' | ') AS productos "
                + "FROM pedido p "
                + "INNER JOIN usuario u ON p.idUsuario = u.idUsuario "
                + "INNER JOIN persona per ON u.idPersona = per.idPersona "
                + "LEFT JOIN pago pa ON p.idPedido = pa.idPedido "
                + "LEFT JOIN detalle_pedido dp ON p.idPedido = dp.idPedido "
                + "LEFT JOIN detalle_producto dprod ON dp.idDetalle = dprod.idDetalle "
                + "LEFT JOIN producto prod ON dprod.idProducto = prod.idProducto "
                + "GROUP BY p.idPedido, p.estado, p.total, "
                + "u.email, per.nombre, per.apellidos, "
                + "pa.idPago, pa.metodo_pago, pa.monto, pa.comprobante, pa.fecha_pago "
                + "ORDER BY p.idPedido DESC";

        try {
            Connection cn = ConexionSingleton.getConnection();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JsonObject item = new JsonObject();

                item.addProperty("idPedido", rs.getInt("idPedido"));

                // Fecha de venta, usando la fecha del pago
                java.sql.Timestamp fecha = rs.getTimestamp("fecha");
                item.addProperty("fecha", fecha != null ? fecha.toString() : "");

                item.addProperty("estado", rs.getString("estado"));
                item.addProperty("total", rs.getDouble("total"));

                item.addProperty("cliente", rs.getString("nombre") + " " + rs.getString("apellidos"));
                item.addProperty("email", rs.getString("email"));

                item.addProperty("idPago", rs.getInt("idPago"));
                item.addProperty("metodoPago", rs.getString("metodo_pago"));
                item.addProperty("monto", rs.getDouble("monto"));
                item.addProperty("comprobante", rs.getString("comprobante"));

                // Si quieres también mandar fechaPago, puedes usar la misma fecha
                item.addProperty("fechaPago", fecha != null ? fecha.toString() : "");

                item.addProperty("productos", rs.getString("productos"));

                data.add(item);
            }

            respuesta.addProperty("success", true);
            respuesta.add("data", data);

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.addProperty("success", false);
            respuesta.addProperty("message", "Error al listar historial de ventas: " + e.getMessage());
        }

        out.print(respuesta.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "PagoController";
    }
}
