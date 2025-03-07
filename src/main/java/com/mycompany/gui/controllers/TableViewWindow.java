package com.mycompany.gui.controllers;

import com.mycompany.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TableViewWindow {
    public static void showTables(DatabaseConnection dbConnection) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tablas en la Base de Datos");

        ListView<String> tableList = new ListView<>();
        ObservableList<String> tables = FXCollections.observableArrayList();
        
        try (Connection conn = dbConnection.connect()) {
            if (conn != null) {
                DatabaseMetaData metaData = conn.getMetaData();
                try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
            }
        } catch (SQLException e) {
            showError("Error al cargar tablas", e.getMessage());
        }

        if (tables.isEmpty()) {
            tables.add("No se encontraron tablas.");
        }

        tableList.setItems(tables);

        VBox layout = new VBox(10, tableList);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(300, 400);
        
        stage.setScene(new Scene(layout));
        stage.show();
    }

    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
