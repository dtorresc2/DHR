package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.OpcionSeguimiento.Seguimiento;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.ListadoFichasNormales.ListadoFichas;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pagos extends Fragment {
    private TableLayout tableLayout;
    RequestQueue requestQueue;
    private Button listador, eliminador;
    private String[] header = {"Descripcion", "Pago"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private EditText descripsion, pagos, otros, desc_dolor;
    private TextView titulo_detalle, titulo_diag, titulo_pres, titulo_piez, total_costo, titulo_costo, celdap, celdat, celdac;
    private TablaDinamica tablaDinamica;
    private int lim, mOpcion = 0;
    private double total;
    private FloatingActionButton agregador;
    private static final String TAG = "MyActivity";
    private Toolbar toolbar;
    private SharedPreferences preferencias;
    private int contador = 0;
    private TextView tituloGasto, totalGasto;

    private Button agregarPago;
    private TextView tituloTotalGatos, totalGastos, tituloPagos, totalPagos, tituloTotalPagos;
    private TextInputEditText descripcionPago, cantidadPago;
    private TextInputLayout descripcionPagoLayout, cantidadPagoLayout;

    private boolean MODO_EDICION = false;

    public Pagos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagos, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Pagos");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                switch (mOpcion) {
//                    case 1:
//                        MenuFichas menuFichas = new MenuFichas();
//                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
//                        transaction.replace(R.id.contenedor, menuFichas);
//                        transaction.commit();
//                        break;
//
//                    case 2:
//                        Seguimiento seguimiento = new Seguimiento();
//                        FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
//                        transaction2.replace(R.id.contenedor, seguimiento);
//                        transaction2.commit();
//                        break;
//                }
                ListadoFichas listadoFichas = new ListadoFichas();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction2.replace(R.id.contenedor, listadoFichas);
                transaction2.commit();
            }
        });


        listador = view.findViewById(R.id.agregarPago);
        listador.setTypeface(face);

        tableLayout = view.findViewById(R.id.tablaPagos);

        descripcionPago = view.findViewById(R.id.descPagos);
        descripcionPago.setTypeface(face);

        cantidadPago = view.findViewById(R.id.costoPagos);
        cantidadPago.setTypeface(face);

        descripcionPagoLayout = view.findViewById(R.id.layoutDescPagos);
        descripcionPagoLayout.setTypeface(face);

        cantidadPagoLayout = view.findViewById(R.id.layoutCostoPagos);
        cantidadPagoLayout.setTypeface(face);

        tituloTotalGatos = view.findViewById(R.id.tituloGasto);
        tituloTotalGatos.setTypeface(face);

        totalGastos = view.findViewById(R.id.totalGasto);
        totalGastos.setTypeface(face);

        tituloPagos = view.findViewById(R.id.tituloPagos);
        tituloPagos.setTypeface(face);

        totalPagos = view.findViewById(R.id.totalAbono);
        totalPagos.setTypeface(face);

        tituloTotalPagos = view.findViewById(R.id.tituloAbono);
        tituloTotalPagos.setTypeface(face);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        listador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

}
