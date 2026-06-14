/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author spide
 */
public class UsuarioDaoImpl implements IUsuario {
    private Connection cn;
    
    @Override
    public Usuario validate(String user, String password) {

Usuario u = null;
        Persona p = null;
        
        PreparedStatement st;
        ResultSet rs;
        String query = null;    
        
        try{
            u = new Usuario();
            p = new Persona();
            String hashedPassword = u.HashPassword(password) ;
            query = " select u.idusuario, u.email,u.rol, p.idpersona,"
                    + " p.nombre "
                    + " FROM persona p, usuario u "
                    + " where p.idpersona = u.idpersona "
                    + " AND u.email = ? "
                    + " AND u.contraseña = ?";
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query);
            st.setString(1, user );
            st.setString(2, hashedPassword );
            rs = st.executeQuery();
            while (rs.next()) {                
                u = new Usuario();
                u.setIdUsuario(rs.getInt("idusuario"));
                u.setEmail(rs.getString("email"));
                u.setRol(Rol.valueOf(rs.getString("rol").toUpperCase()));
                p.setIdPersona(rs.getInt("idpersona"));
                p.setNombre(rs.getString("nombre"));
               u.setPersona(p);
            }
        }catch(Exception e){
            System.out.println("Error al validar usuario:"+ e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
               
            }
            System.out.println("No se pudo validar el usuario");
        }finally {
            if (cn!=null) {
                try {
                } catch (Exception ex) {
                    
                }
            }
        }return u;
        
}  
    
}
