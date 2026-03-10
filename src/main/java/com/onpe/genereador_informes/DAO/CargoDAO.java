package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Actividad;
import com.onpe.genereador_informes.model.Cargo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    public List<Cargo> obtenerCargosConActividades(){
        List<Cargo> listaCargos = new ArrayList<>();

        // 1. Consulta principal: Traer los cargos
        String sqlCargos = "SELECT id_cargo, Nombre_cargo FROM Cargos";

        // 2. Consulta secundaria: Traer actividades filtrando por el ID del cargo (el '?' es un parámetro dinámico)
        String sqlActividades = "SELECT id_actividad, id_cargo, descripcion FROM Actividades WHERE id_cargo = ?";

        try{
            Connection conn = Conexion.obtenerConexion();
            //Ejecutamos la primera consulta
            PreparedStatement pstmtCargos = conn.prepareStatement(sqlCargos);
            ResultSet rsCargos = pstmtCargos.executeQuery();
            while (rsCargos.next()){
                int idCargo = rsCargos.getInt("id_cargo");
                String nombreCargo = rsCargos.getString("Nombre_cargo");
                Cargo cargo = new Cargo(idCargo, nombreCargo);

                //Preparamos la segunda consulta para buscar actividades de ESTE cargo en especiífico
                PreparedStatement pstmtActividades = conn.prepareStatement(sqlActividades);
                pstmtActividades.setInt(1, idCargo);
                ResultSet rsActividades = pstmtActividades.executeQuery();
                while (rsActividades.next()){
                    int idActividad = rsActividades.getInt("id_actividad");
                    String descripcion = rsActividades.getString("descripcion");
                    Actividad actividad = new Actividad(idActividad, idCargo, descripcion);
                    cargo.getListaActividades().add(actividad);
                }
                rsActividades.close();
                pstmtActividades.close();

                listaCargos.add(cargo);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar los cargos y actividades: " + e.getMessage());
        }
        return listaCargos;
    }
}
