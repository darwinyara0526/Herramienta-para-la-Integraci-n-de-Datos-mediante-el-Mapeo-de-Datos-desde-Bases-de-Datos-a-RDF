package com.mycompany.proyectov2;

import com.mycompany.filegeneration.IntegrationHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Clase principal del proyecto que inicia la aplicación gráfica.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML de la interfaz de inicio de sesión o la interfaz principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/view/Login.fxml"));
        Parent root = loader.load();

        // Crear la escena y asignarla al stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Aplicación de Integración de Datos");

        // Mostrar la ventana
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Iniciar la aplicación JavaFX
        launch(args);
    }
}
