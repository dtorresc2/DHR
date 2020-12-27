package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

public class ItemPieza {
    private int mCodigoPieza;
    private int mNumeroPieza;
    private String mNombrePieza;
    private boolean mEstadoPieza;

    public ItemPieza(int codigoPieza, int numeroPieza, String nombrePieza, boolean estadoPieza) {
        mCodigoPieza = codigoPieza;
        mNumeroPieza = numeroPieza;
        mNombrePieza = nombrePieza;
        mEstadoPieza = estadoPieza;
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
}
