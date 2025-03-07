package com.mycompany.filegeneration;

import com.mycompany.database.DatabaseConnectionMySQL;
import com.mycompany.database.DatabaseConnectionPostgreSQL;
import com.mycompany.rdfintegration.RDFIntegrator;
import com.mycompany.database.DatabaseConnection;
import java.nio.file.Paths;
import java.sql.Connection;

public class IntegrationHandler {

    public void executeIntegration() {
        // Datos de conexión - Reemplaza estos valores con los correctos
        String hostMySQL = "127.0.0.2";
        String puertoMySQL = "3344";
        String usuarioMySQL = "roo33t";
        String passwordMySQL = "";
        String nombreBDMySQL = "proyecto";

        String hostPostgreSQL = "localhost";
        String puertoPostgreSQL = "5432";
        String usuarioPostgreSQL = "postgres";
        String passwordPostgreSQL = "1familiayara";
        String nombreBDPostgreSQL = "productos";

        // Crear instancias de conexión con los parámetros correctos
        DatabaseConnectionMySQL mysqlConnection = new DatabaseConnectionMySQL(hostMySQL, puertoMySQL, usuarioMySQL, passwordMySQL, nombreBDMySQL);
        DatabaseConnectionPostgreSQL postgresConnection = new DatabaseConnectionPostgreSQL(hostPostgreSQL, puertoPostgreSQL, usuarioPostgreSQL, passwordPostgreSQL, nombreBDPostgreSQL);

        // Configuración de rutas de salida
        String mysqlTurtlePath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "mysql_output.ttl").toString();
        String mysqlRDFPath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "mysql_output.rdf").toString();

        String postgresTurtlePath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "postgres_output.ttl").toString();
        String postgresRDFPath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "postgres_output.rdf").toString();

        // Procesar bases de datos
        processDatabase(mysqlConnection, mysqlTurtlePath, mysqlRDFPath);
        processDatabase(postgresConnection, postgresTurtlePath, postgresRDFPath);

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
     * Procesa una base de datos específica generando los archivos R2RML (TTL) y
     * RDF
     */
    private void processDatabase(DatabaseConnection dbConnection, String outputTurtlePath, String outputRDFPath) {
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
