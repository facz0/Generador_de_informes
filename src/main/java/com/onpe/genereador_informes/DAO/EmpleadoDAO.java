package com.onpe.genereador_informes.DAO;

import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class EmpleadoDAO {

    public List<Empleado> obtenerTodas(){
        List<Empleado> listaEmpleados = new ArrayList<>();
        String sql = "SELECT id_empleado, dni, nombre, apellido FROM Empleados";

        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id_empleado");
                String dni = rs.getString("dni");
                String nombres = rs.getString("nombre");
                String apellidos = rs.getString("apellido");

                Empleado empleado = new Empleado(id,dni,nombres,apellidos);
                listaEmpleados.add(empleado);
            }
        } catch (SQLException e){
            System.err.println("Error al consultar las áreas: " + e.getMessage());
        }
        return listaEmpleados;
    }

}
