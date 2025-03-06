package com.mycompany.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.File;
import java.io.IOException;
import com.mycompany.gui.models.Usuario;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class AdminController {

    @FXML
    private Label nombreAdminLabel;

    @FXML
    private Label areaLabel;

    @FXML
    private VBox zonaArrastre;

    @FXML
    private ListView<String> listaArchivos;

    @FXML
    private ListView<String> listaTablas;

    @FXML
    private ProgressBar progresoIntegracion;

    @FXML
    private Button botonIntegrar;

    private Usuario usuario;
    private final ObservableList<String> archivos = FXCollections.observableArrayList();
    private final ObservableList<String> tablas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listaArchivos.setItems(archivos);
        listaTablas.setItems(tablas);
        configurarArrastreArchivos();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        if (usuario != null) {
            nombreAdminLabel.setText(usuario.getNombre() + " " + usuario.getApellido());
            areaLabel.setText("Área: " + usuario.getArea());
        } else {
            nombreAdminLabel.setText("No se encontró el usuario");
        }
    }

    private void configurarArrastreArchivos() {
        zonaArrastre.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        zonaArrastre.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    archivos.add(file.getName()); // Guarda solo el nombre del archivo
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    @FXML
    private void iniciarIntegracion() {
        if (archivos.isEmpty()) {
            mostrarAlerta("Error", "No hay archivos para integrar.");
            return;
        }

        progresoIntegracion.setProgress(0.1);

        // Simulación de carga de tablas (se reemplazará con lógica real)
        tablas.setAll("tabla_usuarios", "tabla_ventas", "tabla_productos");

        progresoIntegracion.setProgress(1.0);
    }

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nombreAdminLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirConexionBD() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/view/DatabaseConfig.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Conectar a Base de Datos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre esta
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
