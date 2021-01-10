package com.sistemasdt.dhr.OpcionConsulta.Normal;

public class ItemsFichas {
    private String id;
    private String motivo;
    private String medico;
    private String fecha;

    public ItemsFichas(String aux1, String aux2, String aux3, String aux4){
        id = aux1;
        motivo = aux2;
        medico = aux3;
        fecha = aux4;
    }


    public String getId() {
        return id;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getMedico() {
        return medico;
    }

    public String getFecha() {
        return fecha;
    }
}
