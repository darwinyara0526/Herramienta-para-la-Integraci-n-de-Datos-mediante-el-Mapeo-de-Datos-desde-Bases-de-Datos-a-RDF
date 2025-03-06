package com.mycompany.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConfigHandler {

    private static final String CONFIG_FILE = "data/database_config.json";
    private VBox configContainer;
    private List<DatabaseConfig> databaseConfigs = new ArrayList<>();

    public DatabaseConfigHandler(VBox configContainer) {
        this.configContainer = configContainer;
        loadConfigs();
    }

    public boolean exists(DatabaseConfig newConfig) {
        return databaseConfigs.stream().anyMatch(config -> config.equals(newConfig));
    }

    public void saveConfig(DatabaseConfig config) {
        if (exists(config)) {
            System.out.println("⚠ La configuración ya existe y no se agregará.");
            return;
        }
        System.out.println("🔹 Guardando nueva configuración: " + config.getNombreBD());
        databaseConfigs.add(config);
        saveConfigsToFile();
        addConfigBlock(config);
    }

    private void loadConfigs() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try {
                DatabaseConfig[] configs = mapper.readValue(file, DatabaseConfig[].class);
                databaseConfigs.clear();
                for (DatabaseConfig config : configs) {
                    databaseConfigs.add(config);
                    addConfigBlock(config);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveConfigsToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(CONFIG_FILE);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            mapper.writeValue(file, databaseConfigs);
            System.out.println("✔ Archivo JSON guardado correctamente.");
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteConfig(DatabaseConfig configToDelete) {
        if (databaseConfigs.removeIf(config -> config.equals(configToDelete))) {
            saveConfigsToFile();
            configContainer.getChildren().clear();
            databaseConfigs.forEach(this::addConfigBlock);
            System.out.println("✔ Configuración eliminada: " + configToDelete.getNombreBD());
        } else {
            System.out.println("⚠ No se encontró la configuración para eliminar.");
        }
    }

    private void addConfigBlock(DatabaseConfig config) {
        Button configBlock = new Button();
        configBlock.getStyleClass().add("config-block");

        Image image = null;
        try {
            String imagePath = "/com/mycompany/images/json-icon.png";
            image = new Image(getClass().getResource(imagePath).toExternalForm());
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen del icono: " + e.getMessage());
        }

        if (image != null) {
            ImageView icon = new ImageView(image);
            icon.setFitWidth(24);
            icon.setFitHeight(24);
            configBlock.setGraphic(icon);
        } else {
            configBlock.setText("❌ " + config.getNombreBD());
        }

        configBlock.setText(config.getNombreBD());
        configBlock.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);

        Button deleteButton = new Button("Eliminar");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> deleteConfig(config));
        deleteButton.setVisible(false);

        configBlock.setOnMouseClicked(event -> deleteButton.setVisible(!deleteButton.isVisible()));

        configContainer.getChildren().addAll(configBlock, deleteButton);
        System.out.println("Configuración añadida: " + config.getNombreBD());
    }
}
