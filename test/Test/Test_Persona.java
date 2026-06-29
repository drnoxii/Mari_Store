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
import Util.ConexionSingleton;
import java.time.LocalDate;
import java.util.List;

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
       //t.testLista();
       // t.testUpdate();
       // t.testSearchById();
     //  t.testDelete();
    }
    
    public void insert(){
        Persona p = new Persona();
        
        p.setNombre("Josias David");
        p.setApellidos("Conislla Panayfo");
        p.setTelefono(987654321);
        p.setDNI(80529222);
        p.setFecha_nacimiento(LocalDate.MIN);
        
        Usuario u = new Usuario();
        u.setEmail("jd@gmail.com");
        u.setContraseña("admin123");
        u.setRol(Rol.ADMIN);
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
    
       public void testUpdate() {

       
        System.out.println("===== TEST UPDATE =====");

        if (2 <= 0) {
            System.out.println("No hay ID de persona para actualizar");
            return;
        }

        Persona p = new Persona();

        /*
         Si tu modelo usa setIdPersona(), usa ese.
         Si tu modelo usa setId_Persona(), deja este.
        */
        p.setIdPersona(3);

        p.setNombre("Carlos");
        p.setApellidos("Ramirez Actualizado");
        p.setDNI(80808080);
        p.setTelefono(999888777);

        boolean actualizado = dao.update(p);

        if (actualizado) {
            System.out.println("UPDATE OK");
        } else {
            System.out.println("UPDATE FALLÓ");
        }

        System.out.println();
    }

    public void testLista() {

            
        System.out.println("===== TEST LISTA =====");

        List<Persona> personas = dao.lista();

        if (personas != null && !personas.isEmpty()) {

            for (Persona p : personas) {
                System.out.println(
                        p.getIdPersona() + " - " +
                        p.getNombre() + " " +
                        p.getApellidos() +
                        " | DNI: " + p.getDNI() +
                        " | Teléfono: " + p.getTelefono()
                );
            }

        } else {
            System.out.println("No hay personas registradas o falló el listado");
        }

        System.out.println();
    }

    public  void testDelete() {

        System.out.println("===== TEST DELETE =====");

        if (3 <= 0) {
            System.out.println("No hay ID de persona para eliminar");
            return;
        }

        boolean eliminado = dao.delete(8);

        if (eliminado) {
            System.out.println("DELETE OK");
        } else {
            System.out.println("DELETE FALLÓ");
        }

        System.out.println();
    }

    
    
     public  void testSearchById() {

        System.out.println("===== TEST SEARCH BY ID =====");

        if (2 <= 0) {
            System.out.println("No hay ID de persona para buscar");
            return;
        }

        Persona p = dao.SearchById(2);

        if (p != null) {
            System.out.println("Persona encontrada:");
            System.out.println("ID: " + p.getIdPersona());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Apellidos: " + p.getApellidos());
            System.out.println("DNI: " + p.getDNI());
            System.out.println("Teléfono: " + p.getTelefono());
        } else {
            System.out.println("No se encontró la persona");
        }

        System.out.println();
    }

}
