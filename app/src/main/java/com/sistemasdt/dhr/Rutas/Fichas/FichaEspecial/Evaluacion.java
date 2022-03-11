package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
            if (!MODO_EDICION) {
                // REGISTRAR ALINEACION DENTAL
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ALINEACION_DENTAL", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("BUENA_ALIMENTACION", (ad1.isChecked() == true) ? 1 : 0);
                editor.putInt("APILLAMIENTO_MAXILLAR", (ad2.isChecked() == true) ? 1 : 0);
                editor.putInt("APILLAMIENTO_MANDIBULA", (ad3.isChecked() == true) ? 1 : 0);
                editor.putInt("LINEA_MEDIA", (ad4.isChecked() == true) ? 1 : 0);
                editor.putInt("DISCREPANCIA", (ad5.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR DEGLUCION
                sharedPreferences = getActivity().getSharedPreferences("DEGLUCION", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("PATRON_CORRECTO", (de1.isChecked() == true) ? 1 : 0);
                editor.putInt("PATRON_INCORRECTO", (de2.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR DESARROLLO
                sharedPreferences = getActivity().getSharedPreferences("DESARROLLO", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("DES_FACBUENO", (df1.isChecked() == true) ? 1 : 0);
                editor.putInt("DEF_TERCIOMED", (df2.isChecked() == true) ? 1 : 0);
                editor.putInt("DEF_TERINF", (df3.isChecked() == true) ? 1 : 0);
                editor.putInt("CRECIMIENTO", (df4.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR DISFUCION_TMJ
                sharedPreferences = getActivity().getSharedPreferences("DISFUCION_TMJ", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("TEMPORAL", (tmj1.isChecked() == true) ? 1 : 0);
                editor.putInt("PTERIGOIDEO", (tmj2.isChecked() == true) ? 1 : 0);
                editor.putInt("MASATEROS", (tmj3.isChecked() == true) ? 1 : 0);
                editor.putInt("CERVICAL", (tmj4.isChecked() == true) ? 1 : 0);
                editor.putInt("TRAPECIO", (tmj5.isChecked() == true) ? 1 : 0);
                editor.putInt("TMJCLICK", (tmj6.isChecked() == true) ? 1 : 0);
                editor.putInt("TMJDOLOR", (tmj7.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR FA_SUPERIOR
                sharedPreferences = getActivity().getSharedPreferences("FA_SUPERIOR", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("NORMAL", (fas1.isChecked() == true) ? 1 : 0);
                editor.putInt("ESTRECHA", (fas2.isChecked() == true) ? 1 : 0);
                editor.putInt("APLANADO", (fas3.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR FA_INFERIOR
                sharedPreferences = getActivity().getSharedPreferences("FA_INFERIOR", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("NORMAL", (fai1.isChecked() == true) ? 1 : 0);
                editor.putInt("ESTRECHA", (fai2.isChecked() == true) ? 1 : 0);
                editor.putInt("APLANADO", (fai3.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR HABITOS
                sharedPreferences = getActivity().getSharedPreferences("HABITOS", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("BRUXISMO", (h1.isChecked() == true) ? 1 : 0);
                editor.putInt("SUCCION", (h2.isChecked() == true) ? 1 : 0);
                editor.putInt("CHUPETE", (h3.isChecked() == true) ? 1 : 0);
                editor.putInt("BIBERON", (h4.isChecked() == true) ? 1 : 0);
                editor.putInt("RONCA", (h5.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR LABIOS
                sharedPreferences = getActivity().getSharedPreferences("LABIOS", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("CORRECTA_POST", (lm1.isChecked() == true) ? 1 : 0);
                editor.putInt("INCORRECTA_POST", (lm2.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR LENGUA
                sharedPreferences = getActivity().getSharedPreferences("LENGUA", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("CORRECTA_POSICION", (len1.isChecked() == true) ? 1 : 0);
                editor.putInt("INCORRECTA_POSICION", (len2.isChecked() == true) ? 1 : 0);
                editor.putInt("INSERCION", (len3.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR OCLUSION
                sharedPreferences = getActivity().getSharedPreferences("OCLUSION", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("RELACION_CORRECTA", (oc1.isChecked() == true) ? 1 : 0);
                editor.putInt("SOBRE_MORDIDA", (oc2.isChecked() == true) ? 1 : 0);
                editor.putInt("RESALTE_DENTAL", (oc3.isChecked() == true) ? 1 : 0);
                editor.putInt("MORDIDA_ABIERTA", (oc4.isChecked() == true) ? 1 : 0);
                editor.putInt("MORDIDA_CERRADA", (oc5.isChecked() == true) ? 1 : 0);
                editor.commit();

                // REGISTRAR RESPIRACION
                sharedPreferences = getActivity().getSharedPreferences("RESPIRACION", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("RES_NASALLEV", (rp1.isChecked() == true) ? 1 : 0);
                editor.putInt("RES_NSAFUERTE", (rp2.isChecked() == true) ? 1 : 0);
                editor.putInt("RES_BUCAL", (rp3.isChecked() == true) ? 1 : 0);
                editor.putInt("BUENA_POSTURA", (rp4.isChecked() == true) ? 1 : 0);
                editor.putInt("INCORPOSTURA", (rp5.isChecked() == true) ? 1 : 0);
                editor.commit();

                Evaluacion evaluacion = new Evaluacion();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, evaluacion);
                transaction.commit();
            }

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