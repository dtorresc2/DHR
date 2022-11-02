package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Items;

public class ItemPagoEvaluacion {
    private String fecha;
    private String descripcion;
    private Double pago;

    public ItemPagoEvaluacion(String fecha, String descripcion, Double pago) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.pago = pago;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getPago() {
        return pago;
    }
}
