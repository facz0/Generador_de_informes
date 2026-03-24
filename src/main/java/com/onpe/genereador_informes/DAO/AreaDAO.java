package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<Area> obtenerTodas() {
        List<Area> lista = new ArrayList<>();
        String sql = "SELECT id_area, nombre_area FROM tb_area ORDER BY nombre_area";
        try {
            Connection conn = Conexion.obtenerConexion();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Area(rs.getInt("id_area"), rs.getString("nombre_area")));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener áreas: " + e.getMessage());
        }
        return lista;
    }

    public boolean existe(String nombre) {
        String sql = "SELECT COUNT(*) FROM tb_area WHERE LOWER(nombre_area) = LOWER(?)";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            boolean existe = rs.next() && rs.getInt(1) > 0;
            rs.close();
            ps.close();
            return existe;
        } catch (SQLException e) {
            System.err.println("Error al verificar área: " + e.getMessage());
        }
        return false;
    }

    public boolean crear(String nombre) {
        String sql = "INSERT INTO tb_area (nombre_area) VALUES (?)";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre.toUpperCase());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear área: " + e.getMessage());
        }
        return false;
    }
}
