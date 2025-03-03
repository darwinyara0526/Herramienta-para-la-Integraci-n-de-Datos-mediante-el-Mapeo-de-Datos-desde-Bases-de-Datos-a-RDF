package com.mycompany.rdfintegration;

import org.apache.jena.rdf.model.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase para convertir un modelo RDF a formato CSV.
 */
public class RDFtoCSVConverter {

    /**
     * Exporta un modelo RDF a un archivo CSV.
     *
     * @param model     Modelo RDF a exportar.
     * @param filePath  Ruta del archivo CSV de salida.
     */
    public void exportToCSV(Model model, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Escribir encabezados
            writer.write("Subject,Predicate,Object\n");

            // Iterar sobre todas las declaraciones (tripletas)
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                String subject = stmt.getSubject().toString();
                String predicate = stmt.getPredicate().toString();
                String object = stmt.getObject().toString();

                // Escribir en formato CSV
                writer.write(escapeCSV(subject) + "," + escapeCSV(predicate) + "," + escapeCSV(object) + "\n");
            }

            System.out.println("Exportación a CSV completada: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Escapa caracteres especiales en CSV (maneja comillas y comas).
     */
    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\""); // Doble comilla para escapar
            return "\"" + value + "\""; // Envolver en comillas dobles
        }
        return value;
    }
}
