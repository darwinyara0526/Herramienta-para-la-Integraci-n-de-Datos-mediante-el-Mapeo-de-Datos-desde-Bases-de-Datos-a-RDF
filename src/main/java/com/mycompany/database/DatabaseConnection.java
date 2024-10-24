package com.mycompany.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {
    Connection connect() throws SQLException; // Método para conectar a la base de datos
    void disconnect(Connection conn);         // Método para desconectar la base de datos
    
    String getDatabaseType(); // Método para obtener el tipo de base de datos
}
