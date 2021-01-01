package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;

import java.util.ArrayList;

public class CuentasAdapter extends RecyclerView.Adapter<CuentasAdapter.CuentasViewHolder> {
    private ArrayList<ItemCuenta> mListaCuentas;
    private ViewGroup mViewGroup;

    public static class CuentasViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion;
        public View separador;

        public CuentasViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.tituloCuenta);
            separador = itemView.findViewById(R.id.separadorCuenta);
        }
    }

    public CuentasAdapter(ArrayList<ItemCuenta> listaCuentas){
        mListaCuentas = listaCuentas;
    }

    @NonNull
    @Override
    public CuentasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cuentas, viewGroup, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        CuentasViewHolder cuentasViewHolder = new CuentasViewHolder(view);
        cuentasViewHolder.descripcion.setTypeface(typeface);
        return cuentasViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CuentasViewHolder cuentasViewHolder, int i) {
        ItemCuenta itemCuenta = mListaCuentas.get(i);
        cuentasViewHolder.descripcion.setText(itemCuenta.getUsuarioCuenta());

        if (i < mListaCuentas.size()){
            cuentasViewHolder.separador.setVisibility(View.VISIBLE);
        }
        else {
            cuentasViewHolder.separador.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mListaCuentas.size();
    }


}
