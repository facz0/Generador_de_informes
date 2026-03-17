package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.DAO.CargoAreaDAO;
import com.onpe.genereador_informes.DAO.PersonalDAO;
import com.onpe.genereador_informes.database.Conexion;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDate;

public class EmpleadosView {

    private BorderPane contenedor;
    private PersonalDAO personalDAO = new PersonalDAO();
    private CargoAreaDAO cargoAreaDAO = new CargoAreaDAO();
    private ObservableList<String[]> datosTabla = FXCollections.observableArrayList();
    private TableView<String[]> tabla;

    public EmpleadosView(BorderPane contenedor) {
        this.contenedor = contenedor;
    }

    public void mostrar() {
        contenedor.setTop(DashboardView.crearHeader("Empleados", "Administra los colaboradores del sistema"));
        contenedor.setBottom(null);

        // ===== BARRA SUPERIOR =====
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 24, 8, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por DNI...");
        txtBuscar.setStyle("-fx-padding: 8; -fx-pref-width: 220px;");
        txtBuscar.textProperty().addListener((obs, old, val) -> filtrar(val));

        Button btnNuevo = DashboardView.crearBotonAccion("+ Nuevo Empleado", "#1D2B61");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(txtBuscar, spacer, btnNuevo);

        // ===== TABLA =====
        tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;");
        tabla.setItems(datosTabla);

        tabla.getColumns().addAll(
            col("DNI", 90, 1),
            col("Apellidos", 160, 3),
            col("Nombres", 160, 2),
            col("Cargo", 200, 5),
            col("Gerencia", 160, 7),
            col("N° Contrato", 130, 9),
            col("Estado", 70, 4)
        );

        // Doble click para editar
        tabla.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel != null) abrirFormulario(sel);
            }
        });

        cargarTabla();

        VBox centro = new VBox(0);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        centro.getChildren().addAll(topBar, tabla);
        VBox.setMargin(tabla, new Insets(0, 24, 16, 24));

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

        // Combo de cargos (solo cargos únicos)
        ComboBox<String[]> comboCargo = crearCombo(datosCargosList, 1, -1, "");
        comboCargo.setPromptText("Selecciona un cargo");

        // Combo de áreas filtradas según cargo seleccionado
        ComboBox<String[]> comboArea = crearCombo(datosAreasFiltradas, 1, -1, "");
        comboArea.setPromptText("Primero selecciona un cargo");
        comboArea.setDisable(true);

        // ODPE deshabilitado por defecto
        ComboBox<String[]> comboOdpe = crearCombo(datosOdpe, 1, -1, "");
        comboOdpe.setPromptText("Solo para cargo_area ID 10");
        comboOdpe.setDisable(true);

        // Al seleccionar cargo, cargar áreas asociadas
        comboCargo.setOnAction(e -> {
            String[] cargoSel = comboCargo.getValue();
            comboArea.setValue(null);
            comboOdpe.setValue(null);
            comboOdpe.setDisable(true);
            datosAreasFiltradas.clear();
            if (cargoSel != null) {
                cargarAreasPorCargo(Integer.parseInt(cargoSel[0]), datosAreasFiltradas);
                comboArea.setDisable(false);
                comboArea.setPromptText("Selecciona un área");
            } else {
                comboArea.setDisable(true);
                comboArea.setPromptText("Primero selecciona un cargo");
            }
        });

        // Al seleccionar área, habilitar ODPE solo si id_cargo_area = 10
        comboArea.setOnAction(e -> {
            String[] areaSel = comboArea.getValue();
            comboOdpe.setValue(null);
            if (areaSel != null && areaSel[0].equals("10")) {
                comboOdpe.setDisable(false);
                comboOdpe.setPromptText("Selecciona una ODPE");
            } else {
                comboOdpe.setDisable(true);
                comboOdpe.setPromptText("No aplica para este cargo/área");
            }
        });

        ComboBox<String[]> comboGerencia = crearComboSimple(datosGerencia);
        comboGerencia.setPromptText("Gerencia");

        ComboBox<String> comboEstado = new ComboBox<>(FXCollections.observableArrayList("A", "I"));
        comboEstado.setPromptText("Estado");
        comboEstado.setMaxWidth(Double.MAX_VALUE);

        // Cargar combos
        cargarCombo("SELECT id_cargo, nombre_cargo FROM tb_cargo ORDER BY nombre_cargo", datosCargosList);
        cargarCombo("SELECT id_gerencia, nombre_gerencia FROM tb_gerencia ORDER BY nombre_gerencia", datosGerencia);
        cargarCombo("SELECT id_odpe, nombre_odpe FROM tb_odpe ORDER BY nombre_odpe", datosOdpe);

        // Si es edición, prellenar
        if (empleado != null) {
            txtDni.setText(empleado[1]);
            txtNombre.setText(empleado[2]);
            txtApellido.setText(empleado[3]);
            comboEstado.setValue(empleado[4]);
            txtNumContrato.setText(empleado[9]);
            if (!empleado[10].isEmpty()) dpFechaInicio.setValue(java.time.LocalDate.parse(empleado[10]));
            if (!empleado[11].isEmpty()) dpFechaFin.setValue(java.time.LocalDate.parse(empleado[11]));
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

        // Layout del formulario en 2 columnas
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        agregarFila(grid, 0, "DNI *", txtDni);
        agregarFila(grid, 1, "Nombres *", txtNombre);
        agregarFila(grid, 2, "Apellidos *", txtApellido);
        agregarFila(grid, 3, "Cargo *", comboCargo);
        agregarFila(grid, 4, "Área *", comboArea);
        agregarFila(grid, 5, "Gerencia *", comboGerencia);
        agregarFila(grid, 6, "ODPE", comboOdpe);
        agregarFila(grid, 7, "Estado *", comboEstado);
        agregarFila(grid, 8, "N° Contrato *", txtNumContrato);
        agregarFila(grid, 9, "Fecha inicio *", dpFechaInicio);
        agregarFila(grid, 10, "Fecha fin", dpFechaFin);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(0, 20, 16, 20));
        botones.getChildren().addAll(lblMsg, new Region(), btnCancelar, btnEliminar, btnGuardar);
        HBox.setHgrow(botones.getChildren().get(1), Priority.ALWAYS);

        VBox root = new VBox(0);
        root.getChildren().addAll(grid, botones);

        // Acciones
        btnGuardar.setOnAction(e -> {
            String dni = txtDni.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String numContrato = txtNumContrato.getText().trim();
            String fechaInicio = dpFechaInicio.getValue() != null ? dpFechaInicio.getValue().toString() : "";
            String fechaFin = dpFechaFin.getValue() != null ? dpFechaFin.getValue().toString() : "";
            String[] ca = comboArea.getValue();
            String[] ger = comboGerencia.getValue();
            String estado = comboEstado.getValue();

            if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || numContrato.isEmpty()
                    || fechaInicio.isEmpty() || comboCargo.getValue() == null || ca == null || ger == null || estado == null) {
                setMsg(lblMsg, "⚠ Completa los campos obligatorios (*)", false);
                return;
            }

            int idCargoArea = Integer.parseInt(ca[0]);
            int idGerencia = Integer.parseInt(ger[0]);
            Integer idOdpe = comboOdpe.getValue() != null ? Integer.parseInt(comboOdpe.getValue()[0]) : null;

            boolean ok;
            if (empleado != null) {
                int idPersonal = Integer.parseInt(empleado[0]);
                if (personalDAO.existeDni(dni, idPersonal)) { setMsg(lblMsg, "⚠ El DNI ya está registrado", false); return; }
                ok = personalDAO.actualizar(idPersonal, numContrato, fechaInicio, fechaFin, dni, nombre, apellido, idCargoArea, idGerencia, idOdpe, estado);
            } else {
                if (personalDAO.existeDni(dni, -1)) { setMsg(lblMsg, "⚠ El DNI ya está registrado", false); return; }
                ok = personalDAO.crear(numContrato, fechaInicio, fechaFin, dni, nombre, apellido, idCargoArea, idGerencia, idOdpe, estado);
            }

            if (ok) {
                cargarTabla();
                modal.close();
            } else {
                setMsg(lblMsg, "❌ Error al guardar", false);
            }
        });

        btnEliminar.setOnAction(e -> {
            if (empleado != null && personalDAO.eliminar(Integer.parseInt(empleado[0]))) {
                cargarTabla();
                modal.close();
            }
        });

        modal.setScene(new Scene(root, 520, 520));
        modal.showAndWait();
    }

    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            cargarTabla();
            return;
        }
        ObservableList<String[]> filtrado = FXCollections.observableArrayList();
        for (String[] row : datosTabla) {
            if (row[1].contains(texto)) filtrado.add(row);
        }
        tabla.setItems(filtrado);
    }

    private void cargarTabla() {
        datosTabla.setAll(personalDAO.obtenerTodos());
        tabla.setItems(datosTabla);
    }

    private void cargarCombo(String sql, ObservableList<String[]> lista) {
        lista.clear();
        try {
            Connection conn = Conexion.obtenerConexion();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) lista.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)});
            rs.close(); st.close();
        } catch (SQLException e) {
            System.err.println("Error cargando combo: " + e.getMessage());
        }
    }

    private void agregarFila(GridPane grid, int fila, String etiqueta, Control control) {
        Label lbl = new Label(etiqueta);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");
        lbl.setPrefWidth(120);
        control.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(control, Priority.ALWAYS);
        grid.add(lbl, 0, fila);
        grid.add(control, 1, fila);
    }

    private TableColumn<String[], String> col(String titulo, int ancho, int idx) {
        TableColumn<String[], String> col = new TableColumn<>(titulo);
        col.setPrefWidth(ancho);
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
        ComboBox<String[]> combo = new ComboBox<>(datos);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setEditable(true);

        // Converter para mostrar el texto correcto en el editor
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

        // Autocomplete: filtrar al escribir
        combo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || combo.getValue() != null) return;
            String filtro = newVal.toLowerCase();
            ObservableList<String[]> filtrados = FXCollections.observableArrayList();
            for (String[] item : datos) {
                String texto = idx2 == -1 ? item[idx1] : item[idx1] + sep + item[idx2];
                if (texto.toLowerCase().contains(filtro)) filtrados.add(item);
            }
            combo.setItems(filtrados);
            if (!filtrados.isEmpty()) combo.show();
        });

        combo.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (!focused && combo.getValue() == null) {
                combo.setItems(datos);
                combo.getEditor().clear();
            }
        });

        return combo;
    }

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 7;");
        return tf;
    }

    private void cargarAreasPorCargo(int idCargo, ObservableList<String[]> lista) {
        lista.clear();
        String sql = "SELECT ca.id_cargo_area, a.nombre_area FROM tb_cargo_area ca " +
                "INNER JOIN tb_area a ON ca.id_area = a.id_area WHERE ca.id_cargo = ? ORDER BY a.nombre_area";
        try {
            Connection conn = Conexion.obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCargo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)});
            rs.close(); ps.close();
        } catch (SQLException e) {
            System.err.println("Error cargando áreas por cargo: " + e.getMessage());
        }
    }

    private void setMsg(Label lbl, String texto, boolean exito) {
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (exito ? "#38a169" : "#e53e3e") + ";");
        lbl.setText(texto);
        PauseTransition p = new PauseTransition(Duration.seconds(3));
        p.setOnFinished(e -> lbl.setText(""));
        p.play();
    }
}
