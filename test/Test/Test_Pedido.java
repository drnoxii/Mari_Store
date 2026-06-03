package Test;

import Dao.PedidoDaoImpl;
import Model.Carrito;
import Model.EstadoPedido;
import Model.Pedido;
import Model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class Test_Pedido {

    public static void main(String[] args) {

        // Crear usuario existente en la BD
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1); // Debe existir en tu tabla usuario

        // Crear productos del carrito
        Carrito item1 = new Carrito();
        item1.setIdDetalle(1);   // Debe existir en la BD
        item1.setCantidad(2);
        item1.setPrecioCompra(50.0);
        item1.setSubTotal(100.0);

        Carrito item2 = new Carrito();
        item2.setIdDetalle(2);
        item2.setCantidad(1);
        item2.setPrecioCompra(30.0);
        item2.setSubTotal(30.0);

        // Lista de detalles
        List<Carrito> detalles = new ArrayList<>();
        detalles.add(item1);
        detalles.add(item2);

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(130.0);

        // Si tienes un enum EstadoPedido
        pedido.setEstadopedido(EstadoPedido.PENDIENTE);

        pedido.setDetallePedido(detalles);

        // Probar DAO
        PedidoDaoImpl dao = new PedidoDaoImpl();

        int resultado = dao.generarPedido(pedido);

        if (resultado > 0) {
            System.out.println("Pedido registrado correctamente.");
        } else {
            System.out.println("Error al registrar el pedido.");
        }
    }
}