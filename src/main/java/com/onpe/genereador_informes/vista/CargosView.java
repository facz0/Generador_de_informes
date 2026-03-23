package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.DAO.AreaDAO;
import com.onpe.genereador_informes.DAO.CargoAreaDAO;
import com.onpe.genereador_informes.DAO.CargoDAO;
import com.onpe.genereador_informes.model.Area;
import com.onpe.genereador_informes.model.Cargo;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class CargosView {

    private BorderPane contenedor;
    private CargoDAO cargoDAO = new CargoDAO();
    private AreaDAO areaDAO = new AreaDAO();
    private CargoAreaDAO cargoAreaDAO = new CargoAreaDAO();

    private ObservableList<String[]> datosTabla = FXCollections.observableArrayList();
    private ObservableList<Cargo> datosCargos = FXCollections.observableArrayList();
    private ObservableList<Area> datosAreas = FXCollections.observableArrayList();
    private PaginadorTabla<String[]> paginador;

    public CargosView(BorderPane contenedor) {
        this.contenedor = contenedor;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Cargos y Áreas", "Administra las combinaciones de cargo y área"));
        contenedor.setBottom(null);

        // ===== TABLA =====
        TableView<String[]> tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colNum = PaginadorTabla.crearColumnaNumero();
        TableColumn<String[], String> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));

        TableColumn<String[], String> colArea = new TableColumn<>("Área");
        colArea.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));

        tabla.getColumns().addAll(colNum, colCargo, colArea);
        tabla.setItems(datosTabla);

        paginador = new PaginadorTabla<>(tabla, 20);
        cargarTabla();
        paginador.setDatos(datosTabla);

        // ===== FORMULARIO =====
        VBox formulario = new VBox(12);
        formulario.setPrefWidth(320);
        formulario.setMaxWidth(320);
        formulario.setPadding(new Insets(16));
        formulario.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8;");

        Label lblTitulo = new Label("Nuevo Cargo");
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");

        Label lblCargo = new Label("Nombre del cargo");
        lblCargo.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a5568;");

        TextField txtCargo = new TextField();
        txtCargo.setPromptText("Escribe el nombre del cargo...");
        txtCargo.setStyle("-fx-padding: 8;");

        Label lblArea = new Label("Área asociada");
        lblArea.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a5568;");

        ComboBox<Area> comboArea = new ComboBox<>(datosAreas);
        comboArea.setPromptText("Selecciona un área");
        comboArea.setMaxWidth(Double.MAX_VALUE);
        comboArea.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreArea());
            }
        });
        comboArea.setButtonCell(new ListCell<>() {
            protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreArea());
            }
        });

        cargarCombos();

        Label lblMsg = new Label("");
        lblMsg.setStyle("-fx-font-size: 11px;");
        lblMsg.setWrapText(true);

        Button btnGuardar = DashboardView.crearBotonAccion("Guardar", "#1D2B61");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setMaxWidth(Double.MAX_VALUE);
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8; -fx-cursor: hand;");

        formulario.getChildren().addAll(lblTitulo, new Separator(), lblCargo, txtCargo, lblArea, comboArea, btnGuardar, btnLimpiar, lblMsg);

        btnGuardar.setOnAction(e -> {
            String nombreCargo = txtCargo.getText().trim();
            Area area = comboArea.getValue();
            if (nombreCargo.isEmpty() || area == null) {
                setMensaje(lblMsg, "⚠ Completa todos los campos", false);
                return;
            }
            // Crear cargo si no existe
            int idCargo;
            if (cargoDAO.existe(nombreCargo)) {
                idCargo = cargoDAO.obtenerTodos().stream()
                    .filter(c -> c.getNombreCargo().equalsIgnoreCase(nombreCargo))
                    .findFirst().map(Cargo::getIdCargo).orElse(-1);
            } else {
                cargoDAO.crear(nombreCargo);
                idCargo = cargoDAO.obtenerTodos().stream()
                    .filter(c -> c.getNombreCargo().equalsIgnoreCase(nombreCargo))
                    .findFirst().map(Cargo::getIdCargo).orElse(-1);
            }
            if (idCargo == -1) { setMensaje(lblMsg, "❌ Error al obtener el cargo", false); return; }
            if (cargoAreaDAO.existeAsociacion(idCargo, area.getIdArea())) {
                setMensaje(lblMsg, "⚠ Esta combinación ya existe", false);
                return;
            }
            if (cargoAreaDAO.crear(idCargo, area.getIdArea())) {
                setMensaje(lblMsg, "✓ Cargo guardado correctamente", true);
                txtCargo.clear();
                comboArea.setValue(null);
                cargarTabla();
                cargarCombos();
            }
        });

        btnLimpiar.setOnAction(e -> { txtCargo.clear(); comboArea.setValue(null); lblMsg.setText(""); });

        HBox layout = new HBox(16);
        layout.setPadding(new Insets(20, 24, 20, 24));

        VBox tablaConPaginado = new VBox(8);
        HBox.setHgrow(tablaConPaginado, Priority.ALWAYS);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        tablaConPaginado.getChildren().addAll(tabla, paginador.getControles());

        layout.getChildren().addAll(tablaConPaginado, formulario);
        contenedor.setCenter(layout);
    }

    private void cargarTabla() {
        datosTabla.setAll(cargoAreaDAO.obtenerTodos());
        if (paginador != null) paginador.setDatos(datosTabla);
    }

    private void cargarCombos() {
        datosCargos.setAll(cargoDAO.obtenerTodos());
        datosAreas.setAll(areaDAO.obtenerTodas());
    }

    private void setMensaje(Label lbl, String texto, boolean exito) {
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (exito ? "#38a169" : "#e53e3e") + ";");
        lbl.setText(texto);
        PauseTransition p = new PauseTransition(Duration.seconds(3));
        p.setOnFinished(e -> lbl.setText(""));
        p.play();
    }
}
