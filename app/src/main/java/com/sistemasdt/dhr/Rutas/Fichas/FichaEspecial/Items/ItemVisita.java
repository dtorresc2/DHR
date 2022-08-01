package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Items;

public class ItemVisita {
    private String fecha;
    private String descripcion;
    private Double costo;

    public ItemVisita(String fecha, String descripcion, Double costo) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getCosto() {
        return costo;
    }
}
