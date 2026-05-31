/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Persona;
import Model.Usuario;
import java.util.List;

/**
 *
 * @author spide
 */
public interface IPersona {
   
    public List<Persona> lista();
    public int insert(Persona p, Usuario u);
    public boolean update(Persona p);
    public Persona SearchById(int id);
    public boolean delete(int id);
    
    
}

