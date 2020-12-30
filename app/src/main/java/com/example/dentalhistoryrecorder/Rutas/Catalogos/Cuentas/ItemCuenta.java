package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

public class ItemCuenta {
    private int mCodigoCuenta;
    private String mUsuarioCuenta;

    public ItemCuenta(int codigoCuenta, String usuarioCuenta){
        mCodigoCuenta = codigoCuenta;
        mUsuarioCuenta = usuarioCuenta;
    }

    public int getCodigoCuenta() {
        return mCodigoCuenta;
    }

    private String getUsuarioCuenta(){
        return mUsuarioCuenta;
    }
}
