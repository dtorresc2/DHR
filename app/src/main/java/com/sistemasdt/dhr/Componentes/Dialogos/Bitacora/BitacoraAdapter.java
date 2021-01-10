package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sistemasdt.dhr.R;

import java.util.ArrayList;

public class BitacoraAdapter extends RecyclerView.Adapter<BitacoraAdapter.BitacoraViewHolder> {
    private ArrayList<ItemBitacora> mListaBitacora;

    public static class BitacoraViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion, fecha, cuenta;
        public View separador;

        public BitacoraViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.desc_accion);
            fecha = itemView.findViewById(R.id.fecha_accion);
            cuenta = itemView.findViewById(R.id.cuenta);
            separador = itemView.findViewById(R.id.separadorBitacora);
        }
    }

    public BitacoraAdapter(ArrayList<ItemBitacora> listaBitacora){
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
        bitacoraViewHolder.descripcion.setTypeface(typeface);
        return bitacoraViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BitacoraViewHolder bitacoraViewHolder, int i) {
        ItemBitacora itemBitacora = mListaBitacora.get(i);
        bitacoraViewHolder.cuenta.setText(itemBitacora.getCuenta());
        bitacoraViewHolder.fecha.setText(itemBitacora.getFecha());
        bitacoraViewHolder.descripcion.setText(itemBitacora.getAccion());

        if (i < mListaBitacora.size()){
            bitacoraViewHolder.separador.setVisibility(View.VISIBLE);
        }
        else {
            bitacoraViewHolder.separador.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mListaBitacora.size();
    }
}
