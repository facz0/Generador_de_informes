package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.DAO.UsuarioDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {

    // Credenciales desde BD
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void mostrar(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f6f9;");
        root.setMaxWidth(400);

        // Card
        VBox card = new VBox(16);
        card.setPadding(new Insets(32));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        card.setMaxWidth(360);

        Label lblTitulo = new Label("GGE");
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");
        lblTitulo.setAlignment(Pos.CENTER);
        lblTitulo.setMaxWidth(Double.MAX_VALUE);

        Label lblSubtitulo = new Label("Generador de Informes");
        lblSubtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");
        lblSubtitulo.setAlignment(Pos.CENTER);
        lblSubtitulo.setMaxWidth(Double.MAX_VALUE);

        Separator sep = new Separator();

        Label lblUsuario = new Label("Usuario");
        lblUsuario.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");

        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Ingresa tu usuario");
        txtUsuario.setStyle("-fx-padding: 9; -fx-font-size: 13px;");

        Label lblContrasena = new Label("Contraseña");
        lblContrasena.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");

        PasswordField txtContrasena = new PasswordField();
        txtContrasena.setPromptText("Ingresa tu contraseña");
        txtContrasena.setStyle("-fx-padding: 9; -fx-font-size: 13px;");

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 11px;");

        Button btnIngresar = new Button("Ingresar");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setStyle("-fx-background-color: #1D2B61; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6; -fx-cursor: hand;");

        Button btnRegistrarse = new Button("¿No tienes cuenta? Regístrate");
        btnRegistrarse.setMaxWidth(Double.MAX_VALUE);
        btnRegistrarse.setStyle("-fx-background-color: transparent; -fx-text-fill: #1D2B61; -fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true;");
        btnRegistrarse.setOnAction(e -> new RegistroView().mostrar(stage));

        card.getChildren().addAll(lblTitulo, lblSubtitulo, sep, lblUsuario, txtUsuario, lblContrasena, txtContrasena, lblError, btnIngresar, btnRegistrarse);

        StackPane centrado = new StackPane(card);
        centrado.setAlignment(Pos.CENTER);
        StackPane.setMargin(card, new Insets(20));

        BorderPane layout = new BorderPane(centrado);
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Runnable accionLogin = () -> {
            String usuario = txtUsuario.getText().trim();
            String contrasena = txtContrasena.getText();
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                lblError.setText("⚠ Ingresa usuario y contraseña");
                return;
            }
            // Verificar conexión a BD
            if (com.onpe.genereador_informes.database.Conexion.obtenerConexion() == null) {
                lblError.setText("❌ No se pudo conectar a la base de datos");
                return;
            }
            String[] datos = usuarioDAO.autenticar(usuario, contrasena);
            if (datos != null) {
                String nombreCompleto = datos[0];
                String perfil = datos[1];
                int idGerencia = Integer.parseInt(datos[2]);
                stage.setResizable(true);
                stage.setMaximized(false);
                new DashboardView().mostrar(stage, nombreCompleto, perfil, idGerencia);
                stage.setMaximized(true);
            } else {
                lblError.setText("⚠ Usuario o contraseña incorrectos");
                txtContrasena.clear();
            }
        };

        btnIngresar.setOnAction(e -> accionLogin.run());
        txtContrasena.setOnAction(e -> accionLogin.run());
        txtUsuario.setOnAction(e -> txtContrasena.requestFocus());

        Scene scene = new Scene(layout, 420, 480);
        stage.setTitle("Generador de Informes - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}
