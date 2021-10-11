package com.sistemasdt.dhr.Componentes.MenusInferiores;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sistemasdt.dhr.R;

public class MenuInferiorDos extends BottomSheetDialogFragment {
    private MenuInferiorDos.MenuInferiorListener mMenuInferiorListener;
    private int ID = 1;
    private String titulo = "Titulo #";

    public void eventoClick(MenuInferiorDos.MenuInferiorListener menuInferiorListener){
        mMenuInferiorListener = menuInferiorListener;
    }

    public void recibirTitulo(String mTitulo){
        titulo = mTitulo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_inferior_cuentas, container, false);
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
        tituloMenu.setText(titulo);

        return view;
    }

    public interface MenuInferiorListener {
        void onButtonClicked(int opcion);
    }
}
