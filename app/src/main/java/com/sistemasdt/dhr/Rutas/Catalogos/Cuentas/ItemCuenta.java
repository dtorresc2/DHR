package com.sistemasdt.dhr.Rutas.Catalogos.Cuentas;

public class ItemCuenta {
    private int mCodigoCuenta;
    private String mUsuarioCuenta;

    public ItemCuenta(int codigoCuenta, String usuarioCuenta) {
        mCodigoCuenta = codigoCuenta;
        mUsuarioCuenta = usuarioCuenta;
    }

    public int getCodigoCuenta() {
        return mCodigoCuenta;
    }

    public String getUsuarioCuenta() {
        return mUsuarioCuenta;
    }
}
