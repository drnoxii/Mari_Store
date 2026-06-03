/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Interface.IPersona;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author spide
 */
public class PersonaDaoImpl implements IPersona {

    private Connection cn;
    
    @Override
    public List<Persona> lista() {


        return null;


    }

    @Override
    public int insert(Persona p, Usuario u) {
        
        PreparedStatement st;
        String query = null;
        ResultSet rs;
        int id_persona = 0;
        int r = 0;
        try {
            query = "INSERT INTO persona(nombre,apellidos,DNI,telefono)"
                    + " VALUES (?, ?, ?, ?)";
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, p.getNombre());
            st.setString(2, p.getApellidos());
            st.setInt(3, p.getDNI());
            st.setInt(4, p.getTelefono());
            r = st.executeUpdate();

            if (r != 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    //linea que devuelve el id de la persona creada
                    id_persona = rs.getInt(1);
                    System.out.println("id_recuperado:" + id_persona);
                }
                if (id_persona > 0) {
                    u.setRol(Rol.CLIENTE);
                    String hashedPassword = u.HashPassword(u.getContraseña());
                    query = "INSERT INTO usuario(email,contraseña,rol,idpersona)"
                            + " VALUES (?,?,?,?)";
                    st = cn.prepareStatement(query);
                    st.setString(1, u.getEmail());
                    st.setString(2, hashedPassword);
                    st.setString(3, u.getRol().name());
                    st.setInt(4, id_persona);
                    r = st.executeUpdate();
                } else {
                    System.out.println("Error al agregar una persona");
                }
            }

        } catch (Exception e) {
            System.out.println("error al agregar" + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
                System.out.println("error de rollback" + e.getMessage());

            }

        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }
        return r;

    }

    @Override
    public boolean update(Persona p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Persona SearchById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean delete(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
