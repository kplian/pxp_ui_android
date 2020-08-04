
package com.kplian.pxpui.models.Message;


@SuppressWarnings("unused")
public class Data {

    private String evento;
    private long idConexion;
    private String idContenedor;
    private Object idSession;
    private int idUsuario;
    private String metodo;
    private String nombreUsuario;

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public long getIdConexion() {
        return idConexion;
    }

    public void setIdConexion(long idConexion) {
        this.idConexion = idConexion;
    }

    public String getIdContenedor() {
        return idContenedor;
    }

    public void setIdContenedor(String idContenedor) {
        this.idContenedor = idContenedor;
    }

    public Object getIdSession() {
        return idSession;
    }

    public void setIdSession(Object idSession) {
        this.idSession = idSession;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
