/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalDate;

/**
 *
 * @author spide
 */
public class Envio {
    
    private int idEnvio;
    private Pedido pedido;
    private String direccion;
    private String referencia;
    private LocalDate fecha_entrega;
    private EstadoEnvio estadoenvio;

    public Envio() {
    }

    public Envio(int idEnvio, Pedido pedido, String direccion, String referencia, LocalDate fecha_entrega, EstadoEnvio estadoenvio) {
        this.idEnvio = idEnvio;
        this.pedido = pedido;
        this.direccion = direccion;
        this.referencia = referencia;
        this.fecha_entrega = fecha_entrega;
        this.estadoenvio = estadoenvio;
    }

    public int getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(int idEnvio) {
        this.idEnvio = idEnvio;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDate getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(LocalDate fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public EstadoEnvio getEstadoenvio() {
        return estadoenvio;
    }

    public void setEstadoenvio(EstadoEnvio estadoenvio) {
        this.estadoenvio = estadoenvio;
    }
    
    
    
}
