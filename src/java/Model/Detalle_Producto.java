/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author spide
 */
public class Detalle_Producto {
    private int idDetalle;
    private Productos producto;
    private String talla;
    private String color;
    private int stock;

    public Detalle_Producto() {
    }

    public Detalle_Producto(int idDetalle, Productos producto, String talla, String color, int stock) {
        this.idDetalle = idDetalle;
        this.producto = producto;
        this.talla = talla;
        this.color = color;
        this.stock = stock;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
    
}
