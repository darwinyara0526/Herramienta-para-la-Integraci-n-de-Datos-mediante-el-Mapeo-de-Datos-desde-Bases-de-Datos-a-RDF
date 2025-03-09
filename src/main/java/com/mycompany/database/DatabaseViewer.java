package com.mycompany.database;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseViewer {
    private static Stage activeStage = null;

    public static Stage showTables(DatabaseConfig config) {
        if (activeStage != null && activeStage.isShowing()) {
            activeStage.toFront();
            return activeStage;
        }

        Stage stage = new Stage();
        activeStage = stage;

        VBox root = new VBox();
        CheckBox selectAllCheckBox = new CheckBox("Seleccionar todas");
        ListView<CheckBox> tableListView = new ListView<>();
        Button integrateButton = new Button("Integrar seleccionadas");

        DatabaseConnection connection;

        switch (config.getTipoBD()) {
            case "MySQL":
                connection = new DatabaseConnectionMySQL(config.getHost(), config.getPuerto(), config.getUsuario(), config.getPassword(), config.getNombreBD());
                break;
            case "PostgreSQL":
                connection = new DatabaseConnectionPostgreSQL(config.getHost(), config.getPuerto(), config.getUsuario(), config.getPassword(), config.getNombreBD());
                break;
            default:
                showError("Tipo de base de datos no soportado: " + config.getTipoBD());
                return null;
        }

        try (Connection conn = connection.connect()) {
            if (conn != null) {
                List<String> tables = fetchTables(conn, config.getTipoBD());
                for (String table : tables) {
                    tableListView.getItems().add(new CheckBox(table));
                }
            } else {
                showError("No se pudo establecer conexión con la base de datos.");
                return null;
            }
        } catch (SQLException e) {
            showError("Error al obtener las tablas: " + e.getMessage());
            return null;
        }

        selectAllCheckBox.setOnAction(event -> {
            boolean selectAll = selectAllCheckBox.isSelected();
            for (CheckBox checkBox : tableListView.getItems()) {
                checkBox.setSelected(selectAll);
            }
        });

        integrateButton.setOnAction(event -> {
            List<String> selectedTables = new ArrayList<>();
            for (CheckBox checkBox : tableListView.getItems()) {
                if (checkBox.isSelected()) {
                    selectedTables.add(checkBox.getText());
                }
            }
            System.out.println("Tablas seleccionadas: " + selectedTables);
        });

        root.getChildren().addAll(selectAllCheckBox, tableListView, integrateButton);
        Scene scene = new Scene(root, 300, 400);
        stage.setScene(scene);
        stage.setTitle("Tablas en " + config.getNombreBD());
        stage.show();

        return stage;
    }

    private static List<String> fetchTables(Connection conn, String dbType) throws SQLException {
        List<String> tables = new ArrayList<>();
        String query = "";

        if ("MySQL".equalsIgnoreCase(dbType)) {
            query = "SHOW TABLES";
        } else if ("PostgreSQL".equalsIgnoreCase(dbType)) {
            query = "SELECT tablename FROM pg_tables WHERE schemaname = 'public'";
        }

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}