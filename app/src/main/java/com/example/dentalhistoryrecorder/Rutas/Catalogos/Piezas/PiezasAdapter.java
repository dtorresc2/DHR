package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class PiezasAdapter extends RecyclerView.Adapter<PiezasAdapter.PiezasViewHolder> {

    public static class PiezasViewHolder extends RecyclerView.ViewHolder {
        public PiezasViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public PiezasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PiezasViewHolder piezasViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
