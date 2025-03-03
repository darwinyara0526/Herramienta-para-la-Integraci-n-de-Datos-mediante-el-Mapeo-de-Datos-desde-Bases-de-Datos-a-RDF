package com.mycompany.filegeneration;

import java.util.*;

public class PredicateMapper {

    private static final Map<String, String> COLUMN_PREDICATES = new HashMap<>();
    private static final Set<String> DATE_TYPES = Set.of("date", "timestamp", "datetime");
    private static final Set<String> NUMERIC_TYPES = Set.of("decimal", "numeric", "float", "double");
    private static final Set<String> INT_TYPES = Set.of("int", "smallint", "integer");

    static {
        COLUMN_PREDICATES.put("nombre", "foaf:name");
        COLUMN_PREDICATES.put("email", "ex:correo"); // Corrección de prefijo
        COLUMN_PREDICATES.put("telefono", "foaf:phone");
        COLUMN_PREDICATES.put("direccion", "schema:address");
        COLUMN_PREDICATES.put("ciudad", "schema:addressLocality");
        COLUMN_PREDICATES.put("pais", "schema:addressCountry");
        COLUMN_PREDICATES.put("codigo_postal", "schema:postalCode");
        COLUMN_PREDICATES.put("precio", "schema:price");
        COLUMN_PREDICATES.put("cantidad", "schema:inventoryLevel");
        COLUMN_PREDICATES.put("descripcion", "schema:description");
        COLUMN_PREDICATES.put("id", "dct:identifier");
        COLUMN_PREDICATES.put("url", "schema:url");
        COLUMN_PREDICATES.put("categoria", "ex:id_categoria"); // Corrección en la referencia de categoría
        COLUMN_PREDICATES.put("sexo", "ex:sexo");
    }

    public static String getPredicate(String columnName, String sqlType) {
        String lowerCol = columnName.toLowerCase();
        String lowerType = sqlType.toLowerCase();

        String predicate = COLUMN_PREDICATES.get(lowerCol);
        if (predicate != null) return predicate;

        // Manejo de fechas diferenciando "dateCreated" y "dateModified"
        if (DATE_TYPES.contains(lowerType)) {
            if (lowerCol.contains("modificado") || lowerCol.contains("updated") || lowerCol.contains("change") || lowerCol.contains("venta")) {
                return "schema:dateModified";
            }
            return "schema:dateCreated";
        }

        if (NUMERIC_TYPES.contains(lowerType)) return "schema:price";
        if (INT_TYPES.contains(lowerType)) return "schema:inventoryLevel";

        return "ex:" + columnName.replace("_", ""); // Remueve guiones bajos para consistencia
    }

    public static String getClassForTable(String tableName) {
        String lowerTable = tableName.toLowerCase();

        if (lowerTable.contains("cliente") || lowerTable.contains("personas") || lowerTable.contains("usuario")) {
            return "schema:Person";
        } else if (lowerTable.contains("producto") || lowerTable.contains("item")) {
            return "schema:Product";
        } else if (lowerTable.contains("orden") || lowerTable.contains("factura")) {
            return "schema:Order";
        } else if (lowerTable.contains("empresa") || lowerTable.contains("compania")) {
            return "schema:Organization";
        } else if (lowerTable.contains("categoria")) {
            return "schema:Category";
        }

        return "ex:" + tableName.replace("_", "");
    }
}
