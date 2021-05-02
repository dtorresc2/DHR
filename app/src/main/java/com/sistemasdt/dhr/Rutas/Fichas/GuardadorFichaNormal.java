package com.sistemasdt.dhr.Rutas.Fichas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto.FotoAdapter;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto.ItemFoto;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialOdonto.HistorialOdonDos;

public class GuardadorFichaNormal extends Fragment {
    private Toolbar toolbar;
    private TextView tituloResumenPaciente, resumenPaciente;
    private TextView tituloResumen, tituloResumenTratamientos, resumenTratamientos;
    private TextView tituloResumenPagos, resumenPagos, tituloResumenFotos, resumenFotos;

    public GuardadorFichaNormal() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guardador_ficha_normal, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Resumen de Ficha");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar lista
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Resumen de Ficha");
                builder.setMessage("¿Seguro que desea cancelar?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MenuFichas menuFichas = new MenuFichas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction.replace(R.id.contenedor, menuFichas);
                        transaction.commit();
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        tituloResumenPaciente = view.findViewById(R.id.tituloResumenPaciente);
        tituloResumenPaciente.setTypeface(face);

        resumenPaciente = view.findViewById(R.id.resumenPaciente);
        resumenPaciente.setTypeface(face);

        tituloResumen = view.findViewById(R.id.tituloResumen);
        tituloResumen.setTypeface(face);

        tituloResumenTratamientos = view.findViewById(R.id.tituloResumenTratamientos);
        tituloResumenTratamientos.setTypeface(face);

        resumenTratamientos = view.findViewById(R.id.resumenTratamientos);
        resumenTratamientos.setTypeface(face);

        tituloResumenPagos = view.findViewById(R.id.tituloResumenPagos);
        tituloResumenPagos.setTypeface(face);

        resumenPagos = view.findViewById(R.id.resumenPagos);
        resumenPagos.setTypeface(face);

        tituloResumenFotos = view.findViewById(R.id.tituloResumenFotos);
        tituloResumenFotos.setTypeface(face);

        tituloResumenFotos = view.findViewById(R.id.tituloResumenFotos);
        tituloResumenFotos.setTypeface(face);

        resumenFotos = view.findViewById(R.id.resumenFotos);
        resumenFotos.setTypeface(face);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
        resumenPaciente.setText(sharedPreferences.getString("PACIENTE", "0"));
        resumenTratamientos.setText(sharedPreferences.getString("NO_TRATAMIENTOS", "0"));
        resumenPagos.setText(sharedPreferences.getString("NO_PAGOS", "0"));
        resumenFotos.setText(sharedPreferences.getString("NO_FOTOS", "0"));

        return view;
    }
}