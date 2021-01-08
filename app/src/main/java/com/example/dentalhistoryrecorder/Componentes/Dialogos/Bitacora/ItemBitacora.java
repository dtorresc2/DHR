package com.example.dentalhistoryrecorder.Componentes.Dialogos.Bitacora;

public class ItemBitacora {
    int mCodigoBitacora;
    int mAccion;
    String mFecha;
    String mCuenta;

    public ItemBitacora(int mCodigoBitacora, int mAccion, String mFecha, String mCuenta) {
        this.mCodigoBitacora = mCodigoBitacora;
        this.mAccion = mAccion;
        this.mFecha = mFecha;
        this.mCuenta = mCuenta;
    }

    public int getCodigoBitacora() {
        return mCodigoBitacora;
    }

    public int getAccion() {
        return mAccion;
    }

    public String getFecha() {
        return mFecha;
    }

    public String getCuenta() {
        return mCuenta;
    }
}
