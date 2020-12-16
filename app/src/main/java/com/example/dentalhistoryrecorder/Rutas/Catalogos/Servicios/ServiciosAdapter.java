package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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
    private ViewGroup mViewGroup;

    public static class ServiciosViewHolder extends RecyclerView.ViewHolder {
        public TextView descripcion, monto, estado;
        public View separador;

        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.tituloServicio);
            monto = itemView.findViewById(R.id.montoServicio);
            estado = itemView.findViewById(R.id.estadoServicio);
            separador = itemView.findViewById(R.id.separadorServicio);
        }
    }

    public ServiciosAdapter(ArrayList<ItemServicio> listaServicios) {
        mlistaServicios = listaServicios;
    }

    @NonNull
    @Override
    public ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicio, viewGroup, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        ServiciosViewHolder serviciosViewHolder = new ServiciosViewHolder(view);
        serviciosViewHolder.descripcion.setTypeface(typeface);
        serviciosViewHolder.monto.setTypeface(typeface);
        serviciosViewHolder.estado.setTypeface(typeface);
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
            serviciosViewHolder.estado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.VerdeOscuro));
        } else {
            serviciosViewHolder.estado.setText("Deshabilitado");
            serviciosViewHolder.estado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.RojoOscuro));
        }

        if (i < mlistaServicios.size()) {
            serviciosViewHolder.separador.setVisibility(View.VISIBLE);
        } else {
            serviciosViewHolder.separador.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mlistaServicios.size();
    }
}
