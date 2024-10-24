package com.mycompany.filegeneration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.Lang;

import java.io.FileOutputStream;
import java.io.IOException;

public class RDFConverter {

    private Connection connection;

    public RDFConverter(Connection connection) {
        this.connection = connection;
    }

    public void convertToRDF(String turtleFilePath, String outputRDFPath) {
        System.out.println("Iniciando la conversión de R2RML a RDF...");

        Model model = ModelFactory.createDefaultModel();

        // Cargar el modelo RDF desde el archivo Turtle
        try {
            RDFDataMgr.read(model, turtleFilePath);
            System.out.println("Modelo RDF cargado desde: " + turtleFilePath);

            // Generar el archivo RDF/XML
            try (FileOutputStream out = new FileOutputStream(outputRDFPath)) {
                model.write(out, "RDF/XML");
                System.out.println("Archivo RDF/XML generado en: " + outputRDFPath);
            }
        } catch (IOException e) {
            System.err.println("Error al generar el archivo RDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateRDF(String outputFilePath) {
        Model model = ModelFactory.createDefaultModel();

        // Lógica para leer tablas y columnas de la base de datos
        try (Statement statement = connection.createStatement()) {
            ResultSet tables = statement.executeQuery("SHOW TABLES");

            while (tables.next()) {
                String tableName = tables.getString(1);
                ResultSet columns = statement.executeQuery("SELECT * FROM " + tableName);
                while (columns.next()) {
                    Resource subject = model.createResource("http://example.com/" + tableName + "/" + columns.getString(1));
                    Property predicate = model.createProperty("http://example.com/schema/columnName"); // Aquí puedes mejorar la propiedad
                    model.add(subject, predicate, columns.getString(1));
                }
            }

            // Escribir el modelo en el archivo RDF
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                model.write(out, "RDF/XML");
                System.out.println("Archivo RDF generado correctamente en: " + outputFilePath);
            }
        } catch (SQLException e) {
            System.err.println("Error al acceder a la base de datos: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo RDF: " + e.getMessage());
        }
    }
}
