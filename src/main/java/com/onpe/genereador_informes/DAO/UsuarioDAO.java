package com.onpe.genereador_informes.DAO;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.onpe.genereador_informes.database.Conexion;

import java.sql.*;

public class UsuarioDAO {

    private static final java.util.Map<Integer, String> PERFILES = java.util.Map.of(
        1, "ADMIN",
        2, "GGE",
        3, "SGPE"
    );

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verificarPassword(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

    public String[] autenticar(String usuario, String password) {
        String sql = "SELECT nombres, apellidos, perfil, password FROM tb_usuario WHERE usuario = ? AND estado = 1";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashGuardado = rs.getString("password");
                if (verificarPassword(password, hashGuardado)) {
                    String nombres   = rs.getString("nombres");
                    String apellidos = rs.getString("apellidos");
                    int perfil       = rs.getInt("perfil");
                    // idGerencia: ADMIN=0 (sin filtro), GGE=1, SGPE=2
                    int idGerencia = perfil == 2 ? 1 : perfil == 3 ? 2 : 0;
                    rs.close(); ps.close();
                    return new String[]{
                        nombres + " " + apellidos,
                        PERFILES.getOrDefault(perfil, "USUARIO"),
                        String.valueOf(idGerencia)
                    };
                }
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            System.err.println("Error al autenticar: " + e.getMessage());
        }
        return null;
    }

    public boolean crear(String nombres, String apellidos, String usuario, String password, int perfil) {
        String sql = "INSERT INTO tb_usuario (nombres, apellidos, usuario, password, perfil) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombres);
            ps.setString(2, apellidos);
            ps.setString(3, usuario);
            ps.setString(4, hashPassword(password));
            ps.setInt(5, perfil);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
        }
        return false;
    }

    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM tb_usuario WHERE usuario = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            boolean existe = rs.next() && rs.getInt(1) > 0;
            rs.close(); ps.close();
            return existe;
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        }
        return false;
    }
}
