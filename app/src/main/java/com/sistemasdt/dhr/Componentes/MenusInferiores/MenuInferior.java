package com.sistemasdt.dhr.Componentes.MenusInferiores;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sistemasdt.dhr.R;

public class MenuInferior extends BottomSheetDialogFragment {
    private MenuInferiorListener mMenuInferiorListener;
    private String titulo = "Titulo #";
    private boolean ESTADO = false;
    private int CANTIDAD_FICHAS_NORMALES = 0;

    public void eventoClick(MenuInferiorListener menuInferiorListener) {
        mMenuInferiorListener = menuInferiorListener;
    }

    public void recibirTitulo(String mTitulo) {
        titulo = mTitulo;
    }

    public void recibirEstado(boolean estado) {
        ESTADO = estado;
    }

    public void recibirCantiadFichas(int fichasNormales) {
        CANTIDAD_FICHAS_NORMALES = fichasNormales;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_inferior, container, false);
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");

        LinearLayout opcionEditar = view.findViewById(R.id.opc_editar);
        opcionEditar.setOnClickListener(v -> {
            mMenuInferiorListener.onButtonClicked(1);
            dismiss();
        });

        LinearLayout opcionDeshabilitar = view.findViewById(R.id.opc_deshabilitar);
        opcionDeshabilitar.setOnClickListener(v -> {
            mMenuInferiorListener.onButtonClicked(2);
            dismiss();
        });

        LinearLayout opcionEliminar = view.findViewById(R.id.opc_eliminar);
        opcionEliminar.setOnClickListener(v -> {
            mMenuInferiorListener.onButtonClicked(3);
            dismiss();
        });

        TextView tituloEditar = view.findViewById(R.id.texto_opc_editar);
        tituloEditar.setTypeface(typeface);

        TextView tituloBloquear = view.findViewById(R.id.texto_opc_deshabilitar);
        tituloBloquear.setTypeface(typeface);

        TextView tituloEliminar = view.findViewById(R.id.texto_opc_eliminar);
        tituloEliminar.setTypeface(typeface);

        TextView tituloMenu = view.findViewById(R.id.tituloMenu);
        tituloMenu.setTypeface(typeface);
        tituloMenu.setText(titulo);

        ImageView iconoBloqueo = view.findViewById(R.id.icono_bloqueo);

        LinearLayout especioElimina = view.findViewById(R.id.layoutEliminar);

        if (!ESTADO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconoBloqueo.setImageDrawable(getContext().getDrawable(R.drawable.ic_check));
            }
            tituloBloquear.setText("Habilitar");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconoBloqueo.setImageDrawable(getContext().getDrawable(R.drawable.ic_cerrar));
            }
            tituloBloquear.setText("Deshabilitar");
        }

        if (CANTIDAD_FICHAS_NORMALES > 0) {
            especioElimina.setVisibility(View.GONE);
        } else {
            especioElimina.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public interface MenuInferiorListener {
        void onButtonClicked(int opcion);
    }
}
