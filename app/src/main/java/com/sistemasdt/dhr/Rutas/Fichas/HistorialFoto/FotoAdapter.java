package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sistemasdt.dhr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolderConsulta> {
    //    private List<String> imageList;
    private List<ItemFoto> imageList;
    private Context c;
    boolean isEnable = false;
    boolean isSelectAll = false;
    private List<ItemFoto> selectList = new ArrayList<>();
//    private MainViewModel modeloBitmap;

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
//        modeloBitmap = ViewModelProviders.of((FragmentActivity) c).get(HistorialFotografico.class);

        return new ViewHolderConsulta(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderConsulta viewHolderConsulta, int i) {
        final ItemFoto itemFoto = imageList.get(i);
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
        viewHolderConsulta.ivSelected.setVisibility(itemFoto.isSelected() ?  View.VISIBLE : View.GONE);
        viewHolderConsulta.siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemFoto.setSelected(!itemFoto.isSelected());
                viewHolderConsulta.ivSelected.setVisibility(itemFoto.isSelected() ?  View.VISIBLE : View.GONE);
            }
        });

        // Seleccionador
//        viewHolderConsulta.ivSelected.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (!isEnable) {
//                    ActionMode.Callback callback = new ActionMode.Callback() {
//                        @Override
//                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                            isEnable = true;
//                            Bitmap auxiliar = imageList.get(viewHolderConsulta.getAdapterPosition());
//
//                            if (viewHolderConsulta.ivSelected.getVisibility() == View.GONE) {
//                                viewHolderConsulta.ivSelected.setVisibility(View.VISIBLE);
//                                selectList.add(auxiliar);
//                            }
//                            else {
//                                viewHolderConsulta.ivSelected.setVisibility(View.GONE);
//                                selectList.remove(auxiliar);
////                                viewHolderConsulta.itemView.setBackgroundColor(Color.TRANSPARENT);
//                            }
//
//                            return true;
//                        }
//
//                        @Override
//                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                            return false;
//                        }
//
//                        @Override
//                        public void onDestroyActionMode(ActionMode mode) {
//
//                        }
//                    };
//                }
//                return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
