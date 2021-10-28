package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Adaptadores;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Items.ItemsFichas;

import java.util.ArrayList;

public class AdaptadorConsultaFicha extends RecyclerView.Adapter<AdaptadorConsultaFicha.ViewHolderConsultaFicha> {
    private ArrayList<ItemsFichas> mLista;
    private int lastPosition = -1;
    private OnItemClickListener mlistener;
    private ViewGroup mViewGroup;


    public AdaptadorConsultaFicha(ArrayList<ItemsFichas> lista) {
        mLista = lista;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public static class ViewHolderConsultaFicha extends RecyclerView.ViewHolder {
        public TextView nombrePaciente;
        public TextView motivo;
        public TextView fechaFicha;
        public TextView debeFicha;
        public TextView haberFicha;
        public TextView saldoFicha;
        public TextView estadoFicha;
        public View separadorFicha;

        public ViewHolderConsultaFicha(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nombrePaciente = itemView.findViewById(R.id.nombrePaciente);
            motivo = itemView.findViewById(R.id.motivo);
            fechaFicha = itemView.findViewById(R.id.fechaFicha);
            debeFicha = itemView.findViewById(R.id.debeFicha);
            haberFicha = itemView.findViewById(R.id.haberFicha);
            saldoFicha = itemView.findViewById(R.id.saldoFicha);
            estadoFicha = itemView.findViewById(R.id.estadoFicha);
            separadorFicha = itemView.findViewById(R.id.separadorFicha);

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

    @NonNull
    @Override
    public ViewHolderConsultaFicha onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mViewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ficha, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        ViewHolderConsultaFicha viewHolderConsultaFicha = new ViewHolderConsultaFicha(view, mlistener);
        viewHolderConsultaFicha.nombrePaciente.setTypeface(face);
        viewHolderConsultaFicha.motivo.setTypeface(face);
        viewHolderConsultaFicha.fechaFicha.setTypeface(face);
        viewHolderConsultaFicha.debeFicha.setTypeface(face);
        viewHolderConsultaFicha.haberFicha.setTypeface(face);
        viewHolderConsultaFicha.saldoFicha.setTypeface(face);
        viewHolderConsultaFicha.estadoFicha.setTypeface(face);
        return viewHolderConsultaFicha;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConsultaFicha viewHolderConsultaFicha, int i) {
        ItemsFichas itemsFichas = mLista.get(i);
        viewHolderConsultaFicha.nombrePaciente.setText(itemsFichas.getNombre());
        viewHolderConsultaFicha.motivo.setText(itemsFichas.getMotivo());
        viewHolderConsultaFicha.fechaFicha.setText(itemsFichas.getFecha());
        viewHolderConsultaFicha.debeFicha.setText(String.format("%.2f", itemsFichas.getCargo()));
        viewHolderConsultaFicha.haberFicha.setText(String.format("%.2f", itemsFichas.getAbono()));
        viewHolderConsultaFicha.saldoFicha.setText(String.format("%.2f", itemsFichas.getSaldo()));
//        viewHolderConsultaFicha.haberFicha.setText(String.valueOf(itemsFichas.getAbono()));
//        viewHolderConsultaFicha.saldoFicha.setText(String.valueOf(itemsFichas.getSaldo()));

        if (itemsFichas.getEstado()) {
            viewHolderConsultaFicha.estadoFicha.setText("Habilitado");
            viewHolderConsultaFicha.estadoFicha.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.VerdeOscuro));
        } else {
            viewHolderConsultaFicha.estadoFicha.setText("Deshabilitado");
            viewHolderConsultaFicha.estadoFicha.setBackgroundColor(mViewGroup.getContext().getResources().getColor(R.color.RojoOscuro));
        }

        if (i < mLista.size()) {
            viewHolderConsultaFicha.separadorFicha.setVisibility(View.VISIBLE);
        } else {
            viewHolderConsultaFicha.separadorFicha.setVisibility(View.INVISIBLE);
        }

        setAnimation(viewHolderConsultaFicha.itemView, i);
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

}
