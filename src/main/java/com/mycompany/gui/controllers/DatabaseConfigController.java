package com.mycompany.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private void actualizarCamposBD() {
        String tipoBD = tipoBDComboBox.getValue();
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
    }

    @FXML
    private void probarConexion() {
        // Aquí iría la lógica para probar la conexión
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Prueba de conexión realizada.");
        alert.showAndWait();
    }

    @FXML
    private void guardarConexion() {
        // Guardar la configuración (puede ser en un archivo o en la app)
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Conexión guardada.");
        alert.showAndWait();
    }
}
