package com.mycompany.proyectov2;

import com.mycompany.filegeneration.MySQLR2RMLGenerator;
import com.mycompany.filegeneration.MySQLDataReader;
import com.mycompany.database.DatabaseConnectionMySQL;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Ruta donde se guardarán los archivos R2RML y RDF
        String outputPath = "/home/darwin/Escritorio/Proyecto/archivo.ttl"; // Archivo Turtle

        // Crear instancia de DatabaseConnectionMySQL
        DatabaseConnectionMySQL dbConnection = new DatabaseConnectionMySQL();
        Connection connection = null; // Declarar conexión

        try {
            // Obtener la conexión a la base de datos
            connection = dbConnection.getConnection();

            // Instancia del generador R2RML
            MySQLR2RMLGenerator r2rmlGenerator = new MySQLR2RMLGenerator(connection);

            // Leer y mostrar datos de las tablas
            MySQLDataReader dataReader = new MySQLDataReader(connection); // Ahora funciona correctamente
            dataReader.readTables();

            // Generar el archivo R2RML
            r2rmlGenerator.generateR2RML(outputPath);
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de excepciones
        } finally {
            dbConnection.disconnect(connection); // Asegúrate de desconectar la conexión
        }
    }
}
