package com.mycompany.gui.controllers;

import com.mycompany.database.DatabaseConfigHandler;
import com.mycompany.database.DatabaseSelectionManager;
import com.mycompany.filegeneration.IntegrationHandler;
import com.mycompany.gui.handlers.DragAndDropHandler;
import com.mycompany.gui.models.Usuario;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminController {

    @FXML
    private Label nombreAdminLabel;

    @FXML
    private Label areaLabel;

    @FXML
    private VBox zonaArrastre;

    @FXML
    private VBox zonaArrastre2;

    @FXML
    private VBox contenedorConfigs;

    @FXML
    private ProgressBar progresoIntegracion;

    @FXML
    private Button botonIntegrar;

    private Usuario usuario;
    private DatabaseConfigHandler configHandler;
    private DragAndDropHandler dragAndDropHandler;

    @FXML
    public void initialize() {
        System.out.println("Inicializando AdminController...");
        System.out.println("zonaArrastre: " + zonaArrastre);
        System.out.println("zonaArrastre2: " + zonaArrastre2);
        
        mostrarSeleccion();

        if (zonaArrastre == null || zonaArrastre2 == null) {
            System.err.println("ERROR: zonaArrastre o zonaArrastre2 es NULL después de cargar la UI");
            return;
        }

        // Inicializar el gestor de configuraciones de bases de datos
        configHandler = new DatabaseConfigHandler(contenedorConfigs, zonaArrastre, zonaArrastre2);
        configHandler.loadConfigs();

        // Configurar el Drag and Drop
        dragAndDropHandler = new DragAndDropHandler(configHandler);
        dragAndDropHandler.enableDragAndDrop(zonaArrastre);
        dragAndDropHandler.enableDragAndDrop(zonaArrastre2);
        dragAndDropHandler.enableDragAndDrop(contenedorConfigs);
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        if (usuario != null) {
            nombreAdminLabel.setText(usuario.getNombre() + " " + usuario.getApellido());
            areaLabel.setText("Área: " + usuario.getArea());
        } else {
            nombreAdminLabel.setText("No se encontró el usuario");
        }
    }

    @FXML
    private void iniciarIntegracion() {
        progresoIntegracion.setProgress(0.1);

        // Iniciar el proceso de integración
        System.out.println("Iniciando integración de datos...");

        IntegrationHandler integrationHandler = new IntegrationHandler();
        integrationHandler.executeIntegration(); // Llamada al método de integración

        progresoIntegracion.setProgress(1.0);
        System.out.println("Integración finalizada.");
    }

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nombreAdminLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirConexionBD() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/view/DatabaseConfig.fxml"));
            Parent root = loader.load();

            // Obtener el controlador
            DatabaseConfigController controller = loader.getController();

            // Pasar referencias
            controller.setZonaArrastreReferences(zonaArrastre, zonaArrastre2);
            controller.setConfigContainer(contenedorConfigs); // ✅ Pasar contenedorConfigs

            Stage stage = new Stage();
            stage.setTitle("Conectar a Base de Datos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarSeleccion() {
        Map<String, List<String>> seleccion = DatabaseSelectionManager.getAllSelections();
        seleccion.forEach((db, tables)
                -> System.out.println("📂 BD: " + db + " - Tablas: " + tables)
        );
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
