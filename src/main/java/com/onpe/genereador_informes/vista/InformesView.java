package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.DashboardController;
import com.onpe.genereador_informes.model.Contrato;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
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

public class InformesView {

    private BorderPane contenedor;
    private DashboardController controlador;

    public InformesView(BorderPane contenedor, DashboardController controlador) {
        this.contenedor = contenedor;
        this.controlador = controlador;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Informes de Actividades", "Genera los informes de actividades para los colaboradores seleccionados"));

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
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getApellido() + " " + cell.getValue().getPersonal().getNombre()));

        TableColumn<Contrato, String> colDni = new TableColumn<>("DNI");
        colDni.setPrefWidth(100);
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getDni()));

        TableColumn<Contrato, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setPrefWidth(250);
        colCargo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getCargoArea().getCargo().getNombreCargo()));

        TableColumn<Contrato, String> colGerencia = new TableColumn<>("Gerencia");
        colGerencia.setPrefWidth(200);
        colGerencia.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getGerencia().getNombreGerencia()));

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
                comboCargo.getValue() != null ? comboCargo.getValue() : comboCargo.getEditor().getText().trim(),
                comboGerencia.getValue() == null ? "" : comboGerencia.getValue(),
                comboArea.getValue() != null ? comboArea.getValue() : comboArea.getEditor().getText().trim());
            datos.setAll(filtrados);
            paginador.setDatos(filtrados);
        });

        btnLimpiar.setOnAction(e -> {
            txtDni.clear(); txtNombre.clear();
            comboCargo.setValue(null); comboCargo.getEditor().clear();
            comboGerencia.setValue(null);
            comboArea.setValue(null); comboArea.getEditor().clear();
            comboCargo.setItems(FXCollections.observableArrayList(controlador.obtenerNombresCargos()));
            comboArea.setItems(FXCollections.observableArrayList(controlador.obtenerNombresAreas()));
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

        Button btnGenerar = DashboardView.crearBotonAccion("Generar Informes", "#27ae60");
        btnGenerar.setOnAction(e -> {
            List<Contrato> seleccionados = datos.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            boolean confirmar = DashboardView.mostrarConfirmacion(
                "Confirmar generación",
                seleccionados.isEmpty()
                    ? "¿Generar informes para todos los colaboradores?"
                    : "¿Generar informes para " + seleccionados.size() + " colaborador(es) seleccionado(s)?");
            if (!confirmar) return;
            boolean ok = controlador.generarInformes(seleccionados.isEmpty() ? null : seleccionados);
            if (ok) DashboardView.mostrarAlerta("✅ Informes generados", "Informes generados correctamente.");
            else DashboardView.mostrarAlerta("⚠ Sin datos", "No hay contratos para generar.");
        });

        bottomBar.getChildren().add(btnGenerar);
        contenedor.setBottom(bottomBar);
    }

    private ComboBox<String> crearComboFiltro(List<String> opciones, String prompt, int ancho) {
        ObservableList<String> listaCompleta = FXCollections.observableArrayList(opciones);
        ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList(listaCompleta));
        combo.setEditable(true);
        combo.setPromptText(prompt);
        combo.setPrefWidth(ancho);
        combo.setVisibleRowCount(10);
        combo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(String s) { return s == null ? "" : s; }
            public String fromString(String s) { return s; }
        });
        final boolean[] actualizando = {false};
        combo.getEditor().textProperty().addListener((obs, anterior, nuevo) -> {
            if (actualizando[0]) return;
            // Si el usuario acaba de seleccionar un item, el valor del combo coincide con el texto — no filtrar
            String valorActual = combo.getValue();
            if (valorActual != null && valorActual.equals(nuevo)) return;
            // Cuando borra todo, limpiar la selección para que no quede pegado
            if (nuevo == null || nuevo.isEmpty()) {
                actualizando[0] = true;
                combo.setValue(null);
                combo.setItems(FXCollections.observableArrayList(listaCompleta));
                actualizando[0] = false;
                return;
            }
            // Filtrar según lo escrito
            ObservableList<String> filtrados = listaCompleta.stream()
                .filter(s -> s.toLowerCase().contains(nuevo.toLowerCase()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            actualizando[0] = true;
            combo.setItems(filtrados);
            combo.getEditor().setText(nuevo);
            combo.getEditor().positionCaret(nuevo.length());
            actualizando[0] = false;
            if (!filtrados.isEmpty()) combo.show();
        });
        return combo;
    }
}
