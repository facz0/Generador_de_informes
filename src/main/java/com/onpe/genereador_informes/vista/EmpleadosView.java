package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.EmpleadosController;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class EmpleadosView {

    private BorderPane contenedor;
    private EmpleadosController controlador;
    
    private ObservableList<String[]> datosTabla = FXCollections.observableArrayList();
    private FilteredList<String[]> filteredData;
    private VBox contenedorFiltros = new VBox(8);
    private TableView<String[]> tabla;
    private PaginadorTabla<String[]> paginador;

    public EmpleadosView(BorderPane contenedor) {
        this.contenedor = contenedor;
        this.controlador = new EmpleadosController();
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Empleados", "Administra los colaboradores del sistema"));
        contenedor.setBottom(null);

        // Barra superior
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 24, 8, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button btnAgregarFiltro = DashboardView.crearBotonAccion("+ Agregar Filtro", "#2980b9");
        btnAgregarFiltro.setOnAction(e -> agregarFiltroUI());

        Button btnNuevo = DashboardView.crearBotonAccion("+ Nuevo Empleado", "#1D2B61");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(btnAgregarFiltro, spacer, btnNuevo);

        contenedorFiltros.setPadding(new Insets(0, 24, 8, 24));

        // Tabla
        tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colNum = new TableColumn<>("N°");
        colNum.setCellFactory(col -> new TableCell<String[], String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        tabla.getColumns().addAll(
                colNum,
                col("DNI", 0.07, 1),
                col("Apellidos", 0.12, 3),
                col("Nombres", 0.12, 2),
                col("Cargo", 0.20, 5),
                col("Área", 0.12, 6),
                col("N° Contrato", 0.07, 9),
                col("Fecha inicio de labores", 0.09, 10),
                col("Fecha fin de labores", 0.09, 11),
                col("ODPE", 0.05, 8),
                col("Estado", 0.04, 4));

        // Doble click para editar
        tabla.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel != null)
                    abrirFormulario(sel);
            }
        });

        paginador = new PaginadorTabla<>(tabla, 20);
        cargarTabla();
        paginador.setDatos(datosTabla);

        VBox centro = new VBox(0);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centro.getChildren().addAll(topBar, contenedorFiltros, tabla, paginador.getControles());
        VBox.setMargin(tabla, new Insets(0, 24, 0, 24));
        VBox.setMargin(paginador.getControles(), new Insets(0, 24, 8, 24));

        contenedor.setCenter(centro);

        btnNuevo.setOnAction(e -> abrirFormulario(null));
    }

    private void abrirFormulario(String[] empleado) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle(empleado == null ? "Nuevo Empleado" : "Editar Empleado");
        modal.setResizable(false);

        ObservableList<String[]> datosCargosList = FXCollections.observableArrayList();
        ObservableList<String[]> datosAreasFiltradas = FXCollections.observableArrayList();
        ObservableList<String[]> datosGerencia = FXCollections.observableArrayList();
        ObservableList<String[]> datosOdpe = FXCollections.observableArrayList();

        // Campos
        TextField txtDni = campo("DNI (8 dígitos)");
        TextField txtNombre = campo("Nombres");
        TextField txtApellido = campo("Apellidos");
        TextField txtNumContrato = campo("Número de contrato");

        DatePicker dpFechaInicio = new DatePicker();
        dpFechaInicio.setPromptText("Fecha inicio");
        dpFechaInicio.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpFechaFin = new DatePicker();
        dpFechaFin.setPromptText("Fecha fin (opcional)");
        dpFechaFin.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String[]> comboCargo = crearCombo(datosCargosList, 1, -1, "");
        comboCargo.setPromptText("Selecciona un cargo");

        ComboBox<String[]> comboArea = crearCombo(datosAreasFiltradas, 1, -1, "");
        comboArea.setPromptText("Primero selecciona un cargo");
        comboArea.setDisable(true);

        ComboBox<String[]> comboOdpe = crearCombo(datosOdpe, 1, -1, "");
        comboOdpe.setPromptText("Solo para ODPEs");
        comboOdpe.setDisable(true);

        comboCargo.setOnAction(e -> {
            String[] cargoSel = comboCargo.getValue();
            comboArea.setValue(null);
            comboOdpe.setValue(null);
            comboOdpe.setDisable(true);
            
            if (cargoSel != null) {
                controlador.cargarAreasPorCargo(Integer.parseInt(cargoSel[0]), datosAreasFiltradas);
                comboArea.setDisable(false);
                comboArea.setPromptText("Selecciona un área");
            } else {
                comboArea.setDisable(true);
                comboArea.setPromptText("Primero selecciona un cargo");
            }
        });

        // Al seleccionar área, habilitar ID de cargo_area (58) para habilitar ODPE
        comboArea.setOnAction(e -> {
            String[] areaSel = comboArea.getValue();
            comboOdpe.setValue(null);
            if (areaSel != null && areaSel[0].equals("58")) {
                comboOdpe.setDisable(false);
                comboOdpe.setPromptText("Selecciona una ODPE");
            } else {
                comboOdpe.setDisable(true);
                comboOdpe.setPromptText("No aplica");
            }
        });

        ComboBox<String[]> comboGerencia = crearComboSimple(datosGerencia);
        comboGerencia.setPromptText("Gerencia");

        ComboBox<String> comboEstado = new ComboBox<>(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        comboEstado.setPromptText("Estado");
        comboEstado.setMaxWidth(Double.MAX_VALUE);

        controlador.cargarCargos(datosCargosList);
        controlador.cargarGerencias(datosGerencia);
        controlador.cargarOdpes(datosOdpe);

        // Si es edición, prellenar
        if (empleado != null) {
            txtDni.setText(empleado[1]);
            txtNombre.setText(empleado[2]);
            txtApellido.setText(empleado[3]);
            comboEstado.setValue(empleado[4]);
            txtNumContrato.setText(empleado[9]);
            if (!empleado[10].isEmpty())
                dpFechaInicio.setValue(java.time.LocalDate.parse(empleado[10]));
            if (!empleado[11].isEmpty())
                dpFechaFin.setValue(java.time.LocalDate.parse(empleado[11]));

            // Precargar cargo → dispara listener que carga áreas
            String idCargoStr = empleado[13];
            datosCargosList.stream()
                .filter(c -> c[0].equals(idCargoStr))
                .findFirst()
                .ifPresent(cargo -> {
                    comboCargo.setValue(cargo);
                    // Cargar áreas del cargo y seleccionar la correcta
                    controlador.cargarAreasPorCargo(Integer.parseInt(idCargoStr), datosAreasFiltradas);
                    comboArea.setDisable(false);
                    String idCargoAreaStr = empleado[12];
                    datosAreasFiltradas.stream()
                        .filter(a -> a[0].equals(idCargoAreaStr))
                        .findFirst()
                        .ifPresent(area -> {
                            comboArea.setValue(area);
                            // Si es SUDIME (id_cargo_area=58), habilitar ODPE
                            if (idCargoAreaStr.equals("58") && !empleado[15].isEmpty()) {
                                comboOdpe.setDisable(false);
                                datosOdpe.stream()
                                    .filter(o -> o[0].equals(empleado[15]))
                                    .findFirst()
                                    .ifPresent(comboOdpe::setValue);
                            }
                        });
                });

            // Precargar gerencia
            datosGerencia.stream()
                .filter(g -> g[0].equals(empleado[14]))
                .findFirst()
                .ifPresent(comboGerencia::setValue);
        }

        Label lblMsg = new Label("");
        lblMsg.setStyle("-fx-font-size: 11px;");
        lblMsg.setWrapText(true);

        Button btnGuardar = DashboardView.crearBotonAccion(empleado == null ? "Guardar" : "Actualizar", "#1D2B61");
        Button btnEliminar = DashboardView.crearBotonAccion("Eliminar", "#e53e3e");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        if (empleado == null) btnEliminar.setVisible(false);

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(10); grid.setPadding(new Insets(20));
        ColumnConstraints col1 = new ColumnConstraints(); col1.setMinWidth(120); col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        agregarFila(grid, 0, "DNI *", txtDni);
        agregarFila(grid, 1, "Nombres *", txtNombre);
        agregarFila(grid, 2, "Apellidos *", txtApellido);
        agregarFila(grid, 3, "Cargo *", comboCargo);
        agregarFila(grid, 4, "Área *", comboArea);
        agregarFila(grid, 5, "Gerencia *", comboGerencia);
        agregarFila(grid, 6, "ODPE", comboOdpe);
        agregarFila(grid, 7, "Estado *", comboEstado);
        agregarFila(grid, 8, "N° Contrato", txtNumContrato);
        agregarFila(grid, 9, "Fecha inicio *", dpFechaInicio);
        agregarFila(grid, 10, "Fecha fin", dpFechaFin);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(0, 20, 16, 20));
        botones.getChildren().addAll(lblMsg, new Region(), btnCancelar, btnEliminar, btnGuardar);
        HBox.setHgrow(botones.getChildren().get(1), Priority.ALWAYS);

        VBox root = new VBox(0);
        root.getChildren().addAll(grid, botones);

        btnGuardar.setOnAction(e -> {
            String dni = txtDni.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String numContrato = txtNumContrato.getText().trim();
            
            if (numContrato.isEmpty()) { numContrato = "S/N"; }
            
            String fechaInicio = dpFechaInicio.getValue() != null ? dpFechaInicio.getValue().toString() : "";
            String fechaFin = dpFechaFin.getValue() != null ? dpFechaFin.getValue().toString() : "";
            String[] ca = comboArea.getValue();
            String[] ger = comboGerencia.getValue();
            String estado = comboEstado.getValue();

            if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || fechaInicio.isEmpty()
                    || comboCargo.getValue() == null || ca == null || ger == null || estado == null) {
                setMsg(lblMsg, "⚠ Completa los campos obligatorios (*)", false);
                return;
            }

            int idCargoArea = Integer.parseInt(ca[0]);
            int idGerencia = Integer.parseInt(ger[0]);
            Integer idOdpe = comboOdpe.getValue() != null ? Integer.parseInt(comboOdpe.getValue()[0]) : null;

            String msjError = controlador.procesarGuardado(empleado, numContrato, fechaInicio, fechaFin, dni, nombre, apellido, idCargoArea, idGerencia, idOdpe, estado);

            if (msjError.isEmpty()) {
                cargarTabla();
                modal.close();
            } else {
                setMsg(lblMsg, msjError, false);
            }
        });

        btnEliminar.setOnAction(e -> {
            if (empleado != null) {
                boolean exito = controlador.eliminarEmpleado(Integer.parseInt(empleado[0]));
                if (exito) {
                    cargarTabla();
                    modal.close();
                } else {
                    setMsg(lblMsg, "Error al eliminar de la base de datos", false);
                }
            }
        });

        modal.setScene(new Scene(root, 530, 530));
        modal.showAndWait();
    }

    private void agregarFiltroUI() {
        HBox filaFiltro = new HBox(10);
        filaFiltro.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> comboColumna = new ComboBox<>(FXCollections.observableArrayList(
                "DNI", "Apellidos", "Nombres", "Cargo", "Área", "Estado"));
        comboColumna.setValue("DNI");
        comboColumna.setStyle("-fx-padding: 4;");

        TextField txtValor = campo("Valor a buscar...");

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
        if (paginador == null) return;
        List<String[]> filtrados = datosTabla.stream().filter(row -> {
            for (javafx.scene.Node node : contenedorFiltros.getChildren()) {
                if (!(node instanceof HBox)) continue;
                HBox fila = (HBox) node;
                @SuppressWarnings("unchecked")
                ComboBox<String> combo = (ComboBox<String>) fila.getChildren().get(0);
                TextField txt = (TextField) fila.getChildren().get(1);
                String columna = combo.getValue();
                String filtro = txt.getText().trim().toLowerCase();
                if (filtro.isEmpty()) continue;
                int idx = switch (columna) {
                    case "DNI"      -> 1;
                    case "Nombres"  -> 2;
                    case "Apellidos"-> 3;
                    case "Estado"   -> 4;
                    case "Cargo"    -> 5;
                    case "Área"     -> 6;
                    default -> 1;
                };
                String valor = row[idx] == null ? "" : row[idx].toLowerCase();
                if (!valor.contains(filtro)) return false;
            }
            return true;
        }).collect(java.util.stream.Collectors.toList());
        paginador.setDatos(filtrados);
    }

    private void cargarTabla() {
        datosTabla.setAll(controlador.obtenerTodosLosEmpleados());
        if (paginador != null) paginador.setDatos(datosTabla);
    }

    private void agregarFila(GridPane grid, int fila, String etiqueta, Control control) {
        Label lbl = new Label(etiqueta);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");
        lbl.setMinWidth(120);
        lbl.setPrefWidth(120);
        control.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(control, Priority.ALWAYS);
        grid.add(lbl, 0, fila);
        grid.add(control, 1, fila);
    }

    private TableColumn<String[], String> col(String titulo, double porcentaje, int idx) {
        TableColumn<String[], String> col = new TableColumn<>(titulo);
        col.prefWidthProperty().bind(tabla.widthProperty().multiply(porcentaje));
        col.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[idx]));
        return col;
    }

    private ComboBox<String[]> crearComboSimple(ObservableList<String[]> datos) {
        ComboBox<String[]> combo = new ComboBox<>(datos);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1]);
            }
        });
        combo.setButtonCell(new ListCell<>() {
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1]);
            }
        });
        return combo;
    }

    private ComboBox<String[]> crearCombo(ObservableList<String[]> datos, int idx1, int idx2, String sep) {
        ObservableList<String[]> listaCompleta = FXCollections.observableArrayList(datos);
        ComboBox<String[]> combo = new ComboBox<>(FXCollections.observableArrayList(listaCompleta));
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setEditable(true);
        combo.setVisibleRowCount(10);

        combo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(String[] item) {
                if (item == null) return "";
                return idx2 == -1 ? item[idx1] : item[idx1] + sep + item[idx2];
            }
            public String[] fromString(String s) { return null; }
        });

        combo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(idx2 == -1 ? item[idx1] : item[idx1] + sep + item[idx2]);
            }
        });

        final boolean[] actualizando = {false};
        combo.getEditor().textProperty().addListener((obs, anterior, nuevo) -> {
            if (actualizando[0]) return;
            // Si hay valor seleccionado y coincide con el texto, no filtrar
            String[] valorActual = combo.getValue();
            if (valorActual != null) {
                String textoValor = idx2 == -1 ? valorActual[idx1] : valorActual[idx1] + sep + valorActual[idx2];
                if (textoValor.equals(nuevo)) return;
            }
            // Cuando borra todo, restaurar lista completa y limpiar selección
            if (nuevo == null || nuevo.isEmpty()) {
                actualizando[0] = true;
                combo.setValue(null);
                combo.setItems(FXCollections.observableArrayList(listaCompleta));
                actualizando[0] = false;
                return;
            }
            // Filtrar
            String filtro = nuevo.toLowerCase();
            ObservableList<String[]> filtrados = listaCompleta.stream()
                .filter(item -> {
                    String texto = idx2 == -1 ? item[idx1] : item[idx1] + sep + item[idx2];
                    return texto.toLowerCase().contains(filtro);
                })
                .collect(javafx.collections.FXCollections::observableArrayList,
                         ObservableList::add, ObservableList::addAll);
            actualizando[0] = true;
            combo.setItems(filtrados);
            combo.getEditor().setText(nuevo);
            combo.getEditor().positionCaret(nuevo.length());
            actualizando[0] = false;
            if (!filtrados.isEmpty()) combo.show();
        });

        // Sincronizar listaCompleta cuando cambian los datos externos (ej: áreas por cargo)
        datos.addListener((javafx.collections.ListChangeListener<String[]>) c -> {
            listaCompleta.setAll(datos);
            if (combo.getValue() == null) combo.setItems(FXCollections.observableArrayList(listaCompleta));
        });

        return combo;
    }

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 7;");
        return tf;
    }

    private void setMsg(Label lbl, String texto, boolean exito) {
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (exito ? "#38a169" : "#e53e3e") + ";");
        lbl.setText(texto);
        PauseTransition p = new PauseTransition(Duration.seconds(3));
        p.setOnFinished(e -> lbl.setText(""));
        p.play();
    }
}
