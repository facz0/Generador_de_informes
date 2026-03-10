package com.onpe.genereador_informes.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Atributo PRIVATE: Nadie fuera de esta clase puede tocar la conexión directamente (Encapsulamiento)
    private static Connection conexion = null;
    private static final String URL = "jdbc:sqlite:Informes_BD.db";

    // Metodo PUBLIC: Es la única "puerta" para pedir la conexión
    public static Connection obtenerConexion() {
        try {
            // Patrón Singleton: Si la conexión no existe o está cerrada, la creamos. Si ya existe, la reutilizamos.
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL);
                System.out.println("¡Éxito! Java se ha conectado a la base de datos SQLite.");
            }
        } catch (SQLException e) {
            System.err.println("Error crítico al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;

    }
}
