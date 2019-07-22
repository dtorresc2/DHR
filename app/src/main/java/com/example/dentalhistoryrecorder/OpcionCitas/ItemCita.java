package com.example.dentalhistoryrecorder.OpcionCitas;

public class ItemCita {
    private String mhora;
    private String mfecha;
    private String mnombre;
    private String mdescripcion;
    private String mrealizado;

    public ItemCita(String hora, String fecha, String nombre, String descripcion, String realizado){
        setMhora(hora);
        setMfecha(fecha);
        setMnombre(nombre);
        setMdescripcion(descripcion);
        setMrealizado(realizado);
    }

    public String getMhora() {
        return mhora;
    }

    public void setMhora(String mhora) {
        this.mhora = mhora;
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

    public String getMrealizado() {
        return mrealizado;
    }

    public void setMrealizado(String mrealizado) {
        this.mrealizado = mrealizado;
    }
}
