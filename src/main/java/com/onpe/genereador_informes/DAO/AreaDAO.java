package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Area;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<Area> obtenerTodas(){
        List<Area> listaAreas = new ArrayList<>();
        String sql = "SELECT id_area, Nombre_area FROM Area";

        try {
            // 3. Pedimos la conexión a nuestra clase experta
            Connection conn = Conexion.obtenerConexion();
            // 4. PreparedStatement protege contra inyecciones SQL y prepara la consulta
            PreparedStatement pstmt = conn.prepareStatement(sql);
            // 5. ResultSet es la "tabla virtual" que nos devuelve la base de datos
            ResultSet rs = pstmt.executeQuery();

            // 6. Recorremos fila por fila mientras haya datos (rs.next())
            while(rs.next()){
                // Extraemos los datos de la fila actual
                int id = rs.getInt("id_area");
                String nombre = rs.getString("Nombre_area");

                // ¡POO en acción! Creamos el objeto Area y lo añadimos a la lista
                Area area = new Area(id, nombre);
                listaAreas.add(area);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar las áreas: " + e.getMessage());
        }
        return listaAreas;
    }

}
