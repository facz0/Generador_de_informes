package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoAreaDAO {

    public List<String[]> obtenerTodos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT ca.id_cargo_area, cg.nombre_cargo, a.nombre_area " +
                "FROM tb_cargo_area ca " +
                "INNER JOIN tb_cargo cg ON ca.id_cargo = cg.id_cargo " +
                "INNER JOIN tb_area a ON ca.id_area = a.id_area " +
                "ORDER BY cg.nombre_cargo";
        try {
            Connection conn = Conexion.obtenerConexion();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_cargo_area")),
                    rs.getString("nombre_cargo"),
                    rs.getString("nombre_area")
                });
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener cargo_area: " + e.getMessage());
        }
        return lista;
    }

    public boolean existeAsociacion(int idCargo, int idArea) {
        String sql = "SELECT COUNT(*) FROM tb_cargo_area WHERE id_cargo = ? AND id_area = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargo);
            ps.setInt(2, idArea);
            ResultSet rs = ps.executeQuery();
            boolean existe = rs.next() && rs.getInt(1) > 0;
            rs.close();
            ps.close();
            return existe;
        } catch (SQLException e) {
            System.err.println("Error al verificar asociación: " + e.getMessage());
        }
        return false;
    }

    public boolean crear(int idCargo, int idArea) {
        String sql = "INSERT INTO tb_cargo_area (id_cargo, id_area) VALUES (?, ?)";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargo);
            ps.setInt(2, idArea);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear asociación: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(int idCargoArea, int idCargo, int idArea) {
        String sql = "UPDATE tb_cargo_area SET id_cargo = ?, id_area = ? WHERE id_cargo_area = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargo);
            ps.setInt(2, idArea);
            ps.setInt(3, idCargoArea);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar asociación: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int idCargoArea) {
        String sql = "DELETE FROM tb_cargo_area WHERE id_cargo_area = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargoArea);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar asociación: " + e.getMessage());
        }
        return false;
    }
}
