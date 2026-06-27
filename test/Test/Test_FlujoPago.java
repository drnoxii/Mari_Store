package Test;

import Dao.PagoDaoImpl;
import Dao.PedidoDaoImpl;
import Interface.IPago;
import Interface.IPedido;
import Model.Carrito;
import Model.EstadoPedido;
import Model.MetodoPago;
import Model.Pago;
import Model.Pedido;
import Model.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author spide
 */
public class Test_FlujoPago {

    IPedido pedidoDao = new PedidoDaoImpl();
    IPago pagoDao = new PagoDaoImpl();

    public static void main(String[] args) {

        Test_FlujoPago test = new Test_FlujoPago();

         // test.crearPedidoConPago();

         test.listarPendientes();

        // test.aprobarPago();
    }

    public void crearPedidoConPago() {

        // 1. Crear usuario existente
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1); // Cambia por un idUsuario real de tu BD

        // 2. Crear carrito temporal
        List<Carrito> carrito = new ArrayList<>();

        Carrito item = new Carrito();
        item.setIdDetalle(1); // Cambia por un idDetalle real de detalle_producto
        item.setIdProducto(1);
        item.setNombre("Media prueba");
        item.setCantidad(1);
        item.setPrecioCompra(50.90);
        item.setSubTotal(50.90);

        carrito.add(item);

        // 3. Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(50.90);
        pedido.setEstadopedido(EstadoPedido.PENDIENTE);
        pedido.setDetallePedido(carrito);

        int idPedidoGenerado = pedidoDao.generarPedido(pedido);

        if (idPedidoGenerado > 0) {
            System.out.println("Pedido generado con ID: " + idPedidoGenerado);

            // 4. Crear pago para ese pedido
            Pedido pedidoPago = new Pedido();
            pedidoPago.setIdPedido(idPedidoGenerado);

            Pago pago = new Pago();
            pago.setPedido(pedidoPago);
            pago.setMetodopago(MetodoPago.YAPE);
            pago.setMonto(50.90);
            pago.setComprobante("YAPE-" + System.currentTimeMillis());

            boolean pagoRegistrado = pagoDao.registrarPago(pago);

            if (pagoRegistrado) {
                System.out.println("Pago registrado correctamente");
            } else {
                System.out.println("No se pudo registrar el pago");
            }

        } else {
            System.out.println("No se pudo generar el pedido");
        }
    }

    public void listarPendientes() {

        List<Pago> lista = pagoDao.listarPendientes();

        if (lista != null && !lista.isEmpty()) {

            System.out.println("PAGOS PENDIENTES DE APROBACION");

            for (Pago p : lista) {
                System.out.println("=================================");
                System.out.println("ID Pago: " + p.getIdPago());
                System.out.println("ID Pedido: " + p.getPedido().getIdPedido());
                System.out.println("Metodo Pago: " + p.getMetodopago());
                System.out.println("Monto: " + p.getMonto());
                System.out.println("Comprobante: " + p.getComprobante());
                System.out.println("=================================");
            }

        } else {
            System.out.println("No hay pagos pendientes de aprobacion");
        }
    }

    public void aprobarPago() {

        int idPedido = 7; // Cambia por un pedido PENDIENTE con pago registrado

        boolean result = pedidoDao.actualizarEstado(idPedido, "PROCESADO");

        if (result) {
            System.out.println("Pago aprobado. Pedido cambiado a PROCESADO");
        } else {
            System.out.println("No se pudo aprobar el pago");
        }
    }
}