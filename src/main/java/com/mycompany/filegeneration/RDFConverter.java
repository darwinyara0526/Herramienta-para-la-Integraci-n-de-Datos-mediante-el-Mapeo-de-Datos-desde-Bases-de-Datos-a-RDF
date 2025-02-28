package com.mycompany.filegeneration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Usamos la clase específica para evitar conflictos
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.Lang;
import java.io.FileOutputStream;
import java.io.IOException;

public class RDFConverter {

    private Connection connection;

    public RDFConverter(Connection connection) {
        this.connection = connection;
    }

    // Método para convertir un archivo Turtle a RDF/XML
    public void convertToRDF(String turtleFilePath, String outputRDFPath) {
        System.out.println("🔍 Iniciando la conversión de R2RML a RDF...");

        Model model = ModelFactory.createDefaultModel();

        try {
            RDFDataMgr.read(model, turtleFilePath);
            System.out.println("✅ Modelo RDF cargado desde: " + turtleFilePath);
            System.out.println("📌 Cantidad de triples en el modelo antes de guardar: " + model.size());

            try (FileOutputStream out = new FileOutputStream(outputRDFPath)) {
                model.write(out, "RDF/XML");
                System.out.println("✅ Archivo RDF/XML generado en: " + outputRDFPath);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al generar el archivo RDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para generar un RDF/XML desde la base de datos
    public void generateRDF(String outputFilePath) {
        System.out.println("🔍 Iniciando generación de RDF desde la base de datos...");
        Model model = ModelFactory.createDefaultModel();

        // Definir prefijos
        String ex = "http://example.com/schema/";
        model.setNsPrefix("ex", ex);

        try (Statement statement = connection.createStatement()) {
            ResultSet tables = statement.executeQuery("SHOW TABLES");

            while (tables.next()) {
                String tableName = tables.getString(1);
                String tableURI = "http://example.com/" + tableName + "/";

                System.out.println("📌 Procesando tabla: " + tableName);

                // Consultar los datos de la tabla
                ResultSet rows = statement.executeQuery("SELECT * FROM " + tableName);
                while (rows.next()) {
                    String rowID = rows.getString(1); // Suponemos que la primera columna es un ID
                    Resource rowResource = model.createResource(tableURI + rowID);

                    System.out.println("🔹 Fila ID: " + rowID);

                    // Recorrer todas las columnas y agregarlas como propiedades RDF
                    for (int i = 1; i <= rows.getMetaData().getColumnCount(); i++) {
                        String columnName = rows.getMetaData().getColumnName(i);
                        String columnValue = rows.getString(i);

                        System.out.println("   ➡️ " + columnName + ": " + columnValue);

                        if (columnValue != null) {
                            Property predicate = model.createProperty(ex + columnName);
                            rowResource.addProperty(predicate, columnValue);
                        }
                    }
                }
            }

            // 📌 Mostrar cantidad de triples generados
            System.out.println("📌 Cantidad de triples en el modelo: " + model.size());

            // 📌 Imprimir RDF en formato Turtle en la terminal
            System.out.println("🔹 RDF generado en formato Turtle:");
            model.write(System.out, "TURTLE");

            // Guardar el RDF en un archivo
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                model.write(out, "RDF/XML");
                System.out.println("✅ Archivo RDF generado correctamente en: " + outputFilePath);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al acceder a la base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Error al escribir el archivo RDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
