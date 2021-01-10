package com.sistemasdt.dhr.Rutas.Catalogos.Piezas;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sistemasdt.dhr.R;

import java.util.ArrayList;
import java.util.List;


public class PiezasAdapter extends RecyclerView.Adapter<PiezasAdapter.PiezasViewHolder> implements Filterable {
    private ArrayList<ItemPieza> mListaPiezas;
    private ArrayList<ItemPieza> mListaPiezasFull;
    private ViewGroup mViewGroup;
    private PiezasAdapter.OnClickListener mListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(PiezasAdapter.OnClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public static class PiezasViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion, estado;
        public View separador;

        public PiezasViewHolder(@NonNull View itemView, final PiezasAdapter.OnClickListener listener) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.tituloPieza);
            estado = itemView.findViewById(R.id.estadoPieza);
            separador = itemView.findViewById(R.id.separadorPieza);

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

    public PiezasAdapter(ArrayList<ItemPieza> listaPiezas) {
        mListaPiezas = listaPiezas;
        mListaPiezasFull = new ArrayList<>(listaPiezas);
    }

    @NonNull
    @Override
    public PiezasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pieza, viewGroup, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        PiezasViewHolder piezasViewHolder = new PiezasViewHolder(view, mListener);
        piezasViewHolder.descripcion.setTypeface(typeface);
        piezasViewHolder.estado.setTypeface(typeface);
        return piezasViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PiezasViewHolder piezasViewHolder, int i) {
        ItemPieza itemPieza = mListaPiezas.get(i);
        piezasViewHolder.descripcion.setText(itemPieza.getNumeroPieza() + " - " + itemPieza.getNombrePieza());

        if (itemPieza.getEstadoPieza()) {
            piezasViewHolder.estado.setText("Habilitado");
            piezasViewHolder.estado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.VerdeOscuro));
        } else {
            piezasViewHolder.estado.setText("Deshabilitado");
            piezasViewHolder.estado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.RojoOscuro));
        }

        if (i < mListaPiezas.size()) {
            piezasViewHolder.separador.setVisibility(View.VISIBLE);
        } else {
            piezasViewHolder.separador.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mListaPiezas.size();
    }

    @Override
    public Filter getFilter() {
        return filtroPieza;
    }

    private Filter filtroPieza = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ItemPieza> listaFiltrada = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                listaFiltrada.addAll(mListaPiezasFull);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (ItemPieza item : mListaPiezasFull) {
                    if (item.getNombrePieza().toLowerCase().contains(filter)) {
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
            mListaPiezas.clear();
            mListaPiezas.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
