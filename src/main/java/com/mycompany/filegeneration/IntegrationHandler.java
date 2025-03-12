package com.mycompany.filegeneration;

import com.mycompany.database.*;
import com.mycompany.rdfintegration.RDFIntegrator;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class IntegrationHandler {

    public void executeIntegration(Stage primaryStage) { 
        System.out.println("🔄 Iniciando integración de datos...");

        // Obtener configuraciones de bases de datos
        Map<String, DatabaseConfig> dbConfigs = DatabaseConfigHandler.getAllConfigs();
        System.out.println("📌 Configuraciones obtenidas: " + dbConfigs.keySet());

        if (dbConfigs.isEmpty()) {
            System.out.println("⚠️ No hay configuraciones de bases de datos disponibles. Abortando integración.");
            return;
        }

        // Pedir al usuario que seleccione la carpeta de destino
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de destino");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory == null) {
            System.out.println("⚠️ No se seleccionó ninguna carpeta. Cancelando exportación.");
            return;
        }

        String outputDirectory = selectedDirectory.getAbsolutePath();
        System.out.println("📂 Carpeta seleccionada: " + outputDirectory);

        for (Map.Entry<String, DatabaseConfig> entry : dbConfigs.entrySet()) {
            String dbName = entry.getKey();
            DatabaseConfig config = entry.getValue();
            System.out.println("🔎 Procesando base de datos: " + dbName);

            // Crear conexión
            DatabaseConnection dbConnection;
            if ("MySQL".equalsIgnoreCase(config.getTipoBD())) {
                dbConnection = new DatabaseConnectionMySQL(
                        config.getHost(), config.getPuerto(), config.getUsuario(),
                        config.getPassword(), config.getNombreBD()
                );
            } else if ("PostgreSQL".equalsIgnoreCase(config.getTipoBD())) {
                dbConnection = new DatabaseConnectionPostgreSQL(
                        config.getHost(), config.getPuerto(), config.getUsuario(),
                        config.getPassword(), config.getNombreBD()
                );
            } else {
                System.out.println("⚠️ Tipo de base de datos no soportado: " + config.getTipoBD());
                continue;
            }

            // Obtener tablas seleccionadas
            List<String> selectedTables = DatabaseSelectionManager.getSelectedTables(dbName);
            System.out.println("📌 Tablas seleccionadas para " + dbName + ": " + selectedTables);

            // Definir rutas dinámicas
            String turtlePath = Paths.get(outputDirectory, dbName + "_output.ttl").toString();
            String rdfPath = Paths.get(outputDirectory, dbName + "_output.rdf").toString();

            System.out.println("📂 Guardando archivos en:");
            System.out.println("   - TTL: " + turtlePath);
            System.out.println("   - RDF: " + rdfPath);

            // Procesar la base de datos
            processDatabase(dbConnection, turtlePath, rdfPath);
        }

        // Integración RDFs generados
        System.out.println("🔄 Integrando RDFs...");
        RDFIntegrator integrator = new RDFIntegrator();

        // Exportar en diferentes formatos en la carpeta seleccionada
        String outputBasePath = Paths.get(outputDirectory, "unified_output").toString();
        integrator.exportToRDFXML(outputBasePath + ".rdf");
        integrator.exportToCSV(outputBasePath + ".csv");
        integrator.exportToJSON(outputBasePath + ".json");
        integrator.exportToTTL(outputBasePath + ".ttl");

        System.out.println("✅ Integración finalizada. Exportaciones completadas.");

        // Abrir la carpeta automáticamente
        openFolder(outputDirectory);
    }

    private void processDatabase(DatabaseConnection dbConnection, String outputTurtlePath, String outputRDFPath) {
        Connection connection = null;

        try {
            connection = dbConnection.connect();
            System.out.println("✅ Conectado a la base de datos: " + dbConnection.getDatabaseType());

            // Generar R2RML (TTL)
            R2RMLGenerator r2rmlGenerator = new R2RMLGenerator(connection);
            System.out.println("🔄 Generando archivo TTL...");
            r2rmlGenerator.generateTTL(outputTurtlePath);
            System.out.println("📄 Archivo TTL generado en: " + outputTurtlePath);

            // Convertir TTL a RDF/XML
            RDFConverter rdfConverter = new RDFConverter(connection);
            System.out.println("🔄 Convirtiendo TTL a RDF/XML...");
            rdfConverter.convertToRDF(outputTurtlePath, outputRDFPath);
            System.out.println("📄 Archivo RDF generado en: " + outputRDFPath);

        } catch (Exception e) {
            System.err.println("❌ Error durante el procesamiento de la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbConnection.disconnect(connection);
            System.out.println("🔌 Desconectado de la base de datos.");
        }
    }

    private void openFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (folder.exists()) {
                System.out.println("📂 Abriendo carpeta: " + folderPath);
                new ProcessBuilder("explorer.exe", folderPath).start(); // Abre la carpeta en Windows
            } else {
                System.out.println("⚠️ La carpeta no existe.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al abrir la carpeta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
