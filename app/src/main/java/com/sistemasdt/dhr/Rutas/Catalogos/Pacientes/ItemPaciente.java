package com.sistemasdt.dhr.Rutas.Catalogos.Pacientes;

public class ItemPaciente {
    private int mCodigo;
    private String mNombre;
    private String mTelefono;
    private String mDpi;
    private int mEdad;
    private String mFecha;
    private String mOcupacion;
    private boolean mEstado;
    private boolean mSexo;
    private double mDebe;
    private double mHaber;
    private double mSaldo;
    private int mCantidadFichas;
    private int mCantidadCitas;

    public ItemPaciente(int id, String nombre, String telefono, String dpi, int edad, String fecha,
                        boolean estado, double debe, double haber, double saldo, String ocupacion,
                        boolean sexo, int cantidadFichas, int cantidadCitas) {
        mCodigo = id;
        mNombre = nombre;
        mTelefono = telefono;
        mDpi = dpi;
        mEdad = edad;
        mFecha = fecha;
        mEstado = estado;
        mDebe = debe;
        mHaber = haber;
        mSaldo = saldo;
        mOcupacion = ocupacion;
        mSexo = sexo;
        mCantidadFichas = cantidadFichas;
        mCantidadCitas = cantidadCitas;
    }

    public int getCodigo() {
        return mCodigo;
    }

    public String getNombre() {
        return mNombre;
    }

    public String getTelefono() {
        return mTelefono;
    }

    public String getDpi() {
        return mDpi;
    }

    public int getEdad() {
        return mEdad;
    }

    public String getFecha() {
        return mFecha;
    }

    public double getDebe() {
        return mDebe;
    }

    public double getHaber() {
        return mHaber;
    }

    public double getSaldo() {
        return mSaldo;
    }

    public boolean getEstado() {
        return mEstado;
    }

    public String getOcupacion() {
        return mOcupacion;
    }

    public boolean getGenero() {
        return mSexo;
    }

    public int getCantidadFichas() {
        return mCantidadFichas;
    }

    public int getCantidadCitas() {
        return mCantidadCitas;
    }
}
