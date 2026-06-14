/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Dao.ProductoDaoImpl;
import Interface.IProducto;
import Model.Categoria;
import Model.Detalle_Producto;
import Model.Productos;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author spide
 */
public class Test_Producto {

    IProducto dao = new ProductoDaoImpl();

    public static void main(String[] args) {

        Test_Producto p = new Test_Producto();

       // p.insert();;
        //p.listar();
       // p.buscarid();
       p.update();
      //p.delete();
     // p.updatestock();
    }

    public void insert() {

        Productos p = new Productos();

        Categoria cat = new Categoria();
        cat.setIdCategoria(1);

        p.setCategoria(cat);
        p.setNombre("Polo NIKE");
        p.setDescripcion("Polo deportivo");
        p.setPrecio(89.90);
        p.setImagen("/NIKE.jpg");

        List<Detalle_Producto> variantes = new ArrayList<>();

        Detalle_Producto v1 = new Detalle_Producto();
        v1.setTalla("S");
        v1.setColor("Negro");
        v1.setStock(20);

        Detalle_Producto v2 = new Detalle_Producto();
        v2.setTalla("M");
        v2.setColor("Blanco");
        v2.setStock(40);

        variantes.add(v1);
        variantes.add(v2);

        p.setVariantes(variantes);
        boolean result = dao.insert(p);
        if (result) {
            System.out.println("Producto insertado");
        } else {

            System.out.println("Error al insertar");
        }
    }

    public void listar() {

        List<Productos> lista = dao.lista();

        if (lista != null && !lista.isEmpty()) {
            for (Productos p : lista) {
                System.out.println("=================================");
                System.out.println("ID: " + p.getIdProducto());
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Descripcion: " + p.getDescripcion());
                System.out.println("Precio: " + p.getPrecio());
                System.out.println("Imagen: " + p.getImagen());

              if (p.getCategoria() != null) {
                 System.out.println("Categoria: " + p.getCategoria().getNombre());
                }

                System.out.println("Variantes:");

                if (p.getVariantes() != null && !p.getVariantes().isEmpty()) {

                    System.out.println("IDDetalle\tTalla\tColor\tStock");

                    for (Detalle_Producto d : p.getVariantes()) {
                        System.out.println(
                                d.getIdDetalle() + "\t\t" + d.getTalla() + "\t"+ d.getColor() + "\t"+ d.getStock());
                    }
                } else { 
                    System.out.println("Sin variantes");
                }
                System.out.println("=================================");
            }
        } else {
            System.out.println("No hay productos");
        }
    }
    
    public void buscarid(){
    
    
    Productos p = dao.SearchbyId(2);

    if (p != null) {

        System.out.println("Nombre: " + p.getNombre());
        System.out.println("Descripcion: " + p.getDescripcion());
        System.out.println("Precio: " + p.getPrecio());

        System.out.println("Categoria: "
                + p.getCategoria().getNombre());

        for (Detalle_Producto d : p.getVariantes()) {

            System.out.println(
                    d.getTalla() + " "
                    + d.getColor() + " "
                    + d.getStock());
        }

    } else {
        System.out.println("No existe");
    }
}
    public void update(){
    
    Productos p = new Productos();

    p.setIdProducto(2);

    Categoria cat = new Categoria();
    cat.setIdCategoria(2);

    p.setCategoria(cat);

    p.setNombre("Polo Nike Actualizado");
    p.setDescripcion("Nueva descripcion");
    p.setPrecio(99.90);
    p.setImagen("nike2.jpg");

    List<Detalle_Producto> variantes = new ArrayList<>();

    Detalle_Producto d1 = new Detalle_Producto();
    d1.setIdDetalle(1);
    d1.setTalla("M");
    d1.setColor("Azul");
    d1.setStock(30);

    Detalle_Producto d2 = new Detalle_Producto();
    d2.setIdDetalle(2);
    d2.setTalla("L");
    d2.setColor("Negro");
    d2.setStock(20);

    variantes.add(d1);
    variantes.add(d2);

    p.setVariantes(variantes);

     boolean result = dao.update(p);
        if (result) {
            System.out.println("Producto actualizado");
        }else {
        
            System.out.println("Error al actulizar");
        }
   
   }
    
    public void delete(){
        
    boolean result = dao.delete(3);
  
       if (result) {
           System.out.println("Producto Eliminado");
       } else {
           System.out.println("No se pudo eliminar");
       }
   
   }
    
    public  void updatestock(){
   boolean result = dao.updateStock(2,299);
  
       if (result) {
           System.out.println("Stock Actulizado");
       } else {
           System.out.println("No se pudo actualizar");
       }
   }
   
}
    

