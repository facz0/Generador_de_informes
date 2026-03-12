package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Actividad;
import com.onpe.genereador_informes.model.Area;
import com.onpe.genereador_informes.model.Cargo;
import com.onpe.genereador_informes.model.Contrato;
import com.onpe.genereador_informes.model.Empleado;
import com.onpe.genereador_informes.model.Odpe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratoDao {
    
    // Método auxiliar para cargar las actividades de un cargo
    private List<Actividad> obtenerActividadesPorCargo(int idCargo) {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT id_actividad, descripcion FROM Actividades WHERE id_cargo = ?";
        
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCargo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int idActividad = rs.getInt("id_actividad");
                String descripcion = rs.getString("descripcion");
                Actividad actividad = new Actividad(idActividad, idCargo, descripcion);
                actividades.add(actividad);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener actividades del cargo " + idCargo + ": " + e.getMessage());
        }
        
        return actividades;
    }
    
    public List<Contrato> obtenerContratos(){
        List<Contrato> listaContratos = new ArrayList<>();
        String sql = "SELECT \n" +
                "\tc.numero_contrato, id_contrato, c.fecha_inicio, c.fecha_fin, \n" +
                "\te.id_empleado, e.dni, e.nombre, e.apellido, e.id_odpe,\n" +
                "\tcg.id_cargo, cg.Nombre_cargo, a.id_area, a.Nombre_area, c.estado,\n" +
                "\to.id_odpe, o.nombre_odpe\n" +
                "FROM Contrato c \n" +
                "INNER JOIN Empleados e ON c.id_empleado = e.id_empleado \n" +
                "INNER JOIN Cargos cg ON c.id_cargo = cg.id_cargo \n" +
                "INNER JOIN Area a ON c.id_area = a.id_area \n" +
                "LEFT JOIN ODPE o ON e.id_odpe = o.id_odpe";

        try{
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Empleado empleado = new Empleado();
                empleado.setId_empleado(rs.getInt("id_empleado"));
                empleado.setDni(rs.getString("dni"));
                empleado.setNombres(rs.getString("nombre"));
                empleado.setApellidos(rs.getString("apellido"));
                
                // Cargar ODPE si existe
                int idOdpe = rs.getInt("id_odpe");
                if (idOdpe > 0) {
                    Odpe odpe = new Odpe();
                    odpe.setIdOdpe(idOdpe);
                    odpe.setNombreOdpe(rs.getString("nombre_odpe"));
                    empleado.setOdpe(odpe);
                }

                Cargo cargo = new Cargo();
                cargo.setIdCargo(rs.getInt("id_cargo"));
                cargo.setNombreCargo(rs.getString("Nombre_cargo"));
                
                // CARGAR LAS ACTIVIDADES DEL CARGO
                List<Actividad> actividades = obtenerActividadesPorCargo(cargo.getIdCargo());
                cargo.setListaActividades(actividades);

                Area area = new Area();
                area.setIdArea(rs.getInt("id_area"));
                area.setNombreArea(rs.getString("Nombre_area"));

                LocalDate fechaInicio = LocalDate.parse(rs.getString("fecha_inicio"));
                LocalDate fechaFin = LocalDate.parse(rs.getString("fecha_fin"));

                Contrato contrato = new Contrato();
                contrato.setIdContrato(rs.getInt("id_contrato"));
                contrato.setNumeroContrato(rs.getString("numero_contrato"));
                contrato.setFechaInicio(fechaInicio);
                contrato.setFechaFin(fechaFin);
                contrato.setEstado(rs.getString("estado"));

                contrato.setEmpleado(empleado);
                contrato.setCargo(cargo);
                contrato.setArea(area);

                listaContratos.add(contrato);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e){
            System.err.println("Error al obtener los contratos: " + e.getMessage());
        }
        return listaContratos;
    }


}
