package com.mycompany.database;

import java.util.*;

public class DatabaseSelectionManager {
    private static final Map<String, List<String>> selectedTables = new HashMap<>();

    // Método para guardar la selección de tablas
    public static void saveSelection(String databaseName, List<String> tables) {
        selectedTables.put(databaseName, new ArrayList<>(tables));
        System.out.println("✅ Selección guardada: " + selectedTables);
    }

    // Método para obtener las tablas seleccionadas de una base de datos
    public static List<String> getSelectedTables(String databaseName) {
        return selectedTables.getOrDefault(databaseName, new ArrayList<>());
    }

    // Método para obtener TODAS las selecciones
    public static Map<String, List<String>> getAllSelections() {
        return selectedTables;
    }

    // Método para verificar si una base de datos tiene tablas seleccionadas
    public static boolean hasSelection(String databaseName) {
        return selectedTables.containsKey(databaseName) && !selectedTables.get(databaseName).isEmpty();
    }

    public static void showSelections() {
        selectedTables.forEach((db, tables) ->
            System.out.println("📂 BD: " + db + " - Tablas: " + tables)
        );
    }
}
