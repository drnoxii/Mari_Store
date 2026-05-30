/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.security.Timestamp;
import java.util.List;

/**
 *
 * @author spide
 */
public class Carrito {
    public class Pedido {
    private int idPedido;
    private Persona persona;
    private double total;
    private EstadoPedido estadoPedido;
    private Timestamp fecha;
    private List<Carrito> detallePedido;

        public Pedido() {
        }

        public Pedido(int idPedido, Persona persona, double total, EstadoPedido estadoPedido, Timestamp fecha, List<Carrito> detallePedido) {
            this.idPedido = idPedido;
            this.persona = persona;
            this.total = total;
            this.estadoPedido = estadoPedido;
            this.fecha = fecha;
            this.detallePedido = detallePedido;
        }

        public int getIdPedido() {
            return idPedido;
        }

        public void setIdPedido(int idPedido) {
            this.idPedido = idPedido;
        }

        public Persona getPersona() {
            return persona;
        }

        public void setPersona(Persona persona) {
            this.persona = persona;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public EstadoPedido getEstadoPedido() {
            return estadoPedido;
        }

        public void setEstadoPedido(EstadoPedido estadoPedido) {
            this.estadoPedido = estadoPedido;
        }

        public Timestamp getFecha() {
            return fecha;
        }

        public void setFecha(Timestamp fecha) {
            this.fecha = fecha;
        }

        public List<Carrito> getDetallePedido() {
            return detallePedido;
        }

        public void setDetallePedido(List<Carrito> detallePedido) {
            this.detallePedido = detallePedido;
        }
    
    
}
}
    
    

