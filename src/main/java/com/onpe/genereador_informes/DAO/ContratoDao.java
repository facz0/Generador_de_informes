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

    private List<Actividad> obtenerActividadesPorCargoArea(int idCargoArea) {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT id_actividad, descripcion FROM tb_actividades WHERE id_cargo_area = ?";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCargoArea);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                actividades.add(new Actividad(rs.getInt("id_actividad"), idCargoArea, rs.getString("descripcion")));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener actividades: " + e.getMessage());
        }
        return actividades;
    }

    public List<Contrato> obtenerContratos() {
        List<Contrato> listaContratos = new ArrayList<>();
        String sql = "SELECT " +
                "c.id_contrato, c.numero_contrato, c.fecha_inicio, c.fecha_fin, " +
                "p.id_personal, p.dni, p.nombre, p.apellido, p.estado, p.id_odpe, " +
                "cg.id_cargo, cg.nombre_cargo, " +
                "a.id_area, a.nombre_area, " +
                "ca.id_cargo_area, " +
                "o.nombre_odpe, " +
                "g.id_gerencia, g.nombre_gerencia " +
                "FROM tb_contratos c " +
                "INNER JOIN tb_personal p ON c.id_contrato = p.id_contrato " +
                "INNER JOIN tb_cargo_area ca ON p.id_cargo_area = ca.id_cargo_area " +
                "INNER JOIN tb_cargo cg ON ca.id_cargo = cg.id_cargo " +
                "INNER JOIN tb_area a ON ca.id_area = a.id_area " +
                "INNER JOIN tb_gerencia g ON p.id_gerencia = g.id_gerencia " +
                "LEFT JOIN tb_odpe o ON p.id_odpe = o.id_odpe";

        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setId_empleado(rs.getInt("id_personal"));
                empleado.setDni(rs.getString("dni"));
                empleado.setNombres(rs.getString("nombre"));
                empleado.setApellidos(rs.getString("apellido"));

                int idOdpe = rs.getInt("id_odpe");
                if (idOdpe > 0) {
                    Odpe odpe = new Odpe();
                    odpe.setIdOdpe(idOdpe);
                    odpe.setNombreOdpe(rs.getString("nombre_odpe"));
                    empleado.setOdpe(odpe);
                }

                int idCargoArea = rs.getInt("id_cargo_area");
                Cargo cargo = new Cargo();
                cargo.setIdCargo(rs.getInt("id_cargo"));
                cargo.setNombreCargo(rs.getString("nombre_cargo"));
                cargo.setListaActividades(obtenerActividadesPorCargoArea(idCargoArea));

                Area area = new Area();
                area.setIdArea(rs.getInt("id_gerencia"));
                area.setNombreArea(rs.getString("nombre_gerencia"));

                LocalDate fechaInicio = LocalDate.parse(rs.getString("fecha_inicio"));
                LocalDate fechaFin = rs.getString("fecha_fin") != null ? LocalDate.parse(rs.getString("fecha_fin")) : LocalDate.now();

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
        } catch (SQLException e) {
            System.err.println("Error al obtener los contratos: " + e.getMessage());
            e.printStackTrace();
        }
        return listaContratos;
    }
}
