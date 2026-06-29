package Dao;

import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDaoImpl implements IUsuario {

    private Connection cn;

    @Override
    public Usuario validate(String user, String password) {
        Usuario u = null;

        String query = "SELECT "
                + "u.idusuario, "
                + "u.email, "
                + "u.rol, "
                + "p.idpersona, "
                + "p.nombre, "
                + "p.apellidos, "
                + "p.DNI, "
                + "p.telefono "
                + "FROM usuario u "
                + "INNER JOIN persona p ON p.idpersona = u.idpersona "
                + "WHERE u.email = ? "
                + "AND u.contraseña = ?";

        try {
            Usuario temp = new Usuario();
            String hashedPassword = temp.HashPassword(password);

            cn = ConexionSingleton.getConnection();

            try (PreparedStatement st = cn.prepareStatement(query)) {

                st.setString(1, user);
                st.setString(2, hashedPassword);

                try (ResultSet rs = st.executeQuery()) {

                    if (rs.next()) {
                        u = new Usuario();

                        u.setIdUsuario(rs.getInt("idusuario"));
                        u.setEmail(rs.getString("email"));

                        String rolBD = rs.getString("rol");

                        if (rolBD != null) {
                            u.setRol(Rol.valueOf(rolBD.toUpperCase()));
                        } else {
                            u.setRol(Rol.CLIENTE);
                        }

                        Persona p = new Persona();
                        p.setIdPersona(rs.getInt("idpersona"));
                        p.setNombre(rs.getString("nombre"));
                        p.setApellidos(rs.getString("apellidos"));
                        p.setDNI(rs.getInt("DNI"));
                        p.setTelefono(rs.getInt("telefono"));

                        u.setPersona(p);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
            u = null;
        }

        return u;
    }
}