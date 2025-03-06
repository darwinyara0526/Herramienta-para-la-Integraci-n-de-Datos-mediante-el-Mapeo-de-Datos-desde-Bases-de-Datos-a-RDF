package com.mycompany.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionMySQL implements DatabaseConnection {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnectionMySQL(String host, String puerto, String usuario, String password, String nombreBD) {
        this.url = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        this.user = usuario;
        this.password = password;
    }

    @Override
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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

    @Override
    public String getDatabaseType() {
        return "MySQL";
    }
}
