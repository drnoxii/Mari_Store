/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Productos;
import java.util.List;

/**
 *
 * @author spide
 */
public interface IProducto {
    
    public List<Productos> lista();
    public boolean insert(Productos p);
    public boolean update (Productos p);
    public Productos SearchbyId(int id);
    public boolean delete(int id);
    public boolean updateStock(int id, int stock);
}

