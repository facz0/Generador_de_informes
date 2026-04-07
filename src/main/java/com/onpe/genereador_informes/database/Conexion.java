package com.onpe.genereador_informes.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static Connection conexion = null;

    private static String obtenerRutaBD() {
        // 1. Buscar junto al JAR/ejecutable instalado
        try {
            String rutaJar = Conexion.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            // Limpiar prefijo "/" en Windows (ej: /D:/...)
            if (rutaJar.startsWith("/") && rutaJar.contains(":")) {
                rutaJar = rutaJar.substring(1);
            }
            File dirJar = new File(rutaJar).getParentFile();
            File bdJunto = new File(dirJar, "Informesv2_BD_original.db");
            //File bdJunto = new File(dirJar, "Informesv2_BD.db");
            if (bdJunto.exists()) {
                System.out.println("BD encontrada junto al JAR: " + bdJunto.getAbsolutePath());
                return "jdbc:sqlite:" + bdJunto.getAbsolutePath();
            }
            // Subir un nivel (estructura jpackage: app/app.jar -> buscar en app/)
            File bdPadre = new File(dirJar.getParentFile(), "Informesv2_BD_original.db");
            if (bdPadre.exists()) {
                System.out.println("BD encontrada en carpeta padre: " + bdPadre.getAbsolutePath());
                return "jdbc:sqlite:" + bdPadre.getAbsolutePath();
            }
        } catch (Exception e) {
            System.err.println("Error buscando BD: " + e.getMessage());
        }
        // 2. Fallback: ruta relativa (funciona en desarrollo con Maven)
        System.out.println("Usando ruta relativa de BD (modo desarrollo)");
        return "jdbc:sqlite:Informesv2_BD_original.db";
    }

    public static Connection obtenerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(obtenerRutaBD());
                System.out.println("Conexion a BD establecida.");
            }
        } catch (SQLException e) {
            System.err.println("Error crítico al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}
