package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sistemasdt.dhr.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolderConsulta> {
    //    private List<String> imageList;
    private List<Bitmap> imageList;
    private Context c;

    public static class ViewHolderConsulta extends RecyclerView.ViewHolder {
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
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_foto, viewGroup, false);
        return new ViewHolderConsulta(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConsulta viewHolderConsulta, int i) {
//        final String path = imageList.get(i);=
//        Picasso.with(c)
//                .load(path)
//                .resize(250, 250)
//                .centerCrop()
//                .into(viewHolderConsulta.siv);

        Bitmap image = imageList.get(i);
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
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
