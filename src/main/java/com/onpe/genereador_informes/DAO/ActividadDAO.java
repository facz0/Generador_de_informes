package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Actividad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    public List<Actividad> obtenerPorCargoArea(int idCargoArea) {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT id_actividad, descripcion FROM tb_actividades WHERE id_cargo_area = ? ORDER BY id_actividad";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargoArea);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Actividad(rs.getInt("id_actividad"), idCargoArea, rs.getString("descripcion")));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener actividades: " + e.getMessage());
        }
        return lista;
    }

    public boolean crear(int idCargoArea, String descripcion) {
        String sql = "INSERT INTO tb_actividades (id_cargo_area, descripcion) VALUES (?, ?)";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargoArea);
            ps.setString(2, descripcion);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear actividad: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(int idActividad, String descripcion) {
        String sql = "UPDATE tb_actividades SET descripcion = ? WHERE id_actividad = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, descripcion);
            ps.setInt(2, idActividad);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar actividad: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int idActividad) {
        String sql = "DELETE FROM tb_actividades WHERE id_actividad = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idActividad);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar actividad: " + e.getMessage());
        }
        return false;
    }
}
