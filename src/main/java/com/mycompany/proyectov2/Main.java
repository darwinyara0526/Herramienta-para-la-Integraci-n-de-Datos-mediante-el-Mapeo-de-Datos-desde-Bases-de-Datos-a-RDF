package com.mycompany.proyectov2;

import com.mycompany.filegeneration.R2RMLGenerator;
import com.mycompany.filegeneration.RDFConverter;
import com.mycompany.filegeneration.DataReader;
import com.mycompany.database.DatabaseConnectionMySQL;
import com.mycompany.database.DatabaseConnectionPostgreSQL;
import com.mycompany.database.DatabaseConnection;
import com.mycompany.interfaces.LoginInterface;
import com.mycompany.rdfintegration.RDFIntegrator;
import javax.swing.SwingUtilities;

import java.sql.Connection;

/**
 * Clase principal del proyecto para la generación automática de archivos R2RML (TTL) y RDF
 * a partir de bases de datos relacionales.
 */
public class Main {

    public static void main(String[] args) {
        // Configuración de rutas de salida para MySQL
        //String mysqlTurtlePath = "/home/darwin/Escritorio/Proyecto/mysql_output.ttl";
        //String mysqlRDFPath = "/home/darwin/Escritorio/Proyecto/mysql_output.rdf";

        // Configuración de rutas de salida para PostgreSQL
        //String postgresTurtlePath = "/home/darwin/Escritorio/Proyecto/postgres_output.ttl";
        //String postgresRDFPath = "/home/darwin/Escritorio/Proyecto/postgres_output.rdf";

        // Procesa automáticamente la base de datos MySQL
        //processDatabase(new DatabaseConnectionMySQL(), mysqlTurtlePath, mysqlRDFPath);

        // Procesa automáticamente la base de datos PostgreSQL
        //processDatabase(new DatabaseConnectionPostgreSQL(), postgresTurtlePath, postgresRDFPath);

        // Integración de los archivos RDF de MySQL y PostgreSQL
        //RDFIntegrator integrator = new RDFIntegrator();
        //integrator.integrateAndExport(mysqlRDFPath, postgresRDFPath);
        
        // Iniciar la Login
        SwingUtilities.invokeLater(() -> new LoginInterface().main(new String[0])); // Llamada a LoginInterface
    }

    /**
     * Procesa una base de datos específica generando los archivos R2RML (TTL) y RDF
     *
     * @param dbConnection     Objeto que maneja la conexión a la base de datos
     * @param outputTurtlePath Ruta de salida del archivo .ttl generado
     * @param outputRDFPath    Ruta de salida del archivo .rdf generado
     */
    private static void processDatabase(DatabaseConnection dbConnection, String outputTurtlePath, String outputRDFPath) {
        Connection connection = null;

        try {
            // Establecer conexión a la base de datos
            connection = dbConnection.connect();
            System.out.println("Conectado a la base de datos: " + dbConnection.getDatabaseType());

            // Instancia del generador R2RML para la conexión actual
            R2RMLGenerator r2rmlGenerator = new R2RMLGenerator(connection);

            // Instancia de DataReader para leer las tablas automáticamente
            DataReader dataReader = new DataReader(connection);
            dataReader.readTables();

            // Generación del archivo R2RML (formato Turtle)
            r2rmlGenerator.generateR2RML(outputTurtlePath);
            System.out.println("Archivo TTL generado en: " + outputTurtlePath);

            // Conversión del archivo R2RML (TTL) a RDF/XML
            RDFConverter rdfConverter = new RDFConverter(connection);
            rdfConverter.convertToRDF(outputTurtlePath, outputRDFPath);
            System.out.println("Archivo RDF generado en: " + outputRDFPath);
        } catch (Exception e) {
            System.err.println("Error durante el procesamiento de la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Desconexión segura de la base de datos
            dbConnection.disconnect(connection);
        }
    }
}
