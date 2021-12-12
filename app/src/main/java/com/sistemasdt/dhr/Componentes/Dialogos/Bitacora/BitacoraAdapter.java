package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sistemasdt.dhr.R;

import java.util.ArrayList;

public class BitacoraAdapter extends RecyclerView.Adapter<BitacoraAdapter.BitacoraViewHolder> {
    private ArrayList<ItemBitacora> mListaBitacora;
    private int lastPosition = -1;


    public static class BitacoraViewHolder extends RecyclerView.ViewHolder {
        TextView evento, fecha, cuenta, seccion, accion;
        public View separador;

        public BitacoraViewHolder(@NonNull View itemView) {
            super(itemView);
            evento = itemView.findViewById(R.id.evento);
            fecha = itemView.findViewById(R.id.fecha);
            seccion = itemView.findViewById(R.id.seccion);
            accion = itemView.findViewById(R.id.accion);
            cuenta = itemView.findViewById(R.id.cuenta);
            separador = itemView.findViewById(R.id.separadorBitacora);
        }
    }

    public BitacoraAdapter(ArrayList<ItemBitacora> listaBitacora) {
        mListaBitacora = listaBitacora;
    }

    @NonNull
    @Override
    public BitacoraViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bitacora, viewGroup, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        BitacoraViewHolder bitacoraViewHolder = new BitacoraViewHolder(view);
        bitacoraViewHolder.cuenta.setTypeface(typeface);
        bitacoraViewHolder.fecha.setTypeface(typeface);
        bitacoraViewHolder.seccion.setTypeface(typeface);
        bitacoraViewHolder.accion.setTypeface(typeface);
        bitacoraViewHolder.evento.setTypeface(typeface);
        return bitacoraViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BitacoraViewHolder bitacoraViewHolder, int i) {
        ItemBitacora itemBitacora = mListaBitacora.get(i);
        bitacoraViewHolder.cuenta.setText(itemBitacora.getUsuario());
        bitacoraViewHolder.fecha.setText(itemBitacora.getFecha());
        bitacoraViewHolder.seccion.setText(itemBitacora.getSeccion());
        bitacoraViewHolder.accion.setText(itemBitacora.getAccion());
        bitacoraViewHolder.evento.setText(itemBitacora.getEvento());

        if (i < mListaBitacora.size()) {
            bitacoraViewHolder.separador.setVisibility(View.VISIBLE);
        } else {
            bitacoraViewHolder.separador.setVisibility(View.INVISIBLE);
        }

        setAnimation(bitacoraViewHolder.itemView, i);
    }

    @Override
    public int getItemCount() {
        return mListaBitacora.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
