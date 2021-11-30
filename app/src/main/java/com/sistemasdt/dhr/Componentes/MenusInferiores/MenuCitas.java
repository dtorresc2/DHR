package com.sistemasdt.dhr.Componentes.MenusInferiores;

import android.app.Dialog;
import android.content.Context;
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

public class MenuCitas extends BottomSheetDialogFragment {
    private MenuCitasListener mMenuCitasListener;
    private Context mContext;
    private int ID = 1;
    private String titulo = "Titulo #";

    public void eventoClick(MenuCitas.MenuCitasListener menuCitasListener) {
        mMenuCitasListener = menuCitasListener;
    }

    public void recibirTitulo(String mTitulo) {
        titulo = mTitulo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_citas, container, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        LinearLayout opcionEditar = view.findViewById(R.id.opc_editar);
        opcionEditar.setOnClickListener(v -> {
            mMenuCitasListener.onButtonClicked(1);
            dismiss();
        });

        LinearLayout opcionDeshabilitar = view.findViewById(R.id.opc_deshabilitar);
        opcionDeshabilitar.setOnClickListener(v -> {
            mMenuCitasListener.onButtonClicked(2);
            dismiss();
        });

        LinearLayout opcionEliminar = view.findViewById(R.id.opc_eliminar);
        opcionEliminar.setOnClickListener(v -> {
            mMenuCitasListener.onButtonClicked(3);
            dismiss();
        });

        LinearLayout opcionAsignar = view.findViewById(R.id.opc_asignar);
        opcionAsignar.setOnClickListener(v -> {
            mMenuCitasListener.onButtonClicked(3);
            dismiss();
        });

        TextView tituloEditar = view.findViewById(R.id.texto_opc_editar);
        tituloEditar.setTypeface(typeface);

        TextView tituloBloquear = view.findViewById(R.id.texto_opc_deshabilitar);
        tituloBloquear.setTypeface(typeface);

        TextView tituloEliminar = view.findViewById(R.id.texto_opc_eliminar);
        tituloEliminar.setTypeface(typeface);

        TextView tituloAsignar = view.findViewById(R.id.texto_opc_alarma);
        tituloAsignar.setTypeface(typeface);

        TextView tituloMenu = view.findViewById(R.id.tituloMenu);
        tituloMenu.setTypeface(typeface);
        tituloMenu.setText(titulo);

        return view;
    }

    public interface MenuCitasListener {
        void onButtonClicked(int opcion);
    }
}
