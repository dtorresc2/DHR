package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas.ItemPieza;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas.PiezasAdapter;

import java.util.ArrayList;
import java.util.List;

public class CuentasAdapter extends RecyclerView.Adapter<CuentasAdapter.CuentasViewHolder> implements Filterable {
    private ArrayList<ItemCuenta> mListaCuentas;
    private ArrayList<ItemCuenta> mListaCuentasFull;
    private ViewGroup mViewGroup;
    private CuentasAdapter.OnClickListener mListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(CuentasAdapter.OnClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public static class CuentasViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion;
        public View separador;

        public CuentasViewHolder(@NonNull View itemView, final CuentasAdapter.OnClickListener listener) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.tituloCuenta);
            separador = itemView.findViewById(R.id.separadorCuenta);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CuentasAdapter(ArrayList<ItemCuenta> listaCuentas){
        mListaCuentas = listaCuentas;
        mListaCuentasFull = new ArrayList<>(listaCuentas);
    }

    @NonNull
    @Override
    public CuentasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cuentas, viewGroup, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        CuentasViewHolder cuentasViewHolder = new CuentasViewHolder(view, mListener);
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

    @Override
    public Filter getFilter() {
        return filtroCuenta;
    }

    private Filter filtroCuenta = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ItemCuenta> listaFiltrada = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                listaFiltrada.addAll(mListaCuentasFull);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (ItemCuenta item : mListaCuentasFull) {
                    if (item.getUsuarioCuenta().toLowerCase().contains(filter)) {
                        listaFiltrada.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = listaFiltrada;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mListaCuentas.clear();
            mListaCuentas.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
