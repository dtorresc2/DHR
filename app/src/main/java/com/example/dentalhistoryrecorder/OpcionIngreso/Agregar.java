package com.example.dentalhistoryrecorder.OpcionIngreso;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes.Pacientes;
import com.example.dentalhistoryrecorder.R;


public class Agregar extends Fragment {
    private LinearLayout botonNuevo, botonNuevoEspecial;
    private TextView titulo, texto_boton_nuevo, texto_boton_especial;

    public Agregar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar, container, false);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        botonNuevo = view.findViewById(R.id.fnueva);
        botonNuevoEspecial = view.findViewById(R.id.fenueva);

        titulo = view.findViewById(R.id.titulo_fichas);
        titulo.setTypeface(typeface);
        texto_boton_nuevo = view.findViewById(R.id.texto_fnueva);
        texto_boton_nuevo.setTypeface(typeface);
        texto_boton_especial = view.findViewById(R.id.texto_fenueva);
        texto_boton_especial.setTypeface(typeface);

        //Opcion de Crear Nueva Ficha
        botonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pacientes pacientes = new Pacientes();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, pacientes);
                transaction.commit();
            }
        });

        //Opcion de Crear Nueva Ficha Especial
        botonNuevoEspecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoPacientes listadoPacientes = new ListadoPacientes();
                listadoPacientes.ObtenerOpcion(3);
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, listadoPacientes);
                transaction.commit();

            }
        });

        return view;
    }
}
