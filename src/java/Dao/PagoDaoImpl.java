/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Interface.IPago;
import Model.MetodoPago;
import Model.Pago;
import Model.Pedido;
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
public class PagoDaoImpl implements IPago {

    private Connection cn;
    PreparedStatement st;
    ResultSet rs;

    @Override
    public boolean registrarPago(Pago pago) {
        String query = null;

        boolean registrado = false;

        try {
            cn = ConexionSingleton.getConnection();

            // si existe un pedido pendiente
            query = "SELECT estado FROM pedido WHERE idpedido = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, pago.getPedido().getIdPedido());
            rs = st.executeQuery();

            if (!rs.next()) {
                System.out.println("El pedido no existe");
                cn.rollback();
                return false;
            }

            String estadoPedido = rs.getString("estado");

            if (!estadoPedido.equalsIgnoreCase("PENDIENTE")) {
                System.out.println("El pedido no está PENDIENTE");
                cn.rollback();
                return false;
            }

            // del pago
            query = "SELECT idpago FROM pago WHERE idpedido = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, pago.getPedido().getIdPedido());
            rs = st.executeQuery();

            if (rs.next()) {
                System.out.println("Este pedido ya tiene un pago registrado");
                cn.rollback();
                return false;
            }

            // inserta el pago
            query = "INSERT INTO pago (idpedido, metodo_pago, monto, comprobante) "
                    + "VALUES (?, ?, ?, ?)";

            st = cn.prepareStatement(query);
            st.setInt(1, pago.getPedido().getIdPedido());
            st.setString(2, pago.getMetodopago().name());
            st.setDouble(3, pago.getMonto());
            st.setString(4, pago.getComprobante());

            int filas = st.executeUpdate();

            if (filas > 0) {

                registrado = true;
            } else {

                registrado = false;
            }

        } catch (Exception e) {
            System.out.println("Error al agregar:" + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
                registrado = false;
            }
            System.out.println("No se pudo registrar el pago");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }

        return registrado;
    }

    @Override
    public Pago buscarPorPedido(int idPedido) {
        Pago pago = null;
        String query = null;

        try {
            cn = ConexionSingleton.getConnection();

            query = "SELECT idpago, idpedido, metodo_pago, monto, comprobante "
                    + "FROM pago WHERE idpedido = ?";

            st = cn.prepareStatement(query);
            st.setInt(1, idPedido);
            rs = st.executeQuery();

            if (rs.next()) {
                pago = new Pago();

                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idpedido"));

                pago.setIdPago(rs.getInt("idpago"));
                pago.setPedido(pedido);
                pago.setMetodopago(MetodoPago.valueOf(rs.getString("metodo_pago").toUpperCase()));
                pago.setMonto(rs.getDouble("monto"));
                pago.setComprobante(rs.getString("comprobante"));
            }

        } catch (Exception e) {
            System.out.println("Error al agregar:" + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {

            }
            System.out.println("No se pudo encontro el pago");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }
        return pago;
    }

    @Override
    public List<Pago> listarPendientes() {
        List<Pago> lista = new ArrayList<>();
        String query = null;

        try {
            cn = ConexionSingleton.getConnection();

            query = "SELECT p.idPedido, p.total, p.estado, "
                    + "pa.idPago, pa.metodo_pago, pa.monto, pa.comprobante "
                    + "FROM pedido p "
                    + "INNER JOIN pago pa ON p.idPedido = pa.idPedido "
                    + "WHERE p.estado = 'PENDIENTE'";

            st = cn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                Pago pago = new Pago();

                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setTotal(rs.getDouble("total"));

                pago.setIdPago(rs.getInt("idPago"));
                pago.setPedido(pedido);
                pago.setMetodopago(MetodoPago.valueOf(rs.getString("metodo_pago").toUpperCase()));
                pago.setMonto(rs.getDouble("monto"));
                pago.setComprobante(rs.getString("comprobante"));
                lista.add(pago);
            }

        } catch (Exception e) {
            System.out.println("error al listar pendientes" + e.getMessage());

        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }

            }
        }
        return lista;

    }
}
