package com.mycompany.interfaces;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;

public class MainInterface extends JFrame {
    public MainInterface() {
        // Configurar el JFrame
        setTitle("Main Interface");
        setSize(400, 300);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Establecer un layout de cuadrícula
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        // Componentes de la interfaz principal
        JLabel userLabel = new JLabel("Usuario:");
        JTextField userField = new JTextField();

        JLabel passwordLabel = new JLabel("Contraseña:");
        JTextField passwordField = new JTextField();

        JButton loginButton = new JButton("Iniciar Sesión");
        JButton registerButton = new JButton("Registrarse");
        JButton connectButton = new JButton("Conectar");

        // Añadir los componentes al panel
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(connectButton);

        // Añadir el panel al JFrame
        add(panel);
    }
}
