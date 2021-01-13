package com.sistemasdt.dhr.Rutas.Fichas.FichaForm;

public class ItemBusqueda {
    String id;
    String descripcion;

    public ItemBusqueda (String auxId, String auxDescripcion){
        id = auxId;
        descripcion = auxDescripcion;
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
