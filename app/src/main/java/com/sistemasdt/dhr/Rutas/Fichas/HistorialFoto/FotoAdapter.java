package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sistemasdt.dhr.R;

import java.util.ArrayList;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolderConsulta> {
    private List<ItemFoto> imageList;
    private Context c;
    private FotoAdapter.OnClickListener mListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(FotoAdapter.OnClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public static class ViewHolderConsulta extends RecyclerView.ViewHolder {
        SquareImageView siv;
        ImageView ivSelected;

        public ViewHolderConsulta(@NonNull View itemView) {
            super(itemView);
            siv = itemView.findViewById(R.id.siv);
            ivSelected = itemView.findViewById(R.id.checkFoto);
        }
    }

    public FotoAdapter(Context c, List imageList) {
        this.c = c;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolderConsulta onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_foto, viewGroup, false);
        return new ViewHolderConsulta(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderConsulta viewHolderConsulta, int i) {
        final ItemFoto itemFoto = imageList.get(i);
        final FotoAdapter.OnClickListener listener = this.mListener;
//        @SuppressLint("RecyclerView") final int i

//        final String path = imageList.get(i);=
//        Picasso.with(c)
//                .load(path)
//                .resize(250, 250)
//                .centerCrop()
//                .into(viewHolderConsulta.siv);

        Bitmap image = itemFoto.getFoto();
//        Bitmap image = imageList.get(i).getFoto();
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > 250 || height > 250) {
            float escalaAncho = ((float) 250) / width;
            float escalaAlto = ((float) 250) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);
            image = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
        }

        viewHolderConsulta.siv.setImageBitmap(image);
        viewHolderConsulta.ivSelected.setVisibility(itemFoto.isSelected() ? View.VISIBLE : View.GONE);
        viewHolderConsulta.siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemFoto.setSelected(!itemFoto.isSelected());
                viewHolderConsulta.ivSelected.setVisibility(itemFoto.isSelected() ? View.VISIBLE : View.GONE);

                int contador = 0;

                for (ItemFoto aux : imageList) {
                    if (aux.isSelected()) {
                        contador++;
                    }
                }

                if (listener != null) {
                    listener.onItemClick(contador);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
