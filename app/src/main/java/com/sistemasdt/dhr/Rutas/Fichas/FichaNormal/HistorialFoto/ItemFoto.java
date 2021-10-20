package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;

import android.graphics.Bitmap;

public class ItemFoto {
    private Bitmap foto;
    private boolean isSelected = false;

    public ItemFoto(Bitmap foto, boolean isSelected){
        this.foto = foto;
        this.isSelected = isSelected;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
