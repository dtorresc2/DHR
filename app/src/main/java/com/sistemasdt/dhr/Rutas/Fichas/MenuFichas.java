package com.sistemasdt.dhr.Rutas.Fichas;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.ListadoFichasNormales.ListadoFichas;


public class MenuFichas extends Fragment {
    private LinearLayout botonNuevo, botonNuevoEspecial;
    private TextView titulo, texto_boton_nuevo, texto_boton_especial;

    public MenuFichas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_ficha, container, false);
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
                ListadoFichas listadoFichas = new ListadoFichas();
//                Ficha ficha = new Ficha();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, listadoFichas);
                transaction.commit();
            }
        });

        //Opcion de Crear Nueva Ficha Especial
        botonNuevoEspecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ListadoPacientes listadoPacientes = new ListadoPacientes();
//                listadoPacientes.ObtenerOpcion(3);
//                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
//                transaction.replace(R.id.contenedor, listadoPacientes);
//                transaction.commit();
            }
        });
        return view;
    }
}
