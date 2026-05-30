/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Util.ConexionSingleton;
import java.sql.Connection;

/**
 *
 * @author spide
 */
public class Test_BD {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        Test_BD t= new Test_BD();
        t.testConexion();
    }
    
    public void testConexion(){
    
    ConexionSingleton conn = new ConexionSingleton();
    
    
    try {
    Connection connection = conn.getConnection();
    if(connection != null && !connection.isClosed())
            System.out.println("Conexion Satisfactoria");
    else {
        System.out.println("No se pudo  estrablerce conexion");
    }
            
    } catch (Exception e ){
        System.out.println("Error:"+ e.getMessage());
        e.printStackTrace();
    }    
} }
