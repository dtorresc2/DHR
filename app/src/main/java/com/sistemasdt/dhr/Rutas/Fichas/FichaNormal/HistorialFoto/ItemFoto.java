package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto;

import android.graphics.Bitmap;

public class ItemFoto {
    private Bitmap foto;
    private boolean isSelected = false;
    private String url;

    public ItemFoto(Bitmap foto, String URL, boolean isSelected){
        this.foto = foto;
        this.isSelected = isSelected;
        this.url = URL;
    }

    public String getUrl(){
        return this.url;
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
