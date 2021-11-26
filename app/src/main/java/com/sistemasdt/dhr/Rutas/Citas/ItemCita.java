package com.sistemasdt.dhr.Rutas.Citas;

public class ItemCita {
    private String midCitas;
    private String mfecha;
    private String mnombre;
    private String mdescripcion;
    private boolean mrealizado;

    public ItemCita(String id, String fecha, String nombre, String descripcion, boolean realizado) {
        setMfecha(fecha);
        setMnombre(nombre);
        setMdescripcion(descripcion);
        setMrealizado(realizado);
        setMidCitas(id);
    }

    public String getMfecha() {
        return mfecha;
    }

    public void setMfecha(String mfecha) {
        this.mfecha = mfecha;
    }

    public String getMnombre() {
        return mnombre;
    }

    public void setMnombre(String mnombre) {
        this.mnombre = mnombre;
    }

    public String getMdescripcion() {
        return mdescripcion;
    }

    public void setMdescripcion(String mdescripcion) {
        this.mdescripcion = mdescripcion;
    }

    public boolean getMrealizado() {
        return mrealizado;
    }

    public void setMrealizado(boolean mrealizado) {
        this.mrealizado = mrealizado;
    }

    public String getMidCitas() {
        return midCitas;
    }

    public void setMidCitas(String midCitas) {
        this.midCitas = midCitas;
    }
}
