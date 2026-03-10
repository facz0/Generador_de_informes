package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.DAO.*;
import com.onpe.genereador_informes.database.Conexion;
import com.onpe.genereador_informes.model.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {
    @Override
    public void start(Stage stage){
        DashboardView dashboard = new DashboardView();
        dashboard.mostrar(stage);
    }

    public static void main(String[] args) {
        Conexion.obtenerConexion();
        //Instanciar Dao
        AreaDAO areaDAO = new AreaDAO();
        FirmaDAO firmaDAO = new FirmaDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        CargoDAO cargoDAO = new CargoDAO();
        ContratoDao contratoDao = new ContratoDao();
        // Obtenemos la lista de la base de datos
        //List<Area> misAreas = areaDAO.obtenerTodas();
        //List<Firma> misFirmas = firmaDAO.obtenerTodas();
        //List<Empleado> misEmpleados = empleadoDAO.obtenerTodas();
        //List<Cargo> misCargos = cargoDAO.obtenerCargosConActividades();
        //List<Contrato> contratos = contratoDao.obtenerContratos();
        //System.out.println("--------------- CONTRATOS ----------------");
        //for (Contrato c : contratos){
        //    System.out.println(c.toString());
        //}
        //System.out.println("--- LISTA DE ÁREAS DESDE SQLITE ---");
        //for (Area a : misAreas){
        //    System.out.printf(a.toString() + "\n");
        //}
        //System.out.println("\n--- LISTA DE FIRMAS DESDE SQLITE ---");
        //for (Firma f : misFirmas){
        //    System.out.println(f.toString());
        //}
        //System.out.println("\n--- LISTA DE EMPLEADOS DESDE SQLITE ---");
        //for (Empleado e : misEmpleados){
        //    System.out.println(e.toString());
        //}
        //System.out.println("\n--- LISTA DE CARGOS Y SUS ACTIVIDADES ---");
        //for (Cargo c : misCargos){
        //    System.out.println("CARGO: " + c.getNombreCargo());
        //    int vinieta = 1;
        //    for (Actividad act : c.getListaActividades()){
        //        System.out.println("    " + vinieta + ". " + act.getDescripcion());
        //        vinieta++;
        //    }
        //    System.out.println("----------------------------------------------------------");
        //}
        launch(args);
    }
}