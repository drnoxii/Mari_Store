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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author spide
 */
public class PersonaDaoImpl implements IPersona {

    private Connection cn;
    
    @Override
    public List<Persona> lista() {
        List<Persona> lista= null;
        Persona p ;
        PreparedStatement st;
        ResultSet rs;
        String query = null;


        try {
            query = "SELECT idpersona, nombre, apellidos, DNI, telefono FROM persona";
            lista = new ArrayList<>();
            
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            
        while (rs.next()) {
                p = new Persona();

                p.setIdPersona(rs.getInt("idpersona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidos(rs.getString("apellidos"));
                p.setDNI(rs.getInt("DNI"));
                p.setTelefono(rs.getInt("telefono"));
                
                lista.add(p);
            }

        } catch (Exception e) {
            System.out.println("Error al listar personas: " + e.getMessage());
        }

        return lista;
    
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
                if (u.getRol() == null) {
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
        
        boolean flag = false;
        PreparedStatement st;
        ResultSet rs;
        String query = null;
        
        try {
            query = "UPDATE persona SET nombre = ?, apellidos = ?, DNI = ?, telefono = ? WHERE idpersona = ?";
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query);
            
            
            st.setString(1, p.getNombre());
            st.setString(2, p.getApellidos());
            st.setInt(3, p.getDNI());
            st.setInt(4, p.getTelefono());
            st.setInt(5, p.getIdPersona());
            
             int r = st.executeUpdate();

        if (r > 0) {
            flag = true;
        }
       } catch (Exception e) {
            System.out.println("Error al actualizar:" + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            flag = false;
            System.out.println("No se pudo actualizar el producto");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }

        return flag;
    }

    @Override
    public Persona SearchById(int id) {
        Persona p = null;
        PreparedStatement st;
        ResultSet rs;
        String query = null;
        try {
            query = " SELECT * FROM persona WHERE idPersona=?;";
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            while (rs.next()) {
                p = new Persona();
                p.setIdPersona(rs.getInt("idpersona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidos(rs.getString("apellidos"));
                p.setDNI(rs.getInt("dni"));
                p.setTelefono(rs.getInt("telefono"));
                
            }

        } catch (Exception e) {
            System.out.println("Error al buscar por ID:" + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            System.out.println("No se pudo buscar por ID");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }
        return p;

    }    

    @Override
    public boolean delete(int id) {

        boolean flag = false;
        PreparedStatement st;
        String query;
        try {
            cn = ConexionSingleton.getConnection();
            query = "DELETE FROM usuario WHERE idPersona = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            st.executeUpdate();
            
            query = " DELETE FROM persona WHERE idpersona =?";
            
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            st.executeUpdate();
            flag = true;

        } catch (Exception e) {
            System.out.println("Error al eliminar:" + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            flag = false;
            System.out.println("No se pudo eliminar el producto");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }

        return flag;
    }
    
}
