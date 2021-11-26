package com.sistemasdt.dhr.Rutas.Citas.Adaptador;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sistemasdt.dhr.Rutas.Citas.ItemCita;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Items.ItemsFichas;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorCita extends RecyclerView.Adapter<AdaptadorCita.ViewHolderCita> implements Filterable {
    private ArrayList<ItemCita> mLista;
    private ArrayList<ItemCita> mListaFull;
    private int lastPosition = -1;
    private AdaptadorCita.OnItemClickListener mlistener;
    private ViewGroup mViewGroup;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class ViewHolderCita extends RecyclerView.ViewHolder {
        public TextView mfecha;
        public TextView mnombre;
        public TextView mdescripcion;
        public TextView mrealizado;


        public ViewHolderCita(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mfecha = itemView.findViewById(R.id.fechaCita);
            mnombre = itemView.findViewById(R.id.nombrePaciente);
            mdescripcion = itemView.findViewById(R.id.descCita);
            mrealizado = itemView.findViewById(R.id.realizado);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int posicion = getAdapterPosition();
                        if (posicion != RecyclerView.NO_POSITION){
                            listener.onItemClick(posicion);
                        }
                    }
                }
            });
        }
    }

    public AdaptadorCita(ArrayList<ItemCita> lista) {
        mLista = lista;
        mListaFull = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public AdaptadorCita.ViewHolderCita onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_citas, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        AdaptadorCita.ViewHolderCita viewHolderCita = new AdaptadorCita.ViewHolderCita(view, mlistener);
        viewHolderCita.mfecha.setTypeface(face);
        viewHolderCita.mnombre.setTypeface(face);
        viewHolderCita.mdescripcion.setTypeface(face);
        viewHolderCita.mrealizado.setTypeface(face);
        return viewHolderCita;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCita.ViewHolderCita viewHolderCita, int i) {
        ItemCita itemCita = mLista.get(i);
        viewHolderCita.mfecha.setText(itemCita.getMfecha());
        viewHolderCita.mnombre.setText(itemCita.getMnombre());
        viewHolderCita.mdescripcion.setText(itemCita.getMdescripcion());

        if (itemCita.getMrealizado()) {
            viewHolderCita.mrealizado.setText("Realizado");
            viewHolderCita.mrealizado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.VerdeOscuro));
        } else {
            viewHolderCita.mrealizado.setText("Pendiente");
            viewHolderCita.mrealizado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.RojoOscuro));
        }
        setAnimation(viewHolderCita.itemView, i);
    }

    @Override
    public int getItemCount() {
        return mLista.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ItemCita> listaFiltrada = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                listaFiltrada.addAll(mListaFull);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (ItemCita item : mListaFull) {
                    if (item.getMnombre().toLowerCase().contains(filter)) {
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
            mLista.clear();
            mLista.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
