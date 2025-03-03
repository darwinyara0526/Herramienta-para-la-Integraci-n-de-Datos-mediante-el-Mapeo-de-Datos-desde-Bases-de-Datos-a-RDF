package com.mycompany.rdfintegration;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import java.io.*;

public class RDFtoHTMLConverter {
    
    public static String convertRDFtoHTML(String rdfFilePath) {
        Model model = ModelFactory.createDefaultModel();
        try (InputStream in = new FileInputStream(rdfFilePath)) {
            model.read(in, null, "TTL"); // Cambia "TTL" a "RDF/XML" si el archivo es RDF/XML
        } catch (IOException e) {
            e.printStackTrace();
            return "Error leyendo el archivo RDF";
        }
        
        String queryString = """
            PREFIX foaf: <http://xmlns.com/foaf/0.1/>
            PREFIX schema: <https://schema.org/>
            SELECT ?id ?name ?type
            WHERE {
                ?entity a ?type .
                OPTIONAL { ?entity foaf:name ?name }
                BIND(STR(?entity) AS ?id)
            }
        """;

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>RDF Data</title></head><body>");
            html.append("<h2>Datos RDF</h2>");
            html.append("<table border='1'><tr><th>ID</th><th>Nombre</th><th>Tipo</th></tr>");

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String id = soln.get("id").toString();
                String name = soln.contains("name") ? soln.get("name").toString() : "-";
                String type = soln.get("type").toString();

                html.append("<tr>");
                html.append("<td>" + id + "</td>");
                html.append("<td>" + name + "</td>");
                html.append("<td>" + type + "</td>");
                html.append("</tr>");
            }

            html.append("</table></body></html>");
            return html.toString();
        }
    }

    public static void main(String[] args) {
        String rdfFilePath = "data.ttl"; // Cambia el nombre del archivo según tu necesidad
        String htmlContent = convertRDFtoHTML(rdfFilePath);
        
        try (PrintWriter out = new PrintWriter("output.html")) {
            out.println(htmlContent);
            System.out.println("Archivo HTML generado: output.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
