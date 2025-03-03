package com.mycompany.proyectov2;

import com.mycompany.filegeneration.R2RMLGenerator;
import com.mycompany.filegeneration.RDFConverter;
import com.mycompany.filegeneration.DataReader;
import com.mycompany.database.DatabaseConnectionMySQL;
import com.mycompany.database.DatabaseConnectionPostgreSQL;
import com.mycompany.database.DatabaseConnection;
import com.mycompany.rdfintegration.RDFIntegrator;

import java.nio.file.Paths;
import java.sql.Connection;

/**
 * Clase principal del proyecto para la generación automática de archivos R2RML
 * (TTL) y RDF a partir de bases de datos relacionales.
 */
public class Main {

    public static void main(String[] args) {
        // Configuración de rutas de salida para MySQL y PostgreSQL
        String mysqlTurtlePath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "mysql_output.ttl").toString();
        String mysqlRDFPath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "mysql_output.rdf").toString();

        String postgresTurtlePath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "postgres_output.ttl").toString();
        String postgresRDFPath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "postgres_output.rdf").toString();

        // Procesar bases de datos y generar RDF
        processDatabase(new DatabaseConnectionMySQL(), mysqlTurtlePath, mysqlRDFPath);
        processDatabase(new DatabaseConnectionPostgreSQL(), postgresTurtlePath, postgresRDFPath);

        // Integrar RDFs de MySQL y PostgreSQL
        RDFIntegrator integrator = new RDFIntegrator();
        integrator.integrateRDF(mysqlRDFPath, postgresRDFPath);

        // Exportar en diferentes formatos
        String outputBasePath = "C:/Users/darwi/OneDrive/Desktop/RutaProyecto/unified_output";
        integrator.exportToRDFXML(outputBasePath + ".rdf");
        integrator.exportToCSV(outputBasePath + ".csv");
        integrator.exportToJSON(outputBasePath + ".json");
        integrator.exportToTTL(outputBasePath + ".ttl");

        System.out.println("Exportaciones completadas.");
    }

    /**
     * Procesa una base de datos específica generando los archivos R2RML (TTL) y RDF
     */
    private static void processDatabase(DatabaseConnection dbConnection, String outputTurtlePath, String outputRDFPath) {
        Connection connection = null;

        try {
            connection = dbConnection.connect();
            System.out.println("Conectado a la base de datos: " + dbConnection.getDatabaseType());

            // Generar R2RML (TTL)
            R2RMLGenerator r2rmlGenerator = new R2RMLGenerator(connection);
            r2rmlGenerator.generateTTL(outputTurtlePath);
            System.out.println("Archivo TTL generado en: " + outputTurtlePath);

            // Convertir TTL a RDF/XML
            RDFConverter rdfConverter = new RDFConverter(connection);
            rdfConverter.convertToRDF(outputTurtlePath, outputRDFPath);
            System.out.println("Archivo RDF generado en: " + outputRDFPath);
        } catch (Exception e) {
            System.err.println("Error durante el procesamiento de la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbConnection.disconnect(connection);
        }
    }
}
