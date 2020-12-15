package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

public class ItemServicio {
    private int mCodigoServicio;
    private String mDescripcionServicio;
    private double mMontoServicio;
    private boolean mEstadoServicio;

    public ItemServicio(int codigoServicio, String descripcionServicio, double montoServicio, boolean estadoServicio) {
        mCodigoServicio = codigoServicio;
        mDescripcionServicio = descripcionServicio;
        mMontoServicio = montoServicio;
        mEstadoServicio = estadoServicio;
    }

    public int getCodigoServicio() {
        return mCodigoServicio;
    }

    public String getDescripcionServicio() {
        return mDescripcionServicio;
    }

    public double getMontoServicio() {
        return mMontoServicio;
    }

    public boolean getEstadoServicio() {
        return mEstadoServicio;
    }
}
