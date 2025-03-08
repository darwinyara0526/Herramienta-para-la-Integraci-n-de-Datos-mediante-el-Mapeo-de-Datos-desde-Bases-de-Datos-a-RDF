package com.mycompany.database;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseViewer {

    public static void showTables(DatabaseConfig config) {
        Stage stage = new Stage();
        VBox root = new VBox();
        ListView<String> tableListView = new ListView<>();
        
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
                return;
        }
        
        try (Connection conn = connection.connect()) {
            if (conn != null) {
                List<String> tables = fetchTables(conn, config.getTipoBD());
                tableListView.getItems().addAll(tables);
            } else {
                showError("No se pudo establecer conexión con la base de datos.");
                return;
            }
        } catch (SQLException e) {
            showError("Error al obtener las tablas: " + e.getMessage());
            return;
        }

        root.getChildren().add(tableListView);
        Scene scene = new Scene(root, 300, 400);
        stage.setScene(scene);
        stage.setTitle("Tablas en " + config.getNombreBD());
        stage.show();
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
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
