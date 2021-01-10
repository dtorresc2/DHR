package com.sistemasdt.dhr.Rutas.Catalogos;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Cuentas.ListadoCuentas;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.sistemasdt.dhr.Rutas.Catalogos.Piezas.ListadoPiezas;
import com.sistemasdt.dhr.Rutas.Catalogos.Servicios.ListadoServicios;


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
                ListadoServicios listadoServicios = new ListadoServicios();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoServicios);
                transaction.commit();
            }
        });

        opcion_piezas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoPiezas listadoPiezas = new ListadoPiezas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoPiezas);
                transaction.commit();
            }
        });

        opcion_cuentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoCuentas listadoCuentas = new ListadoCuentas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoCuentas);
                transaction.commit();
            }
        });

        opcion_pacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoPacientes listadoPacientes = new ListadoPacientes();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoPacientes);
                transaction.commit();
            }
        });

        return view;
    }
}