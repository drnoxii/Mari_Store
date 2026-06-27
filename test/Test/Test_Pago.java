/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Dao.PagoDaoImpl;
import Interface.IPago;
import Model.MetodoPago;
import Model.Pago;
import Model.Pedido;
import java.util.List;

/**
 *
 * @author spide
 */
public class Test_Pago {

   IPago dao = new PagoDaoImpl();
    public static void main(String[] args) {
   

    

        Test_Pago p = new Test_Pago();

         //p.registrar();
        // p.buscarPorPedido();
        p.listarPendientes();
    }

    public void registrar() {

        Pago pago = new Pago();

        Pedido pedido = new Pedido();
        pedido.setIdPedido(2); // Cambia por un idPedido que esté PROCESADO y sin pago

        pago.setPedido(pedido);
        pago.setMetodopago(MetodoPago.YAPE); // Cambia según tu enum
        pago.setMonto(150.00);
        pago.setComprobante("COMP-001");

        boolean result = dao.registrarPago(pago);

        if (result) {
            System.out.println("Pago registrado correctamente");
        } else {
            System.out.println("Error al registrar pago");
        }
    }

    public void buscarPorPedido() {

        int idPedido = 2; // Cambia por el idPedido que quieras buscar

        Pago pago = dao.buscarPorPedido(idPedido);

        if (pago != null) {
            System.out.println("=================================");
            System.out.println("ID Pago: " + pago.getIdPago());
            System.out.println("ID Pedido: " + pago.getPedido().getIdPedido());
            System.out.println("Método de Pago: " + pago.getMetodopago());
            System.out.println("Monto: " + pago.getMonto());
            System.out.println("Comprobante: " + pago.getComprobante());
            System.out.println("=================================");
        } else {
            System.out.println("No se encontró pago para el pedido con ID: " + idPedido);
        }
    }

    public void listarPendientes() {

        List<Pago> lista = dao.listarPendientes();

        if (lista != null && !lista.isEmpty()) {

            System.out.println("PEDIDOS PROCESADOS PENDIENTES DE PAGO");

            for (Pago p : lista) {
                System.out.println("=================================");
                System.out.println("ID Pedido: " + p.getPedido().getIdPedido());
                System.out.println("Monto pendiente: " + p.getMonto());
                System.out.println("=================================");
            }

        } else {
            System.out.println("No hay pedidos pendientes de pago");
        }
    }
}