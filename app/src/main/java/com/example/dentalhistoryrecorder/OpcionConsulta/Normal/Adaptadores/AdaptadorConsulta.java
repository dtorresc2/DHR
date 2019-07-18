package com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Adaptadores;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.OpcionConsulta.Items;
import com.example.dentalhistoryrecorder.R;

import java.util.ArrayList;

public class AdaptadorConsulta extends RecyclerView.Adapter<AdaptadorConsulta.ViewHolderConsulta> {
    private ArrayList<Items> mLista;
    private int lastPosition = -1;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class ViewHolderConsulta extends RecyclerView.ViewHolder {
        public TextView mid;
        public TextView mnombre;
        public TextView mcontadorN;
        public TextView mcontadorE;
        public TextView medad;
        public TextView mfecha;

        public ViewHolderConsulta(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mid = itemView.findViewById(R.id.idPac);
            mnombre = itemView.findViewById(R.id.nombrePaciente);
            mcontadorN = itemView.findViewById(R.id.contadorN);
            mcontadorE = itemView.findViewById(R.id.contadorE);
            medad = itemView.findViewById(R.id.edadPaciente);
            mfecha = itemView.findViewById(R.id.fechaPaciente);

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

    public AdaptadorConsulta(ArrayList<Items> lista) {
        mLista = lista;
    }

    @NonNull
    @Override
    public ViewHolderConsulta onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
        ViewHolderConsulta viewHolderConsulta = new ViewHolderConsulta(view, mlistener);
        viewHolderConsulta.mid.setTypeface(face);
        viewHolderConsulta.mnombre.setTypeface(face);
        viewHolderConsulta.mcontadorN.setTypeface(face);
        viewHolderConsulta.mcontadorE.setTypeface(face);
        viewHolderConsulta.medad.setTypeface(face);
        viewHolderConsulta.mfecha.setTypeface(face);
        return viewHolderConsulta;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConsulta viewHolderConsulta, int i) {
        Items items = mLista.get(i);
        viewHolderConsulta.mid.setText(items.getMid());
        viewHolderConsulta.mnombre.setText(items.getMnombre());
        viewHolderConsulta.mcontadorN.setText(items.getMcontadorN());
        viewHolderConsulta.mcontadorE.setText(items.getMcontadorE());
        viewHolderConsulta.medad.setText(items.getMedad());
        viewHolderConsulta.mfecha.setText(items.getMfecha());
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
}
