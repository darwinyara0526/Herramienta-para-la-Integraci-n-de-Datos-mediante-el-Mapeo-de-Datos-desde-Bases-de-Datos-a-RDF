package com.mycompany.rdfintegration;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

public class RDFIntegrator {

    public void integrateAndExport(String mysqlRDFPath, String postgresRDFPath) {
        try {
            // Leer los archivos RDF de MySQL y PostgreSQL y unificarlos en un solo modelo
            Model unifiedModel = ModelFactory.createDefaultModel();
            unifiedModel.read(mysqlRDFPath);
            unifiedModel.read(postgresRDFPath);

            // Consultar el modelo unificado con SPARQL (ejemplo simple)
            String sparqlQuery = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object } LIMIT 10";
            Query query = QueryFactory.create(sparqlQuery);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, unifiedModel)) {
                ResultSet results = qexec.execSelect();
                ResultSetFormatter.out(System.out, results, query);
            }

            // Exportar el modelo unificado a un archivo RDF/XML
            String outputUnifiedRDFPath = Paths.get("C:", "Users", "darwi", "OneDrive", "Desktop", "RutaProyecto", "unified_output.rdf").toString();
            try (OutputStream out = new FileOutputStream(outputUnifiedRDFPath)) {
                unifiedModel.write(out, "RDF/XML");
                System.out.println("Modelo RDF unificado exportado a: " + outputUnifiedRDFPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
