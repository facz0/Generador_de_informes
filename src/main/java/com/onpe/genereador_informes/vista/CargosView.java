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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class CargosView {

    private BorderPane contenedor;
    private CargoDAO cargoDAO = new CargoDAO();
    private AreaDAO areaDAO = new AreaDAO();
    private CargoAreaDAO cargoAreaDAO = new CargoAreaDAO();

    private ObservableList<String[]> datosTabla = FXCollections.observableArrayList();
    private ObservableList<Cargo> datosCargos = FXCollections.observableArrayList();
    private ObservableList<Area> datosAreas = FXCollections.observableArrayList();
    private PaginadorTabla<String[]> paginador;
    private List<String[]> todosLosDatosTabla = new java.util.ArrayList<>();

    // Estado de edición
    private int idCargoAreaEditando = -1;

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

        // ===== FORMULARIO CARGO =====
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

        Runnable limpiarFormulario = () -> {
            idCargoAreaEditando = -1;
            lblTitulo.setText("Nuevo Cargo");
            btnGuardar.setText("Guardar");
            txtCargo.clear();
            comboArea.setValue(null);
            lblMsg.setText("");
        };

        btnGuardar.setOnAction(e -> {
            String nombreCargo = txtCargo.getText().trim().toUpperCase();
            Area area = comboArea.getValue();
            if (nombreCargo.isEmpty() || area == null) {
                setMensaje(lblMsg, "⚠ Completa todos los campos", false);
                return;
            }

            if (idCargoAreaEditando != -1) {
                // Modo edición
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
                if (cargoAreaDAO.actualizar(idCargoAreaEditando, idCargo, area.getIdArea())) {
                    setMensaje(lblMsg, "✓ Actualizado correctamente", true);
                    limpiarFormulario.run();
                    cargarTabla();
                    cargarCombos();
                } else {
                    setMensaje(lblMsg, "❌ Error al actualizar", false);
                }
            } else {
                // Modo creación
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
                    setMensaje(lblMsg, "⚠ Esta asociación ya existe", false);
                    return;
                }
                if (cargoAreaDAO.crear(idCargo, area.getIdArea())) {
                    setMensaje(lblMsg, "✓ Cargo guardado correctamente", true);
                    limpiarFormulario.run();
                    cargarTabla();
                    cargarCombos();
                }
            }
        });

        btnLimpiar.setOnAction(e -> limpiarFormulario.run());

        // ===== COLUMNA ACCIONES =====
        TableColumn<String[], Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(90);
        colAcciones.setMinWidth(90);
        colAcciones.setMaxWidth(90);
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox caja = new HBox(6, btnEditar, btnEliminar);
            {
                caja.setAlignment(Pos.CENTER);
                String estiloBase = "-fx-background-radius: 4; -fx-padding: 3 7; -fx-cursor: hand; -fx-font-size: 13px;";
                btnEditar.setStyle("-fx-background-color: #ebf4ff; -fx-text-fill: #2b6cb0; " + estiloBase);
                btnEliminar.setStyle("-fx-background-color: #fff5f5; -fx-text-fill: #c53030; " + estiloBase);
                btnEliminar.setVisible(false);
                btnEliminar.setManaged(false);

                btnEditar.setOnAction(e -> {
                    String[] fila = getTableView().getItems().get(getIndex());
                    idCargoAreaEditando = Integer.parseInt(fila[0]);
                    lblTitulo.setText("Editar Cargo");
                    btnGuardar.setText("Actualizar");
                    txtCargo.setText(fila[1]);
                    // Seleccionar el área correspondiente en el combo
                    datosAreas.stream()
                        .filter(a -> a.getNombreArea().equalsIgnoreCase(fila[2]))
                        .findFirst()
                        .ifPresent(comboArea::setValue);
                });

                btnEliminar.setOnAction(e -> {
                    String[] fila = getTableView().getItems().get(getIndex());
                    boolean confirmar = DashboardView.mostrarConfirmacion(
                        "Eliminar registro",
                        "Esta seguro de eliminar el cargo y área \"" + fila[1] + " - " + fila[2] + "\"?");
                    if (!confirmar) return;
                    int id = Integer.parseInt(fila[0]);
                    if (cargoAreaDAO.eliminar(id)) {
                        cargarTabla();
                        cargarCombos();
                        if (idCargoAreaEditando == id) limpiarFormulario.run();
                    }
                });
            }

            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : caja);
            }
        });

        tabla.getColumns().add(colAcciones);

        HBox layout = new HBox(16);
        layout.setPadding(new Insets(20, 24, 20, 24));

        // ===== FILTRO =====
        TextField txtFiltro = new TextField();
        txtFiltro.setPromptText("Buscar por cargo...");
        txtFiltro.setPrefWidth(260);
        txtFiltro.setStyle("-fx-padding: 7;");

        Button btnLimpiarFiltro = new Button("✕");
        btnLimpiarFiltro.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 4; -fx-padding: 5 9; -fx-cursor: hand;");
        btnLimpiarFiltro.setVisible(false);

        HBox barraFiltro = new HBox(8, txtFiltro, btnLimpiarFiltro);
        barraFiltro.setAlignment(Pos.CENTER_LEFT);
        barraFiltro.setPadding(new Insets(0, 0, 8, 0));

        // Lista completa para filtrar
        final List<String[]>[] todosLosDatos = new List[]{cargoAreaDAO.obtenerTodos()};

        txtFiltro.textProperty().addListener((obs, ant, nuevo) -> {
            btnLimpiarFiltro.setVisible(nuevo != null && !nuevo.isEmpty());
            if (nuevo == null || nuevo.isEmpty()) {
                paginador.setDatos(FXCollections.observableArrayList(todosLosDatosTabla));
            } else {
                String lower = nuevo.toLowerCase();
                List<String[]> filtrados = todosLosDatosTabla.stream()
                    .filter(f -> f[1].toLowerCase().contains(lower))
                    .collect(Collectors.toList());
                paginador.setDatos(FXCollections.observableArrayList(filtrados));
            }
        });

        btnLimpiarFiltro.setOnAction(e -> txtFiltro.clear());

        VBox tablaConPaginado = new VBox(8);
        HBox.setHgrow(tablaConPaginado, Priority.ALWAYS);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        tablaConPaginado.getChildren().addAll(barraFiltro, tabla, paginador.getControles());

        // ===== FORMULARIO NUEVA ÁREA =====
        VBox formularioArea = new VBox(12);
        formularioArea.setPrefWidth(320);
        formularioArea.setMaxWidth(320);
        formularioArea.setPadding(new Insets(16));
        formularioArea.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8;");

        Label lblTituloArea = new Label("Nueva Área");
        lblTituloArea.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");

        Label lblNombreArea = new Label("Nombre del área");
        lblNombreArea.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a5568;");

        TextField txtArea = new TextField();
        txtArea.setPromptText("Escribe el nombre del área...");
        txtArea.setStyle("-fx-padding: 8;");

        Label lblMsgArea = new Label("");
        lblMsgArea.setStyle("-fx-font-size: 11px;");
        lblMsgArea.setWrapText(true);

        Button btnGuardarArea = DashboardView.crearBotonAccion("Guardar", "#1D2B61");
        btnGuardarArea.setMaxWidth(Double.MAX_VALUE);

        Button btnLimpiarArea = new Button("Limpiar");
        btnLimpiarArea.setMaxWidth(Double.MAX_VALUE);
        btnLimpiarArea.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8; -fx-cursor: hand;");

        formularioArea.getChildren().addAll(lblTituloArea, new Separator(), lblNombreArea, txtArea, btnGuardarArea, btnLimpiarArea, lblMsgArea);

        btnGuardarArea.setOnAction(e -> {
            String nombreArea = txtArea.getText().trim().toUpperCase();
            if (nombreArea.isEmpty()) {
                setMensaje(lblMsgArea, "⚠ Escribe el nombre del área", false);
                return;
            }
            if (areaDAO.existe(nombreArea)) {
                setMensaje(lblMsgArea, "⚠ Esta área ya existe", false);
                return;
            }
            if (areaDAO.crear(nombreArea)) {
                setMensaje(lblMsgArea, "✓ Área guardada correctamente", true);
                txtArea.clear();
                cargarCombos();
            } else {
                setMensaje(lblMsgArea, "❌ Error al guardar el área", false);
            }
        });

        btnLimpiarArea.setOnAction(e -> { txtArea.clear(); lblMsgArea.setText(""); });

        VBox panelDerecho = new VBox(16);
        panelDerecho.getChildren().addAll(formulario, formularioArea);

        layout.getChildren().addAll(tablaConPaginado, panelDerecho);
        contenedor.setCenter(layout);
    }

    private void cargarTabla() {
        todosLosDatosTabla = cargoAreaDAO.obtenerTodos();
        datosTabla.setAll(todosLosDatosTabla);
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
