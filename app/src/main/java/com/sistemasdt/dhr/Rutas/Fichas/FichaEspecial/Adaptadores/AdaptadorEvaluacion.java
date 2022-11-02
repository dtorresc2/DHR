package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Adaptadores;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistemasdt.dhr.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorEvaluacion extends RecyclerView.Adapter<AdaptadorEvaluacion.ViewHolderEvaluaciones> implements Filterable {
    private ArrayList<ItemEvaluacion> mLista;
    private ArrayList<ItemEvaluacion> mListaFull;
    private int lastPosition = -1;
    private OnItemClickListener mlistener;
    private ViewGroup mViewGroup;

    public AdaptadorEvaluacion(ArrayList<ItemEvaluacion> lista) {
        mLista = lista;
        mListaFull = new ArrayList<>(lista);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public static class ViewHolderEvaluaciones extends RecyclerView.ViewHolder {
        public TextView nombrePaciente;
        public TextView motivo;
        public TextView fechaFicha;
        public TextView debeFicha;
        public TextView haberFicha;
        public TextView saldoFicha;
        public TextView estadoFicha;
        public View separadorFicha;

        public ViewHolderEvaluaciones(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nombrePaciente = itemView.findViewById(R.id.nombrePaciente);
            motivo = itemView.findViewById(R.id.motivo);
            fechaFicha = itemView.findViewById(R.id.fechaFicha);
            debeFicha = itemView.findViewById(R.id.debeFicha);
            haberFicha = itemView.findViewById(R.id.haberFicha);
            saldoFicha = itemView.findViewById(R.id.saldoFicha);
            estadoFicha = itemView.findViewById(R.id.estadoFicha);
            separadorFicha = itemView.findViewById(R.id.separadorFicha);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int posicion = getAdapterPosition();
                    if (posicion != RecyclerView.NO_POSITION) {
                        listener.onItemClick(posicion);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolderEvaluaciones onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_evaluacion, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        ViewHolderEvaluaciones viewHolderEvaluaciones = new ViewHolderEvaluaciones(view, mlistener);
        viewHolderEvaluaciones.nombrePaciente.setTypeface(face);
        viewHolderEvaluaciones.motivo.setTypeface(face);
        viewHolderEvaluaciones.fechaFicha.setTypeface(face);
        viewHolderEvaluaciones.debeFicha.setTypeface(face);
        viewHolderEvaluaciones.haberFicha.setTypeface(face);
        viewHolderEvaluaciones.saldoFicha.setTypeface(face);
        viewHolderEvaluaciones.estadoFicha.setTypeface(face);
        return viewHolderEvaluaciones;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderEvaluaciones holder, int position) {
        ItemEvaluacion itemEvaluacion = mLista.get(position);
        holder.nombrePaciente.setText(itemEvaluacion.getNombre());
        holder.motivo.setText(itemEvaluacion.getMotivo());
        holder.fechaFicha.setText(itemEvaluacion.getFecha());
        holder.debeFicha.setText(String.format("%.2f", itemEvaluacion.getCargo()));
        holder.haberFicha.setText(String.format("%.2f", itemEvaluacion.getAbono()));
        holder.saldoFicha.setText(String.format("%.2f", itemEvaluacion.getSaldo()));

        if (itemEvaluacion.getEstado()) {
            holder.estadoFicha.setText("Habilitado");
            holder.estadoFicha.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.VerdeOscuro));
        } else {
            holder.estadoFicha.setText("Deshabilitado");
            holder.estadoFicha.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.RojoOscuro));
        }

        if (position < mLista.size()) {
            holder.separadorFicha.setVisibility(View.VISIBLE);
        } else {
            holder.separadorFicha.setVisibility(View.INVISIBLE);
        }

        setAnimation(holder.itemView, position);
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
        return filtro;
    }

    private Filter filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ItemEvaluacion> listaFiltrada = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                listaFiltrada.addAll(mListaFull);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (ItemEvaluacion item : mListaFull) {
                    if (item.getNombre().toLowerCase().contains(filter)) {
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
