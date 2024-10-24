package com.mycompany.proyectov2;

import com.mycompany.filegeneration.R2RMLGenerator;
import com.mycompany.filegeneration.RDFConverter;
import com.mycompany.filegeneration.DataReader;
import com.mycompany.database.DatabaseConnectionMySQL;
import com.mycompany.database.DatabaseConnectionPostgreSQL;
import com.mycompany.database.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Rutas para los archivos R2RML y RDF de MySQL
        String mysqlTurtlePath = "/home/darwin/Escritorio/Proyecto/mysql_output.ttl";
        String mysqlRDFPath = "/home/darwin/Escritorio/Proyecto/mysql_output.rdf";

        // Rutas para los archivos R2RML y RDF de PostgreSQL
        String postgresTurtlePath = "/home/darwin/Escritorio/Proyecto/postgres_output.ttl";
        String postgresRDFPath = "/home/darwin/Escritorio/Proyecto/postgres_output.rdf";

        // Procesar MySQL automáticamente
        processDatabase(new DatabaseConnectionMySQL(), mysqlTurtlePath, mysqlRDFPath);

        // Procesar PostgreSQL automáticamente
        processDatabase(new DatabaseConnectionPostgreSQL(), postgresTurtlePath, postgresRDFPath);
    }

    /**
     * Método para procesar una base de datos y generar los archivos TTL y RDF
     */
    private static void processDatabase(DatabaseConnection dbConnection, String outputTurtlePath, String outputRDFPath) {
        Connection connection = null; // Declarar conexión

        try {
            // Conectar a la base de datos
            connection = dbConnection.connect();
            System.out.println("Conectado a la base de datos: " + dbConnection.getDatabaseType());

            // Instancia del generador R2RML
            R2RMLGenerator r2rmlGenerator = new R2RMLGenerator(connection);

            // Leer y procesar todas las tablas de la base de datos automáticamente
            DataReader dataReader = new DataReader(connection);
            dataReader.readTables();  // Se encargará de leer todas las tablas automáticamente

            // Generar el archivo R2RML (Turtle)
            r2rmlGenerator.generateR2RML(outputTurtlePath);
            System.out.println("Archivo TTL generado en: " + outputTurtlePath);

            // Convertir el archivo Turtle a RDF/XML
            RDFConverter rdfConverter = new RDFConverter(connection);
            rdfConverter.convertToRDF(outputTurtlePath, outputRDFPath);
            System.out.println("Archivo RDF generado en: " + outputRDFPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Asegurarse de desconectar la base de datos
            dbConnection.disconnect(connection);
        }
    }
}
