package com.mycompany.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionMySQL implements DatabaseConnection {
    // Parámetros de conexión
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/proyecto"; // Cambiado el host, puerto y base de datos
    private static final String USER = "root"; // Usuario root
    private static final String PASSWORD = "1familiayara"; // Contraseña

    @Override
    public Connection connect() throws SQLException {
        // Intenta establecer una conexión a la base de datos
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void disconnect(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión MySQL cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión MySQL: " + e.getMessage());
            }
        }
    }

    // Implementación del nuevo método getDatabaseType
    @Override
    public String getDatabaseType() {
        return "MySQL";
    }
}
