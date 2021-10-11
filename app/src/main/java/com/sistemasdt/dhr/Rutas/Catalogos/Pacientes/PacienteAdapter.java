package com.sistemasdt.dhr.Rutas.Catalogos.Pacientes;

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
import android.widget.TextView;

import com.sistemasdt.dhr.R;

import java.util.ArrayList;
import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.ViewHolderConsulta> implements Filterable {
    private ArrayList<ItemPaciente> mLista;
    private ArrayList<ItemPaciente> mListaFull;
    private int lastPosition = -1;
    private OnItemClickListener mlistener;
    private ViewGroup mViewGroup;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public static class ViewHolderConsulta extends RecyclerView.ViewHolder {
        public TextView mid;
        public TextView mnombre;
        public TextView medad;
        public TextView mfecha;
        public TextView mdebe;
        public TextView mhaber;
        public TextView msaldo;
        public TextView mestado;
        public View separador;

        public ViewHolderConsulta(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
//            mid = itemView.findViewById(R.id.co);
            mnombre = itemView.findViewById(R.id.nombrePaciente);
            medad = itemView.findViewById(R.id.edadPaciente);
            mfecha = itemView.findViewById(R.id.fechaNacimientoPaciente);
            mdebe = itemView.findViewById(R.id.debePaciente);
            mhaber = itemView.findViewById(R.id.haberPaciente);
            msaldo = itemView.findViewById(R.id.saldoPaciente);
            mestado = itemView.findViewById(R.id.estadoPaciente);
            separador = itemView.findViewById(R.id.separadorPaciente);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int posicion = getAdapterPosition();
                        if (posicion != RecyclerView.NO_POSITION) {
                            listener.onItemClick(posicion);
                        }
                    }
                }
            });
        }
    }

    public PacienteAdapter(ArrayList<ItemPaciente> lista) {
        mLista = lista;
        mListaFull = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public ViewHolderConsulta onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        ViewHolderConsulta viewHolderConsulta = new ViewHolderConsulta(view, mlistener);
//        viewHolderConsulta.mid.setTypeface(face);
        viewHolderConsulta.mnombre.setTypeface(face);
        viewHolderConsulta.medad.setTypeface(face);
        viewHolderConsulta.mfecha.setTypeface(face);
        viewHolderConsulta.mdebe.setTypeface(face);
        viewHolderConsulta.mhaber.setTypeface(face);
        viewHolderConsulta.msaldo.setTypeface(face);
        viewHolderConsulta.mestado.setTypeface(face);
        return viewHolderConsulta;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConsulta viewHolderConsulta, int i) {
        ItemPaciente itemPaciente = mLista.get(i);
        viewHolderConsulta.mnombre.setText(itemPaciente.getNombre());
        viewHolderConsulta.mfecha.setText(itemPaciente.getFecha());
        viewHolderConsulta.mdebe.setText(String.format("%.2f", itemPaciente.getDebe()));
        viewHolderConsulta.mhaber.setText(String.format("%.2f", itemPaciente.getHaber()));
        viewHolderConsulta.msaldo.setText(String.format("%.2f", itemPaciente.getSaldo()));

        if (itemPaciente.getEdad() == 1) {
            viewHolderConsulta.medad.setText(String.valueOf(itemPaciente.getEdad()) + " Año");
        }
        else {
            viewHolderConsulta.medad.setText(String.valueOf(itemPaciente.getEdad()) + " Años");
        }

        if (itemPaciente.getEstado()) {
            viewHolderConsulta.mestado.setText("Habilitado");
            viewHolderConsulta.mestado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.VerdeOscuro));
        } else {
            viewHolderConsulta.mestado.setText("Deshabilitado");
            viewHolderConsulta.mestado.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.RojoOscuro));
        }

        if (i < mLista.size()) {
            viewHolderConsulta.separador.setVisibility(View.VISIBLE);
        } else {
            viewHolderConsulta.separador.setVisibility(View.INVISIBLE);
        }
        setAnimation(viewHolderConsulta.itemView, i);
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
        return filtroPaciente;
    }

    private Filter filtroPaciente = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ItemPaciente> listaFiltrada = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                listaFiltrada.addAll(mListaFull);
            }
            else {
                String filter = constraint.toString().toLowerCase().trim();
                for (ItemPaciente item : mListaFull){
                    if (item.getNombre().toLowerCase().contains(filter)){
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
