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
}
