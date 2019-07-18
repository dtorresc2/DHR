package com.example.dentalhistoryrecorder.OpcionConsulta;

public class Items {
    private String mid;
    private String mnombre;
    private String mcontadorN;
    private String mcontadorE;
    private String medad;
    private String mfecha;

    public Items(String id, String nombre, String contadorN, String contadorE, String edad, String fecha){
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
