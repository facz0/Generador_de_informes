package com.onpe.genereador_informes.controlador;
import com.onpe.genereador_informes.DAO.PersonalDAO;
import com.onpe.genereador_informes.database.Conexion;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

public class EmpleadosController {
    
    private PersonalDAO personalDAO;

    public EmpleadosController() {
        this.personalDAO = new PersonalDAO();
    }

    public List<String[]> obtenerTodosLosEmpleados() {
        return personalDAO.obtenerTodos();
    }

    public void cargarCargos(ObservableList<String[]> lista) {
        ejecutarConsultaCombo("SELECT id_cargo, nombre_cargo FROM tb_cargo ORDER BY nombre_cargo", lista);
    }

    public void cargarGerencias(ObservableList<String[]> lista) {
        ejecutarConsultaCombo("SELECT id_gerencia, nombre_gerencia FROM tb_gerencia ORDER BY nombre_gerencia", lista);
    }

    public void cargarOdpes(ObservableList<String[]> lista) {
        ejecutarConsultaCombo("SELECT id_odpe, nombre_odpe FROM tb_odpe ORDER BY nombre_odpe", lista);
    }

    public void cargarAreasPorCargo(int idCargo, ObservableList<String[]> lista) {
        lista.clear();
        String sql = "SELECT ca.id_cargo_area, a.nombre_area FROM tb_cargo_area ca " +
                "INNER JOIN tb_area a ON ca.id_area = a.id_area WHERE ca.id_cargo = ? ORDER BY a.nombre_area";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCargo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cargando áreas por cargo: " + e.getMessage());
        }
    }

    private void ejecutarConsultaCombo(String sql, ObservableList<String[]> lista) {
        lista.clear();
        try (Connection conn = Conexion.obtenerConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)});
            }
        } catch (SQLException e) {
            System.err.println("Error cargando combo: " + e.getMessage());
        }
    }

    // --- 3. LÓGICA DE NEGOCIO (GUARDAR / ACTUALIZAR) ---
    // Devuelve un String vacío "" si fue un éxito, o un mensaje de error si falló.
    public String procesarGuardado(String[] empleadoActual, String numContrato, String fechaInicio, String fechaFin,
                                   String dni, String nombre, String apellido, int idCargoArea,
                                   int idGerencia, Integer idOdpe, String estado) {

        boolean esEdicion = (empleadoActual != null);
        int idPersonal = esEdicion ? Integer.parseInt(empleadoActual[0]) : -1;


        if (personalDAO.existeDni(dni, idPersonal)) {
            return "⚠ El DNI ya está registrado en el sistema";
        }

        boolean exito;
        if (esEdicion) {
            exito = personalDAO.actualizar(idPersonal, numContrato, fechaInicio, fechaFin, dni, nombre, apellido,
                    idCargoArea, idGerencia, idOdpe, estado);
        } else {
            exito = personalDAO.crear(numContrato, fechaInicio, fechaFin, dni, nombre, apellido, idCargoArea,
                    idGerencia, idOdpe, estado);
        }

        return exito ? "" : "Error interno al guardar en la base de datos";
    }

    // --- 4. ELIMINACIÓN ---
    public boolean eliminarEmpleado(int idPersonal) {
        return personalDAO.eliminar(idPersonal);
    }

}
