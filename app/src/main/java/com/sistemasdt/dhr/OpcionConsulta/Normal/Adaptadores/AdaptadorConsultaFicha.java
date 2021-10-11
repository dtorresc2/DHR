package com.sistemasdt.dhr.OpcionConsulta.Normal.Adaptadores;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sistemasdt.dhr.OpcionConsulta.Normal.ItemsFichas;
import com.sistemasdt.dhr.R;

import java.util.ArrayList;

public class AdaptadorConsultaFicha extends RecyclerView.Adapter<AdaptadorConsultaFicha.ViewHolderConsultaFicha> {
    private ArrayList<ItemsFichas> mLista;
    private int lastPosition = -1;
    private OnItemClickListener mlistener;

    public AdaptadorConsultaFicha (ArrayList<ItemsFichas> lista){
        mLista = lista;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener){
        mlistener = listener;
    }

    public static class ViewHolderConsultaFicha extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView motivo;
        public TextView medico;
        public TextView fecha;

        public ViewHolderConsultaFicha(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
//            id = itemView.findViewById(R.id.idFic);
//            motivo = itemView.findViewById(R.id.e1);
//            medico = itemView.findViewById(R.id.e2);
//            fecha = itemView.findViewById(R.id.e3);

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

    @NonNull
    @Override
    public ViewHolderConsultaFicha onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ficha, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        ViewHolderConsultaFicha viewHolderConsultaFicha = new ViewHolderConsultaFicha(view, mlistener);
        viewHolderConsultaFicha.id.setTypeface(face);
        viewHolderConsultaFicha.motivo.setTypeface(face);
        viewHolderConsultaFicha.medico.setTypeface(face);
        viewHolderConsultaFicha.fecha.setTypeface(face);
        return viewHolderConsultaFicha;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConsultaFicha viewHolderConsultaFicha, int i) {
        ItemsFichas itemsFichas = mLista.get(i);
        viewHolderConsultaFicha.id.setText(itemsFichas.getId());
        viewHolderConsultaFicha.motivo.setText(itemsFichas.getMotivo());
        viewHolderConsultaFicha.medico.setText(itemsFichas.getMedico());
        viewHolderConsultaFicha.fecha.setText(itemsFichas.getFecha());
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
