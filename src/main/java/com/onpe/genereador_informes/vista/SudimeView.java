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

public class SudimeView {

    private static final int ID_CARGO_AREA_SUDIME = 58;

    private BorderPane contenedor;
    private DashboardController controlador;

    public SudimeView(BorderPane contenedor, DashboardController controlador) {
        this.contenedor = contenedor;
        this.controlador = controlador;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("SUDIME", "Supervisores de Distribución de Material Electoral"));

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

        TableColumn<Contrato, String> colNum = PaginadorTabla.crearColumnaNumero();

        TableColumn<Contrato, String> colNombre = new TableColumn<>("Colaborador");
        colNombre.setPrefWidth(280);
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getApellido() + " " + cell.getValue().getPersonal().getNombre()));

        TableColumn<Contrato, String> colDni = new TableColumn<>("DNI");
        colDni.setPrefWidth(100);
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonal().getDni()));

        TableColumn<Contrato, String> colContrato = new TableColumn<>("N° Contrato");
        colContrato.setPrefWidth(220);
        colContrato.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroContrato()));

        TableColumn<Contrato, String> colOdpe = new TableColumn<>("ODPE");
        colOdpe.setPrefWidth(200);
        colOdpe.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getPersonal().getOdpe() != null ? cell.getValue().getPersonal().getOdpe().getNombreOdpe() : ""));

        tabla.getColumns().addAll(colNum, colCheck, colNombre, colDni, colContrato, colOdpe);

        List<Contrato> listaSudime = controlador.obtenerContratosPorCargoArea(ID_CARGO_AREA_SUDIME);
        datos.setAll(listaSudime);

        PaginadorTabla<Contrato> paginador = new PaginadorTabla<>(tabla, 20);
        paginador.setDatos(listaSudime);

        // ===== FILTROS =====
        TextField txtDni = new TextField();
        txtDni.setPromptText("DNI");
        txtDni.setPrefWidth(110);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");
        txtNombre.setPrefWidth(180);

        ComboBox<String> comboOdpe = crearComboFiltro(controlador.obtenerNombresOdpesSudime(), "ODPE", 220);

        Button btnFiltrar = DashboardView.crearBotonAccion("Filtrar", "#1D2B61");
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 7 14; -fx-cursor: hand;");

        HBox filtros = new HBox(10);
        filtros.setPadding(new Insets(0, 0, 6, 0));
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.getChildren().addAll(txtDni, txtNombre, comboOdpe);

        HBox filtrosBotones = new HBox(10);
        filtrosBotones.setPadding(new Insets(0, 0, 10, 0));
        filtrosBotones.setAlignment(Pos.CENTER_LEFT);
        filtrosBotones.getChildren().addAll(btnFiltrar, btnLimpiar);

        btnFiltrar.setOnAction(e -> {
            String dniVal = txtDni.getText().trim().toLowerCase();
            String nombreVal = txtNombre.getText().trim().toLowerCase();
            String odpeVal = comboOdpe.getValue() != null ? comboOdpe.getValue().toLowerCase() : comboOdpe.getEditor().getText().trim().toLowerCase();
            List<Contrato> filtrados = listaSudime.stream().filter(c ->
                (dniVal.isEmpty() || c.getPersonal().getDni().toLowerCase().contains(dniVal))
                && (nombreVal.isEmpty() || (c.getPersonal().getNombre() + " " + c.getPersonal().getApellido()).toLowerCase().contains(nombreVal))
                && (odpeVal.isEmpty() || (c.getPersonal().getOdpe() != null && c.getPersonal().getOdpe().getNombreOdpe().toLowerCase().contains(odpeVal)))
            ).collect(java.util.stream.Collectors.toList());
            datos.setAll(filtrados);
            paginador.setDatos(filtrados);
        });

        btnLimpiar.setOnAction(e -> {
            txtDni.clear(); txtNombre.clear();
            comboOdpe.setValue(null); comboOdpe.getEditor().clear();
            comboOdpe.setItems(FXCollections.observableArrayList(controlador.obtenerNombresOdpesSudime()));
            datos.setAll(listaSudime);
            paginador.setDatos(listaSudime);
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

        Button btnInforme = DashboardView.crearBotonAccion("Generar Informe", "#27ae60");
        btnInforme.setOnAction(e -> {
            List<Contrato> seleccionados = datos.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            boolean confirmar = DashboardView.mostrarConfirmacion(
                "Confirmar generación",
                seleccionados.isEmpty()
                    ? "¿Generar informes para todos los SUDIME?"
                    : "¿Generar informes para " + seleccionados.size() + " SUDIME seleccionado(s)?");
            if (!confirmar) return;
            boolean ok = controlador.generarInformesSudime(seleccionados.isEmpty() ? null : seleccionados);
            if (ok) DashboardView.mostrarAlerta("✅ Informes generados", "Informes SUDIME generados correctamente.");
            else DashboardView.mostrarAlerta("⚠ Sin datos", "No hay contratos SUDIME para generar.");
        });

        Button btnFM38 = DashboardView.crearBotonAccion("Generar FM38", "#2980b9");
        btnFM38.setOnAction(e -> {
            List<Contrato> seleccionados = datos.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            boolean confirmar = DashboardView.mostrarConfirmacion(
                "Confirmar generación",
                seleccionados.isEmpty()
                    ? "¿Generar FM38 para todos los SUDIME?"
                    : "¿Generar FM38 para " + seleccionados.size() + " SUDIME seleccionado(s)?");
            if (!confirmar) return;
            boolean ok = controlador.generarFM38Sudime(seleccionados.isEmpty() ? null : seleccionados);
            if (ok) DashboardView.mostrarAlerta("✅ FM38 generados", "Formularios FM38 SUDIME generados correctamente.");
            else DashboardView.mostrarAlerta("⚠ Sin datos", "No hay contratos SUDIME para generar.");
        });

        bottomBar.getChildren().addAll(btnInforme, btnFM38);
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
            String valorActual = combo.getValue();
            if (valorActual != null && valorActual.equals(nuevo)) return;
            if (nuevo == null || nuevo.isEmpty()) {
                actualizando[0] = true;
                combo.setValue(null);
                combo.setItems(FXCollections.observableArrayList(listaCompleta));
                actualizando[0] = false;
                return;
            }
            ObservableList<String> filtrados = listaCompleta.stream()
                .filter(s -> s.toLowerCase().contains(nuevo.toLowerCase()))
                .collect(java.util.stream.Collectors.toCollection(FXCollections::observableArrayList));
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
