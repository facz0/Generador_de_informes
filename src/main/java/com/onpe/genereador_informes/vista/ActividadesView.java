package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.DAO.ActividadDAO;
import com.onpe.genereador_informes.DAO.CargoAreaDAO;
import com.onpe.genereador_informes.model.Actividad;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;

public class ActividadesView {

    private BorderPane contenedor;
    private ActividadDAO actividadDAO = new ActividadDAO();
    private CargoAreaDAO cargoAreaDAO = new CargoAreaDAO();

    private TableView<Actividad> tabla = new TableView<>();
    private ObservableList<Actividad> datosTabla = FXCollections.observableArrayList();
    private ObservableList<String[]> datosCargoArea = FXCollections.observableArrayList();

    private ComboBox<String[]> comboCargoArea;
    private TextArea txtDescripcion;
    private Button btnGuardar;
    private Label lblMsg;
    private Actividad actividadEditando = null;

    public ActividadesView(BorderPane contenedor) {
        this.contenedor = contenedor;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Actividades", "Administra las actividades por cargo y área"));
        contenedor.setBottom(null);

        // ===== COMBO CARGO+AREA =====
        comboCargoArea = new ComboBox<>(datosCargoArea);
        comboCargoArea.setPromptText("Selecciona cargo + área");
        comboCargoArea.setMaxWidth(Double.MAX_VALUE);
        comboCargoArea.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1] + " — " + item[2]);
            }
        });
        comboCargoArea.setButtonCell(new ListCell<>() {
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1] + " — " + item[2]);
            }
        });
        comboCargoArea.setOnAction(e -> cargarTabla());
        cargarCargoArea();

        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 24, 0, 24));
        HBox.setHgrow(comboCargoArea, Priority.ALWAYS);
        Label lblFiltro = new Label("Cargo + Área:");
        lblFiltro.setStyle("-fx-font-size: 13px; -fx-text-fill: #4a5568;");
        lblFiltro.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topBar.getChildren().addAll(lblFiltro, comboCargoArea);

        // ===== TABLA =====
        TableColumn<Actividad, String> colId = new TableColumn<>("#");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdActividad())));

        TableColumn<Actividad, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        tabla.getColumns().addAll(colId, colDesc);
        tabla.setItems(datosTabla);
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");
        tabla.setOnMouseClicked(e -> {
            Actividad seleccionada = tabla.getSelectionModel().getSelectedItem();
            if (seleccionada != null) cargarEnFormulario(seleccionada);
        });

        // ===== FORMULARIO =====
        VBox formulario = new VBox(10);
        formulario.setPrefWidth(320);
        formulario.setPadding(new Insets(16));
        formulario.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8;");

        Label lblTitulo = new Label("Nueva Actividad");
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");

        txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción de la actividad...");
        txtDescripcion.setPrefRowCount(4);
        txtDescripcion.setWrapText(true);
        txtDescripcion.setStyle("-fx-padding: 8;");

        btnGuardar = DashboardView.crearBotonAccion("Guardar", "#1D2B61");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        Button btnEliminar = DashboardView.crearBotonAccion("Eliminar", "#e53e3e");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);

        Button btnNuevo = new Button("Nueva actividad");
        btnNuevo.setMaxWidth(Double.MAX_VALUE);
        btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8; -fx-cursor: hand;");

        lblMsg = new Label("");
        lblMsg.setStyle("-fx-font-size: 11px;");

        formulario.getChildren().addAll(lblTitulo, new Separator(), txtDescripcion, btnGuardar, btnEliminar, btnNuevo, lblMsg);

        // ===== ACCIONES =====
        btnGuardar.setOnAction(e -> guardar());
        btnEliminar.setOnAction(e -> eliminar());
        btnNuevo.setOnAction(e -> limpiarFormulario());

        // ===== LAYOUT =====
        VBox centroIzq = new VBox(12);
        centroIzq.setPadding(new Insets(16, 16, 16, 24));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centroIzq.getChildren().addAll(topBar, tabla);

        HBox layout = new HBox(16);
        layout.setPadding(new Insets(0, 24, 16, 0));
        HBox.setHgrow(centroIzq, Priority.ALWAYS);
        layout.getChildren().addAll(centroIzq, formulario);

        contenedor.setCenter(layout);
    }

    private void guardar() {
        String desc = txtDescripcion.getText().trim();
        if (desc.isEmpty()) { setMensaje("⚠ La descripción es obligatoria", false); return; }

        String[] cargoArea = comboCargoArea.getValue();
        if (cargoArea == null) { setMensaje("⚠ Selecciona un cargo + área", false); return; }

        if (actividadEditando != null) {
            if (actividadDAO.actualizar(actividadEditando.getIdActividad(), desc)) {
                setMensaje("✓ Actividad actualizada", true);
                limpiarFormulario();
                cargarTabla();
            }
        } else {
            int idCargoArea = Integer.parseInt(cargoArea[0]);
            if (actividadDAO.crear(idCargoArea, desc)) {
                setMensaje("✓ Actividad creada", true);
                limpiarFormulario();
                cargarTabla();
            }
        }
    }

    private void eliminar() {
        if (actividadEditando == null) { setMensaje("⚠ Selecciona una actividad", false); return; }
        if (actividadDAO.eliminar(actividadEditando.getIdActividad())) {
            setMensaje("✓ Actividad eliminada", true);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void cargarEnFormulario(Actividad a) {
        actividadEditando = a;
        txtDescripcion.setText(a.getDescripcion());
    }

    private void limpiarFormulario() {
        actividadEditando = null;
        txtDescripcion.clear();
        tabla.getSelectionModel().clearSelection();
    }

    private void cargarTabla() {
        String[] seleccion = comboCargoArea.getValue();
        if (seleccion == null) return;
        int idCargoArea = Integer.parseInt(seleccion[0]);
        List<Actividad> lista = actividadDAO.obtenerPorCargoArea(idCargoArea);
        datosTabla.setAll(lista);
        limpiarFormulario();
    }

    private void cargarCargoArea() {
        datosCargoArea.setAll(cargoAreaDAO.obtenerTodos());
    }

    private void setMensaje(String texto, boolean exito) {
        lblMsg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (exito ? "#38a169" : "#e53e3e") + ";");
        lblMsg.setText(texto);
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(e -> lblMsg.setText(""));
        pausa.play();
    }
}
