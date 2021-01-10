package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

public class ItemBitacora {
    int mCodigoBitacora;
    String mAccion;
    String mFecha;
    String mCuenta;

    public ItemBitacora(int mCodigoBitacora, String mAccion, String mFecha, String mCuenta) {
        this.mCodigoBitacora = mCodigoBitacora;
        this.mAccion = mAccion;
        this.mFecha = mFecha;
        this.mCuenta = mCuenta;
    }

    public int getCodigoBitacora() {
        return mCodigoBitacora;
    }

    public String getAccion() {
        return mAccion;
    }

    public String getFecha() {
        return mFecha;
    }

    public String getCuenta() {
        return mCuenta;
    }
}
