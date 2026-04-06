package com.onpe.genereador_informes.vista;

import com.onpe.genereador_informes.DAO.UsuarioDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RegistroView {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void mostrar(Stage stage) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(32));
        card.setMaxWidth(380);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        Label lblTitulo = new Label("Crear cuenta");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1D2B61;");

        Label lblSub = new Label("Completa los datos para registrarte");
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

        Separator sep = new Separator();

        TextField txtNombres   = campo("Nombres");
        TextField txtApellidos = campo("Apellidos");
        TextField txtUsuario   = campo("Nombre de usuario");
        PasswordField txtPassword  = new PasswordField();
        txtPassword.setPromptText("Contraseña");
        txtPassword.setStyle("-fx-padding: 9; -fx-font-size: 13px;");
        PasswordField txtConfirm = new PasswordField();
        txtConfirm.setPromptText("Confirmar contraseña");
        txtConfirm.setStyle("-fx-padding: 9; -fx-font-size: 13px;");

        ComboBox<String> comboPerfil = new ComboBox<>();
        comboPerfil.getItems().addAll("GGE", "SGPE");
        comboPerfil.setPromptText("Selecciona un perfil");
        comboPerfil.setMaxWidth(Double.MAX_VALUE);

        Label lblMsg = new Label("");
        lblMsg.setStyle("-fx-font-size: 11px;");
        lblMsg.setWrapText(true);

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setStyle("-fx-background-color: #1D2B61; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6; -fx-cursor: hand;");

        Button btnVolver = new Button("← Volver al login");
        btnVolver.setMaxWidth(Double.MAX_VALUE);
        btnVolver.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
        btnVolver.setOnAction(e -> new LoginView().mostrar(stage));

        card.getChildren().addAll(
            lblTitulo, lblSub, sep,
            etiqueta("Nombres *"), txtNombres,
            etiqueta("Apellidos *"), txtApellidos,
            etiqueta("Usuario *"), txtUsuario,
            etiqueta("Contraseña *"), txtPassword,
            etiqueta("Confirmar contraseña *"), txtConfirm,
            etiqueta("Perfil *"), comboPerfil,
            lblMsg, btnRegistrar, btnVolver
        );

        btnRegistrar.setOnAction(e -> {
            String nombres   = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String usuario   = txtUsuario.getText().trim();
            String pass      = txtPassword.getText();
            String confirm   = txtConfirm.getText();
            String perfilStr = comboPerfil.getValue();

            if (nombres.isEmpty() || apellidos.isEmpty() || usuario.isEmpty() || pass.isEmpty() || perfilStr == null) {
                setMsg(lblMsg, "⚠ Completa todos los campos", false); return;
            }
            if (!pass.equals(confirm)) {
                setMsg(lblMsg, "⚠ Las contraseñas no coinciden", false); return;
            }
            if (usuarioDAO.existeUsuario(usuario)) {
                setMsg(lblMsg, "⚠ El usuario ya existe", false); return;
            }
            int perfil = switch (perfilStr) { case "GGE" -> 2; default -> 3; };
            if (usuarioDAO.crear(nombres, apellidos, usuario, pass, perfil)) {
                DashboardView.mostrarAlerta("✅ Registro exitoso", "Usuario creado correctamente. Ahora puedes iniciar sesión.");
                new LoginView().mostrar(stage);
            } else {
                setMsg(lblMsg, "❌ Error al crear el usuario", false);
            }
        });

        ScrollPane scroll = new ScrollPane(card);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f4f6f9; -fx-background-color: #f4f6f9;");
        scroll.setPadding(new Insets(20));

        BorderPane layout = new BorderPane(scroll);
        layout.setStyle("-fx-background-color: #f4f6f9;");

        stage.setScene(new Scene(layout, 460, 620));
        stage.setTitle("Registro de usuario");
        stage.centerOnScreen();
        stage.show();
    }

    private Label etiqueta(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");
        return lbl;
    }

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 9; -fx-font-size: 13px;");
        return tf;
    }

    private void setMsg(Label lbl, String texto, boolean exito) {
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (exito ? "#38a169" : "#e53e3e") + ";");
        lbl.setText(texto);
    }
}
