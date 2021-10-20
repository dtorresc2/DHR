package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialMedico;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdon;

public class HistorialMedDos extends Fragment {
    private Toolbar toolbar;
    private CheckBox corazon, artritris, tuberculosis, f_reuma, pres_alta, pres_baja, diabetes, anemia, epilepsia;
    private TextInputEditText otro;
    private FloatingActionButton guardador;
    private TextInputLayout otrosLayout;

    public HistorialMedDos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historialmed_dos, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Medico (2/2)");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistorialMed historialMed = new HistorialMed();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, historialMed);
                transaction.commit();
            }
        });

        guardador = view.findViewById(R.id.guardador_hm2);

        corazon = view.findViewById(R.id.corazon);
        corazon.setTypeface(face);
        artritris = view.findViewById(R.id.artritris);
        artritris.setTypeface(face);
        tuberculosis = view.findViewById(R.id.tuberculosis);
        tuberculosis.setTypeface(face);
        f_reuma = view.findViewById(R.id.fiebre);
        f_reuma.setTypeface(face);
        pres_alta = view.findViewById(R.id.presion_alta);
        pres_alta.setTypeface(face);
        pres_baja = view.findViewById(R.id.presion_baja);
        pres_baja.setTypeface(face);
        diabetes = view.findViewById(R.id.diabetes);
        diabetes.setTypeface(face);
        anemia = view.findViewById(R.id.anemia);
        anemia.setTypeface(face);
        epilepsia = view.findViewById(R.id.epilepsia);
        epilepsia.setTypeface(face);
        otro = view.findViewById(R.id.otro_hm);
        otro.setTypeface(face);

        otrosLayout = view.findViewById(R.id.otroLayout);
        otrosLayout.setTypeface(face);

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED2", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = sharedPreferences.edit();
                escritor.putBoolean("CORAZON", corazon.isChecked());
                escritor.putBoolean("ARTRITIS", artritris.isChecked());
                escritor.putBoolean("TUBERCULOSIS", tuberculosis.isChecked());
                escritor.putBoolean("FIEBREREU", f_reuma.isChecked());
                escritor.putBoolean("PRESION_ALTA", pres_alta.isChecked());
                escritor.putBoolean("PRESION_BAJA", pres_baja.isChecked());
                escritor.putBoolean("DIABETES", diabetes.isChecked());
                escritor.putBoolean("ANEMIA", anemia.isChecked());
                escritor.putBoolean("EPILEPSIA", epilepsia.isChecked());
                escritor.putString("OTROS", otro.getText().toString());
                escritor.commit();

                HistorialOdon historialOdon = new HistorialOdon();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, historialOdon);
                transaction.commit();
            }
        });

        obtenerDatos();

        return view;
    }

    public void obtenerDatos() {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED2", Context.MODE_PRIVATE);
        corazon.setChecked(sharedPreferences.getBoolean("CORAZON", false));
        artritris.setChecked(sharedPreferences.getBoolean("ARTRITIS", false));
        tuberculosis.setChecked(sharedPreferences.getBoolean("TUBERCULOSIS", false));
        f_reuma.setChecked(sharedPreferences.getBoolean("FIEBREREU", false));
        pres_alta.setChecked(sharedPreferences.getBoolean("PRESION_ALTA", false));
        pres_baja.setChecked(sharedPreferences.getBoolean("PRESION_BAJA", false));
        diabetes.setChecked(sharedPreferences.getBoolean("DIABETES", false));
        anemia.setChecked(sharedPreferences.getBoolean("ANEMIA", false));
        epilepsia.setChecked(sharedPreferences.getBoolean("EPILEPSIA", false));
        otro.setText(sharedPreferences.getString("OTROS", ""));
    }
}
