package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.DashboardController;
import com.onpe.genereador_informes.model.Contrato;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    private int idGerencia;

    private ObservableList<Contrato> datosTabla = FXCollections.observableArrayList();
    private FilteredList<Contrato> filteredData;
    private VBox contenedorFiltros = new VBox(6);
    private Map<Contrato, BooleanProperty> seleccion = new HashMap<>();
    private List<Contrato> datosVisibles = new java.util.ArrayList<>();

    public SudimeView(BorderPane contenedor, DashboardController controlador, int idGerencia) {
        this.contenedor = contenedor;
        this.controlador = controlador;
        this.idGerencia = idGerencia;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Supervisores de Distribución de Material Electoral", "SUDIME"));

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
        chkTodos.setOnAction(e -> {
            boolean val = chkTodos.isSelected();
            datosVisibles.forEach(c -> { seleccion.putIfAbsent(c, new SimpleBooleanProperty(false)); seleccion.get(c).set(val); });
            tabla.refresh();
        });

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

        List<Contrato> listaSudime = controlador.obtenerContratosPorCargoArea(ID_CARGO_AREA_SUDIME).stream()
            .filter(c -> idGerencia == 0 || c.getPersonal().getGerencia().getIdGerencia() == idGerencia)
            .collect(java.util.stream.Collectors.toList());
        datosTabla.setAll(listaSudime);
        datosVisibles = new java.util.ArrayList<>(listaSudime);

        PaginadorTabla<Contrato> paginador = new PaginadorTabla<>(tabla, 35);

        TableColumn<Contrato, String> colNum = paginador.crearColumnaNumeroConOffset();
        tabla.getColumns().addAll(colNum, colCheck, colNombre, colDni, colContrato, colOdpe);

        paginador.setDatos(listaSudime);

        // ===== BARRA SUPERIOR CON FILTROS =====
        contenedorFiltros.setPadding(new Insets(0, 24, 8, 24));

        Button btnAgregarFiltro = DashboardView.crearBotonAccion("+ Agregar Filtro", "#2980b9");
        btnAgregarFiltro.setOnAction(e -> agregarFiltroUI(paginador));

        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 24, 8, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().add(btnAgregarFiltro);

        VBox centro = new VBox(0);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        VBox.setMargin(tabla, new Insets(0, 24, 0, 24));
        VBox.setMargin(paginador.getControles(), new Insets(0, 24, 8, 24));
        centro.getChildren().addAll(topBar, contenedorFiltros, tabla, paginador.getControles());
        contenedor.setCenter(centro);

        // ===== BOTTOM =====
        HBox bottomBar = new HBox(12);
        bottomBar.setPadding(new Insets(12, 24, 12, 24));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");

        Button btnInforme = DashboardView.crearBotonAccion("Generar Informe", "#27ae60");
        btnInforme.setOnAction(e -> {
            List<Contrato> seleccionados = datosVisibles.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            boolean confirmar = DashboardView.mostrarConfirmacion(
                "Confirmar generación",
                seleccionados.isEmpty()
                    ? "¿Generar informes para todos los SUDIME visibles (" + datosVisibles.size() + ")?"
                    : "¿Generar informes para " + seleccionados.size() + " SUDIME seleccionado(s)?");
            if (!confirmar) return;
            List<Contrato> lista = seleccionados.isEmpty() ? null : seleccionados;
            DashboardView.ejecutarTareaConCarga(
                "Generando Informe SUDIME",
                () -> controlador.generarInformesSudime(lista),
                () -> DashboardView.mostrarAlerta("✅ Informes generados", "Informes SUDIME generados correctamente."),
                () -> DashboardView.mostrarAlerta("❌ Error", "No se pudo generar el informe SUDIME. Cierra el PDF si está abierto e intenta de nuevo.")
            );
        });

        Button btnFM38 = DashboardView.crearBotonAccion("Generar FM38", "#2980b9");
        btnFM38.setOnAction(e -> {
            List<Contrato> seleccionados = datosVisibles.stream()
                .filter(c -> seleccion.containsKey(c) && seleccion.get(c).get())
                .collect(Collectors.toList());
            boolean confirmar = DashboardView.mostrarConfirmacion(
                "Confirmar generación",
                seleccionados.isEmpty()
                    ? "¿Generar FM38 para todos los SUDIME visibles (" + datosVisibles.size() + ")?"
                    : "¿Generar FM38 para " + seleccionados.size() + " SUDIME seleccionado(s)?");
            if (!confirmar) return;
            List<Contrato> lista = seleccionados.isEmpty() ? null : seleccionados;
            DashboardView.ejecutarTareaConCarga(
                "Generando FM38 SUDIME",
                () -> controlador.generarFM38Sudime(lista),
                () -> DashboardView.mostrarAlerta("✅ FM38 generados", "Formularios FM38 SUDIME generados correctamente."),
                () -> DashboardView.mostrarAlerta("❌ Error", "No se pudo generar el FM38 SUDIME. Cierra el PDF si está abierto e intenta de nuevo.")
            );
        });

        bottomBar.getChildren().addAll(btnInforme, btnFM38);
        contenedor.setBottom(bottomBar);
    }

    private void agregarFiltroUI(PaginadorTabla<Contrato> paginador) {
        HBox filaFiltro = new HBox(10);
        filaFiltro.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> comboColumna = new ComboBox<>(FXCollections.observableArrayList(
                "DNI", "Nombre", "ODPE"));
        comboColumna.setValue("DNI");
        comboColumna.setStyle("-fx-padding: 4;");

        TextField txtValor = new TextField();
        txtValor.setPromptText("Valor a buscar...");
        txtValor.setStyle("-fx-padding: 7;");

        Button btnEliminar = new Button("✕");
        btnEliminar.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4;");

        filaFiltro.getChildren().addAll(comboColumna, txtValor, btnEliminar);
        contenedorFiltros.getChildren().add(filaFiltro);

        btnEliminar.setOnAction(e -> { contenedorFiltros.getChildren().remove(filaFiltro); aplicarFiltros(paginador); });
        comboColumna.setOnAction(e -> aplicarFiltros(paginador));
        txtValor.textProperty().addListener((obs, o, n) -> aplicarFiltros(paginador));
    }

    private void aplicarFiltros(PaginadorTabla<Contrato> paginador) {
        List<Contrato> filtrados = datosTabla.stream().filter(c -> {
            for (javafx.scene.Node node : contenedorFiltros.getChildren()) {
                if (!(node instanceof HBox)) continue;
                HBox fila = (HBox) node;
                @SuppressWarnings("unchecked")
                ComboBox<String> combo = (ComboBox<String>) fila.getChildren().get(0);
                TextField txt = (TextField) fila.getChildren().get(1);
                String filtro = txt.getText().trim().toLowerCase();
                if (filtro.isEmpty()) continue;
                String valor = switch (combo.getValue()) {
                    case "DNI"    -> c.getPersonal().getDni();
                    case "Nombre" -> c.getPersonal().getNombre() + " " + c.getPersonal().getApellido();
                    case "ODPE"   -> c.getPersonal().getOdpe() != null ? c.getPersonal().getOdpe().getNombreOdpe() : "";
                    default -> "";
                };
                if (!valor.toLowerCase().contains(filtro)) return false;
            }
            return true;
        }).collect(java.util.stream.Collectors.toList());
        datosVisibles = filtrados;
        paginador.setDatos(filtrados);
    }
}
