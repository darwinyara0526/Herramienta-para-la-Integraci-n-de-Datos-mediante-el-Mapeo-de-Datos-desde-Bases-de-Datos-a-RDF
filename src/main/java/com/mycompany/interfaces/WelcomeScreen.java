package com.mycompany.interfaces;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Font;

public class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        // Configurar el JFrame
        setTitle("Welcome");
        setSize(300, 150);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Agregar un JLabel con el mensaje
        JLabel label = new JLabel("Herramienta de Integración", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label);

        // Configurar un temporizador para cerrar esta ventana después de 2 segundos
        Timer timer = new Timer(2000, e -> {
            // Cerrar la pantalla de bienvenida y abrir la interfaz principal
            dispose();
            new MainInterface().setVisible(true);
        });
        timer.setRepeats(false); // Para que se ejecute solo una vez
        timer.start();
    }

    public static void main(String[] args) {
        // Iniciar la pantalla de bienvenida
        new WelcomeScreen().setVisible(true);
    }
}

