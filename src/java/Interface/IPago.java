/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Pago;
import java.util.List;

/**
 *
 * @author spide
 */
public interface IPago {
    public boolean registrarPago(Pago pago);
    public Pago buscarPorPedido(int idPedido);
    public List<Pago> listarPendientes();
    
}
