package com.example.dentalhistoryrecorder.Componentes;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;
import com.itextpdf.text.pdf.parser.Line;

public class MenuInferior extends BottomSheetDialogFragment {
    private MenuInferiorListener mMenuInferiorListener;
    private Context mContext;
    private int ID = 1;
    private String titulo = "Titulo #";

    public void eventoClick(MenuInferiorListener menuInferiorListener){
        mMenuInferiorListener = menuInferiorListener;
    }

    public void recibirTitulo(String mTitulo, int mID){
        titulo = mTitulo;
        ID = mID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_inferior, container, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        LinearLayout opcionEditar = view.findViewById(R.id.opc_editar);
        opcionEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuInferiorListener.onButtonClicked(1);
                dismiss();
            }
        });

        LinearLayout opcionEliminar = view.findViewById(R.id.opc_eliminar);
        opcionEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuInferiorListener.onButtonClicked(2);
                dismiss();
            }
        });

        TextView tituloEditar = view.findViewById(R.id.texto_opc_editar);
        tituloEditar.setTypeface(typeface);

        TextView tituloEliminar = view.findViewById(R.id.texto_opc_eliminar);
        tituloEliminar.setTypeface(typeface);

        TextView tituloMenu = view.findViewById(R.id.tituloMenu);
        tituloMenu.setTypeface(typeface);
        tituloMenu.setText(titulo + ID);

        return view;
    }

    public interface MenuInferiorListener {
        void onButtonClicked(int opcion);
    }
}
