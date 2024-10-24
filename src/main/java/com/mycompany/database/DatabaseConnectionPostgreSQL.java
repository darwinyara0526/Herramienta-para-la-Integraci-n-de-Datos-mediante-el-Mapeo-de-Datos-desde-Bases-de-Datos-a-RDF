package com.mycompany.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionPostgreSQL implements DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/productos"; // Cambia según tus credenciales
    private static final String USER = "darwinyara26"; // Cambia según tus credenciales
    private static final String PASSWORD = "1familiayara"; // Cambia según tus credenciales

    @Override
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void disconnect(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión PostgreSQL cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión PostgreSQL: " + e.getMessage());
            }
        }
    }

    // Implementación del nuevo método getDatabaseType
    @Override
    public String getDatabaseType() {
        return "PostgreSQL"; // Devuelve el tipo de base de datos
    }
}
