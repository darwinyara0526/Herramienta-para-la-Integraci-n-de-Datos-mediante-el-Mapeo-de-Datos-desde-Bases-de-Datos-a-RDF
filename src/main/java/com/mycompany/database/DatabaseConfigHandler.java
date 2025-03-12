package com.mycompany.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

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

        Runtime.getRuntime().addShutdownHook(new Thread(this::limpiarArchivoConfig));
    }

    // ✅ Nuevo constructor sin interfaz gráfica
    public DatabaseConfigHandler() {
        this.loadConfigs();  // Solo carga configuraciones
    }

    public static Map<String, DatabaseConfig> getAllConfigs() {
        Map<String, DatabaseConfig> configMap = new HashMap<>();

        DatabaseConfigHandler handler = new DatabaseConfigHandler(); // ✅ Usa el nuevo constructor

        for (DatabaseConfig config : handler.getDatabaseConfigs()) {
            configMap.put(config.getNombreBD(), config);
        }

        return configMap;
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

        Platform.runLater(() -> {
            addConfigBlock(config);  // ✅ Agregar visualmente
        });
    }

    public void loadConfigs() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_FILE);

        if (file.exists()) {
            try {
                DatabaseConfig[] configs = mapper.readValue(file, DatabaseConfig[].class);
                databaseConfigs.clear();

                // Verificar si configContainer no es nulo antes de usarlo
                if (configContainer != null) {
                    Platform.runLater(() -> configContainer.getChildren().clear());
                }

                for (DatabaseConfig config : configs) {
                    databaseConfigs.add(config);
                    if (configContainer != null) {
                        Platform.runLater(() -> addConfigBlock(config));
                    }
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

        // Cargar imagen del icono
        Image image = null;
        try {
            String imagePath = "/com/mycompany/images/json-icon.png";
            image = new Image(getClass().getResource(imagePath).toExternalForm());
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar la imagen del icono.");
        }

        if (image != null) {
            ImageView icon = new ImageView(image);
            icon.setFitWidth(24);
            icon.setFitHeight(24);
            configBlock.setGraphic(icon);
        }

        // Habilitar arrastre
        configBlock.setOnDragDetected(event -> {
            Dragboard db = configBlock.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(config.getNombreBD());
            db.setContent(content);
            event.consume();
        });

        Button deleteButton = new Button("Eliminar");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> deleteConfig(config));
        deleteButton.setVisible(false);

        configBlock.setOnMouseClicked(event -> deleteButton.setVisible(!deleteButton.isVisible()));

        configContainer.getChildren().addAll(configBlock, deleteButton);
        System.out.println("✔ Configuración añadida: " + config.getNombreBD());

        configBlock.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {  // Doble clic para eliminar
                deleteConfig(config);
            } else {
                deleteButton.setVisible(!deleteButton.isVisible());
            }
        });
    }

    private DatabaseConfig getConfigByName(String nombreBD) {
        System.out.println("🔍 Buscando configuración con nombre: " + nombreBD);
        for (DatabaseConfig config : databaseConfigs) {
            System.out.println("   🔎 Comparando con: " + config.getNombreBD());
            if (config.getNombreBD().equalsIgnoreCase(nombreBD)) {
                System.out.println("✅ Coincidencia encontrada.");
                return config;
            }
        }
        System.out.println("⚠️ No se encontró coincidencia para: " + nombreBD);
        return null;
    }

    private void setupDragAndDrop() {
        setupDropTarget(zonaArrastre);
        setupDropTarget(zonaArrastre2);
    }

    private void setupDropTarget(VBox zona) {
        zona.setOnDragOver(event -> {
            if (event.getGestureSource() != zona
                    && (event.getDragboard().hasFiles() || event.getDragboard().hasString())) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        zona.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            System.out.println("🔹 Evento de arrastre detectado en " + zona.getId());

            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                System.out.println("📂 Archivo detectado: " + file.getAbsolutePath());

                if (file.getName().endsWith(".json")) {
                    System.out.println("✅ Archivo JSON válido, procesando...");
                    procesarArchivo(file);
                } else {
                    System.out.println("⚠️ No es un archivo JSON válido.");
                }
            } else if (db.hasString()) {
                String nombreBD = db.getString();
                System.out.println("🔹 Nombre de BD recibido: " + nombreBD);

                DatabaseConfig config = getConfigByName(nombreBD);
                if (config != null) {
                    System.out.println("✅ Configuración encontrada: " + config.getNombreBD());
                    Platform.runLater(() -> DatabaseViewer.showTables(config));
                } else {
                    System.out.println("⚠️ No se encontró configuración para: " + nombreBD);
                }
            }

            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void procesarArchivo(File file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DatabaseConfig config = objectMapper.readValue(file, DatabaseConfig.class);

            Platform.runLater(() -> {
                DatabaseViewer.showTables(config);
            });

        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
        }
    }

    private void limpiarArchivoConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("🗑 Archivo de configuración eliminado correctamente al cerrar la aplicación.");
            } else {
                System.out.println("⚠ No se pudo eliminar el archivo de configuración.");
            }
        }
    }

}
