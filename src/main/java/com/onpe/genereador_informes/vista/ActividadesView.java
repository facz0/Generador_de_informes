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

        // ===== COMBO CARGO+AREA (autocompletable) =====
        cargarCargoArea();

        comboCargoArea = new ComboBox<>(datosCargoArea);
        comboCargoArea.setPromptText("Busca por cargo o área...");
        comboCargoArea.setMaxWidth(Double.MAX_VALUE);
        comboCargoArea.setEditable(true);
        comboCargoArea.setVisibleRowCount(10);
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
        comboCargoArea.setConverter(new javafx.util.StringConverter<>() {
            public String toString(String[] item) { return item == null ? "" : item[1] + " — " + item[2]; }
            public String[] fromString(String s) { return null; }
        });

        final boolean[] actualizandoCombo = {false};
        comboCargoArea.getEditor().textProperty().addListener((obs, anterior, nuevo) -> {
            if (actualizandoCombo[0]) return;
            String[] valorActual = comboCargoArea.getValue();
            if (valorActual != null && (valorActual[1] + " — " + valorActual[2]).equals(nuevo)) return;
            if (nuevo == null || nuevo.isEmpty()) {
                actualizandoCombo[0] = true;
                comboCargoArea.setValue(null);
                comboCargoArea.setItems(datosCargoArea);
                actualizandoCombo[0] = false;
                return;
            }
            String filtro = nuevo.toLowerCase();
            ObservableList<String[]> filtrados = datosCargoArea.stream()
                .filter(s -> (s[1] + " " + s[2]).toLowerCase().contains(filtro))
                .collect(javafx.collections.FXCollections::observableArrayList,
                         ObservableList::add, ObservableList::addAll);
            actualizandoCombo[0] = true;
            comboCargoArea.setItems(filtrados);
            comboCargoArea.getEditor().setText(nuevo);
            comboCargoArea.getEditor().positionCaret(nuevo.length());
            actualizandoCombo[0] = false;
            if (!filtrados.isEmpty()) comboCargoArea.show();
        });
        comboCargoArea.valueProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) cargarTabla();
        });

        Label lblFiltro = new Label("Cargo:");
        lblFiltro.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #4a5568;");

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 0, 0));
        topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(comboCargoArea, Priority.ALWAYS);
        topBar.getChildren().addAll(lblFiltro, comboCargoArea);

        // ===== TABLA =====
        TableColumn<Actividad, String> colId = new TableColumn<>("#");
        colId.setPrefWidth(40);
        colId.setMinWidth(40);
        colId.setMaxWidth(40);
        colId.setCellFactory(col -> new TableCell<Actividad, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        TableColumn<Actividad, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colId.setPrefWidth(150);
        tabla.getColumns().addAll(colId, colDesc);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setItems(datosTabla);
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");

        // Drag & Drop para reordenar filas
        tabla.setRowFactory(tv -> {
            TableRow<Actividad> row = new TableRow<>();
            row.setOnDragDetected(e -> {
                if (!row.isEmpty()) {
                    javafx.scene.input.Dragboard db = row.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    javafx.scene.input.ClipboardContent cc = new javafx.scene.input.ClipboardContent();
                    cc.putString(String.valueOf(row.getIndex()));
                    db.setContent(cc);
                    e.consume();
                }
            });
            row.setOnDragOver(e -> {
                if (e.getDragboard().hasString()) {
                    e.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
                    e.consume();
                }
            });
            row.setOnDragDropped(e -> {
                javafx.scene.input.Dragboard db = e.getDragboard();
                if (db.hasString()) {
                    int origen = Integer.parseInt(db.getString());
                    int destino = row.isEmpty() ? datosTabla.size() - 1 : row.getIndex();
                    Actividad item = datosTabla.remove(origen);
                    datosTabla.add(destino, item);
                    tabla.refresh(); // Actualiza la numeración
                    e.setDropCompleted(true);
                    e.consume();
                }
            });
            return row;
        });

        tabla.setOnMouseClicked(e -> {
            Actividad seleccionada = tabla.getSelectionModel().getSelectedItem();
            if (seleccionada != null) cargarEnFormulario(seleccionada);
        });

        // ===== FORMULARIO =====
        VBox formulario = new VBox(10);
        formulario.setPrefWidth(320);
        formulario.setMinWidth(320);
        formulario.setMaxWidth(320);
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
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centroIzq.getChildren().addAll(topBar, tabla);

        HBox layout = new HBox(16);
        layout.setPadding(new Insets(20, 24, 16, 24));
        HBox.setHgrow(centroIzq, Priority.ALWAYS);
        VBox.setVgrow(formulario, Priority.NEVER);
        javafx.scene.layout.HBox.setMargin(formulario, new Insets(0));
        layout.setAlignment(javafx.geometry.Pos.TOP_LEFT);
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
