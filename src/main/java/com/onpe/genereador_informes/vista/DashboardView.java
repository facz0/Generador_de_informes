package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.DashboardController;
import com.onpe.genereador_informes.model.Contrato;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class DashboardView {
    private DashboardController controlador;

     public DashboardView(){
         this.controlador = new DashboardController();
     }

     public  void mostrar(Stage stage){
         BorderPane root = new BorderPane();

         //Menu
         VBox menuLateral = new VBox(20);
         menuLateral.setPrefWidth(280);
         menuLateral.setPadding(new Insets(20));
         menuLateral.setStyle("-fx-background-color: #2c3e50;");

         Label lblPerfil = new Label("Bienvenid@ Admin");
         lblPerfil.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

         //botones del menu
         String estilobtnMenu = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-alignment: BASELINE_LEFT;";

         Button btnCrearColab = new Button("Crear Colaborador");
         btnCrearColab.setStyle(estilobtnMenu);
         btnCrearColab.setMaxWidth(Double.MAX_VALUE);

         Button btnEditarColab = new Button("Editar Colaborador");
         btnEditarColab.setStyle(estilobtnMenu);
         btnEditarColab.setMaxWidth(Double.MAX_VALUE);

         Button btnCargosActividades = new Button("Configurar Cargos y Actividades");
         btnCargosActividades.setStyle(estilobtnMenu);
         btnCargosActividades.setMaxWidth(Double.MAX_VALUE);

         Region spacerMenu = new Region();
         VBox.setVgrow(spacerMenu, Priority.ALWAYS);

         Button btnCerrarSesion = new Button("Cerrar Sesión");
         btnCerrarSesion.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
         btnCerrarSesion.setMaxWidth(Double.MAX_VALUE);

         menuLateral.getChildren().addAll(lblPerfil, new Separator(), btnCrearColab, btnEditarColab, btnCargosActividades, spacerMenu, btnCerrarSesion);
         //manda el menú lateral a la izquierda del contenedor
         root.setLeft(menuLateral);

         //CONTENIDO CENTRAL
         BorderPane contenidoDerecho = new BorderPane();

         //Arriba: Barra de busqueda
         HBox topBar = new HBox(20); //Hbox apila de derecha a izquierda
         topBar.setPadding(new Insets(15));
         topBar.setAlignment(Pos.CENTER_RIGHT);

         TextField txtBuscar = new TextField();
         txtBuscar.setPromptText("Buscar por dni o apellido");
         topBar.getChildren().add(txtBuscar);

         contenidoDerecho.setTop(topBar);

         //Centro: Tabla
         TableView<Contrato> tabla =  new TableView<>();

         TableColumn<Contrato, String> colNombre = new TableColumn<>("Colaborador");
         colNombre.setPrefWidth(300);
         colNombre.setCellValueFactory(cell -> new SimpleStringProperty(
                 cell.getValue().getEmpleado().getNombres() + " " + cell.getValue().getEmpleado().getApellidos()
         ));

         TableColumn<Contrato, String> colCargo = new TableColumn<>("Cargo");
         colCargo.setPrefWidth(250);
         colCargo.setCellValueFactory(cell -> new SimpleStringProperty(
                 cell.getValue().getCargo().getNombreCargo()
         ));
         tabla.getColumns().addAll(colNombre, colCargo);
         try{
             List<Contrato> listaContratos  = controlador.obtenerDatosParaTabla();
             ObservableList<Contrato> datos = FXCollections.observableArrayList(listaContratos);
             tabla.setItems(datos);
         } catch (Exception e){
             System.out.println("Error cargando la tabla: " + e.getMessage());
         }
         contenidoDerecho.setCenter(tabla);

         //Botones
         HBox bottomBar = new HBox(20);
         bottomBar.setPadding(new Insets(20));
         bottomBar.setAlignment(Pos.CENTER);

         Button btnInforme = new Button("Generar Informe de Actividades");
         btnInforme.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-weight: bold;");
         btnInforme.setOnAction(e -> {
             System.out.println("🔄 Generando Informes de Actividades...");
             controlador.generarSoloInformesActividades();
         });

         Button btnFm38 = new Button("Generar FM38");
         btnFm38.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-weight: bold;");
         btnFm38.setOnAction(e -> {
             System.out.println("🔄 Generando formularios FM38...");
             controlador.generarSoloFM38();
         });
         
         Button btnGenerarTodo = new Button("Generar 05");
         btnGenerarTodo.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-weight: bold;");
         btnGenerarTodo.setOnAction(e -> {
             System.out.println("🔄 Generando todos los documentos...");
             controlador.generarTodosLosInformes();
         });


         bottomBar.getChildren().addAll(btnInforme, btnFm38, btnGenerarTodo);
         contenidoDerecho.setBottom(bottomBar);

         root.setCenter(contenidoDerecho);
         Scene scene = new Scene(root, 1000,600);
         stage.setTitle("Generador de indormes - GGE");
         stage.setScene(scene);
         stage.show();


     }
}
