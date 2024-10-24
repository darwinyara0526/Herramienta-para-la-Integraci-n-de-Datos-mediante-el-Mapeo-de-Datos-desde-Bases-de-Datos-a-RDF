package com.mycompany.filegeneration;

import java.sql.*;

public class DataReader {
    private Connection connection;

    public DataReader(Connection connection) {
        this.connection = connection; // Guardar la conexión
    }

    // Leer todas las tablas dinámicamente desde la base de datos
    public void readTables() {
        ResultSet tables = null;

        try {
            // Obtener los metadatos de la base de datos para listar las tablas
            DatabaseMetaData metaData = connection.getMetaData();
            tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            // Iterar sobre las tablas encontradas
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                // Ignorar la tabla sys_config u otras tablas de configuración si es necesario
                if (tableName.equalsIgnoreCase("sys_config")) {
                    System.out.println("La tabla " + tableName + " es una tabla de configuración y se ha ignorado.");
                } else {
                    // Mostrar los datos de la tabla
                    showTableData(tableName);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al leer las tablas: " + e.getMessage());
        } finally {
            // Cerrar el ResultSet de tablas
            try {
                if (tables != null) tables.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar el ResultSet de tablas: " + e.getMessage());
            }
        }
    }

    // Mostrar los datos de una tabla específica
    public void showTableData(String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
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
            // Manejar la excepción si ocurre un error al leer una tabla
            System.out.println("Error al leer los datos de la tabla " + tableName);
            System.out.println(e.getMessage());
        } finally {
            // Cerrar los recursos en el bloque finally
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar los recursos: " + e.getMessage());
            }
        }
    }
}
