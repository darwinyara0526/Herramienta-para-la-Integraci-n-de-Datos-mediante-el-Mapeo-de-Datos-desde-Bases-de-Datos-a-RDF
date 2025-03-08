package com.mycompany.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConfigHandler {

    private static final String CONFIG_FILE = "data/database_config.json";
    private VBox configContainer;
    private VBox zonaArrastre;
    private VBox zonaArrastre2;
    private List<DatabaseConfig> databaseConfigs = new ArrayList<>();

    public DatabaseConfigHandler(VBox configContainer, VBox zonaArrastre, VBox zonaArrastre2) {
        this.configContainer = configContainer;
        this.zonaArrastre = zonaArrastre;
        this.zonaArrastre2 = zonaArrastre2;
        loadConfigs();
        setupDragAndDrop();
    }

    public List<DatabaseConfig> getDatabaseConfigs() {
        return databaseConfigs;
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
        loadConfigs();
    }

    public void loadConfigs() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try {
                DatabaseConfig[] configs = mapper.readValue(file, DatabaseConfig[].class);
                databaseConfigs.clear();
                configContainer.getChildren().clear();
                for (DatabaseConfig config : configs) {
                    databaseConfigs.add(config);
                    addConfigBlock(config);
                }
            } catch (IOException e) {
                System.out.println("❌ Error al cargar el archivo JSON: " + e.getMessage());
            }
        } else {
            System.out.println("⚠ No se encontró archivo de configuraciones, se iniciará vacío.");
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
        }
    }

    public void deleteConfig(DatabaseConfig configToDelete) {
        if (databaseConfigs.removeIf(config -> config.equals(configToDelete))) {
            saveConfigsToFile();
            loadConfigs();
            System.out.println("✔ Configuración eliminada: " + configToDelete.getNombreBD());
        } else {
            System.out.println("⚠ No se encontró la configuración para eliminar.");
        }
    }

    private void addConfigBlock(DatabaseConfig config) {
        Button configBlock = new Button(config.getNombreBD());
        configBlock.getStyleClass().add("config-block");

        try {
            String imagePath = "/com/mycompany/images/json-icon.png";
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            ImageView icon = new ImageView(image);
            icon.setFitWidth(24);
            icon.setFitHeight(24);
            configBlock.setGraphic(icon);
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar la imagen del icono.");
        }

        configBlock.setOnMouseClicked(event -> connectAndShowTables(config));
        configContainer.getChildren().add(configBlock);
        System.out.println("✔ Configuración añadida: " + config.getNombreBD());
    }

    private void setupDragAndDrop() {
        setupDropHandler(zonaArrastre);
        setupDropHandler(zonaArrastre2);
    }

    private void setupDropHandler(VBox zona) {
        zona.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        zona.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    if (file.getName().toLowerCase().endsWith(".json")) {
                        processDroppedFile(file);
                        success = true;
                    } else {
                        System.out.println("⚠ Archivo no válido: " + file.getName());
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void processDroppedFile(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            DatabaseConfig config = mapper.readValue(file, DatabaseConfig.class);
            saveConfig(config);
            connectAndShowTables(config);
        } catch (IOException e) {
            System.out.println("❌ Error al procesar el archivo JSON: " + e.getMessage());
        }
    }

    public void connectAndShowTables(DatabaseConfig config) {
        System.out.println("🔹 Iniciando conexión con la base de datos: " + config.getNombreBD());
        DatabaseConnection connection;
        switch (config.getTipoBD()) {
            case "MySQL":
                connection = new DatabaseConnectionMySQL(config.getHost(), config.getPuerto(), config.getUsuario(), config.getPassword(), config.getNombreBD());
                break;
            case "PostgreSQL":
                connection = new DatabaseConnectionPostgreSQL(config.getHost(), config.getPuerto(), config.getUsuario(), config.getPassword(), config.getNombreBD());
                break;
            default:
                System.out.println("❌ Tipo de base de datos no soportado");
                return;
        }

        try (Connection conn = connection.connect()) {
            if (conn != null) {
                System.out.println("✔ Conexión establecida con éxito.");
                DatabaseViewer.showTables(config);
            } else {
                System.out.println("❌ No se pudo establecer conexión con la base de datos.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}