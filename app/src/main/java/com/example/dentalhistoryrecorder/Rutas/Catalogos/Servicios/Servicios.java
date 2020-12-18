package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.annotation.SuppressLint;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.itextpdf.text.xml.simpleparser.EntitiesToUnicode;

public class Servicios extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText descripcionServicio, montoServicio;
    private RadioButton trueServicio, falseServicio;
    private FloatingActionButton guardadorServicio;
    private TextView tituloServicio;

    private boolean modoEdicion;
//    private Typeface typeface;

    public Servicios() {
        modoEdicion = false;
    }

    public void editarServicio(int id) {
        modoEdicion = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servicios, container, false);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Servicio Nuevo");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListadoServicios listadoServicios = new ListadoServicios();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoServicios);
                transaction.commit();
            }
        });

        descripcionServicio = view.findViewById(R.id.descripcionServicio);
        descripcionServicio.setTypeface(typeface);

        montoServicio = view.findViewById(R.id.montoServicio);
        montoServicio.setTypeface(typeface);

        tituloServicio = view.findViewById(R.id.tituloEstadoServicio);
        tituloServicio.setTypeface(typeface);

        trueServicio = view.findViewById(R.id.trueServicio);
        trueServicio.setTypeface(typeface);

        falseServicio = view.findViewById(R.id.falseServicio);
        falseServicio.setTypeface(typeface);

        guardadorServicio = view.findViewById(R.id.grabarServicio);
        guardadorServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}