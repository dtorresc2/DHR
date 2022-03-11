package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.sistemasdt.dhr.R;


public class Evaluacion extends Fragment {
    private Toolbar toolbar;
    private TextView titulo1, titulo2, titulo3, titulo4, titulo5, titulo6, titulo7, titulo8, titulo9, titulo10, titulo11, titulo12;
    private CheckBox ad1, ad2, ad3, ad4, ad5;
    private RadioButton fas1, fas2, fas3;
    private RadioButton fai1, fai2, fai3;
    private CheckBox oc1, oc2, oc3, oc4, oc5;
    private CheckBox df1, df2, df3, df4;
    private RadioButton rp1, rp2, rp4, rp5;
    private CheckBox rp3;
    private RadioButton len1, len2;
    private CheckBox len3;
    private RadioButton de1, de2;
    private RadioButton lm1, lm2;
    private CheckBox h1, h2, h3, h4, h5;
    private CheckBox tmj1, tmj2, tmj3, tmj4, tmj5, tmj6, tmj7;
    private TextInputEditText descripcion;
    private FloatingActionButton siguiente;

    private boolean MODO_EDICION = false;

    public Evaluacion() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_evaluacion, container, false);

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
        ad1 = view.findViewById(R.id.ad1);
        ad2 = view.findViewById(R.id.ad2);
        ad3 = view.findViewById(R.id.ad3);
        ad4 = view.findViewById(R.id.ad4);
        ad5 = view.findViewById(R.id.ad5);

        //Forma del Arco Superior
        titulo2 = view.findViewById(R.id.titulo2);
        fas1 = view.findViewById(R.id.fas1);
        fas2 = view.findViewById(R.id.fas2);
        fas3 = view.findViewById(R.id.fas3);

        //Forma del Arco Inferior
        titulo3 = view.findViewById(R.id.titulo3);
        fai1 = view.findViewById(R.id.fai1);
        fai2 = view.findViewById(R.id.fai2);
        fai3 = view.findViewById(R.id.fai3);

        //Oclusion
        titulo4 = view.findViewById(R.id.titulo4);
        oc1 = view.findViewById(R.id.oc1);
        oc2 = view.findViewById(R.id.oc2);
        oc3 = view.findViewById(R.id.oc3);
        oc4 = view.findViewById(R.id.oc4);
        oc5 = view.findViewById(R.id.oc5);

        //Desarrollo Facial
        titulo5 = view.findViewById(R.id.titulo5);
        df1 = view.findViewById(R.id.df1);
        df2 = view.findViewById(R.id.df2);
        df3 = view.findViewById(R.id.df3);
        df4 = view.findViewById(R.id.df4);

        //Respiracion y Postura
        titulo6 = view.findViewById(R.id.titulo6);
        rp1 = view.findViewById(R.id.rp1);
        rp2 = view.findViewById(R.id.rp2);
        rp3 = view.findViewById(R.id.rp3);
        rp4 = view.findViewById(R.id.rp4);
        rp5 = view.findViewById(R.id.rp5);

        //Lengua
        titulo7 = view.findViewById(R.id.titulo7);
        len1 = view.findViewById(R.id.len1);
        len2 = view.findViewById(R.id.len2);
        len3 = view.findViewById(R.id.len3);

        //Deglucion
        titulo8 = view.findViewById(R.id.titulo8);
        de1 = view.findViewById(R.id.de1);
        de2 = view.findViewById(R.id.de2);

        //Labios y Mejilla
        titulo9 = view.findViewById(R.id.titulo9);
        lm1 = view.findViewById(R.id.lm1);
        lm2 = view.findViewById(R.id.lm2);

        //Habitos
        titulo10 = view.findViewById(R.id.titulo10);
        h1 = view.findViewById(R.id.h1);
        h2 = view.findViewById(R.id.h2);
        h3 = view.findViewById(R.id.h3);
        h4 = view.findViewById(R.id.h4);
        h5 = view.findViewById(R.id.h5);

        //TMJ
        titulo11 = view.findViewById(R.id.titulo11);
        tmj1 = view.findViewById(R.id.tmj1);
        tmj2 = view.findViewById(R.id.tmj2);
        tmj3 = view.findViewById(R.id.tmj3);
        tmj4 = view.findViewById(R.id.tmj4);
        tmj5 = view.findViewById(R.id.tmj5);
        tmj6 = view.findViewById(R.id.tmj6);
        tmj7 = view.findViewById(R.id.tmj7);

        //Otros
        titulo12 = view.findViewById(R.id.titulo12);
        descripcion = view.findViewById(R.id.otrosEvaluacion);

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