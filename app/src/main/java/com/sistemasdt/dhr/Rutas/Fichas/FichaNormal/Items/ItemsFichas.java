package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Items;

public class ItemsFichas {
    private int id;
    private String nombre;
    private String motivo;
    private String fecha;
    private double cargo;
    private double abono;
    private double saldo;
    private boolean estado;

    public ItemsFichas(int ID, String NOMBRE, String MOTIVO, String FECHA, double CARGO, double ABONO, double SALDO, boolean ESTADO) {
        id = ID;
        nombre = NOMBRE;
        motivo = MOTIVO;
        fecha = FECHA;
        cargo = CARGO;
        abono = ABONO;
        saldo = SALDO;
        estado = ESTADO;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getFecha() {
        return fecha;
    }

    public double getCargo() {
        return cargo;
    }

    public double getAbono() {
        return abono;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean getEstado() {
        return estado;
    }
}
