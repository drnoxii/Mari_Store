/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.security.Timestamp;
import java.util.List;

/**
 *
 * @author spide
 */
public class Carrito {

    private int idDetalle;          
    private int idProducto;         
    private String nombre;          
    private String descripcion;     
    private String imagen;          
    private String talla;           
    private int cantidad;           
    private int stock;             
    private double precioCompra;    
    private double subTotal;  

    public Carrito() {
    }

    public Carrito(int idDetalle, int idProducto, String nombre, String descripcion, String imagen, String talla, int cantidad, int stock, double precioCompra, double subTotal) {
        this.idDetalle = idDetalle;
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.talla = talla;
        this.cantidad = cantidad;
        this.stock = stock;
        this.precioCompra = precioCompra;
        this.subTotal = subTotal;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
    
       

       

        
    
}

    
    

