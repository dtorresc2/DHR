package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CuentasAdapter extends RecyclerView.Adapter<CuentasAdapter.CuentasViewHolder> {
    private ArrayList<ItemCuenta> mListaCuentas;

    public static class CuentasViewHolder extends RecyclerView.ViewHolder {
        public CuentasViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public CuentasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CuentasViewHolder cuentasViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
