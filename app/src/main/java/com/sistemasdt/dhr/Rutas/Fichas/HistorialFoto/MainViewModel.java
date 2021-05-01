package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

public class MainViewModel extends ViewModel {
    public MutableLiveData<Bitmap> mutableLiveData = new MutableLiveData<>();

    public void setBitmap(Bitmap bitmap) {
        mutableLiveData.setValue(bitmap);
    }

    public MutableLiveData<Bitmap> getBitmap() {
        return mutableLiveData;
    }
}
