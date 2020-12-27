package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

import android.graphics.Typeface;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;

public class Piezas extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText nombrePieza, numeroPieza;
    private RadioButton truePieza, falsePieza;
    private FloatingActionButton guardadorPieza;
    private TextView tituloPieza;

    private boolean modoEdicion;
    private Typeface typeface;
    private int ID_PIEZA;

    public Piezas() {
        modoEdicion = false;
    }

    public void editarPieza(int id) {
        modoEdicion = true;
        ID_PIEZA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_piezas, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        if (!modoEdicion)
            toolbar.setTitle("Pieza Nueva");
        else
            toolbar.setTitle("Pieza #" + ID_PIEZA);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListadoPiezas listadoPiezas = new ListadoPiezas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoPiezas);
                transaction.commit();
            }
        });

        nombrePieza = view.findViewById(R.id.nombrePieza);
        nombrePieza.setTypeface(typeface);

        numeroPieza = view.findViewById(R.id.numeroPieza);
        numeroPieza.setTypeface(typeface);

        tituloPieza = view.findViewById(R.id.tituloEstadoPieza);
        tituloPieza.setTypeface(typeface);

        truePieza = view.findViewById(R.id.truePieza);
        truePieza.setTypeface(typeface);

        falsePieza = view.findViewById(R.id.falsePieza);
        falsePieza.setTypeface(typeface);

        guardadorPieza = view.findViewById(R.id.grabarPieza);
        guardadorPieza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}