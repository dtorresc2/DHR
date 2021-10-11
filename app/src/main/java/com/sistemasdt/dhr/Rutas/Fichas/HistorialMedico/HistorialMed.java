package com.sistemasdt.dhr.Rutas.Fichas.HistorialMedico;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.sistemasdt.dhr.Rutas.Fichas.FichaForm.Ficha;
import com.sistemasdt.dhr.R;

public class HistorialMed extends Fragment {
    private Toolbar toolbar;
    private CheckBox hospitalizado, alergia, medicamento, tratamiento, hemorragia;
    private EditText desc_hos, desc_alergia, desc_medicamento, otro;
    private FloatingActionButton guardador;
    private static final String TAG = "MyActivity";

    public HistorialMed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historialmed, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        //Barra de Titulo
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Medico (1/2)");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ficha ficha = new Ficha();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, ficha);
                transaction.commit();
            }
        });

        guardador = view.findViewById(R.id.guardador_hm);

        //Componentes del Formulario
        hospitalizado = view.findViewById(R.id.hospitalizado);
        hospitalizado.setTypeface(face);
        alergia = view.findViewById(R.id.alergia);
        alergia.setTypeface(face);
        medicamento = view.findViewById(R.id.medicamento);
        medicamento.setTypeface(face);
        desc_hos = view.findViewById(R.id.desc_hospi);
        desc_hos.setTypeface(face);
        desc_alergia = view.findViewById(R.id.desc_alergia);
        desc_alergia.setTypeface(face);
        desc_medicamento = view.findViewById(R.id.desc_medi);
        desc_medicamento.setTypeface(face);

        tratamiento = view.findViewById(R.id.tratamiento);
        tratamiento.setTypeface(face);
        hemorragia = view.findViewById(R.id.hemorragia);
        hemorragia.setTypeface(face);

        //Detalle
        hospitalizado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (hospitalizado.isChecked()) {
                    desc_hos.setEnabled(true);
                } else {
                    desc_hos.setEnabled(false);
                }
            }
        });

        alergia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (alergia.isChecked()) {
                    desc_alergia.setEnabled(true);
                } else {
                    desc_alergia.setEnabled(false);
                }
            }
        });

        medicamento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (medicamento.isChecked()) {
                    desc_medicamento.setEnabled(true);
                } else {
                    desc_medicamento.setEnabled(false);
                }
            }
        });

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED1", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = sharedPreferences.edit();
                escritor.putBoolean("HOSPITALIZADO", hospitalizado.isChecked());
                escritor.putString("DESCRIPCION_HOS", desc_hos.getText().toString());
                escritor.putBoolean("TRATAMIENTO_MEDICO", tratamiento.isChecked());
                escritor.putBoolean("ALERGIA", alergia.isChecked());
                escritor.putString("DESCRIPCION_ALERGIA", desc_alergia.getText().toString());
                escritor.putBoolean("HEMORRAGIA", hemorragia.isChecked());
                escritor.putBoolean("MEDICAMENTO", medicamento.isChecked());
                escritor.putString("DESCRIPCION_MEDICAMENTO", desc_medicamento.getText().toString());
                escritor.commit();

                HistorialMedDos historialMedDos = new HistorialMedDos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, historialMedDos);
                transaction.commit();
            }
        });

        cargarDatos();

        return view;
    }

    public void cargarDatos() {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED1", Context.MODE_PRIVATE);
        hospitalizado.setChecked(sharedPreferences.getBoolean("HOSPITALIZADO", false));
        desc_hos.setText(sharedPreferences.getString("DESCRIPCION_HOS", ""));
        alergia.setChecked(sharedPreferences.getBoolean("ALERGIA", false));
        desc_alergia.setText(sharedPreferences.getString("DESCRIPCION_ALERGIA", ""));
        medicamento.setChecked(sharedPreferences.getBoolean("MEDICAMENTO", false));
        desc_medicamento.setText(sharedPreferences.getString("DESCRIPCION_MEDICAMENTO", ""));
        tratamiento.setChecked(sharedPreferences.getBoolean("TRATAMIENTO_MEDICO", false));
        hemorragia.setChecked(sharedPreferences.getBoolean("HEMORRAGIA", false));
    }
}