package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sistemasdt.dhr.R;

import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolderConsulta> {
    private List<String> imageList;
    private Context c;

    public static class ViewHolderConsulta extends RecyclerView.ViewHolder{
        SquareImageView siv;
        public ViewHolderConsulta(@NonNull View itemView) {
            super(itemView);
            siv = itemView.findViewById(R.id.siv);
        }
    }

    public FotoAdapter(Context c, List imageList) {
        this.c = c;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolderConsulta onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConsulta viewHolderConsulta, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
