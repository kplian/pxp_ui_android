package com.kplian.pxpui.models;

public class WebSocketConnectionSetUser {

    private int id_usuario;
    private String nombre_usuario;
    private String evento;
    private String id_contenedor;
    private String metodo;

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getId_contenedor() {
        return id_contenedor;
    }

    public void setId_contenedor(String id_contenedor) {
        this.id_contenedor = id_contenedor;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
}
