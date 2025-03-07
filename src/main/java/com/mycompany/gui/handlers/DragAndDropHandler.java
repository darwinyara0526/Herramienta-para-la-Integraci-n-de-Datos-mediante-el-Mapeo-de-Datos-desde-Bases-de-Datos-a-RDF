package com.mycompany.gui.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.database.DatabaseConfig;
import com.mycompany.database.DatabaseConfigHandler;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragAndDropHandler {

    private final DatabaseConfigHandler configHandler;

    public DragAndDropHandler(DatabaseConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    public void enableDragAndDrop(VBox dropZone) {
        dropZone.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles() || event.getGestureSource() instanceof Button) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles()) {
                List<File> files = dragboard.getFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        success |= processJsonFile(file);
                    }
                }
            } else if (event.getGestureSource() instanceof Button) {
                Button draggedButton = (Button) event.getGestureSource();
                dropZone.getChildren().add(draggedButton);
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean processJsonFile(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(file);
            if (rootNode.isArray()) {
                boolean atLeastOneSuccess = false;
                for (JsonNode configNode : rootNode) {
                    DatabaseConfig config = parseDatabaseConfig(configNode);
                    if (config != null) {
                        configHandler.saveConfig(config);
                        atLeastOneSuccess = true;
                    }
                }
                return atLeastOneSuccess;
            } else {
                DatabaseConfig config = parseDatabaseConfig(rootNode);
                if (config != null) {
                    configHandler.saveConfig(config);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error al leer el archivo JSON: " + e.getMessage());
        }
        return false;
    }

    private DatabaseConfig parseDatabaseConfig(JsonNode node) {
        try {
            if (node.has("tipoBD") && node.has("host") && node.has("puerto") && node.has("usuario") && node.has("password") && node.has("nombreBD")) {
                return new DatabaseConfig(
                    node.get("tipoBD").asText(),
                    node.get("host").asText(),
                    node.get("puerto").asText(),
                    node.get("usuario").asText(),
                    node.get("password").asText(),
                    node.get("nombreBD").asText()
                );
            } else {
                System.out.println("⚠ Archivo JSON no tiene todos los campos requeridos.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al parsear configuración: " + e.getMessage());
        }
        return null;
    }
}
