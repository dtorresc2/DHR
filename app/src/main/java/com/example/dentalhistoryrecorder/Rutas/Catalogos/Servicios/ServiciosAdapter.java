package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;

import java.util.ArrayList;

public class ServiciosAdapter extends RecyclerView.Adapter<ServiciosAdapter.ServiciosViewHolder> {
    private ArrayList<ItemServicio> mlistaServicios;
    public static class ServiciosViewHolder extends  RecyclerView.ViewHolder {
        public TextView descripcion, monto, estado;

        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.tituloServicio);
            monto = itemView.findViewById(R.id.montoServicio);
            estado = itemView.findViewById(R.id.estadoServicio);
        }
    }

    public ServiciosAdapter(ArrayList<ItemServicio> listaServicios) {
        mlistaServicios = listaServicios;
    }

    @NonNull
    @Override
    public ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicio, viewGroup, false);
        ServiciosViewHolder serviciosViewHolder = new ServiciosViewHolder(view);
        return serviciosViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ServiciosViewHolder serviciosViewHolder, int i) {
        ItemServicio itemServicio = mlistaServicios.get(i);

        serviciosViewHolder.descripcion.setText(itemServicio.getCodigoServicio() + "-" + itemServicio.getDescripcionServicio());
        serviciosViewHolder.monto.setText(String.format("%.2f", itemServicio.getMontoServicio()));

        if (itemServicio.getEstadoServicio()) {
            serviciosViewHolder.estado.setText("Habilitado");
            serviciosViewHolder.estado.setBackgroundColor(R.color.VerdeOscuro);
        }
        else {
            serviciosViewHolder.estado.setText("Deshabilitado");
            serviciosViewHolder.estado.setBackgroundColor(R.color.RojoOscuro);
        }
    }

    @Override
    public int getItemCount() {
        return mlistaServicios.size();
    }
}
