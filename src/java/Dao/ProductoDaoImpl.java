/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Interface.IProducto;
import Model.Categoria;
import Model.Detalle_Producto;
import Model.Productos;
import Util.ConexionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author spide
 */
public class ProductoDaoImpl implements IProducto{

    private Connection cn;
    
    
        Productos pr;
        PreparedStatement st;
        ResultSet rs;
        String query = null;
       
    
    @Override
    public List<Productos> lista() {

        List<Productos> lista = null;
        
        try {
            query = "SELECT p.idProducto, p.nombre, p.descripcion, p.precio, p.imagen, "
                  + "c.idCategoria, c.nombre AS nombreCategoria, "
                  + "dp.idDetalle, dp.talla, dp.color, dp.stock "
                  + "FROM Producto p "
                  + "JOIN Categoria c ON p.idCategoria = c.idCategoria "
                  + "JOIN Detalle_producto dp ON p.idProducto = dp.idProducto";
            
            lista = new ArrayList <>();
            
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {  
               pr = new Productos();
               pr.setIdProducto((rs.getInt("idproducto")));
               pr.setNombre(rs.getString("nombre"));
               pr.setDescripcion(rs.getString("descripcion"));
               pr.setPrecio(rs.getDouble("precio"));
               pr.setImagen(rs.getString("imagen"));
               
               Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getInt("idCategoria"));
                cat.setNombre(rs.getString("nombreCategoria"));
                pr.setCategoria(cat);
                
                query = "SELECT idDetalle, talla, color, stock FROM Detalle_producto WHERE idProducto=?";
            st = cn.prepareStatement(query);
            st.setInt(1, pr.getIdProducto());
            ResultSet rsVariantes = st.executeQuery();
            List<Detalle_Producto> variantes = new ArrayList<>();
            while (rsVariantes.next()) {
                Detalle_Producto variante = new Detalle_Producto();
                variante.setIdDetalle(rsVariantes.getInt("idDetalle"));
                variante.setTalla(rsVariantes.getString("talla"));
                variante.setColor(rsVariantes.getString("color"));
                variante.setStock(rsVariantes.getInt("stock"));
                variantes.add(variante);
            }
            pr.setVariantes(variantes);
            lista.add(pr);
                
                                        }
            
        } catch(Exception e){
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
        }return lista;


    }

    @Override
    public boolean insert(Productos p) {
           
        boolean flag = false;
    try {
        query = "INSERT INTO producto (idcategoria ,nombre, descripcion, precio,imagen)"
    + "VALUES(?,?,?,?,?)";
        cn = ConexionSingleton.getConnection();
        st = cn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
        st.setInt(1, p.getCategoria().getIdCategoria());
        st.setString(2, p.getNombre());
        st.setString(3, p.getDescripcion());
        st.setDouble(4, p.getPrecio());
        st.setString(5, p.getImagen());
        st.executeUpdate();
        
        rs = st.getGeneratedKeys();
        int idProducto = 0;
        if (rs.next()) {
            idProducto = rs.getInt(1);
        } 
        
        query = "INSERT INTO Detalle_producto (idProducto, talla, color, stock) VALUES (?,?,?,?)";
        st = cn.prepareStatement(query);
        for (Detalle_Producto variante : p.getVariantes()) {
            st.setInt(1, idProducto);
            st.setString(2, variante.getTalla());
            st.setString(3, variante.getColor());
            st.setInt(4, variante.getStock());
            st.executeUpdate();
        
            flag = true; 
            
        }} 
        catch(Exception e){
            System.out.println("Error al buscar:"+ e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
               flag = false;
            }
            System.out.println("No se pudo insertar el producto");
        }finally {
            if (cn!=null) {
                try {
                } catch (Exception ex) {
                    
                }
            }
        }return flag;

    }
    
    @Override
    public boolean update(Productos p) {
        boolean flag = false;
        
                
        try {
        query = "UPDATE producto SET idcategoria= ?, nombre=?, descripcion= ?, precio =?, imagen=?"
    + "WHERE idproducto =?";
        cn = ConexionSingleton.getConnection();
        st = cn.prepareStatement(query);
        st.setInt(1,p.getCategoria().getIdCategoria());
        st.setString(2, p.getNombre());
        st.setString(3, p.getDescripcion());
        st.setDouble(4, p.getPrecio());
        st.setString(5, p.getImagen());
        st.setInt(6,p.getIdProducto());
        st.executeUpdate();
        
        query = "UPDATE Detalle_producto SET talla=?, color=?, stock=? WHERE idDetalle=?";
        st = cn.prepareStatement(query);
        for (Detalle_Producto variante : p.getVariantes()) {
            st.setString(1, variante.getTalla());
            st.setString(2, variante.getColor());
            st.setInt(3, variante.getStock());
            st.setInt(4, variante.getIdDetalle());
            st.executeUpdate();
        }
        
        flag = true;
            
        } catch(Exception e){
            System.out.println("Error al actulizar:"+ e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
               flag = false;
            }
            System.out.println("No se pudo actualizar el producto");
        }finally {
            if (cn!=null) {
                try {
                } catch (Exception ex) {
                    
                }
            }
        }return flag;

    }

    @Override
    public Productos SearchbyId(int id) {
        try {
            query = "SELECT p.idProducto, p.nombre, p.descripcion, p.precio, p.imagen, "
              + "c.idCategoria, c.nombre AS nombreCategoria "
              + "FROM Producto p "
              + "JOIN Categoria c ON p.idCategoria = c.idCategoria "
              + "WHERE p.idProducto=?";
                        
            cn = ConexionSingleton.getConnection();
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            while (rs.next()) {  
               pr = new Productos();
               pr.setIdProducto(rs.getInt("idproducto"));
               pr.setNombre(rs.getString("nombre"));
               pr.setDescripcion(rs.getString("descripcion"));
               pr.setPrecio(rs.getDouble("precio"));
               pr.setImagen(rs.getString("imagen"));
               Categoria cat = new Categoria();
               cat.setIdCategoria(rs.getInt("idCategoria"));
                cat.setNombre(rs.getString("nombreCategoria"));
                pr.setCategoria(cat);
                
            query = "SELECT idDetalle, talla, color, stock FROM Detalle_producto WHERE idProducto=?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            ResultSet rsVariantes = st.executeQuery();
            List<Detalle_Producto> variantes = new ArrayList<>();
            while (rsVariantes.next()) {
                Detalle_Producto variante = new Detalle_Producto();
                variante.setIdDetalle(rsVariantes.getInt("idDetalle"));
                variante.setTalla(rsVariantes.getString("talla"));
                variante.setColor(rsVariantes.getString("color"));
                variante.setStock(rsVariantes.getInt("stock"));
                variantes.add(variante);
            }
            pr.setVariantes(variantes);
                
                
                                        }
            
        } catch(Exception e){
            System.out.println("Error al validar el ID:"+ e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
               
            }
            System.out.println("No se pudo validar el ID");
        }finally {
            if (cn!=null) {
                try {
                } catch (Exception ex) {
                    
                }
            }
        }return pr;


    }

    @Override
    public boolean delete(int id) {
        
        boolean flag = false;
          try {
              
        cn = ConexionSingleton.getConnection();
        query = "DELETE FROM Detalle_producto WHERE idProducto=?";
        st = cn.prepareStatement(query);
        st.setInt(1, id);
        st.executeUpdate();      
              
              
              
        query = "DELETE FROM producto WHERE idproducto = ?";
   
        
        st = cn.prepareStatement(query);
        st.setInt(1, id);
        st.executeUpdate();
        flag = true;
            
        } catch(Exception e){
            System.out.println("Error al eliminar:"+ e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
               flag = false;
            }
            System.out.println("No se pudo eliminar el producto");
        }finally {
            if (cn!=null) {
                try {
                } catch (Exception ex) {
                    
                }
            }
        }return flag;


    }

    @Override
    public boolean updateStock(int id, int stock) {
         
        boolean flag = false;
        
        try {
        query = "UPDATE detalle_producto SET stock = ? "
    + "WHERE idDetalle = ?";
        cn = ConexionSingleton.getConnection();
        st = cn.prepareStatement(query);
        st.setInt(1, stock);
        st.setInt(2, id);
        st.executeUpdate();
        flag = true;
            
        } catch(Exception e){
            System.out.println("Error al actulizar el stock:"+ e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
               flag = false;
            }
            System.out.println("No se pudo actualizar el stock");
        }finally {
            if (cn!=null) {
                try {
                } catch (Exception ex) {
                    
                }
            }
        }return flag;


    }
    
}
