package Dao;

import Interface.IPersona;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDaoImpl implements IPersona {

    private Connection cn;

    @Override
    public List<Persona> lista() {
        List<Persona> lista = new ArrayList<>();

        String query = "SELECT idpersona, nombre, apellidos, DNI, telefono FROM persona";

        try {
            cn = ConexionSingleton.getConnection();

            try (PreparedStatement st = cn.prepareStatement(query);
                 ResultSet rs = st.executeQuery()) {

                while (rs.next()) {
                    Persona p = new Persona();

                    p.setIdPersona(rs.getInt("idpersona"));
                    p.setNombre(rs.getString("nombre"));
                    p.setApellidos(rs.getString("apellidos"));
                    p.setDNI(rs.getInt("DNI"));
                    p.setTelefono(rs.getInt("telefono"));

                    lista.add(p);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar personas: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public int insert(Persona p, Usuario u) {
        int resultado = 0;
        int idPersonaGenerado = 0;

        String sqlPersona = "INSERT INTO persona(nombre, apellidos, DNI, telefono) VALUES (?, ?, ?, ?)";
        String sqlUsuario = "INSERT INTO usuario(email, contraseña, rol, idpersona) VALUES (?, ?, ?, ?)";

        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);

            try (PreparedStatement stPersona = cn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

                stPersona.setString(1, p.getNombre());
                stPersona.setString(2, p.getApellidos());
                stPersona.setInt(3, p.getDNI());
                stPersona.setInt(4, p.getTelefono());

                int filasPersona = stPersona.executeUpdate();

                if (filasPersona > 0) {
                    try (ResultSet rs = stPersona.getGeneratedKeys()) {
                        if (rs.next()) {
                            idPersonaGenerado = rs.getInt(1);
                            System.out.println("id_persona recuperado: " + idPersonaGenerado);
                        }
                    }
                }
            }

            if (idPersonaGenerado == 0) {
                cn.rollback();
                return 0;
            }

            /*
             * Si no mandas rol desde el AuthController,
             * por defecto se registra como CLIENTE.
             *
             * Si mandas ADMIN, se respeta ADMIN.
             */
            if (u.getRol() == null) {
                u.setRol(Rol.CLIENTE);
            }

            String hashedPassword = u.HashPassword(u.getContraseña());

            try (PreparedStatement stUsuario = cn.prepareStatement(sqlUsuario)) {

                stUsuario.setString(1, u.getEmail());
                stUsuario.setString(2, hashedPassword);
                stUsuario.setString(3, u.getRol().name());
                stUsuario.setInt(4, idPersonaGenerado);

                resultado = stUsuario.executeUpdate();
            }

            cn.commit();

        } catch (Exception e) {
            System.out.println("Error al agregar persona/usuario: " + e.getMessage());

            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (Exception ex) {
                System.out.println("Error de rollback: " + ex.getMessage());
            }

            resultado = 0;

        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                }
            } catch (Exception ex) {
                System.out.println("Error restaurando autoCommit: " + ex.getMessage());
            }
        }

        return resultado;
    }

    @Override
    public boolean update(Persona p) {
        boolean flag = false;

        String query = "UPDATE persona SET nombre = ?, apellidos = ?, DNI = ?, telefono = ? WHERE idpersona = ?";

        try {
            cn = ConexionSingleton.getConnection();

            try (PreparedStatement st = cn.prepareStatement(query)) {

                st.setString(1, p.getNombre());
                st.setString(2, p.getApellidos());
                st.setInt(3, p.getDNI());
                st.setInt(4, p.getTelefono());
                st.setInt(5, p.getIdPersona());

                int r = st.executeUpdate();

                if (r > 0) {
                    flag = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error al actualizar persona: " + e.getMessage());
            flag = false;
        }

        return flag;
    }

    @Override
    public Persona SearchById(int id) {
        Persona p = null;

        String query = "SELECT idpersona, nombre, apellidos, DNI, telefono FROM persona WHERE idpersona = ?";

        try {
            cn = ConexionSingleton.getConnection();

            try (PreparedStatement st = cn.prepareStatement(query)) {

                st.setInt(1, id);

                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        p = new Persona();

                        p.setIdPersona(rs.getInt("idpersona"));
                        p.setNombre(rs.getString("nombre"));
                        p.setApellidos(rs.getString("apellidos"));
                        p.setDNI(rs.getInt("DNI"));
                        p.setTelefono(rs.getInt("telefono"));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar persona por ID: " + e.getMessage());
        }

        return p;
    }

    @Override
    public boolean delete(int id) {
        boolean flag = false;

        String sqlUsuario = "DELETE FROM usuario WHERE idpersona = ?";
        String sqlPersona = "DELETE FROM persona WHERE idpersona = ?";

        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);

            try (PreparedStatement stUsuario = cn.prepareStatement(sqlUsuario)) {
                stUsuario.setInt(1, id);
                stUsuario.executeUpdate();
            }

            try (PreparedStatement stPersona = cn.prepareStatement(sqlPersona)) {
                stPersona.setInt(1, id);

                int r = stPersona.executeUpdate();

                if (r > 0) {
                    flag = true;
                }
            }

            cn.commit();

        } catch (Exception e) {
            System.out.println("Error al eliminar persona: " + e.getMessage());

            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (Exception ex) {
                System.out.println("Error de rollback: " + ex.getMessage());
            }

            flag = false;

        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                }
            } catch (Exception ex) {
                System.out.println("Error restaurando autoCommit: " + ex.getMessage());
            }
        }

        return flag;
    }
}