package com.mycompany.database;

import java.util.Objects;

public class DatabaseConfig {

    private String tipoBD;
    private String host;
    private String puerto;
    private String usuario;
    private String password;
    private String nombreBD;

    // Constructor vacío necesario para la deserialización JSON
    public DatabaseConfig() {
    }

    public DatabaseConfig(String tipoBD, String host, String puerto, String usuario, String password, String nombreBD) {
        this.tipoBD = tipoBD;
        this.host = host;
        this.puerto = puerto;
        this.usuario = usuario;
        this.password = password;
        this.nombreBD = nombreBD;
    }

    // Getters y Setters
    public String getTipoBD() {
        return tipoBD;
    }

    public void setTipoBD(String tipoBD) {
        this.tipoBD = tipoBD;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreBD() {
        return nombreBD;
    }

    public void setNombreBD(String nombreBD) {
        this.nombreBD = nombreBD;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DatabaseConfig that = (DatabaseConfig) obj;
        return Objects.equals(tipoBD, that.tipoBD) &&
               Objects.equals(host, that.host) &&
               Objects.equals(puerto, that.puerto) &&
               Objects.equals(usuario, that.usuario) &&
               Objects.equals(nombreBD, that.nombreBD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoBD, host, puerto, usuario, nombreBD);
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "tipoBD='" + tipoBD + '\'' +
                ", host='" + host + '\'' +
                ", puerto='" + puerto + '\'' +
                ", usuario='" + usuario + '\'' +
                ", nombreBD='" + nombreBD + '\'' +
                '}';
    }
}
