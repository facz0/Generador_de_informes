package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalDAO {

    // Obtener todos los empleados con sus datos relacionados
    public List<String[]> obtenerTodos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT p.id_personal, p.dni, p.nombre, p.apellido, p.estado, " +
                "cg.nombre_cargo, a.nombre_area, g.nombre_gerencia, " +
                "o.nombre_odpe, c.numero_contrato, c.fecha_inicio, c.fecha_fin " +
                "FROM tb_personal p " +
                "INNER JOIN tb_cargo_area ca ON p.id_cargo_area = ca.id_cargo_area " +
                "INNER JOIN tb_cargo cg ON ca.id_cargo = cg.id_cargo " +
                "INNER JOIN tb_area a ON ca.id_area = a.id_area " +
                "INNER JOIN tb_gerencia g ON p.id_gerencia = g.id_gerencia " +
                "LEFT JOIN tb_odpe o ON p.id_odpe = o.id_odpe " +
                "LEFT JOIN (" +
                "    SELECT id_personal, MAX(id_contrato) as ultimo_contrato " +
                "    FROM tb_contratos GROUP BY id_personal" +
                ")" +
                " ultimos ON p.id_personal = ultimos.id_personal " +
                "LEFT JOIN tb_contratos c ON ultimos.ultimo_contrato = c.id_contrato " +
                "ORDER BY p.apellido";
        try {
            Connection conn = Conexion.obtenerConexion();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_personal")),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("estado"),
                    rs.getString("nombre_cargo"),
                    rs.getString("nombre_area"),
                    rs.getString("nombre_gerencia"),
                    rs.getString("nombre_odpe") != null ? rs.getString("nombre_odpe") : "",
                    rs.getString("numero_contrato") != null ? rs.getString("numero_contrato") : "SIN CONTRATO",
                    rs.getString("fecha_inicio") != null ? rs.getString("fecha_inicio") : "",
                    rs.getString("fecha_fin") != null ? rs.getString("fecha_fin") : ""
                });
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener personal: " + e.getMessage());
        }
        return lista;
    }

    // Crear contrato y empleado en una sola transacción
    public boolean crear(String numeroContrato, String fechaInicio, String fechaFin,
                         String dni, String nombre, String apellido,
                         int idCargoArea, int idGerencia, Integer idOdpe, String estado) {
        Connection conn = Conexion.obtenerConexion();
        try {
            conn.setAutoCommit(false);

            // 1. Insertar personal
            PreparedStatement psPersonal = conn.prepareStatement(
                    "INSERT INTO tb_personal (dni, nombre, apellido, id_cargo_area, id_gerencia, id_odpe, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psPersonal.setString(1, dni);
            psPersonal.setString(2, nombre);
            psPersonal.setString(3, apellido);
            psPersonal.setInt(4, idCargoArea);
            psPersonal.setInt(5, idGerencia);
            if (idOdpe != null) psPersonal.setInt(6, idOdpe); else psPersonal.setNull(6, Types.INTEGER);
            psPersonal.setString(7, estado);
            psPersonal.executeUpdate();

            ResultSet keys = psPersonal.getGeneratedKeys();
            int idPersonal = keys.next() ? keys.getInt(1) : -1;
            psPersonal.close();

            if (idPersonal == -1){ conn.rollback(); return false; }

            //2. Insertar contrato
            PreparedStatement psContrato = conn.prepareStatement(
                "INSERT INTO tb_contratos (numero_contrato, fecha_inicio, fecha_fin, id_perosnal) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            psContrato.setString(1, numeroContrato);
            psContrato.setString(2, fechaInicio);
            psContrato.setString(3, fechaFin.isEmpty() ? null : fechaFin);
            psContrato.setInt(4, idPersonal);
            psContrato.executeUpdate();
            psContrato.close();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al crear personal: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    // Actualizar datos del empleado y su contrato
    public boolean actualizar(int idPersonal, String numeroContrato, String fechaInicio, String fechaFin,
                              String dni, String nombre, String apellido,
                              int idCargoArea, int idGerencia, Integer idOdpe, String estado) {
        Connection conn = Conexion.obtenerConexion();
        try {
            conn.setAutoCommit(false);

            // Obtener id_contrato del personal
            PreparedStatement psGet = conn.prepareStatement(
                    "SELECT id_contrato FROM tb_contratos WHERE id_personal = ? ORDER BY id_contrato DESC LIMIT 1"
            );
            psGet.setInt(1, idPersonal);
            ResultSet rs = psGet.executeQuery();
            int idContrato = rs.next() ? rs.getInt("id_contrato") : -1;
            psGet.close();

            if (idContrato == -1) { conn.rollback(); return false; }

            // Actualizar contrato
            PreparedStatement psContrato = conn.prepareStatement(
                "UPDATE tb_contratos SET numero_contrato = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_contrato = ?");
            psContrato.setString(1, numeroContrato);
            psContrato.setString(2, fechaInicio);
            psContrato.setString(3, fechaFin.isEmpty() ? null : fechaFin);
            psContrato.setInt(4, idContrato);
            psContrato.executeUpdate();
            psContrato.close();

            // Actualizar personal
            PreparedStatement psPersonal = conn.prepareStatement(
                "UPDATE tb_personal SET dni=?, nombre=?, apellido=?, id_cargo_area=?, id_gerencia=?, id_odpe=?, estado=? WHERE id_personal=?");
            psPersonal.setString(1, dni);
            psPersonal.setString(2, nombre);
            psPersonal.setString(3, apellido);
            psPersonal.setInt(4, idCargoArea);
            psPersonal.setInt(5, idGerencia);
            if (idOdpe != null) psPersonal.setInt(6, idOdpe); else psPersonal.setNull(6, Types.INTEGER);
            psPersonal.setString(7, estado);
            psPersonal.setInt(8, idPersonal);
            psPersonal.executeUpdate();
            psPersonal.close();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al actualizar personal: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean eliminar(int idPersonal) {
        Connection conn = Conexion.obtenerConexion();
        try {
            conn.setAutoCommit(false);

            PreparedStatement psContrato = conn.prepareStatement("DELETE FROM tb_contratos WHERE id_personal = ?");
            psContrato.setInt(1, idPersonal);
            psContrato.executeUpdate();

            psContrato.close();

            PreparedStatement psPersonal = conn.prepareStatement("DELETE FROM tb_personal WHERE id_personal = ?");
            psPersonal.setInt(1, idPersonal);
            psPersonal.executeUpdate();
            psPersonal.close();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al eliminar personal: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean existeDni(String dni, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM tb_personal WHERE dni = ? AND id_personal != ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dni);
            ps.setInt(2, idExcluir);
            ResultSet rs = ps.executeQuery();
            boolean existe = rs.next() && rs.getInt(1) > 0;
            rs.close(); ps.close();
            return existe;
        } catch (SQLException e) {
            System.err.println("Error al verificar DNI: " + e.getMessage());
        }
        return false;
    }
}
