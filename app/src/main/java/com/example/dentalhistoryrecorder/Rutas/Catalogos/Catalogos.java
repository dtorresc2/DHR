package com.example.dentalhistoryrecorder.Rutas.Catalogos;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;


public class Catalogos extends Fragment {
    TextView titulo, texto_pacientes, texto_servicios, texto_piezas, texto_cuentas;
    LinearLayout opcion_pacientes, opcion_servicios, opcion_piezas, opcion_cuentas;

    public Catalogos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalogos, container, false);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        titulo = view.findViewById(R.id.cat_titulo);
        titulo.setTypeface(typeface);

        texto_pacientes = view.findViewById(R.id.cat_texto_pacientes);
        texto_pacientes.setTypeface(typeface);

        texto_servicios = view.findViewById(R.id.cat_texto_servicios);
        texto_servicios.setTypeface(typeface);

        texto_piezas = view.findViewById(R.id.cat_texto_piezas);
        texto_piezas.setTypeface(typeface);

        texto_cuentas = view.findViewById(R.id.cat_texto_cuentas);
        texto_cuentas.setTypeface(typeface);

        opcion_pacientes = view.findViewById(R.id.cat_opc_pacientes);
        opcion_servicios = view.findViewById(R.id.cat_opc_servicios);
        opcion_piezas = view.findViewById(R.id.cat_opc_piezas);
        opcion_cuentas = view.findViewById(R.id.cat_opc_cuentas);

        opcion_servicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}