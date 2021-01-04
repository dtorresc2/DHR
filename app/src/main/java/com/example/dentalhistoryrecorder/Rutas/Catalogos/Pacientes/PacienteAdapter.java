package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;

import java.util.ArrayList;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.ViewHolderConsulta> {
    private ArrayList<ItemPaciente> mLista;
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
//        viewHolderConsulta.mid.setText(itemPaciente.getCodigo());
        viewHolderConsulta.mnombre.setText(itemPaciente.getNombre());
        viewHolderConsulta.medad.setText(String.valueOf(itemPaciente.getEdad()));
        viewHolderConsulta.mfecha.setText(itemPaciente.getFecha());
        viewHolderConsulta.mdebe.setText(String.valueOf(itemPaciente.getDebe()));
        viewHolderConsulta.mhaber.setText(String.valueOf(itemPaciente.getHaber()));
        viewHolderConsulta.msaldo.setText(String.valueOf(itemPaciente.getSaldo()));

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
}
