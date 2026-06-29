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

                default: {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Acción no encontrada");
                    out.print(jsonResponse.toString());
                    return;
                }
            }
        }
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
