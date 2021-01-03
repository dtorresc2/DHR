package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;

public class ItemPaciente {
    private String mid;
    private String mnombre;
    private String mcontadorN;
    private String mcontadorE;
    private String medad;
    private String mfecha;

    public ItemPaciente(String id, String nombre, String contadorN, String contadorE, String edad, String fecha){
        mid = id;
        mnombre = nombre;
        mcontadorN = contadorN;
        mcontadorE = contadorE;
        medad = edad;
        mfecha = fecha;
    }

    public String getMid() {
        return mid;
    }

    public String getMnombre() {
        return mnombre;
    }

    public String getMcontadorN() {
        return mcontadorN;
    }

    public String getMcontadorE() {
        return mcontadorE;
    }

    public String getMedad() {
        return medad;
    }

    public String getMfecha() {
        return mfecha;
    }
}
