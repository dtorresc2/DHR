package com.sistemasdt.dhr.OpcionCitas.Adaptador;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sistemasdt.dhr.OpcionCitas.ItemCita;
import com.sistemasdt.dhr.R;

import java.util.ArrayList;

public class AdaptadorCita extends RecyclerView.Adapter<AdaptadorCita.ViewHolderCita> {
    private ArrayList<ItemCita> mLista;
    private int lastPosition = -1;
    private AdaptadorCita.OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class ViewHolderCita extends RecyclerView.ViewHolder {
        public TextView mhora;
        public TextView mfecha;
        public TextView mnombre;
        public TextView mdescripcion;
        public ImageView mrealizado;


        public ViewHolderCita(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mhora = itemView.findViewById(R.id.horaCita);
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
    }

    @NonNull
    @Override
    public AdaptadorCita.ViewHolderCita onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_citas, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        AdaptadorCita.ViewHolderCita viewHolderCita = new AdaptadorCita.ViewHolderCita(view, mlistener);
        viewHolderCita.mhora.setTypeface(face);
        viewHolderCita.mfecha.setTypeface(face);
        viewHolderCita.mnombre.setTypeface(face);
        viewHolderCita.mdescripcion.setTypeface(face);
        return viewHolderCita;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCita.ViewHolderCita viewHolderCita, int i) {
        ItemCita itemCita = mLista.get(i);
        viewHolderCita.mhora.setText(itemCita.getMhora());
        viewHolderCita.mfecha.setText(itemCita.getMfecha());
        viewHolderCita.mnombre.setText(itemCita.getMnombre());
        viewHolderCita.mdescripcion.setText(itemCita.getMdescripcion());
        if (itemCita.getMrealizado().equals("0")){
            viewHolderCita.mrealizado.setImageResource(R.drawable.ic_estadocita);
        }
        else{
            viewHolderCita.mrealizado.setImageResource(R.drawable.ic_aceptado);
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
}
