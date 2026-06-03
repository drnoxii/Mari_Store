/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author spide
 */
public class Usuario {
    
    private int idUsuario;
    private Persona persona;
    private String email;
    private String contraseña;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(int idUsuario, Persona persona, String email, String contraseña, Rol rol) {
        this.idUsuario = idUsuario;
        this.persona = persona;
        this.email = email;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    public String HashPassword(String password){

    try {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(password.getBytes());
    StringBuilder hexString = new StringBuilder();
    for(byte b :hash){
    String hex = Integer.toHexString(0xff & b);
    if(hex.length()==1){
    hexString.append('0');
    }
    hexString.append(hex);

    }
    return 
            hexString.toString();
    } catch(NoSuchAlgorithmException e) {
        e.printStackTrace();
        throw new RuntimeException("Error al generar el hash",e);
    
    }

}
    
    
}
