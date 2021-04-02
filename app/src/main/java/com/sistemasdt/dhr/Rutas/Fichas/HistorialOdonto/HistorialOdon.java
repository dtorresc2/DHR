package com.sistemasdt.dhr.Rutas.Fichas.HistorialOdonto;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.sistemasdt.dhr.Rutas.Fichas.HistorialMedico.HistorialMedDos;
import com.sistemasdt.dhr.R;

public class HistorialOdon extends Fragment {
    private Toolbar toolbar;
    private FloatingActionButton guardador;
    CheckBox dolor, gingivitis;
    private EditText desc_dolor;
    private TextInputEditText otros;

    public HistorialOdon() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historial_odon, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Odontodologico (1/2)");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistorialMedDos historialMedDos = new HistorialMedDos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, historialMedDos);
                transaction.commit();
            }
        });

        dolor = view.findViewById(R.id.dolor);
        dolor.setTypeface(face);
        gingivitis = view.findViewById(R.id.gingivitis);
        gingivitis.setTypeface(face);
        otros = view.findViewById(R.id.otros_ho);
        otros.setTypeface(face);
        desc_dolor = view.findViewById(R.id.desc_dolor);

        guardador = view.findViewById(R.id.guardador_hd);

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HOD1", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = sharedPreferences.edit();
                escritor.putBoolean("DOLOR", dolor.isChecked());
                escritor.putString("DESC_DOLOR", desc_dolor.getText().toString());
                escritor.putBoolean("GINGIVITIS", gingivitis.isChecked());
                escritor.putString("OTROS", otros.getText().toString());
                escritor.commit();

                HistorialOdonDos historialOdonDos = new HistorialOdonDos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, historialOdonDos);
                transaction.commit();
            }
        });

        obtenerDatos();

        return view;
    }

    public void obtenerDatos() {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HOD1", Context.MODE_PRIVATE);
        dolor.setChecked(sharedPreferences.getBoolean("DOLOR", false));
        gingivitis.setChecked(sharedPreferences.getBoolean("GINGIVITIS", false));
        desc_dolor.setText(sharedPreferences.getString("DESC_DOLOR", ""));
        otros.setText(sharedPreferences.getString("OTROS", ""));
    }
}
