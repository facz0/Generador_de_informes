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
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmpleado().getApellidos() + " " + cell.getValue().getEmpleado().getNombres()));

        TableColumn<Contrato, String> colDni = new TableColumn<>("DNI");
        colDni.setPrefWidth(100);
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmpleado().getDni()));

        TableColumn<Contrato, String> colContrato = new TableColumn<>("N° Contrato");
        colContrato.setPrefWidth(220);
        colContrato.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroContrato()));

        TableColumn<Contrato, String> colOdpe = new TableColumn<>("ODPE");
        colOdpe.setPrefWidth(200);
        colOdpe.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getEmpleado().getOdpe() != null ? cell.getValue().getEmpleado().getOdpe().getNombreOdpe() : ""));

        tabla.getColumns().addAll(colNum, colCheck, colNombre, colDni, colContrato, colOdpe);

        List<Contrato> listaSudime = controlador.obtenerContratosPorCargoArea(ID_CARGO_AREA_SUDIME);
        datos.setAll(listaSudime);

        PaginadorTabla<Contrato> paginador = new PaginadorTabla<>(tabla, 20);
        paginador.setDatos(listaSudime);

        VBox centro = new VBox(8);
        centro.setPadding(new Insets(20, 24, 0, 24));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centro.getChildren().addAll(tabla, paginador.getControles());
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
            if (seleccionados.isEmpty()) { DashboardView.mostrarAlerta("⚠ Sin selección", "Selecciona al menos un colaborador."); return; }
            if (controlador.generarInformesSudime(seleccionados))
                DashboardView.mostrarAlerta("✅ Informes generados", seleccionados.size() + " informes SUDIME generados.");
        });

        Button btnFM38 = DashboardView.crearBotonAccion("Generar FM38", "#2980b9");
        btnFM38.setOnAction(e -> {
            List<Contrato> seleccionados = datos.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            if (seleccionados.isEmpty()) { DashboardView.mostrarAlerta("⚠ Sin selección", "Selecciona al menos un colaborador."); return; }
            if (controlador.generarFM38Sudime(seleccionados))
                DashboardView.mostrarAlerta("✅ FM38 generados", seleccionados.size() + " formularios FM38 SUDIME generados.");
        });

        bottomBar.getChildren().addAll(btnInforme, btnFM38);
        contenedor.setBottom(bottomBar);
    }
}
