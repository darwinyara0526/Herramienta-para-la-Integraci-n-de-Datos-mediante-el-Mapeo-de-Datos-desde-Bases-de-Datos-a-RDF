package com.mycompany.proyectov2;

import com.mycompany.filegeneration.MySQLR2RMLGenerator;
import com.mycompany.filegeneration.MySQLDataReader;
import com.mycompany.database.DatabaseConnectionMySQL;

public class Main {
    public static void main(String[] args) {
        // Ruta donde se guardarán los archivos R2RML y RDF
        String outputPath = "/home/darwin/Escritorio/Proyecto/archivo.ttl"; // Archivo Turtle

        // Crear instancia de DatabaseConnectionMySQL
        DatabaseConnectionMySQL dbConnection = new DatabaseConnectionMySQL();

        // Instancia del generador R2RML
        MySQLR2RMLGenerator r2rmlGenerator = new MySQLR2RMLGenerator(dbConnection);

        // Leer y mostrar datos de las tablas
        MySQLDataReader dataReader = new MySQLDataReader(dbConnection);
        dataReader.readTables();

        // Proceso automatizado
        r2rmlGenerator.generateR2RML(outputPath);
    }
}
