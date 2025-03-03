package com.mycompany.rdfintegration;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.*;

public class RDFIntegrator {

    private Model unifiedModel;

    public RDFIntegrator() {
        this.unifiedModel = ModelFactory.createDefaultModel();
    }

    public void integrateRDF(String mysqlRDFPath, String postgresRDFPath) {
        try {
            unifiedModel.read(mysqlRDFPath);
            unifiedModel.read(postgresRDFPath);
            System.out.println("Modelos RDF integrados correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queryRDF() {
        String sparqlQuery = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object } LIMIT 10";
        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, unifiedModel)) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(System.out, results, query);
        }
    }

    public void exportToRDFXML(String outputPath) {
        try (OutputStream out = new FileOutputStream(outputPath)) {
            unifiedModel.write(out, "RDF/XML");
            System.out.println("Exportado a RDF/XML: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToCSV(String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            StmtIterator iter = unifiedModel.listStatements();
            writer.write("Subject,Predicate,Object\n");
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                writer.write(stmt.getSubject() + "," + stmt.getPredicate() + "," + stmt.getObject() + "\n");
            }
            System.out.println("Exportado a CSV: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToJSON(String outputPath) {
        try (OutputStream out = new FileOutputStream(outputPath)) {
            RDFDataMgr.write(out, unifiedModel, RDFFormat.JSONLD);
            System.out.println("Exportado a JSON: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToTTL(String outputPath) {
        try (OutputStream out = new FileOutputStream(outputPath)) {
            RDFDataMgr.write(out, unifiedModel, RDFFormat.TURTLE);
            System.out.println("Exportado a TTL: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
