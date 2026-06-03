/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Dao.PersonaDaoImpl;
import Dao.UsuarioDaoImpl;
import Interface.IPersona;
import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import java.time.LocalDate;

/**
 *
 * @author spide
 */
public class Test_Persona {

    IPersona dao = new PersonaDaoImpl();
    IUsuario Udao = new UsuarioDaoImpl();
    
    
    
    public static void main(String[] args) {
       
        Test_Persona t = new Test_Persona(); 
        t.insert();
       // t.valid_user();
        
    }
    
    public void insert(){
        Persona p = new Persona();
        
        p.setNombre("Josias David");
        p.setApellidos("Conislla Panayfo");
        p.setTelefono(987654321);
        p.setDNI(72899110);
        p.setFecha_nacimiento(LocalDate.MIN);
        
        Usuario u = new Usuario();
        u.setEmail("josias@gmail.com");
        u.setContraseña("admin123");
        u.setRol(Rol.CLIENTE);
        int result = dao.insert(p, u);
        
        if (result > 0 ){
        System.out.println("Persona y User Creada");
        System.out.println("Usuario:" + u.getEmail());
        System.out.println("Rol asignado" + u.getRol());
        
        }else{
         System.out.println("No se pudo realizar el registro");
        }
    
    }
    
    public void valid_user(){
    
    Usuario u = Udao.validate("josias@gmail.com", "admin123");
        if (u!= null && u.getPersona()!= null) {
            System.out.println("Bievenido "+ u.getPersona() .getNombre());
            System.out.println("Rol:"+ u.getRol());
            System.out.println("Usuario:"+ u.getEmail());
            System.out.println("User_id:"+ u.getIdUsuario());
            System.out.println("persona_id:"+ u.getPersona() .getIdPersona());
            
        }else{
            System.out.println("Credenciales incorrectas");
        
        }
    }    
}
