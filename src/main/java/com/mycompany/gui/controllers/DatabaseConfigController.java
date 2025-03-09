package com.mycompany.gui.controllers;

import com.mycompany.database.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConfigController {

    @FXML
    private ComboBox<String> tipoBDComboBox;
    @FXML
    private TextField hostField, puertoField, usuarioField, nombreBDField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label labelBD;
    @FXML
    private Button guardarButton, eliminarButton;
    @FXML
    private VBox configContainer, zonaArrastre, zonaArrastre2;

    private DatabaseConfigHandler configHandler;

    @FXML
    private void initialize() {
        if (zonaArrastre == null) {
            System.err.println("⚠️ Advertencia: zonaArrastre no está inicializado.");
        }
        if (zonaArrastre2 == null) {
            System.err.println("⚠️ Advertencia: zonaArrastre2 no está inicializado.");
        }

        configHandler = new DatabaseConfigHandler(configContainer, zonaArrastre, zonaArrastre2);
        tipoBDComboBox.getItems().addAll("MySQL", "PostgreSQL");
        guardarButton.setDisable(true);
        eliminarButton.setVisible(false);
    }

    @FXML
    private void actualizarCamposBD() {
        try {
            String tipoBD = tipoBDComboBox.getValue();
            if (tipoBD == null) {
                return;
            }

            switch (tipoBD) {
                case "MySQL", "MariaDB", "PostgreSQL" -> {
                    labelBD.setText("Nombre de Base de Datos:");
                    puertoField.setText(tipoBD.equals("PostgreSQL") ? "5432" : "3306");
                }
                case "SQL Server" -> {
                    labelBD.setText("Nombre de Instancia:");
                    puertoField.setText("1433");
                }
                case "Oracle" -> {
                    labelBD.setText("SID o Servicio:");
                    puertoField.setText("1521");
                }
            }
            if ("MySQL".equals(tipoBD)) {
                hostField.setText("127.0.0.1");
                usuarioField.setText("root");
                passwordField.setText("");
                nombreBDField.setText("proyecto");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar campos", e.getMessage());
        }
    }

    @FXML
    private void probarConexion() {
        try {
            String tipoBD = tipoBDComboBox.getValue();
            if (tipoBD == null || hostField.getText().isBlank() || puertoField.getText().isBlank() || usuarioField.getText().isBlank() || nombreBDField.getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Faltan datos", "Por favor, complete todos los campos.");
                return;
            }

            DatabaseConnection dbConnection = switch (tipoBD) {
                case "MySQL" ->
                    new DatabaseConnectionMySQL(hostField.getText().trim(), puertoField.getText().trim(), usuarioField.getText().trim(), passwordField.getText().trim(), nombreBDField.getText().trim());
                case "PostgreSQL" ->
                    new DatabaseConnectionPostgreSQL(hostField.getText().trim(), puertoField.getText().trim(), usuarioField.getText().trim(), passwordField.getText().trim(), nombreBDField.getText().trim());
                default ->
                    null;
            };

            if (dbConnection == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Base de datos no soportada", "Solo se admite MySQL y PostgreSQL.");
                return;
            }

            try (Connection conn = dbConnection.connect()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Conexión exitosa", "Se estableció la conexión con " + tipoBD);
                guardarButton.setDisable(false);
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo conectar", "Detalles: " + e.getMessage());
            guardarButton.setDisable(true);
        }
    }

    @FXML
    private void guardarConexion() {
        try {
            DatabaseConfig config = new DatabaseConfig(
                    tipoBDComboBox.getValue(),
                    hostField.getText(),
                    puertoField.getText(),
                    usuarioField.getText(),
                    passwordField.getText(),
                    nombreBDField.getText()
            );

            configHandler.saveConfig(config);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardar Configuración", "Éxito", "Configuración guardada correctamente.");
            eliminarButton.setVisible(true);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
