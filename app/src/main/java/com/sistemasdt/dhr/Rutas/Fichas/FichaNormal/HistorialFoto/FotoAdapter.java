package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sistemasdt.dhr.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        final int contador = i;
        final ItemFoto itemFoto = imageList.get(i);
        final FotoAdapter.OnClickListener listener = this.mListener;

        if (itemFoto.getFoto() != null) {
            Bitmap image = itemFoto.getFoto();
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
            viewHolderConsulta.siv.setOnClickListener(v -> {
                itemFoto.setSelected(!itemFoto.isSelected());
                viewHolderConsulta.ivSelected.setVisibility(itemFoto.isSelected() ? View.VISIBLE : View.GONE);

                int conteo = 0;

                for (ItemFoto aux : imageList) {
                    if (aux.isSelected()) {
                        conteo++;
                    }
                }

                if (listener != null) {
                    listener.onItemClick(conteo);
                }
            });
        } else {
            Picasso.with(c)
                    .load(itemFoto.getUrl())
                    .placeholder(R.drawable.logonuevo)
                    .resize(250, 250)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            viewHolderConsulta.siv.setImageBitmap(bitmap);
                            ItemFoto aux = new ItemFoto(bitmap, "", false);
                            imageList.set(contador, aux);

                            viewHolderConsulta.ivSelected.setVisibility(aux.isSelected() ? View.VISIBLE : View.GONE);
                            viewHolderConsulta.siv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    aux.setSelected(!aux.isSelected());
                                    viewHolderConsulta.ivSelected.setVisibility(aux.isSelected() ? View.VISIBLE : View.GONE);

                                    int conteo = 0;

                                    for (ItemFoto aux : imageList) {
                                        if (aux.isSelected()) {
                                            conteo++;
                                        }
                                    }

                                    if (listener != null) {
                                        listener.onItemClick(conteo);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            viewHolderConsulta.siv.setImageDrawable(placeHolderDrawable);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public ItemFoto getItem(int index) {
        return this.imageList.get(index);
    }
}
