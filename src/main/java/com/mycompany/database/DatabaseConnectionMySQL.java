package com.mycompany.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionMySQL {
    // Parámetros de conexión
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/proyecto"; // Cambiado el host, puerto y base de datos
    private static final String USER = "root"; // Usuario root
    private static final String PASSWORD = "1familiayara"; // Contraseña

    // Método para obtener la conexión
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Método para desconectar la conexión
    public void disconnect(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
