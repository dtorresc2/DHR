package com.sistemasdt.dhr.Rutas.Catalogos.Piezas;

public class ItemPieza {
    private int mCodigoPieza;
    private int mNumeroPieza;
    private String mNombrePieza;
    private boolean mEstadoPieza;
    private int mNumeroFichas;

    public ItemPieza(int codigoPieza, int numeroPieza, String nombrePieza, boolean estadoPieza, int numeroFichas) {
        mCodigoPieza = codigoPieza;
        mNumeroPieza = numeroPieza;
        mNombrePieza = nombrePieza;
        mEstadoPieza = estadoPieza;
        mNumeroFichas = numeroFichas;
    }

    public int getCodigoPieza() {
        return mCodigoPieza;
    }

    public int getNumeroPieza() {
        return mNumeroPieza;
    }

    public String getNombrePieza() {
        return mNombrePieza;
    }

    public boolean getEstadoPieza() {
        return mEstadoPieza;
    }

    public int getNumeroFichas() {
        return mNumeroFichas;
    }
}
