package com.sistemasdt.dhr.Rutas.Fichas.HistorialOdonto;

public class ItemTratamiento {
    private int pieza;
    private int servicio;
    private String descripcionServicio;
    private double monto;
    private String fecha;

    public ItemTratamiento(int pieza, int servicio, String descripcionServicio, double monto, String fecha) {
        this.pieza = pieza;
        this.servicio = servicio;
        this.descripcionServicio = descripcionServicio;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getPieza() {
        return pieza;
    }

    public int getServicio() {
        return servicio;
    }

    public String getDescripcionServicio() {
        return descripcionServicio;
    }

    public double getMonto() {
        return monto;
    }

    public String getFechaRegistro() {
        return fecha;
    }
}
