package com.sistemasdt.dhr.Rutas.Catalogos.Servicios;

public class ItemServicio {
    private int mCodigoServicio;
    private String mDescripcionServicio;
    private double mMontoServicio;
    private boolean mEstadoServicio;
    private int mCantidadFichasNormales;

    public ItemServicio(int codigoServicio, String descripcionServicio, double montoServicio, boolean estadoServicio, int fichas_normales) {
        mCodigoServicio = codigoServicio;
        mDescripcionServicio = descripcionServicio;
        mMontoServicio = montoServicio;
        mEstadoServicio = estadoServicio;
        mCantidadFichasNormales = fichas_normales;
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

    public int getCantidadFichasNormales() {
        return mCantidadFichasNormales;
    }

}
