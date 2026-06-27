/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Pedido;

/**
 *
 * @author spide
 */
public interface IPedido {
    public int generarPedido(Pedido pedidos);
    public boolean actualizarEstado(int idPedido, String nuevoEstado); 
}
