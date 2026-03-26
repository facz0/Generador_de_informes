package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.controlador.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardView {
    private DashboardController controlador;
    private BorderPane contenidoCentral;

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
        Button btnSudime = crearBotonMenu(" SUDIME");

        Label lblMantenimiento = new Label("MANTENIMIENTO");
        lblMantenimiento.setStyle(ESTILO_SECCION);
        lblMantenimiento.setMaxWidth(Double.MAX_VALUE);

        Button btnCargos = crearBotonMenu(" Cargos y Áreas");
        Button btnEmpleados = crearBotonMenu(" Empleados");
        Button btnActividades = crearBotonMenu("Actividades");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnCerrar = new Button("Cerrar Sesión");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10; -fx-cursor: hand;");
        VBox.setMargin(btnCerrar, new Insets(0, 16, 16, 16));

        menuLateral.getChildren().addAll(header, lblReportes, btnInformes, btnFM38, btnSudime, lblMantenimiento, btnCargos, btnEmpleados, btnActividades, spacer, btnCerrar);
        root.setLeft(menuLateral);

        contenidoCentral = new BorderPane();
        contenidoCentral.setStyle("-fx-background-color: #f4f6f9;");
        new InformesView(contenidoCentral, controlador).mostrar();
        root.setCenter(contenidoCentral);

        btnCerrar.setOnAction(e -> stage.close());
        btnInformes.setOnAction(e -> new InformesView(contenidoCentral, controlador).mostrar());
        btnFM38.setOnAction(e -> new FM38View(contenidoCentral, controlador).mostrar());
        btnSudime.setOnAction(e -> new SudimeView(contenidoCentral, controlador).mostrar());
        btnCargos.setOnAction(e -> new CargosView(contenidoCentral).mostrar());
        btnEmpleados.setOnAction(e -> new EmpleadosView(contenidoCentral).mostrar());
        btnActividades.setOnAction(e -> new ActividadesView(contenidoCentral).mostrar());

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("Generador de Informes - GGE");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
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

    static void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnSi, btnNo);
        alert.getDialogPane().lookupAll(".button-bar").forEach(node -> {
            if (node instanceof ButtonBar) {
                ((ButtonBar) node).setButtonMinWidth(60);
                ((ButtonBar) node).setPadding(new Insets(8, 12, 8, 12));
            }
        });
        return alert.showAndWait().orElse(btnNo) == btnSi;
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
