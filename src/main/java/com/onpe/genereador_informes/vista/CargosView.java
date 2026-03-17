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

    public CargosView(BorderPane contenedor) {
        this.contenedor = contenedor;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Cargos y Áreas", "Administra las combinaciones de cargo y área"));
        contenedor.setBottom(null);

        // ===== TABLA =====
        TableView<String[]> tablaCargoArea = new TableView<>();
        tablaCargoArea.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");

        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));

        TableColumn<String[], String> colCargo = new TableColumn<>("Cargo");
        colCargo.setPrefWidth(250);
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));

        TableColumn<String[], String> colArea = new TableColumn<>("Área");
        colArea.setPrefWidth(250);
        colArea.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));

        tablaCargoArea.getColumns().addAll(colId, colCargo, colArea);
        tablaCargoArea.setItems(datosTabla);
        cargarTabla();

        // ===== PANEL DERECHO =====
        VBox panelDerecho = new VBox(16);
        panelDerecho.setPrefWidth(300);
        panelDerecho.setPadding(new Insets(0, 0, 0, 16));
        panelDerecho.getChildren().addAll(
                crearPanelAsociar(),
                crearPanelNuevoCargo(),
                crearPanelNuevaArea()
        );

        HBox layout = new HBox(0);
        layout.setPadding(new Insets(20, 24, 20, 24));
        HBox.setHgrow(tablaCargoArea, Priority.ALWAYS);
        layout.getChildren().addAll(tablaCargoArea, panelDerecho);
        contenedor.setCenter(layout);
    }

    private VBox crearPanelAsociar() {
        VBox panel = crearPanel("Asociar Cargo + Área");

        ComboBox<Cargo> comboCargo = new ComboBox<>(datosCargos);
        comboCargo.setMaxWidth(Double.MAX_VALUE);
        comboCargo.setPromptText("Selecciona un cargo");
        comboCargo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Cargo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreCargo());
            }
        });
        comboCargo.setButtonCell(new ListCell<>() {
            protected void updateItem(Cargo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreCargo());
            }
        });

        ComboBox<Area> comboArea = new ComboBox<>(datosAreas);
        comboArea.setMaxWidth(Double.MAX_VALUE);
        comboArea.setPromptText("Selecciona un área");
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

        Button btnAsociar = DashboardView.crearBotonAccion("Asociar", "#1D2B61");
        btnAsociar.setMaxWidth(Double.MAX_VALUE);
        btnAsociar.setOnAction(e -> {
            Cargo cargo = comboCargo.getValue();
            Area area = comboArea.getValue();
            if (cargo == null || area == null) {
                setMensaje(lblMsg, "⚠ Selecciona cargo y área", false);
                return;
            }
            if (cargoAreaDAO.existeAsociacion(cargo.getIdCargo(), area.getIdArea())) {
                setMensaje(lblMsg, "⚠ Esta asociación ya existe", false);
                return;
            }
            if (cargoAreaDAO.crear(cargo.getIdCargo(), area.getIdArea())) {
                setMensaje(lblMsg, "✓ Asociación creada", true);
                comboCargo.setValue(null);
                comboArea.setValue(null);
                cargarTabla();
            }
        });

        panel.getChildren().addAll(new Label("Cargo:"), comboCargo, new Label("Área:"), comboArea, btnAsociar, lblMsg);
        return panel;
    }

    private VBox crearPanelNuevoCargo() {
        VBox panel = crearPanel("Nuevo Cargo");
        TextField txt = new TextField();
        txt.setPromptText("Nombre del cargo");
        txt.setStyle("-fx-padding: 7;");
        Label lblMsg = new Label("");
        Button btn = DashboardView.crearBotonAccion("Crear Cargo", "#2980b9");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            String nombre = txt.getText().trim();
            if (nombre.isEmpty()) { setMensaje(lblMsg, "⚠ El nombre es obligatorio", false); return; }
            if (cargoDAO.existe(nombre)) { setMensaje(lblMsg, "⚠ Este cargo ya existe", false); return; }
            if (cargoDAO.crear(nombre)) {
                setMensaje(lblMsg, "✓ Cargo creado", true);
                txt.clear();
                cargarCombos();
            }
        });
        panel.getChildren().addAll(txt, btn, lblMsg);
        return panel;
    }

    private VBox crearPanelNuevaArea() {
        VBox panel = crearPanel("Nueva Área");
        TextField txt = new TextField();
        txt.setPromptText("Nombre del área");
        txt.setStyle("-fx-padding: 7;");
        Label lblMsg = new Label("");
        Button btn = DashboardView.crearBotonAccion("Crear Área", "#27ae60");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            String nombre = txt.getText().trim();
            if (nombre.isEmpty()) { setMensaje(lblMsg, "⚠ El nombre es obligatorio", false); return; }
            if (areaDAO.existe(nombre)) { setMensaje(lblMsg, "⚠ Esta área ya existe", false); return; }
            if (areaDAO.crear(nombre)) {
                setMensaje(lblMsg, "✓ Área creada", true);
                txt.clear();
                cargarCombos();
            }
        });
        panel.getChildren().addAll(txt, btn, lblMsg);
        return panel;
    }

    private VBox crearPanel(String titulo) {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(14));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8;");
        Label lbl = new Label(titulo);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");
        panel.getChildren().addAll(lbl, new Separator());
        return panel;
    }

    private void setMensaje(Label lbl, String texto, boolean exito) {
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (exito ? "#38a169" : "#e53e3e") + ";");
        lbl.setText(texto);
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(e -> lbl.setText(""));
        pausa.play();
    }

    private void cargarTabla() {
        datosTabla.setAll(cargoAreaDAO.obtenerTodos());
    }

    private void cargarCombos() {
        datosCargos.setAll(cargoDAO.obtenerTodos());
        datosAreas.setAll(areaDAO.obtenerTodas());
    }
}
