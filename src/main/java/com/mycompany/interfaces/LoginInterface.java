package com.mycompany.interfaces;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginInterface extends Application {
    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Cargar el archivo HTML desde recursos usando la ruta correcta
        String url = getClass().getResource("/html/index.html").toExternalForm();
        webEngine.load(url);

        // Crear una escena sin especificar el tamaño
        Scene scene = new Scene(webView);

        primaryStage.setTitle("Mi Aplicación de Escritorio");
        
        // Obtener el tamaño de la pantalla
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        
        // Hacer que la aplicación se ajuste al tamaño de la pantalla
        primaryStage.setResizable(true); // Habilitar el redimensionamiento

        // Asignar la escena al escenario y mostrarlo
        primaryStage.setScene(scene);
        primaryStage.show();

        // Opcional: Configurar el WebView para que llene todo el espacio
        webView.setPrefSize(primaryStage.getWidth(), primaryStage.getHeight());
        webView.setMinSize(0, 0); // Permitir que el WebView se ajuste al tamaño de la ventana
    }

    public static void main(String[] args) {
        launch(args);
    }
}
