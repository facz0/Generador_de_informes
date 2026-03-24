package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Cargo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    public List<Cargo> obtenerTodos() {
        List<Cargo> lista = new ArrayList<>();
        String sql = "SELECT id_cargo, nombre_cargo FROM tb_cargo ORDER BY nombre_cargo";
        try {
            Connection conn = Conexion.obtenerConexion();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Cargo(rs.getInt("id_cargo"), rs.getString("nombre_cargo")));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener cargos: " + e.getMessage());
        }
        return lista;
    }

    public boolean existe(String nombre) {
        String sql = "SELECT COUNT(*) FROM tb_cargo WHERE LOWER(nombre_cargo) = LOWER(?)";
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
            System.err.println("Error al verificar cargo: " + e.getMessage());
        }
        return false;
    }

    public boolean crear(String nombre) {
        String sql = "INSERT INTO tb_cargo (nombre_cargo) VALUES (?)";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre.toUpperCase());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear cargo: " + e.getMessage());
        }
        return false;
    }
}
