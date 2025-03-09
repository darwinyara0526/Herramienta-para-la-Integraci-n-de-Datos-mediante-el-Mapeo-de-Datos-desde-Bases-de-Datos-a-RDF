package com.mycompany.gui.controllers;

import com.mycompany.database.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Platform;

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
        tipoBDComboBox.getItems().addAll("MySQL", "PostgreSQL", "SQL Server", "Oracle");

        Platform.runLater(() -> {
            System.out.println("✅ Inicializando DatabaseConfigController...");

            System.out.println("🔍 zonaArrastre: " + zonaArrastre);
            System.out.println("🔍 zonaArrastre2: " + zonaArrastre2);
            System.out.println("🔍 configContainer: " + configContainer);

            if (zonaArrastre == null || zonaArrastre2 == null || configContainer == null) {
                System.err.println("❌ ERROR: zonaArrastre, zonaArrastre2 o configContainer son NULL después de cargar la UI");
            } else {
                configHandler = new DatabaseConfigHandler(configContainer, zonaArrastre, zonaArrastre2);
                System.out.println("✅ configHandler inicializado correctamente.");
            }
        });

        // Deshabilitar botón Guardar hasta que la conexión sea exitosa
        guardarButton.setDisable(true);
    }

    @FXML
    private void actualizarCamposBD() {
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
    }

    @FXML
    private void probarConexion() {
        String tipoBD = tipoBDComboBox.getValue();
        if (tipoBD == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Faltan datos", "Seleccione un tipo de base de datos.");
            return;
        }

        String host = hostField.getText().trim();
        String puerto = puertoField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String password = passwordField.getText().trim();
        String nombreBD = nombreBDField.getText().trim();

        if (host.isEmpty() || puerto.isEmpty() || usuario.isEmpty() || nombreBD.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Faltan datos", "Por favor, complete todos los campos.");
            return;
        }

        DatabaseConnection dbConnection = switch (tipoBD) {
            case "MySQL" ->
                new DatabaseConnectionMySQL(host, puerto, usuario, password, nombreBD);
            case "PostgreSQL" ->
                new DatabaseConnectionPostgreSQL(host, puerto, usuario, password, nombreBD);
            default ->
                null;
        };

        if (dbConnection == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Base de datos no soportada", "Solo se admite MySQL y PostgreSQL.");
            return;
        }

        try (Connection conn = dbConnection.connect()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Conexión exitosa", "Se estableció la conexión con " + tipoBD);
            guardarButton.setDisable(false); // Habilitar botón Guardar
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo conectar", "Detalles: " + e.getMessage());
            guardarButton.setDisable(true);
        }
    }

    @FXML
    private void guardarConexion() {
        System.out.println("🔹 Botón Guardar presionado...");

        if (configHandler == null) {
            System.err.println("❌ ERROR: configHandler es NULL. No se puede guardar la configuración.");
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error interno", "No se pudo guardar la configuración porque el gestor no está inicializado.");
            return;
        }

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

            if (eliminarButton != null) {
                eliminarButton.setVisible(true);
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar", "Detalles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setConfigHandler(DatabaseConfigHandler configHandler) {
        this.configHandler = configHandler;
        System.out.println("✅ configHandler recibido correctamente.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
