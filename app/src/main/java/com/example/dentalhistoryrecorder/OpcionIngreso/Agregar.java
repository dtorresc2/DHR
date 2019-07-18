package com.example.dentalhistoryrecorder.OpcionIngreso;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Consultar;
import com.example.dentalhistoryrecorder.OpcionIngreso.Especial.IngCostos;
import com.example.dentalhistoryrecorder.OpcionIngreso.Normal.IngPersonales;
import com.example.dentalhistoryrecorder.R;


public class Agregar extends Fragment {
    private Toolbar toolbar;
    private Button botonNuevo, botonNuevoEspecial;

    public Agregar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Opciones de Ingreso");
        botonNuevo = (Button) view.findViewById(R.id.fnueva);
        botonNuevo.setTypeface(face);
        botonNuevoEspecial = (Button) view.findViewById(R.id.fenueva);
        botonNuevoEspecial.setTypeface(face);

        //Opcion de Crear Nueva Ficha
        botonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Opcion 1",Toast.LENGTH_SHORT).show();
                //IngresoNormal ingresoNormal = new IngresoNormal();
                IngPersonales ingPersonales = new IngPersonales();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                //transaction.replace(R.id.contenedor, ingresoNormal);
                transaction.replace(R.id.contenedor, ingPersonales);
                transaction.commit();
            }
        });

        //Opcion de Crear Nueva Ficha Especial
        botonNuevoEspecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*IngCostos ingCostos = new IngCostos();*/

                Consultar consultar = new Consultar();
                consultar.ObtenerOpcion(3);
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, consultar);
                transaction.commit();

            }
        });

        return view;
    }
}
