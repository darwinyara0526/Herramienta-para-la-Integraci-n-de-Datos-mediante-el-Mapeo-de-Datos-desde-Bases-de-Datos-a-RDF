package com.mycompany.rdfintegration;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.StringWriter;

public class RDFtoJSONConverter {
    public static String convertRDFtoJSON(String rdfFilePath) {
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, rdfFilePath);

        JSONArray jsonArray = new JSONArray();
        StmtIterator iter = model.listStatements();

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("subject", stmt.getSubject().toString());
            jsonObject.put("predicate", stmt.getPredicate().toString());
            if (stmt.getObject().isLiteral()) {
                jsonObject.put("object", stmt.getObject().asLiteral().getString());
            } else {
                jsonObject.put("object", stmt.getObject().toString());
            }
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString(2);
    }

    public static void main(String[] args) {
        String rdfFilePath = "data.rdf"; // Reemplaza con la ruta de tu archivo RDF
        String jsonOutput = convertRDFtoJSON(rdfFilePath);
        System.out.println(jsonOutput);
    }
}
