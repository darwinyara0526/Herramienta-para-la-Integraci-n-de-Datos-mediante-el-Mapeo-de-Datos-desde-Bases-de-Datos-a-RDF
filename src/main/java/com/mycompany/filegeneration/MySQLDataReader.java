package com.mycompany.filegeneration;

import com.mycompany.database.DatabaseConnectionMySQL;
import java.sql.*;

public class MySQLDataReader {
    private DatabaseConnectionMySQL dbConnection;

    public MySQLDataReader(DatabaseConnectionMySQL dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void readTables() {
        String[] tableNames = {"data_users", "registro", "sys_config"}; // Asegúrate de incluir las tablas que quieres leer

        for (String tableName : tableNames) {
            showTableData(tableName);
        }
    }

    public void showTableData(String tableName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            System.out.println("Tabla: " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Imprimir los nombres de las columnas
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Imprimir los datos de las filas
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            // Manejar la excepción para la tabla sys_config
            if (tableName.equals("sys_config")) {
                System.out.println("La tabla " + tableName + " es una tabla de configuración y se ha ignorado.");
            } else {
                System.out.println("Error al leer los datos de la tabla " + tableName);
                System.out.println(e.getMessage());
            }
        } finally {
            // Cerrar recursos en el bloque finally
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar los recursos: " + e.getMessage());
            }
        }
    }
}
