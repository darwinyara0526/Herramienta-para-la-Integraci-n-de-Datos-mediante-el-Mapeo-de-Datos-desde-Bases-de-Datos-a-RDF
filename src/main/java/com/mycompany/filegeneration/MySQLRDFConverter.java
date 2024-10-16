package com.mycompany.filegeneration;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

import java.io.FileOutputStream;
import java.io.IOException;

public class MySQLRDFConverter {
    
    public void convertToRDF(String turtleFilePath, String outputRDFPath) {
        // Verifica si el archivo RDF ya fue generado en el generador de R2RML
        System.out.println("Iniciando la conversión de R2RML a RDF...");
        
        try {
            Model model = ModelFactory.createDefaultModel();
            // Carga el modelo RDF desde el archivo Turtle
            RDFDataMgr.read(model, turtleFilePath);
            
            // Genera el archivo RDF/XML
            try (FileOutputStream out = new FileOutputStream(outputRDFPath)) {
                model.write(out, "RDF/XML");
                System.out.println("Archivo RDF generado en: " + outputRDFPath);
            }
        } catch (IOException e) {
            System.err.println("Error al generar el archivo RDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
