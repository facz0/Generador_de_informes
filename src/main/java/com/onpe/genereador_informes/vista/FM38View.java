package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.DashboardController;
import com.onpe.genereador_informes.model.Contrato;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FM38View {

    private BorderPane contenedor;
    private DashboardController controlador;

    public FM38View(BorderPane contenedor, DashboardController controlador) {
        this.contenedor = contenedor;
        this.controlador = controlador;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Formularios FM38", "Genera los formularios FM38 para los colaboradores seleccionados"));

        ObservableList<Contrato> datos = FXCollections.observableArrayList();
        Map<Contrato, BooleanProperty> seleccion = new HashMap<>();

        // ===== TABLA =====
        TableView<Contrato> tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");

        CheckBox chkTodos = new CheckBox();
        TableColumn<Contrato, Boolean> colCheck = new TableColumn<>();
        colCheck.setGraphic(chkTodos);
        colCheck.setPrefWidth(40);
        colCheck.setCellValueFactory(cell -> {
            seleccion.putIfAbsent(cell.getValue(), new SimpleBooleanProperty(false));
            return seleccion.get(cell.getValue());
        });
        colCheck.setCellFactory(col -> new TableCell<>() {
            private final CheckBox chk = new CheckBox();
            { chk.setOnAction(e -> { Contrato c = getTableView().getItems().get(getIndex()); seleccion.get(c).set(chk.isSelected()); }); }
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                chk.setSelected(item != null && item);
                setGraphic(chk);
            }
        });
        chkTodos.setOnAction(e -> { boolean val = chkTodos.isSelected(); seleccion.values().forEach(p -> p.set(val)); tabla.refresh(); });

        TableColumn<Contrato, String> colNombre = new TableColumn<>("Colaborador");
        colNombre.setPrefWidth(280);
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmpleado().getApellidos() + " " + cell.getValue().getEmpleado().getNombres()));

        TableColumn<Contrato, String> colDni = new TableColumn<>("DNI");
        colDni.setPrefWidth(100);
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmpleado().getDni()));

        TableColumn<Contrato, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setPrefWidth(250);
        colCargo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCargo().getNombreCargo()));

        TableColumn<Contrato, String> colGerencia = new TableColumn<>("Gerencia");
        colGerencia.setPrefWidth(200);
        colGerencia.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getArea().getNombreArea()));

        TableColumn<Contrato, String> colNum = PaginadorTabla.crearColumnaNumero();
        tabla.getColumns().addAll(colNum, colCheck, colNombre, colDni, colCargo, colGerencia);

        List<Contrato> todosLosDatos = controlador.obtenerDatosParaTabla();
        datos.setAll(todosLosDatos);

        PaginadorTabla<Contrato> paginador = new PaginadorTabla<>(tabla, 20);
        paginador.setDatos(todosLosDatos);

        // ===== FILTROS =====
        TextField txtDni = new TextField();
        txtDni.setPromptText("DNI");
        txtDni.setPrefWidth(110);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");
        txtNombre.setPrefWidth(180);

        ComboBox<String> comboCargo = crearComboFiltro(controlador.obtenerNombresCargos(), "Cargo", 200);
        ComboBox<String> comboGerencia = new ComboBox<>(FXCollections.observableArrayList(controlador.obtenerNombresGerencias()));
        comboGerencia.setPromptText("Gerencia");
        comboGerencia.setPrefWidth(180);
        ComboBox<String> comboArea = crearComboFiltro(controlador.obtenerNombresAreas(), "Área", 200);

        Button btnFiltrar = DashboardView.crearBotonAccion("Filtrar", "#1D2B61");
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 7 14; -fx-cursor: hand;");

        HBox filtros = new HBox(10);
        filtros.setPadding(new Insets(0, 0, 6, 0));
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.getChildren().addAll(txtDni, txtNombre, comboCargo, comboGerencia, comboArea);

        HBox filtrosBotones = new HBox(10);
        filtrosBotones.setPadding(new Insets(0, 0, 10, 0));
        filtrosBotones.setAlignment(Pos.CENTER_LEFT);
        filtrosBotones.getChildren().addAll(btnFiltrar, btnLimpiar);

        btnFiltrar.setOnAction(e -> {
            List<Contrato> filtrados = controlador.filtrarContratos(
                txtDni.getText().trim(), txtNombre.getText().trim(),
                comboCargo.getEditor().getText().trim(),
                comboGerencia.getValue() == null ? "" : comboGerencia.getValue(),
                comboArea.getEditor().getText().trim());
            datos.setAll(filtrados);
            paginador.setDatos(filtrados);
        });

        btnLimpiar.setOnAction(e -> {
            txtDni.clear(); txtNombre.clear();
            comboCargo.setValue(null); comboCargo.getEditor().clear();
            comboGerencia.setValue(null);
            comboArea.setValue(null); comboArea.getEditor().clear();
            List<Contrato> todos = controlador.obtenerDatosParaTabla();
            datos.setAll(todos);
            paginador.setDatos(todos);
        });

        VBox centro = new VBox(8);
        centro.setPadding(new Insets(20, 24, 0, 24));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centro.getChildren().addAll(filtros, filtrosBotones, tabla, paginador.getControles());
        contenedor.setCenter(centro);

        // ===== BOTTOM =====
        HBox bottomBar = new HBox(12);
        bottomBar.setPadding(new Insets(12, 24, 12, 24));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");

        Button btnGenerar = DashboardView.crearBotonAccion("Generar FM38", "#2980b9");
        btnGenerar.setOnAction(e -> {
            List<Contrato> seleccionados = datos.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            if (seleccionados.isEmpty()) { DashboardView.mostrarAlerta("⚠ Sin selección", "Selecciona al menos un colaborador."); return; }
            if (controlador.generarFM38Seleccionados(seleccionados))
                DashboardView.mostrarAlerta("✅ FM38 generados", seleccionados.size() + " formularios FM38 generados correctamente.");
        });

        bottomBar.getChildren().add(btnGenerar);
        contenedor.setBottom(bottomBar);
    }

    private ComboBox<String> crearComboFiltro(List<String> opciones, String prompt, int ancho) {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("");
        lista.addAll(opciones);
        ComboBox<String> combo = new ComboBox<>(lista);
        combo.setEditable(true);
        combo.setPromptText(prompt);
        combo.setPrefWidth(ancho);
        combo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(String s) { return s == null ? "" : s; }
            public String fromString(String s) { return s; }
        });
        combo.getEditor().textProperty().addListener((o, a, n) -> {
            if (combo.getValue() != null) return;
            ObservableList<String> filtrados = FXCollections.observableArrayList();
            filtrados.add("");
            lista.stream().filter(s -> !s.isEmpty() && s.toLowerCase().contains(n.toLowerCase())).forEach(filtrados::add);
            combo.setItems(filtrados);
            if (!filtrados.isEmpty()) combo.show();
        });
        return combo;
    }
}
