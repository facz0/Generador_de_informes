package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Actividad;
import com.onpe.genereador_informes.model.Area;
import com.onpe.genereador_informes.model.Cargo;
import com.onpe.genereador_informes.model.CargoArea;
import com.onpe.genereador_informes.model.Contrato;
import com.onpe.genereador_informes.model.Gerencia;
import com.onpe.genereador_informes.model.Odpe;
import com.onpe.genereador_informes.model.Personal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratoDao {

    //Metodo para cargar las actividades
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
                "INNER JOIN ( " +
                "    SELECT id_personal, MAX(id_contrato) as ultimo_contrato " +
                "    FROM tb_contratos " +
                "    GROUP BY id_personal " +
                ") ultimos ON c.id_contrato = ultimos.ultimo_contrato " +
                "INNER JOIN tb_personal p ON c.id_personal = p.id_personal " +
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
                Personal personal = new Personal();
                personal.setIdPersonal(rs.getInt("id_personal"));
                personal.setDni(rs.getString("dni"));
                personal.setNombre(rs.getString("nombre"));
                personal.setApellido(rs.getString("apellido"));
                personal.setEstado(rs.getString("estado"));

                int idOdpe = rs.getInt("id_odpe");
                if (idOdpe > 0) {
                    Odpe odpe = new Odpe();
                    odpe.setIdOdpe(idOdpe);
                    odpe.setNombreOdpe(rs.getString("nombre_odpe"));
                    personal.setOdpe(odpe);
                }

                Gerencia gerencia = new Gerencia();
                gerencia.setIdGerencia(rs.getInt("id_gerencia"));
                gerencia.setNombreGerencia(rs.getString("nombre_gerencia"));
                personal.setGerencia(gerencia);

                Cargo cargo = new Cargo();
                cargo.setIdCargo(rs.getInt("id_cargo"));
                cargo.setNombreCargo(rs.getString("nombre_cargo"));
                
                int idCargoArea = rs.getInt("id_cargo_area");
                cargo.setListaActividades(obtenerActividadesPorCargoArea(idCargoArea));

                Area area = new Area();
                area.setIdArea(rs.getInt("id_area"));
                area.setNombreArea(rs.getString("nombre_area"));

                CargoArea cargoArea = new CargoArea();
                cargoArea.setIdCargoArea(idCargoArea);
                cargoArea.setCargo(cargo);
                cargoArea.setArea(area);

                personal.setCargoArea(cargoArea);

                LocalDate fechaInicio = LocalDate.parse(rs.getString("fecha_inicio"));
                LocalDate fechaFin = rs.getString("fecha_fin") != null ? LocalDate.parse(rs.getString("fecha_fin")) : LocalDate.now();

                Contrato contrato = new Contrato();
                contrato.setIdContrato(rs.getInt("id_contrato"));
                contrato.setNumeroContrato(rs.getString("numero_contrato"));
                contrato.setFechaInicio(fechaInicio);
                contrato.setFechaFin(fechaFin);
                contrato.setPersonal(personal);

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
