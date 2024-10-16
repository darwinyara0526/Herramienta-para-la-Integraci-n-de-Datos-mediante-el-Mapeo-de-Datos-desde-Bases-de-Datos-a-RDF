package com.mycompany.filegeneration;

import com.mycompany.database.DatabaseConnectionMySQL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;

import java.io.FileOutputStream; 
import java.io.IOException; 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLR2RMLGenerator {
    private DatabaseConnectionMySQL dbConnection;

    public MySQLR2RMLGenerator(DatabaseConnectionMySQL dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void generateR2RML(String outputPath) {
        Connection connection = null;
        Statement statement = null;

        try {
            // Establece la conexión a la base de datos
            connection = dbConnection.getConnection();
            statement = connection.createStatement();

            // Obtener todas las tablas de la base de datos
            String tablesQuery = "SHOW TABLES";
            ResultSet tablesResult = statement.executeQuery(tablesQuery);

            // Crea un modelo RDF
            Model model = ModelFactory.createDefaultModel();
            String namespace = "http://example.com/your_namespace#";

            boolean dataFound = false; // Variable para controlar si se generó algún dato

            while (tablesResult.next()) {
                String tableName = tablesResult.getString(1);

                // Ignorar la tabla sys_config
                if (tableName.equals("sys_config")) {
                    continue;
                }

                // Obtener las columnas de la tabla actual
                String columnsQuery = "SELECT * FROM " + tableName;
                try (Statement columnStatement = connection.createStatement();
                     ResultSet columnsResult = columnStatement.executeQuery(columnsQuery)) {

                    while (columnsResult.next()) {
                        // Obtener el ID y las propiedades de la fila
                        String id = columnsResult.getString(1); // Asumiendo que la primera columna es el ID
                        Resource resource = model.createResource(namespace + id);

                        // Recorrer todas las columnas de la fila
                        for (int i = 2; i <= columnsResult.getMetaData().getColumnCount(); i++) { // Comenzamos en 2 para omitir el ID
                            String columnName = columnsResult.getMetaData().getColumnName(i);
                            Property property = model.createProperty(namespace + columnName);
                            String value = columnsResult.getString(i);
                            
                            // Agrega propiedades al recurso
                            resource.addProperty(property, value);
                        }
                        dataFound = true; // Se encontraron datos
                    }
                }
            }

            // Solo se guarda el modelo si se encontró al menos un dato
            if (dataFound) {
                // Guarda el modelo RDF en el archivo
                model.write(new FileOutputStream(outputPath), "TURTLE");
                System.out.println("Archivo TTL generado correctamente: " + outputPath);

                // También genera el archivo RDF
                String rdfOutputPath = outputPath.replace(".ttl", ".rdf");
                model.write(new FileOutputStream(rdfOutputPath), "RDF/XML");
                System.out.println("Archivo RDF generado en: " + rdfOutputPath);
            } else {
                System.out.println("No se encontraron datos para generar archivos RDF.");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            dbConnection.disconnect(connection);
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
