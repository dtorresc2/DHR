package com.sistemasdt.dhr.Componentes.Dialogos.Configuracion;

import android.app.Dialog;

import android.content.Context;
import android.content.SharedPreferences;

import android.graphics.Typeface;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Switch;
import android.widget.TextView;


import com.sistemasdt.dhr.R;


public class DialogoConfiguracion extends DialogFragment {
    private Toolbar toolbar;
    private TextView titulo1, titulo3, subtitulo4, subtitulo5;
    private TextView etiquetaVersion;
    private Switch recordarCuenta;
    private TextView compartir, abrirManual;

    public DialogoConfiguracion() {

    }

    public static DialogoConfiguracion display(FragmentManager fragmentManager) {
        DialogoConfiguracion dialogoConfiguracion = new DialogoConfiguracion();
        dialogoConfiguracion.show(fragmentManager, "My Activity");
        return dialogoConfiguracion;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_configuracion, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Configuracion del Sistema");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));

        toolbar.inflateMenu(R.menu.opciones_toolbarcitas);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_aceptar:
                    dismiss();
                    return true;

                default:
                    return false;
            }
        });

        titulo1 = view.findViewById(R.id.tituloGeneral);
        titulo1.setTypeface(face);

        titulo3 = view.findViewById(R.id.tituloAyuda);
        titulo3.setTypeface(face);

        recordarCuenta = view.findViewById(R.id.recordarCuenta);
        recordarCuenta.setTypeface(face);

        subtitulo4 = view.findViewById(R.id.tituloAcerca1);
        subtitulo4.setTypeface(face);

        etiquetaVersion = view.findViewById(R.id.tituloAcerca2);
        etiquetaVersion.setTypeface(face);

        subtitulo5 = view.findViewById(R.id.tituloAcerca3);
        subtitulo5.setTypeface(face);

        abrirManual = view.findViewById(R.id.abrirPDF);
        abrirManual.setTypeface(face);

        final SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        recordarCuenta.setChecked(preferencias.getBoolean("recordar", false));

        recordarCuenta.setOnClickListener(v -> {
            if (recordarCuenta.isChecked()) {
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putBoolean("recordar", true);
                editor.commit();
            } else {
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putBoolean("recordar", false);
                editor.commit();
            }
        });

        return view;
    }
}
