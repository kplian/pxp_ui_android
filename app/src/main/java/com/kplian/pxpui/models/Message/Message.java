
package com.kplian.pxpui.models.Message;


@SuppressWarnings("unused")
public class Message {

    private com.kplian.pxpui.models.Message.Data data;
    private Object from;
    private long idConexionFrom;
    private String mensaje;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Object getFrom() {
        return from;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public long getIdConexionFrom() {
        return idConexionFrom;
    }

    public void setIdConexionFrom(long idConexionFrom) {
        this.idConexionFrom = idConexionFrom;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
