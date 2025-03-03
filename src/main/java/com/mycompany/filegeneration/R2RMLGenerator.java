package com.mycompany.filegeneration;

import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generador de archivos TTL desde una base de datos relacional.
 */
public class R2RMLGenerator {

    private Connection connection;

    public R2RMLGenerator(Connection connection) {
        this.connection = connection;
    }

    public void generateTTL(String outputFilePath) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String[] types = {"TABLE"};
            ResultSet tables = metaData.getTables(null, null, "%", types);

            try (FileWriter writer = new FileWriter(outputFilePath, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write("@prefix ex: <http://example.com/schema/> .\n");
                writer.write("@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n");
                writer.write("@prefix schema: <https://schema.org/> .\n");
                writer.write("@prefix dct: <http://purl.org/dc/terms/> .\n");
                writer.write("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n\n");

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    if (isSystemTable(tableName)) {
                        continue;
                    }

                    String primaryKeyColumn = getPrimaryKeyColumn(tableName);
                    if (primaryKeyColumn == null) {
                        System.err.println("No se encontró clave primaria para la tabla: " + tableName);
                        continue;
                    }

                    String rdfClass = getRDFClass(tableName);
                    String query = "SELECT * FROM " + tableName;
                    try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(query)) {

                        ResultSetMetaData rsMetaData = resultSet.getMetaData();
                        int columnCount = rsMetaData.getColumnCount();

                        while (resultSet.next()) {
                            String primaryKeyValue = resultSet.getString(primaryKeyColumn);
                            String subject = "ex:" + escapeIdentifier(tableName) + "_" + escapeIdentifier(primaryKeyValue);

                            writer.write(subject + " a " + rdfClass + " ;\n");

                            boolean firstPredicate = true;

                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = rsMetaData.getColumnName(i);
                                String columnValue = resultSet.getString(columnName);
                                String sqlType = rsMetaData.getColumnTypeName(i).toLowerCase();

                                if (columnValue != null) {
                                    String predicate = PredicateMapper.getPredicate(columnName, sqlType);
                                    String rdfDataType = getRDFDataType(sqlType);
                                    columnValue = formatValue(sqlType, columnValue);

                                    if (!firstPredicate) {
                                        writer.write("    ");
                                    }
                                    writer.write(predicate + " \"" + escapeLiteral(columnValue) + "\"^^" + rdfDataType);

                                    firstPredicate = false;
                                    if (i < columnCount) {
                                        writer.write(" ;\n");
                                    }
                                }
                            }
                            writer.write(" .\n\n");
                        }
                    }
                }
                System.out.println("Archivo TTL generado correctamente en: " + outputFilePath);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private boolean isSystemTable(String tableName) {
        String[] systemTablePrefixes = {
            // MySQL y MariaDB
            "pma__", "mysql", "sys", "performance_schema", "information_schema",
            // PostgreSQL
            "pg_", "pg_catalog", "pg_toast", "pg_statistic", "pg_temp_", "pg_logical_", "pg_snapshot", "pg_subscription", "pg_stat_", "pg_roles", "pg_user",
            // Oracle Database
            "dba_", "all_", "user_", "cdb_", "v$", "gv$", "sys_",
            // SQL Server
            "sys.", "msdb", "model", "tempdb", "resource", "db_", "dm_", "fn_", "sp_", "xp_",
            // SQLite
            "sqlite_",
            // IBM Db2
            "sysibm.", "syscat.", "sysstat.", "sysdummy1", "syspublic.", "sysproc.", "sysibmadm.",
            // MongoDB (Colecciones del sistema)
            "system.", "admin.", "local.", "config.", "oplog.rs", "oplog.$main",
            // Cassandra
            "system", "system_schema", "system_auth", "system_distributed", "system_traces",
            // Redis (Convenciones típicas de claves del sistema)
            "__", "redis_", "backup_", "config_",
            // Neo4j (Índices y metadatos del sistema)
            "system", "schema", "dbms.", "metadata.", "internal.",
            // CouchDB (Bases de datos internas y metadatos)
            "_users", "_replicator", "_global_changes", "_metadata",
            // DynamoDB (Tablas internas y metadatos)
            "aws_dynamodb_", "system_", "stream_", "backup_",
            // Firebase Firestore (Colecciones internas y metadatos)
            "__", "firebase_", "firestore_", "config_"
        };

        for (String prefix : systemTablePrefixes) {
            if (tableName.toLowerCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private String getPrimaryKeyColumn(String tableName) {
        try {
            ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(null, null, tableName);
            if (primaryKeys.next()) {
                return primaryKeys.getString("COLUMN_NAME");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo clave primaria para " + tableName + ": " + e.getMessage());
        }
        return null;
    }

    private String formatValue(String sqlType, String value) {
        if (sqlType.equalsIgnoreCase("date")) {
            try {
                LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return date.toString();
            } catch (Exception e) {
                System.err.println("Error parsing date: " + value);
            }
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("timestamp")) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                System.err.println("Error parsing datetime: " + value);
            }
        }
        return value;
    }

    private String escapeIdentifier(String identifier) {
        return identifier.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private String escapeLiteral(String literal) {
        return literal.replace("\"", "\\\"");
    }

    private String getRDFClass(String tableName) {
        tableName = tableName.toLowerCase(); // Normalizar a minúsculas para evitar errores

        if (tableName.contains("cliente") || tableName.contains("persona") || tableName.contains("usuario")) {
            return "schema:Person"; // Para clientes, usuarios y personas en general
        } else if (tableName.contains("producto") || tableName.contains("item") || tableName.contains("articulo")) {
            return "schema:Product"; // Para productos, artículos y bienes
        } else if (tableName.contains("categoria") || tableName.contains("tipo") || tableName.contains("grupo")) {
            return "schema:Category"; // Para categorías y agrupaciones
        } else if (tableName.contains("pedido") || tableName.contains("orden") || tableName.contains("factura")) {
            return "schema:Order"; // Para pedidos y facturas
        } else if (tableName.contains("empleado") || tableName.contains("trabajador") || tableName.contains("staff")) {
            return "schema:EmployeeRole"; // Para empleados y roles laborales
        } else if (tableName.contains("empresa") || tableName.contains("negocio") || tableName.contains("compania")) {
            return "schema:Organization"; // Para empresas y organizaciones
        } else if (tableName.contains("direccion") || tableName.contains("ubicacion") || tableName.contains("sucursal")) {
            return "schema:Place"; // Para direcciones y ubicaciones
        } else if (tableName.contains("evento") || tableName.contains("reunion") || tableName.contains("cita")) {
            return "schema:Event"; // Para eventos y reuniones
        } else if (tableName.contains("transaccion") || tableName.contains("pago") || tableName.contains("compra")) {
            return "schema:Payment"; // Para pagos y transacciones
        } else if (tableName.contains("vehiculo") || tableName.contains("auto") || tableName.contains("coche")) {
            return "schema:Vehicle"; // Para vehículos y transporte
        } else if (tableName.contains("documento") || tableName.contains("archivo") || tableName.contains("contrato")) {
            return "schema:Document"; // Para documentos y archivos
        } else if (tableName.contains("recurso") || tableName.contains("servicio") || tableName.contains("activo")) {
            return "schema:Service"; // Para servicios y activos
        } else if (tableName.contains("comentario") || tableName.contains("review") || tableName.contains("valoracion")) {
            return "schema:Review"; // Para comentarios y reseñas
        } else if (tableName.contains("mensaje") || tableName.contains("notificacion") || tableName.contains("email")) {
            return "schema:Message"; // Para mensajes y notificaciones
        } else if (tableName.contains("publicacion") || tableName.contains("post") || tableName.contains("blog")) {
            return "schema:BlogPosting"; // Para blogs y publicaciones
        } else if (tableName.contains("foto") || tableName.contains("imagen") || tableName.contains("galeria")) {
            return "schema:ImageObject"; // Para imágenes y contenido multimedia
        } else if (tableName.contains("video") || tableName.contains("stream") || tableName.contains("pelicula")) {
            return "schema:VideoObject"; // Para videos y contenido audiovisual
        } else if (tableName.contains("audio") || tableName.contains("cancion") || tableName.contains("podcast")) {
            return "schema:AudioObject"; // Para audio y podcasts
        } else if (tableName.contains("foro") || tableName.contains("discusion") || tableName.contains("chat")) {
            return "schema:DiscussionForumPosting"; // Para foros y discusiones
        }

        return "schema:Thing"; // Valor por defecto para casos no identificados
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