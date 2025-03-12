package com.mycompany.filegeneration;

import com.mycompany.database.*;
import com.mycompany.rdfintegration.RDFIntegrator;
import com.mycompany.database.DatabaseConfigHandler;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class IntegrationHandler {

    public void executeIntegration() {
        System.out.println("🔄 Iniciando integración de datos...");

        // Obtener configuraciones de bases de datos
        Map<String, DatabaseConfig> dbConfigs = DatabaseConfigHandler.getAllConfigs();
        System.out.println("📌 Configuraciones obtenidas: " + dbConfigs.keySet());

        if (dbConfigs.isEmpty()) {
            System.out.println("⚠️ No hay configuraciones de bases de datos disponibles. Abortando integración.");
            return;
        }

        for (Map.Entry<String, DatabaseConfig> entry : dbConfigs.entrySet()) {
            String dbName = entry.getKey();
            DatabaseConfig config = entry.getValue();
            System.out.println("🔎 Procesando base de datos: " + dbName);

            // Crear conexión
            DatabaseConnection dbConnection;
            if ("MySQL".equalsIgnoreCase(config.getTipoBD())) {
                dbConnection = new DatabaseConnectionMySQL(
                        config.getHost(),
                        config.getPuerto(),
                        config.getUsuario(),
                        config.getPassword(),
                        config.getNombreBD()
                );
            } else if ("PostgreSQL".equalsIgnoreCase(config.getTipoBD())) {
                dbConnection = new DatabaseConnectionPostgreSQL(
                        config.getHost(),
                        config.getPuerto(),
                        config.getUsuario(),
                        config.getPassword(),
                        config.getNombreBD()
                );
            } else {
                System.out.println("⚠️ Tipo de base de datos no soportado: " + config.getTipoBD());
                continue;
            }

            // Obtener tablas seleccionadas
            List<String> selectedTables = DatabaseSelectionManager.getSelectedTables(dbName);
            System.out.println("📌 Tablas seleccionadas para " + dbName + ": " + selectedTables);

            if (selectedTables.isEmpty()) {
                System.out.println("⚠️ No hay tablas seleccionadas para la base de datos: " + dbName);
                System.out.println("📌 Se ejecutará processDatabase() de todas formas.");
            }

// Configuración de rutas de salida
            String turtlePath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", dbName + "_output.ttl").toString();
            String rdfPath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", dbName + "_output.rdf").toString();

            System.out.println("📂 Generando archivos en:");
            System.out.println("   - TTL: " + turtlePath);
            System.out.println("   - RDF: " + rdfPath);

// Procesar la base de datos, **incluso si no hay tablas seleccionadas**
            processDatabase(dbConnection, turtlePath, rdfPath);

        }

        // Integración RDFs generados
        System.out.println("🔄 Integrando RDFs...");

// Lista de archivos RDF generados dinámicamente
        List<String> rdfFiles = dbConfigs.keySet().stream()
                .map(name -> Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", name + "_output.rdf").toString())
                .toList();

        RDFIntegrator integrator = new RDFIntegrator();

// Verificar que haya al menos un RDF para integrar
        if (rdfFiles.isEmpty()) {
            System.out.println("⚠️ No hay archivos RDF generados para integrar.");
        } else if (rdfFiles.size() == 1) {
            System.out.println("⚠️ Solo se generó un archivo RDF, no hay necesidad de integración múltiple.");
        } else {
            // Tomar el primer RDF como base
            String rdfBase = rdfFiles.get(0);

            // Integrar los siguientes RDFs sobre el primero
            for (int i = 1; i < rdfFiles.size(); i++) {
                System.out.println("🔗 Integrando RDF: " + rdfFiles.get(i) + " en " + rdfBase);
                integrator.integrateRDF(rdfBase, rdfFiles.get(i));
            }
        }

// Exportar en diferentes formatos
        String outputBasePath = "C:/Users/darwi/OneDrive/Desktop/RutaProyecto/unified_output";
        integrator.exportToRDFXML(outputBasePath + ".rdf");
        integrator.exportToCSV(outputBasePath + ".csv");
        integrator.exportToJSON(outputBasePath + ".json");
        integrator.exportToTTL(outputBasePath + ".ttl");

        System.out.println("✅ Integración finalizada. Exportaciones completadas.");

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

}
