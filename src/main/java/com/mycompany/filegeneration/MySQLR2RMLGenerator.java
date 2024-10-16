package com.mycompany.filegeneration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MySQLR2RMLGenerator {

    private Connection connection; // Conexión a la base de datos

    // Constructor que inicializa la conexión a la base de datos
    public MySQLR2RMLGenerator(Connection connection) {
        this.connection = connection;
    }

    // Método principal para generar el archivo R2RML en formato TTL
    public void generateR2RML(String outputFilePath) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        String[] types = {"TABLE"};
        ResultSet tables = metaData.getTables(null, null, "%", types);

        // Crear archivo de salida TTL
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                // Ignorar tabla `sys_config`
                if (tableName.equalsIgnoreCase("sys_config")) {
                    continue;
                }

                // Obtener la columna única (clave primaria)
                String primaryKeyColumn = getPrimaryKeyColumn(tableName);
                if (primaryKeyColumn == null) {
                    System.err.println("No se encontró clave primaria para la tabla: " + tableName);
                    continue; // Si no hay clave primaria, saltar a la siguiente tabla
                }

                // Obtener columnas de la tabla
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME").toLowerCase(); // Obtener tipo de dato en minúsculas
                    String rdfDataType = getRDFDataType(dataType); // Convertir al tipo de dato RDF adecuado

                    // Generar plantilla R2RML para esta columna
                    String tripleMap = createTripleMap(tableName, columnName, primaryKeyColumn, rdfDataType);

                    // Escribir la plantilla al archivo TTL
                    writer.write(tripleMap);
                }
            }

            System.out.println("Archivo R2RML TTL generado correctamente en: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
            throw e; // Lanzar la excepción para que sea manejada por el llamador
        } catch (Exception e) {
            System.err.println("Error al generar el archivo R2RML: " + e.getMessage());
            throw e; // Lanzar la excepción para que sea manejada por el llamador
        }
    }

    // Método para obtener la columna clave primaria de una tabla
    private String getPrimaryKeyColumn(String tableName) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

        // Retornar la primera clave primaria encontrada
        if (primaryKeys.next()) {
            return primaryKeys.getString("COLUMN_NAME");
        }
        return null; // Si no hay clave primaria
    }

    // Método para crear el triple map en formato TTL para la tabla y la columna dadas
    private String createTripleMap(String tableName, String columnName, String primaryKeyColumn, String rdfDataType) {
        return String.format("<#%s_%s>\n"
                + "    a rr:TriplesMap;\n"
                + "    rr:logicalTable [ rr:tableName \"%s\" ];\n"
                + "    rr:subjectMap [ rr:template \"http://example.com/%s/{%s}\" ];\n"
                + "    rr:predicateObjectMap [\n"
                + "        rr:predicate \"http://example.com/schema/%s\";\n"
                + "        rr:objectMap [ rr:column \"%s\"; rr:datatype %s ]\n"
                + "    ].\n\n",
                escapeIdentifier(tableName), escapeIdentifier(columnName), escapeIdentifier(tableName),
                escapeIdentifier(tableName), escapeIdentifier(primaryKeyColumn), escapeIdentifier(columnName),
                escapeIdentifier(columnName), rdfDataType);
    }

    // Escapar caracteres especiales en identificadores
    private String escapeIdentifier(String identifier) {
        return identifier.replaceAll("[\"\\\\]", "\\\\$0"); // Escapa comillas y barras invertidas
    }

    // Mapa para convertir tipos de datos de SQL a tipos de datos RDF (XSD)
    private String getRDFDataType(String sqlType) {
        Map<String, String> typeMap = new HashMap<>();

        // Tipos de datos XSD
        typeMap.put("int", "xsd:integer");
        typeMap.put("integer", "xsd:integer");
        typeMap.put("tinyint", "xsd:boolean");  // Asumir que tinyint es booleano (0/1)
        typeMap.put("smallint", "xsd:integer");
        typeMap.put("bigint", "xsd:integer");
        typeMap.put("bit", "xsd:boolean");
        typeMap.put("float", "xsd:float");
        typeMap.put("double", "xsd:double");
        typeMap.put("decimal", "xsd:decimal");
        typeMap.put("numeric", "xsd:decimal");
        typeMap.put("date", "xsd:date");
        typeMap.put("datetime", "xsd:dateTime");
        typeMap.put("timestamp", "xsd:dateTime");
        typeMap.put("varchar", "xsd:string");
        typeMap.put("char", "xsd:string");
        typeMap.put("text", "xsd:string");

        // Tipos adicionales XSD
        typeMap.put("boolean", "xsd:boolean");
        typeMap.put("byte", "xsd:byte");
        typeMap.put("short", "xsd:short");
        typeMap.put("long", "xsd:long");
        typeMap.put("unsignedByte", "xsd:unsignedByte");
        typeMap.put("unsignedShort", "xsd:unsignedShort");
        typeMap.put("unsignedInt", "xsd:unsignedInt");
        typeMap.put("unsignedLong", "xsd:unsignedLong");
        typeMap.put("hexBinary", "xsd:hexBinary");
        typeMap.put("base64Binary", "xsd:base64Binary");
        typeMap.put("anyURI", "xsd:anyURI");
        typeMap.put("QName", "xsd:QName");
        typeMap.put("string", "xsd:string");
        typeMap.put("dateTime", "xsd:dateTime");
        typeMap.put("duration", "xsd:duration");
        typeMap.put("gDay", "xsd:gDay");
        typeMap.put("gMonth", "xsd:gMonth");
        typeMap.put("gMonthDay", "xsd:gMonthDay");
        typeMap.put("gYear", "xsd:gYear");
        typeMap.put("gYearMonth", "xsd:gYearMonth");
        typeMap.put("time", "xsd:time");
        typeMap.put("normalizedString", "xsd:normalizedString");
        typeMap.put("token", "xsd:token");
        typeMap.put("language", "xsd:language");
        typeMap.put("NMTOKEN", "xsd:NMTOKEN");
        typeMap.put("NMTOKENS", "xsd:NMTOKENS");
        typeMap.put("Name", "xsd:Name");
        typeMap.put("NCName", "xsd:NCName");
        typeMap.put("ID", "xsd:ID");
        typeMap.put("IDREF", "xsd:IDREF");
        typeMap.put("IDREFS", "xsd:IDREFS");
        typeMap.put("ENTITY", "xsd:ENTITY");
        typeMap.put("ENTITIES", "xsd:ENTITIES");

        return typeMap.getOrDefault(sqlType, "xsd:string"); // Si el tipo no está mapeado, usar string por defecto
    }

}
