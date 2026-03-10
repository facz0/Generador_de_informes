package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Firma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FirmaDAO {

    public List<Firma> obtenerTodas(){
        List<Firma> listaFirmas = new ArrayList<>();
        String sql = "SELECT id_firma, Nombre_encargado, descripcion FROM Firma";

        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id_firma");
                String nombre = rs.getString("Nombre_encargado");
                String descripcion = rs.getString("descripcion");

                Firma firma = new Firma(id, nombre, descripcion);
                listaFirmas.add(firma);
            }
        } catch (SQLException e){
            System.err.println("Error al consultar las firmas: " + e.getMessage());
        }
        return listaFirmas;
    }

}
