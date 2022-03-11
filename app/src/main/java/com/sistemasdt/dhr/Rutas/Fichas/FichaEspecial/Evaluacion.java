package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;

import com.sistemasdt.dhr.R;


public class Evaluacion extends Fragment {
    private Toolbar toolbar;
    private TextView titulo1, titulo2, titulo3, titulo4, titulo5, titulo6, titulo7, titulo8, titulo9, titulo10, titulo11, titulo12;
    private CheckBox ad1, ad2, ad3, ad4, ad5;
    private CheckBox fas1, fas2, fas3;
    private CheckBox fai1, fai2, fai3;
    private CheckBox oc1, oc2, oc3, oc4, oc5;
    private CheckBox df1, df2, df3, df4;
    private CheckBox rp1, rp2, rp3, rp4, rp5;
    private CheckBox len1, len2, len3;
    private CheckBox de1, de2;
    private CheckBox lm1, lm2;
    private CheckBox h1, h2, h3, h4, h5;
    private CheckBox tmj1, tmj2, tmj3, tmj4, tmj5, tmj6, tmj7;
    private EditText descripcion;
    private FloatingActionButton siguiente;

    private boolean MODO_EDICION = false;

    public Evaluacion() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_evaluacion, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle("Evaluacion");

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                Contrato contrato = new Contrato();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, contrato);
                transaction.commit();
            } else {
                ListadoEvaluaciones listadoEvaluaciones = new ListadoEvaluaciones();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, listadoEvaluaciones);
                transaction.commit();
            }
        });

        //Alineacion Dental
        titulo1 = view.findViewById(R.id.titulo1);
        titulo1.setTypeface(face);
        ad1 = view.findViewById(R.id.ad1);
        ad1.setTypeface(face);
        ad2 = view.findViewById(R.id.ad2);
        ad2.setTypeface(face);
        ad3 = view.findViewById(R.id.ad3);
        ad3.setTypeface(face);
        ad4 = view.findViewById(R.id.ad4);
        ad4.setTypeface(face);
        ad5 = view.findViewById(R.id.ad5);
        ad5.setTypeface(face);

        //Forma del Arco Superior
        titulo2 = view.findViewById(R.id.titulo2);
        titulo2.setTypeface(face);
        fas1 = view.findViewById(R.id.fas1);
        fas1.setTypeface(face);
        fas2 = view.findViewById(R.id.fas2);
        fas2.setTypeface(face);
        fas3 = view.findViewById(R.id.fas3);
        fas3.setTypeface(face);

        //Forma del Arco Inferior
        titulo3 = view.findViewById(R.id.titulo3);
        titulo3.setTypeface(face);
        fai1 = view.findViewById(R.id.fai1);
        fai1.setTypeface(face);
        fai2 = view.findViewById(R.id.fai2);
        fai2.setTypeface(face);
        fai3 = view.findViewById(R.id.fai3);
        fai3.setTypeface(face);

        //Oclusion
        titulo4 = view.findViewById(R.id.titulo4);
        titulo4.setTypeface(face);
        oc1 = view.findViewById(R.id.oc1);
        oc1.setTypeface(face);
        oc2 = view.findViewById(R.id.oc2);
        oc2.setTypeface(face);
        oc3 = view.findViewById(R.id.oc3);
        oc3.setTypeface(face);
        oc4 = view.findViewById(R.id.oc4);
        oc4.setTypeface(face);
        oc5 = view.findViewById(R.id.oc5);
        oc5.setTypeface(face);

        //Desarrollo Facial
        titulo5 = view.findViewById(R.id.titulo5);
        titulo5.setTypeface(face);
        df1 = view.findViewById(R.id.df1);
        df1.setTypeface(face);
        df2 = view.findViewById(R.id.df2);
        df2.setTypeface(face);
        df3 = view.findViewById(R.id.df3);
        df3.setTypeface(face);
        df4 = view.findViewById(R.id.df4);
        df4.setTypeface(face);

        //Respiracion y Postura
        titulo6 = view.findViewById(R.id.titulo6);
        titulo6.setTypeface(face);
        rp1 = view.findViewById(R.id.rp1);
        rp1.setTypeface(face);
        rp2 = view.findViewById(R.id.rp2);
        rp2.setTypeface(face);
        rp3 = view.findViewById(R.id.rp3);
        rp3.setTypeface(face);
        rp4 = view.findViewById(R.id.rp4);
        rp4.setTypeface(face);
        rp5 = view.findViewById(R.id.rp5);
        rp5.setTypeface(face);

        //Lengua
        titulo7 = view.findViewById(R.id.titulo7);
        titulo7.setTypeface(face);
        len1 = view.findViewById(R.id.len1);
        len1.setTypeface(face);
        len2 = view.findViewById(R.id.len2);
        len2.setTypeface(face);
        len3 = view.findViewById(R.id.len3);
        len3.setTypeface(face);

        //Deglucion
        titulo8 = view.findViewById(R.id.titulo8);
        titulo8.setTypeface(face);
        de1 = view.findViewById(R.id.de1);
        de1.setTypeface(face);
        de2 = view.findViewById(R.id.de2);
        de2.setTypeface(face);

        //Labios y Mejilla
        titulo9 = view.findViewById(R.id.titulo9);
        titulo9.setTypeface(face);
        lm1 = view.findViewById(R.id.lm1);
        lm1.setTypeface(face);
        lm2 = view.findViewById(R.id.lm2);
        lm2.setTypeface(face);

        //Habitos
        titulo10 = view.findViewById(R.id.titulo10);
        titulo10.setTypeface(face);
        h1 = view.findViewById(R.id.h1);
        h1.setTypeface(face);
        h2 = view.findViewById(R.id.h2);
        h2.setTypeface(face);
        h3 = view.findViewById(R.id.h3);
        h3.setTypeface(face);
        h4 = view.findViewById(R.id.h4);
        h4.setTypeface(face);
        h5 = view.findViewById(R.id.h5);
        h5.setTypeface(face);

        //TMJ
        titulo11 = view.findViewById(R.id.titulo11);
        titulo11.setTypeface(face);
        tmj1 = view.findViewById(R.id.tmj1);
        tmj1.setTypeface(face);
        tmj2 = view.findViewById(R.id.tmj2);
        tmj2.setTypeface(face);
        tmj3 = view.findViewById(R.id.tmj3);
        tmj3.setTypeface(face);
        tmj4 = view.findViewById(R.id.tmj4);
        tmj4.setTypeface(face);
        tmj5 = view.findViewById(R.id.tmj5);
        tmj5.setTypeface(face);
        tmj6 = view.findViewById(R.id.tmj6);
        tmj6.setTypeface(face);
        tmj7 = view.findViewById(R.id.tmj7);
        tmj7.setTypeface(face);

        //Otros
        titulo12 = view.findViewById(R.id.titulo12);
        titulo12.setTypeface(face);
        descripcion = view.findViewById(R.id.otrosEvaluacion);
        descripcion.setTypeface(face);

        siguiente = view.findViewById(R.id.siguiente);
        siguiente.setOnClickListener(v -> {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.dismiss();
        });

        return view;
    }
}