package com.mycompany.filegeneration;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DataReader {

    private Connection connection;

    private static final Set<String> IGNORED_TABLES = new HashSet<>();

    static {
        String[] ignored = {
            "pma__bookmark", "pma__central_columns", "pma__column_info", "pma__designer_settings",
            "pma__export_templates", "pma__favorite", "pma__history", "pma__navigationhiding",
            "pma__pdf_pages", "pma__recent", "pma__relation", "pma__savedsearches", "pma__table_coords",
            "pma__table_info", "pma__table_uiprefs", "pma__tracking", "pma__userconfig",
            "pma__usergroups", "pma__users", "sys_config", "sys_created", "sys_updated"
        };
        for (String table : ignored) {
            IGNORED_TABLES.add(table.toLowerCase());
        }
    }

    public DataReader(Connection connection) {
        this.connection = connection;
    }

    public void readTables() {
        System.out.println("🔍 Iniciando la lectura de tablas...");
        try (ResultSet tables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("📌 Revisando tabla: " + tableName);

                if (isIgnoredTable(tableName)) {
                    System.out.println("❌ Tabla ignorada: " + tableName);
                    continue;
                }

                if (!tableExists(tableName)) {
                    System.out.println("⚠️ Tabla no accesible o no existe: " + tableName);
                    continue;
                }

                System.out.println("✅ Procesando tabla: " + tableName);
                showTableData(tableName);
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al leer las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isIgnoredTable(String tableName) {
        return IGNORED_TABLES.contains(tableName.toLowerCase());
    }

    private boolean tableExists(String tableName) {
        try (ResultSet tables = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        } catch (SQLException e) {
            System.out.println("❌ Error verificando existencia de tabla: " + tableName);
            e.printStackTrace();
            return false;
        }
    }

    public void showTableData(String tableName) {
        System.out.println("📌 Intentando leer datos de la tabla: " + tableName);
        String query = "SELECT * FROM " + tableName;
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("🔹 Datos de la tabla: " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }

            if (!hasData) {
                System.out.println("⚠️ La tabla está vacía.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al leer datos de la tabla: " + tableName + " -> " + e.getMessage());
            e.printStackTrace();
        }
    }
}
