package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.DashboardController;
import com.onpe.genereador_informes.model.Contrato;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import java.time.LocalDate;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.cell.CheckBoxTableCell;

public class DashboardView {
    private DashboardController controlador;
    private BorderPane contenidoCentral;
    private Map<Contrato, BooleanProperty> selecciones = new HashMap<>();
    private FilteredList<Contrato> filteredData;
    private VBox contenedorFiltros = new VBox(8);

    static final String COLOR_MENU = "#1D2B61";
    static final String COLOR_HOVER = "#2a3d8f";
    static final String COLOR_SECCION = "#162050";
    static final String ESTILO_BTN = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-alignment: BASELINE_LEFT; -fx-padding: 8 16 8 32; -fx-cursor: hand;";
    static final String ESTILO_BTN_HOVER = "-fx-background-color: #2a3d8f; -fx-text-fill: white; -fx-font-size: 13px; -fx-alignment: BASELINE_LEFT; -fx-padding: 8 16 8 32; -fx-cursor: hand;";
    static final String ESTILO_SECCION = "-fx-background-color: transparent; -fx-text-fill: #a0aec0; -fx-font-size: 11px; -fx-alignment: BASELINE_LEFT; -fx-padding: 12 16 4 16; -fx-font-weight: bold;";

    public DashboardView() {
        this.controlador = new DashboardController();
    }

    public void mostrar(Stage stage) {
        BorderPane root = new BorderPane();

        // ===== MENÚ LATERAL =====
        VBox menuLateral = new VBox(0);
        menuLateral.setPrefWidth(220);
        menuLateral.setStyle("-fx-background-color: " + COLOR_MENU + ";");

        VBox header = new VBox(4);
        header.setPadding(new Insets(24, 16, 20, 16));
        header.setStyle("-fx-background-color: " + COLOR_SECCION + ";");
        Label lblTitulo = new Label("GGE");
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label lblSubtitulo = new Label("Generador de Informes");
        lblSubtitulo.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 11px;");
        header.getChildren().addAll(lblTitulo, lblSubtitulo);

        Label lblReportes = new Label("REPORTES");
        lblReportes.setStyle(ESTILO_SECCION);
        lblReportes.setMaxWidth(Double.MAX_VALUE);

        Button btnInformes = crearBotonMenu("📄  Informes de Actividades");
        Button btnFM38 = crearBotonMenu("📋  Formularios FM38");

        Label lblMantenimiento = new Label("MANTENIMIENTO");
        lblMantenimiento.setStyle(ESTILO_SECCION);
        lblMantenimiento.setMaxWidth(Double.MAX_VALUE);

        Button btnCargos = crearBotonMenu("🗂️  Cargos y Áreas");
        Button btnEmpleados = crearBotonMenu("👥  Empleados");
        Button btnActividades = crearBotonMenu("📝  Actividades");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnCerrar = new Button("Cerrar Sesión");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10; -fx-cursor: hand;");
        VBox.setMargin(btnCerrar, new Insets(0, 16, 16, 16));

        menuLateral.getChildren().addAll(header, lblReportes, btnInformes, btnFM38, lblMantenimiento, btnCargos, btnEmpleados, btnActividades, spacer, btnCerrar);
        root.setLeft(menuLateral);

        contenidoCentral = new BorderPane();
        contenidoCentral.setStyle("-fx-background-color: #f4f6f9;");
        mostrarVistaInformes();
        root.setCenter(contenidoCentral);

        btnInformes.setOnAction(e -> mostrarVistaInformes());
        btnFM38.setOnAction(e -> mostrarVistaFM38());
        btnCargos.setOnAction(e -> new CargosView(contenidoCentral).mostrar());
        btnEmpleados.setOnAction(e -> new EmpleadosView(contenidoCentral).mostrar());
        btnActividades.setOnAction(e -> new ActividadesView(contenidoCentral).mostrar());

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("Generador de Informes - GGE");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void mostrarVistaInformes() {
        contenedorFiltros.getChildren().clear();
        contenedorFiltros.setPadding(new Insets(0, 24, 0, 24));

        VBox topBox = crearHeader("Informes de Actividades", "Genera los informes de actividades para todos los colaboradores");
        
        Button btnAgregarFiltro = crearBotonAccion("+ Agregar Filtro", "#2980b9");
        btnAgregarFiltro.setOnAction(e -> agregarFiltroUI());
        
        HBox barFiltro = new HBox(12);
        barFiltro.setPadding(new Insets(16, 24, 8, 24));
        barFiltro.getChildren().add(btnAgregarFiltro);

        VBox topContainer = new VBox(topBox, barFiltro, contenedorFiltros);
        contenidoCentral.setTop(topContainer);

        TableView<Contrato> tabla = crearTablaContratos();
        VBox centro = new VBox(16);
        centro.setPadding(new Insets(8, 24, 20, 24));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centro.getChildren().add(tabla);
        contenidoCentral.setCenter(centro);

        HBox bottomBar = new HBox(12);
        bottomBar.setPadding(new Insets(16, 24, 16, 24));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");
        Button btnSeleccionarTodos = crearBotonAccion("Seleccionar Todos", "#27ae60");
        btnSeleccionarTodos.setOnAction(e -> {
            boolean allSelected = tabla.getItems().stream()
                    .allMatch(c -> selecciones.containsKey(c) && selecciones.get(c).get());
            for (Contrato c : tabla.getItems()) {
                selecciones.computeIfAbsent(c, k -> new SimpleBooleanProperty(false)).set(!allSelected);
            }
        });

        Button btnGenerar = crearBotonAccion("Generar Informes", "#2980b9"); 
        btnGenerar.setOnAction(e -> {
            List<Contrato> seleccionados = tabla.getItems().stream()
                    .filter(c -> selecciones.containsKey(c) && selecciones.get(c).get()
                    && "ACTIVO".equalsIgnoreCase(c.getPersonal().getEstado()))
                    .collect(Collectors.toList());
            if (!seleccionados.isEmpty()) {
                controlador.generarSoloInformesActividades(seleccionados);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione al menos un trabajador con el estado ACTIVO");
                alert.show();
            }
        });
        bottomBar.getChildren().addAll(btnSeleccionarTodos, btnGenerar);
        contenidoCentral.setBottom(bottomBar);
    }

    private void mostrarVistaFM38() {
        contenedorFiltros.getChildren().clear();
        contenedorFiltros.setPadding(new Insets(0, 24, 0, 24));

        VBox topBox = crearHeader("Formularios FM38", "Genera los formularios FM38 para todos los colaboradores");
        
        Button btnAgregarFiltro = crearBotonAccion("+ Agregar Filtro", "#2980b9");
        btnAgregarFiltro.setOnAction(e -> agregarFiltroUI());
        
        HBox barFiltro = new HBox(12);
        barFiltro.setPadding(new Insets(16, 24, 8, 24));
        barFiltro.getChildren().add(btnAgregarFiltro);

        VBox topContainer = new VBox(topBox, barFiltro, contenedorFiltros);
        contenidoCentral.setTop(topContainer);

        TableView<Contrato> tabla = crearTablaContratos();
        VBox centro = new VBox(16);
        centro.setPadding(new Insets(8, 24, 20, 24));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centro.getChildren().add(tabla);
        contenidoCentral.setCenter(centro);

        HBox bottomBar = new HBox(12);
        bottomBar.setPadding(new Insets(16, 24, 16, 24));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");
        Button btnSeleccionarTodos = crearBotonAccion("Seleccionar Todos", "#27ae60");
        btnSeleccionarTodos.setOnAction(e -> {
            boolean allSelected = tabla.getItems().stream()
                    .allMatch(c -> selecciones.containsKey(c) && selecciones.get(c).get());
            for (Contrato c : tabla.getItems()) {
                selecciones.computeIfAbsent(c, k -> new SimpleBooleanProperty(false)).set(!allSelected);
            }
        });

        Button btnGenerar = crearBotonAccion("Generar FM38", "#2980b9");
        btnGenerar.setOnAction(e -> {
            List<Contrato> seleccionados = tabla.getItems().stream()
                    .filter(c -> selecciones.containsKey(c) && selecciones.get(c).get()
                    && "ACTIVO".equalsIgnoreCase(c.getPersonal().getEstado()))
                    .collect(Collectors.toList());
            if (!seleccionados.isEmpty()) {
                controlador.generarSoloFM38(seleccionados);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecciones al menos un trabajador con el estado ACTIVO");
                alert.show();
            }
        });
        bottomBar.getChildren().addAll(btnSeleccionarTodos, btnGenerar);
        contenidoCentral.setBottom(bottomBar);
    }

    private void mostrarVistaPendiente(String mensaje) {
        contenidoCentral.setTop(null);
        contenidoCentral.setBottom(null);
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: #f4f6f9;");
        Label lbl = new Label("🚧  " + mensaje);
        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #a0aec0;");
        pane.getChildren().add(lbl);
        contenidoCentral.setCenter(pane);
    }

    private TableView<Contrato> crearTablaContratos() {
        TableView<Contrato> tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");
        tabla.setEditable(true);

        TableColumn<Contrato, String> colNum = new TableColumn<>("N°");
        colNum.prefWidthProperty().bind(tabla.widthProperty().multiply(0.04));
        colNum.setCellFactory(col -> new TableCell<Contrato, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        TableColumn<Contrato, Boolean> colSeleccion = new TableColumn<>("☑");
        colSeleccion.prefWidthProperty().bind(tabla.widthProperty().multiply(0.04));
        colSeleccion.setCellFactory(tc -> new TableCell<Contrato, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            
            @Override
            protected void updateItem(Boolean item, boolean empty){
                super.updateItem(item, empty);
                if(empty || getTableRow() == null){
                    setGraphic(null);
                    if(getTableRow() != null ) getTableRow().setStyle("");
                } else {
                    Contrato contrato = getTableView().getItems().get(getIndex());
                    BooleanProperty prop = selecciones.computeIfAbsent(contrato, k -> new SimpleBooleanProperty(false));
                    
                    // Unbind first to prevent multiple bindings if cell is reused
                    checkBox.selectedProperty().unbindBidirectional(prop); 
                    checkBox.setSelected(prop.get());
                    checkBox.selectedProperty().bindBidirectional(prop);
                    
                    // Solo registrar el listener de fila si no se ha hecho antes
                    if (getTableRow().getUserData() == null || !(boolean)getTableRow().getUserData()) {
                        prop.addListener((obs, oldVal, newVal) -> {
                            // Este listener funciona de manera segura
                            if (getTableRow() != null) {
                                if (newVal) {
                                    getTableRow().setStyle("-fx-background-color: #dbeafe;"); 
                                } else {
                                    getTableRow().setStyle(""); 
                                }
                            }
                        });
                        getTableRow().setUserData(true);
                    }
                    
                    if (prop.get()) {
                        getTableRow().setStyle("-fx-background-color: #dbeafe;");
                    } else {
                        getTableRow().setStyle("");
                    }

                    setGraphic(checkBox);
                    setAlignment(Pos.CENTER);
                }
            }
        });


        TableColumn<Contrato, String> colDni = new TableColumn<>("DNI");
        colDni.prefWidthProperty().bind(tabla.widthProperty().multiply(0.08));
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getDni()));

        TableColumn<Contrato, String> colApellidos = new TableColumn<>("Apellidos");
        colApellidos.prefWidthProperty().bind(tabla.widthProperty().multiply(0.12));
        colApellidos.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getApellido()));

        TableColumn<Contrato, String> colNombres = new TableColumn<>("Nombres");
        colNombres.prefWidthProperty().bind(tabla.widthProperty().multiply(0.12));
        colNombres.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getNombre()));

        TableColumn<Contrato, String> colCargo = new TableColumn<>("Cargo");
        colCargo.prefWidthProperty().bind(tabla.widthProperty().multiply(0.15));
        colCargo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getCargoArea().getCargo().getNombreCargo()));

        TableColumn<Contrato, String> colArea = new TableColumn<>("Área");
        colArea.prefWidthProperty().bind(tabla.widthProperty().multiply(0.12));
        colArea.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getCargoArea().getArea().getNombreArea()));

        TableColumn<Contrato, String> colNumContrato = new TableColumn<>("N° Contrato");
        colNumContrato.prefWidthProperty().bind(tabla.widthProperty().multiply(0.09));
        colNumContrato.setCellValueFactory(cell -> {
            String num = cell.getValue().getNumeroContrato();
            return new SimpleStringProperty(num != null ? num : "SIN CONTRATO");
        });

        TableColumn<Contrato, String> colFechaInicio = new TableColumn<>("Fecha inicio");
        colFechaInicio.prefWidthProperty().bind(tabla.widthProperty().multiply(0.09));
        colFechaInicio.setCellValueFactory(cell -> {
            LocalDate d = cell.getValue().getFechaInicio();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        TableColumn<Contrato, String> colFechaFin = new TableColumn<>("Fecha fin");
        colFechaFin.prefWidthProperty().bind(tabla.widthProperty().multiply(0.08));
        colFechaFin.setCellValueFactory(cell -> {
            LocalDate d = cell.getValue().getFechaFin();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        TableColumn<Contrato, String> colOdpe = new TableColumn<>("ODPE");
        colOdpe.prefWidthProperty().bind(tabla.widthProperty().multiply(0.07));
        colOdpe.setCellValueFactory(cell -> {
            com.onpe.genereador_informes.model.Odpe o = cell.getValue().getPersonal().getOdpe();
            return new SimpleStringProperty(o != null && o.getNombreOdpe() != null ? o.getNombreOdpe() : "");
        });

        tabla.getColumns().addAll(colSeleccion, colNum, colDni, colApellidos, colNombres, colCargo, colArea, colNumContrato, colFechaInicio, colFechaFin, colOdpe);

        try {
            List<Contrato> lista = controlador.obtenerDatosParaTabla();
            ObservableList<Contrato> datosTabla = FXCollections.observableArrayList(lista);
            filteredData = new FilteredList<>(datosTabla, p -> true);
            SortedList<Contrato> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tabla.comparatorProperty());
            tabla.setItems(sortedData);
        } catch (Exception e) {
            System.out.println("Error cargando tabla: " + e.getMessage());
        }
        return tabla;
    }

    private void agregarFiltroUI() {
        HBox filaFiltro = new HBox(10);
        filaFiltro.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> comboColumna = new ComboBox<>(FXCollections.observableArrayList(
                "DNI", "Apellidos", "Nombres", "Cargo", "Área", "N° Contrato", "ODPE"
        ));
        comboColumna.setValue("DNI");
        comboColumna.setStyle("-fx-padding: 4;");

        TextField txtValor = new TextField();
        txtValor.setPromptText("Valor a buscar...");
        txtValor.setStyle("-fx-padding: 7;");

        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        filaFiltro.getChildren().addAll(comboColumna, txtValor, btnEliminar);
        contenedorFiltros.getChildren().add(filaFiltro);

        btnEliminar.setOnAction(e -> {
            contenedorFiltros.getChildren().remove(filaFiltro);
            aplicarFiltros();
        });
        comboColumna.setOnAction(e -> aplicarFiltros());
        txtValor.textProperty().addListener((obs, oldV, newV) -> aplicarFiltros());

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (filteredData == null) return;
        filteredData.setPredicate(contrato -> {
            for (javafx.scene.Node node : contenedorFiltros.getChildren()) {
                if (node instanceof HBox) {
                    HBox fila = (HBox) node;
                    @SuppressWarnings("unchecked")
                    ComboBox<String> combo = (ComboBox<String>) fila.getChildren().get(0);
                    TextField txt = (TextField) fila.getChildren().get(1);

                    String columna = combo.getValue();
                    String filtro = txt.getText().trim().toLowerCase();

                    if (filtro.isEmpty()) continue;

                    String valorCelda = "";
                    switch (columna) {
                        case "DNI": valorCelda = contrato.getPersonal().getDni(); break;
                        case "Nombres": valorCelda = contrato.getPersonal().getNombre(); break;
                        case "Apellidos": valorCelda = contrato.getPersonal().getApellido(); break;
                        case "Cargo": valorCelda = contrato.getPersonal().getCargoArea().getCargo().getNombreCargo(); break;
                        case "Área": valorCelda = contrato.getPersonal().getCargoArea().getArea().getNombreArea(); break;
                        case "N° Contrato": valorCelda = contrato.getNumeroContrato() != null ? contrato.getNumeroContrato() : "SIN CONTRATO"; break;
                        case "ODPE": 
                            com.onpe.genereador_informes.model.Odpe o = contrato.getPersonal().getOdpe();
                            if (o != null) valorCelda = o.getNombreOdpe();
                            break;
                    }

                    if (valorCelda == null) valorCelda = "";
                    valorCelda = valorCelda.toLowerCase();

                    if (columna.equals("DNI") || columna.equals("Nombres") || columna.equals("Apellidos") || columna.equals("Cargo") || columna.equals("Área")) {
                        if (!valorCelda.startsWith(filtro)) return false;
                    } else {
                        if (!valorCelda.contains(filtro)) return false;
                    }
                }
            }
            return true;
        });
    }

    static VBox crearHeader(String titulo, String subtitulo) {
        VBox header = new VBox(4);
        header.setPadding(new Insets(24, 24, 16, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");
        Label lblSub = new Label(subtitulo);
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");
        header.getChildren().addAll(lblTitulo, lblSub);
        return header;
    }

    static Button crearBotonAccion(String texto, String color) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 10 24; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 6;");
        return btn;
    }

    private Button crearBotonMenu(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(ESTILO_BTN);
        btn.setOnMouseEntered(e -> btn.setStyle(ESTILO_BTN_HOVER));
        btn.setOnMouseExited(e -> btn.setStyle(ESTILO_BTN));
        return btn;
    }
}
