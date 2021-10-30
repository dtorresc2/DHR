package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha;

public class ItemPago {

    private String descripcionPago;
    private double monto;
    private String fecha;

    public ItemPago(String descripcionPago, double monto, String fecha) {
        this.descripcionPago = descripcionPago;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getDescripcionPago() {
        return descripcionPago;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }
}
