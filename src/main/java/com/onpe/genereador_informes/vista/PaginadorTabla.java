package com.onpe.genereador_informes.vista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.List;

/**
 * Componente reutilizable de paginado para TableView.
 * Uso:
 *   PaginadorTabla<T> paginador = new PaginadorTabla<>(tabla, 20);
 *   paginador.setDatos(lista);
 *   VBox.getChildren().add(paginador.getControles());
 */
public class PaginadorTabla<T> {

    private final TableView<T> tabla;
    private final int tamanioPagina;
    private List<T> todosLosDatos;
    private int paginaActual = 0;

    private final Label lblInfo = new Label();
    private final Button btnAnterior = new Button("◀");
    private final Button btnSiguiente = new Button("▶");
    private final Label lblPagina = new Label();

    public PaginadorTabla(TableView<T> tabla, int tamanioPagina) {
        this.tabla = tabla;
        this.tamanioPagina = tamanioPagina;
        configurarBotones();
    }

    private void configurarBotones() {
        String estiloBtn = "-fx-background-color: #1D2B61; -fx-text-fill: white; -fx-padding: 5 12; -fx-cursor: hand; -fx-background-radius: 4;";
        String estiloBtnDis = "-fx-background-color: #e2e8f0; -fx-text-fill: #a0aec0; -fx-padding: 5 12; -fx-background-radius: 4;";

        btnAnterior.setStyle(estiloBtn);
        btnSiguiente.setStyle(estiloBtn);
        lblInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");
        lblPagina.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D2B61; -fx-font-weight: bold;");

        btnAnterior.setOnAction(e -> { if (paginaActual > 0) { paginaActual--; actualizar(); } });
        btnSiguiente.setOnAction(e -> { if (paginaActual < totalPaginas() - 1) { paginaActual++; actualizar(); } });

        btnAnterior.disableProperty().addListener((o, a, n) -> btnAnterior.setStyle(n ? estiloBtnDis : estiloBtn));
        btnSiguiente.disableProperty().addListener((o, a, n) -> btnSiguiente.setStyle(n ? estiloBtnDis : estiloBtn));
    }

    public void setDatos(List<T> datos) {
        this.todosLosDatos = datos;
        this.paginaActual = 0;
        actualizar();
    }

    private void actualizar() {
        if (todosLosDatos == null || todosLosDatos.isEmpty()) {
            tabla.setItems(FXCollections.observableArrayList());
            lblInfo.setText("Sin resultados");
            lblPagina.setText("");
            btnAnterior.setDisable(true);
            btnSiguiente.setDisable(true);
            return;
        }

        int inicio = paginaActual * tamanioPagina;
        int fin = Math.min(inicio + tamanioPagina, todosLosDatos.size());
        ObservableList<T> pagina = FXCollections.observableArrayList(todosLosDatos.subList(inicio, fin));
        tabla.setItems(pagina);
        tabla.refresh();

        lblInfo.setText("Mostrando " + (inicio + 1) + " - " + fin + " de " + todosLosDatos.size());
        lblPagina.setText("Página " + (paginaActual + 1) + " / " + totalPaginas());
        btnAnterior.setDisable(paginaActual == 0);
        btnSiguiente.setDisable(paginaActual >= totalPaginas() - 1);
    }

    private int totalPaginas() {
        if (todosLosDatos == null || todosLosDatos.isEmpty()) return 1;
        return (int) Math.ceil((double) todosLosDatos.size() / tamanioPagina);
    }

    public HBox getControles() {
        HBox controles = new HBox(10);
        controles.setPadding(new Insets(8, 0, 0, 0));
        controles.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        controles.getChildren().addAll(lblInfo, spacer, btnAnterior, lblPagina, btnSiguiente);
        return controles;
    }

    // Columna de numeración reutilizable
    public static <T> TableColumn<T, String> crearColumnaNumero() {
        TableColumn<T, String> colNum = new TableColumn<>("#");
        colNum.setPrefWidth(45);
        colNum.setMinWidth(45);
        colNum.setMaxWidth(45);
        colNum.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        return colNum;
    }
}
