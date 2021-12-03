package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

public class ItemBitacora {
    String accion;
    String evento;
    String seccion;
    String fecha;
    String usuario;

    public ItemBitacora(String mAccion, String mEvento, String mSeccion, String mFecha, String mUsuario) {
        this.accion = mAccion;
        this.evento = mEvento;
        this.seccion = mSeccion;
        this.fecha = mFecha;
        this.usuario = mUsuario;
    }

    public String getAccion() {
        return accion;
    }

    public String getEvento() {
        return evento;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }
}
