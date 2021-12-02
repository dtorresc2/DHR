package com.sistemasdt.dhr.Componentes.MenusInferiores;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sistemasdt.dhr.R;

public class MenuInferiorFicha extends BottomSheetDialogFragment {
    MenuFichaListener mMenuFichaListener;
    private String titulo = "Titulo #";
    private boolean ESTADO = false;

    public void recibirTitulo(String mTitulo) {
        titulo = mTitulo;
    }

    public void recibirEstado(boolean estado) {
        ESTADO = estado;
    }

    public void eventoClick(MenuInferiorFicha.MenuFichaListener menuFichaListener) {
        mMenuFichaListener = menuFichaListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        LinearLayout opcionEditar = view.findViewById(R.id.opc_editar);
        opcionEditar.setOnClickListener(v -> {
            mMenuFichaListener.onButtonClicked(1);
            dismiss();
        });

        LinearLayout opcionDeshabilitar = view.findViewById(R.id.opc_deshabilitar);
        opcionDeshabilitar.setOnClickListener(v -> {
            mMenuFichaListener.onButtonClicked(2);
            dismiss();
        });

        LinearLayout opcionGenerar = view.findViewById(R.id.opc_generar);
        opcionGenerar.setOnClickListener(v -> {
            mMenuFichaListener.onButtonClicked(3);
            dismiss();
        });

        LinearLayout opcionEliminar = view.findViewById(R.id.opc_eliminar);
        opcionEliminar.setOnClickListener(v -> {
            mMenuFichaListener.onButtonClicked(4);
            dismiss();
        });

        TextView tituloEditar = view.findViewById(R.id.texto_opc_editar);
        tituloEditar.setTypeface(typeface);

        TextView tituloBloquear = view.findViewById(R.id.texto_opc_deshabilitar);
        tituloBloquear.setTypeface(typeface);

        TextView tituloGenerar = view.findViewById(R.id.texto_opc_generar);
        tituloGenerar.setTypeface(typeface);

        TextView tituloEliminar = view.findViewById(R.id.texto_opc_eliminar);
        tituloEliminar.setTypeface(typeface);

        TextView tituloMenu = view.findViewById(R.id.tituloMenu);
        tituloMenu.setTypeface(typeface);
        tituloMenu.setText(titulo);

        return view;
    }

    public interface MenuFichaListener {
        void onButtonClicked(int opcion);
    }
}
