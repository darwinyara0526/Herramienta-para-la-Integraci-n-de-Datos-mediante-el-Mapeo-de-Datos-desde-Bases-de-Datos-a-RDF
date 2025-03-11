package com.mycompany.gui.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.database.DatabaseConfig;
import com.mycompany.database.DatabaseConfigHandler;
import com.mycompany.database.DatabaseSelectionManager;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;

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
                        List<String> tablasDisponibles = obtenerTablasDeBaseDeDatos(config);
                        List<String> tablasSeleccionadas = mostrarDialogoSeleccionTablas(tablasDisponibles);

                        if (!tablasSeleccionadas.isEmpty()) {
                            DatabaseSelectionManager.saveSelection(config.getNombreBD(), tablasSeleccionadas);
                        }
                        atLeastOneSuccess = true;
                    }
                }
                return atLeastOneSuccess;
            } else {
                DatabaseConfig config = parseDatabaseConfig(rootNode);
                if (config != null) {
                    configHandler.saveConfig(config);
                    List<String> tablasDisponibles = obtenerTablasDeBaseDeDatos(config);
                    List<String> tablasSeleccionadas = mostrarDialogoSeleccionTablas(tablasDisponibles);

                    if (!tablasSeleccionadas.isEmpty()) {
                        DatabaseSelectionManager.saveSelection(config.getNombreBD(), tablasSeleccionadas);
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error al leer el archivo JSON: " + e.getMessage());
        }
        return false;
    }

// Simulación de método para obtener tablas de la BD (esto debería conectarse a la BD real)
    private List<String> obtenerTablasDeBaseDeDatos(DatabaseConfig config) {
        return List.of("table1", "table2", "table3"); // Simulación
    }

// Mostrar un cuadro de selección para que el usuario elija tablas
    private List<String> mostrarDialogoSeleccionTablas(List<String> tablas) {
        List<String> seleccionadas = new ArrayList<>();

        if (tablas.isEmpty()) {
            return seleccionadas;
        }

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Tablas");
        dialog.setHeaderText("Seleccione las tablas que desea usar");

        VBox content = new VBox();
        CheckBox[] checkBoxes = new CheckBox[tablas.size()];
        for (int i = 0; i < tablas.size(); i++) {
            checkBoxes[i] = new CheckBox(tablas.get(i));
            content.getChildren().add(checkBoxes[i]);
        }

        dialog.getDialogPane().setContent(content);
        ButtonType aceptarButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(aceptarButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == aceptarButton) {
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        seleccionadas.add(checkBox.getText());
                    }
                }
                return seleccionadas;
            }
            return null;
        });

        dialog.showAndWait();
        return seleccionadas;
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
