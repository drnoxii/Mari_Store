/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.List;

/**
 *
 * @author spide
 */
public class Pedido {
    private int idPedido;
    private Usuario usuario;
    private double total;
    private EstadoPedido estadopedido;
    private List<Carrito> detallePedido;

    public Pedido() {
    }

    public Pedido(int idPedido, Usuario usuario, double total, EstadoPedido estadopedido) {
        this.idPedido = idPedido;
        this.usuario = usuario;
        this.total = total;
        this.estadopedido = estadopedido;
    }

    public Pedido(List<Carrito> detallePedido) {
        this.detallePedido = detallePedido;
    }

    public List<Carrito> getDetallePedido() {
        return detallePedido;
    }

    public void setDetallePedido(List<Carrito> detallePedido) {
        this.detallePedido = detallePedido;
    }
    

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstadopedido() {
        return estadopedido;
    }

    public void setEstadopedido(EstadoPedido estadopedido) {
        this.estadopedido = estadopedido;
    }
    
    
    
    
}
