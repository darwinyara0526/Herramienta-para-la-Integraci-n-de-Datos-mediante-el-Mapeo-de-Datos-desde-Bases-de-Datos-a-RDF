package com.mycompany.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.Parent;

import com.mycompany.gui.models.Usuario;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML
    private VBox leftPane, rightPane;
    @FXML
    private ImageView toolImage;
    @FXML
    private Button downloadGuideButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private HBox mainContainer;

    /**
     * Configuración inicial de la interfaz
     */
    public void initialize() {
        ajustarVentana();
        configurarPaneles();
        cargarImagen();

        // credenciales correo
        emailField.setText("correoadmin@gmail.com");
        passwordField.setText("user123");
        loginButton.setOnAction(event -> verificarCredenciales());
    }

    /**
     * Ajusta la ventana al tamaño de la pantalla
     */
    private void ajustarVentana() {
        Platform.runLater(() -> {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(loginButton.getScene());
            stage.setMaximized(true);
            stage.setFullScreen(false);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
        });
    }

    /**
     * Configura el tamaño de los paneles
     */
    private void configurarPaneles() {
        leftPane.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.4));
        rightPane.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.6));
        toolImage.setPreserveRatio(true);
        toolImage.fitWidthProperty().bind(rightPane.widthProperty().multiply(0.9));
        toolImage.fitHeightProperty().bind(rightPane.heightProperty().multiply(0.9));
    }

    /**
     * Carga la imagen en el panel derecho
     */
    private void cargarImagen() {
        String imagePath = "/com/mycompany/images/Image1.png";
        Image img = new Image(getClass().getResourceAsStream(imagePath));
        if (!img.isError()) {
            toolImage.setImage(img);
        }
    }

    /**
     * Verifica las credenciales ingresadas
     */
    private void verificarCredenciales() {
        String email = emailField.getText();
        String password = passwordField.getText();
        Usuario usuario = obtenerUsuario(email, password);
        if (usuario != null) {
            cambiarPantalla(usuario);
        } else {
            mostrarAlerta("Credenciales incorrectas", "El correo o la contraseña son incorrectos.");
        }
    }

    /**
     * Obtiene un usuario desde el archivo de datos
     */
    private Usuario obtenerUsuario(String email, String password) {
        try (InputStream is = getClass().getResourceAsStream("/com/mycompany/data/usuarios.txt"); InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 7 && datos[3].equals(email) && datos[4].equals(password)) {
                    return new Usuario(datos[0], datos[1], datos[2], datos[3], datos[5], datos[6]);
                }
            }
        } catch (IOException | NullPointerException e) {
            mostrarAlerta("Error", "No se pudo leer el archivo de usuarios.");
        }
        return null;
    }

    /**
     * Cambia a la pantalla correspondiente según el rol del usuario
     */
    private void cambiarPantalla(Usuario usuario) {
        String fxmlFile = switch (usuario.getRol()) {
            case "admin" ->
                "/com/mycompany/view/Admin.fxml";
            case "registrado" ->
                "/com/mycompany/view/Registered.fxml";
            case "invitado" ->
                "/com/mycompany/view/Guest.fxml";
            default ->
                null;
        };

        if (fxmlFile != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();
                Scene newScene = new Scene(root);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(newScene);

                Platform.runLater(() -> {
                    stage.setMaximized(true);
                    stage.setFullScreen(false);
                    stage.setWidth(Screen.getPrimary().getBounds().getWidth());
                    stage.setHeight(Screen.getPrimary().getBounds().getHeight());
                });

                stage.show();
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo cargar la pantalla.");
            }
        }
    }

    /**
     * Muestra una alerta con el mensaje indicado
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
