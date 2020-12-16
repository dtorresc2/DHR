package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;

public class ServiciosAdapter extends RecyclerView.Adapter<ServiciosAdapter.ServiciosViewHolder> {

    public static class ServiciosViewHolder extends  RecyclerView.ViewHolder {
        public TextView descripcion, monto, estado;

        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.tituloServicio);
            monto = itemView.findViewById(R.id.montoServicio);
            estado = itemView.findViewById(R.id.estadoServicio);
        }
    }

    @NonNull
    @Override
    public ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ServiciosViewHolder serviciosViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
