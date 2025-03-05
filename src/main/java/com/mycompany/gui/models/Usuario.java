package com.mycompany.gui.models;

public class Usuario {
    private String id;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
    private String area;

    public Usuario(String id, String nombre, String apellido, String correo, String rol, String area) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.rol = rol;
        this.area = area;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public String getArea() { return area; }
}
