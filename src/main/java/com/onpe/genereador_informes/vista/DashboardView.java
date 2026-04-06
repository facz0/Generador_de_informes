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

    public void mostrar(Stage stage, String usuario, String gerencia, int idGerencia) {
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
        Button btnSudime = crearBotonMenu("📄 SUDIME");

        Label lblMantenimiento = new Label("ADMINISTRACIÓN");
        lblMantenimiento.setStyle(ESTILO_SECCION);
        lblMantenimiento.setMaxWidth(Double.MAX_VALUE);

        Button btnCargos = crearBotonMenu("🗂️ Cargos y Áreas");
        Button btnEmpleados = crearBotonMenu("👥 Empleados");
        Button btnActividades = crearBotonMenu("📝 Actividades");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Panel usuario abajo con menú desplegable
        // Avatar con iniciales
        String iniciales = usuario.length() >= 2
            ? (usuario.substring(0, 1) + usuario.substring(usuario.indexOf(".") > 0 ? usuario.indexOf(".") + 1 : 1, usuario.indexOf(".") > 0 ? usuario.indexOf(".") + 2 : 2)).toUpperCase()
            : usuario.substring(0, 1).toUpperCase();

        javafx.scene.layout.StackPane avatar = new javafx.scene.layout.StackPane();
        avatar.setMinSize(36, 36);
        avatar.setMaxSize(36, 36);
        avatar.setStyle("-fx-background-color: #2a3d8f; -fx-background-radius: 18;");
        Label lblIniciales = new Label(iniciales);
        lblIniciales.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        avatar.getChildren().add(lblIniciales);

        VBox infoUsuario = new VBox(2);
        Label lblNombreUsuario = new Label(usuario);
        lblNombreUsuario.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label lblGerencia = new Label(gerencia);
        lblGerencia.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 11px;");
        infoUsuario.getChildren().addAll(lblNombreUsuario, lblGerencia);
        HBox.setHgrow(infoUsuario, Priority.ALWAYS);

        HBox panelUsuario = new HBox(10);
        panelUsuario.setAlignment(Pos.CENTER_LEFT);
        panelUsuario.setPadding(new Insets(12, 16, 12, 16));
        panelUsuario.setStyle("-fx-background-color: " + COLOR_SECCION + "; -fx-cursor: hand;");
        panelUsuario.getChildren().addAll(infoUsuario, avatar);

        javafx.scene.control.ContextMenu menuUsuario = new javafx.scene.control.ContextMenu();
        javafx.scene.control.MenuItem itemCerrar = new javafx.scene.control.MenuItem("  Cerrar Sesión");
        itemCerrar.setStyle("-fx-font-size: 13px;");
        menuUsuario.getItems().add(itemCerrar);

        panelUsuario.setOnMouseClicked(e ->
            menuUsuario.show(panelUsuario, e.getScreenX(), e.getScreenY()));

        itemCerrar.setOnAction(e -> {
            stage.setResizable(false);
            new LoginView().mostrar(stage);
        });

        menuLateral.getChildren().addAll(header, lblReportes, btnInformes, btnFM38, btnSudime, lblMantenimiento, btnCargos, btnEmpleados, btnActividades, spacer, panelUsuario);
        root.setLeft(menuLateral);

        contenidoCentral = new BorderPane();
        contenidoCentral.setStyle("-fx-background-color: #f4f6f9;");
        new InformesView(contenidoCentral, controlador, idGerencia).mostrar();
        root.setCenter(contenidoCentral);

        btnInformes.setOnAction(e -> new InformesView(contenidoCentral, controlador, idGerencia).mostrar());
        btnFM38.setOnAction(e -> new FM38View(contenidoCentral, controlador, idGerencia).mostrar());
        btnSudime.setOnAction(e -> new SudimeView(contenidoCentral, controlador, idGerencia).mostrar());
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
        javafx.stage.Stage modal = new javafx.stage.Stage();
        modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        modal.setResizable(false);
        modal.setTitle(titulo);

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d3748;");
        lblMensaje.setMaxWidth(320);

        Button btnOk = new Button("Aceptar");
        btnOk.setStyle("-fx-background-color: #1D2B61; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 24; -fx-background-radius: 6; -fx-cursor: hand;");
        btnOk.setOnAction(e -> modal.close());

        HBox botones = new HBox(btnOk);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(16, 0, 0, 0));

        VBox contenido = new VBox(12, lblMensaje, botones);
        contenido.setPadding(new Insets(24));
        contenido.setStyle("-fx-background-color: white;");

        modal.setScene(new javafx.scene.Scene(contenido));
        modal.showAndWait();
    }

    static boolean mostrarConfirmacion(String titulo, String mensaje) {
        javafx.stage.Stage modal = new javafx.stage.Stage();
        modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        modal.setResizable(false);
        modal.setTitle(titulo);

        final boolean[] resultado = {false};

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d3748;");
        lblMensaje.setMaxWidth(320);

        Button btnSi = new Button("Sí, generar");
        btnSi.setStyle("-fx-background-color: #1D2B61; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnSi.setOnAction(e -> { resultado[0] = true; modal.close(); });

        Button btnNo = new Button("Cancelar");
        btnNo.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8 20; -fx-cursor: hand;");
        btnNo.setOnAction(e -> modal.close());

        HBox botones = new HBox(10, btnNo, btnSi);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(16, 0, 0, 0));

        VBox contenido = new VBox(12, lblMensaje, botones);
        contenido.setPadding(new Insets(24));
        contenido.setStyle("-fx-background-color: white;");

        modal.setScene(new javafx.scene.Scene(contenido));
        modal.showAndWait();
        return resultado[0];
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
