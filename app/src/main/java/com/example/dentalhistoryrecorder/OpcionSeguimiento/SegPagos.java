package com.example.dentalhistoryrecorder.OpcionSeguimiento;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.OpcionIngreso.Normal.Ing_HFoto;
import com.example.dentalhistoryrecorder.OpcionSeguimiento.Seguimiento;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Tabla.TablaDinamica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SegPagos extends Fragment {
    private TableLayout tableLayout;
    RequestQueue requestQueue;
    private Button listador, eliminador;
    private String[] header = {"Descripsion", "Pago"};
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

    public SegPagos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seg_pagos, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        listador = view.findViewById(R.id.guardador_hm);
        listador.setTypeface(face);
        eliminador = view.findViewById(R.id.eliminador);
        eliminador.setTypeface(face);

        preferencias = getActivity().getSharedPreferences("Consultar", Context.MODE_PRIVATE);

        tableLayout = view.findViewById(R.id.tablaPagos);

        descripsion = view.findViewById(R.id.descPagos);
        descripsion.setTypeface(face);
        pagos = view.findViewById(R.id.costoPagos);
        pagos.setTypeface(face);

        titulo_diag = view.findViewById(R.id.titulo_diagnostico);
        titulo_diag.setTypeface(face);
        titulo_pres = view.findViewById(R.id.titulo_presupuesto);
        titulo_pres.setTypeface(face);
        total_costo = view.findViewById(R.id.tota_costo);
        total_costo.setTypeface(face);
        titulo_costo = view.findViewById(R.id.titulo_costo);
        titulo_costo.setTypeface(face);
        agregador = view.findViewById(R.id.guardador_hd2);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        toolbar.setTitle("Historial Odontodologico");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mOpcion) {
                    case 1:
                        Agregar agregar = new Agregar();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction.replace(R.id.contenedor, agregar);
                        transaction.commit();
                        break;

                    case 2:
                        Seguimiento seguimiento = new Seguimiento();
                        FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction2.replace(R.id.contenedor, seguimiento);
                        transaction2.commit();
                        break;
                }
            }
        });

        listador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = 0;
                String[] item = new String[]{
                        descripsion.getText().toString(),
                        pagos.getText().toString()
                };
                tablaDinamica.addItem(item);
                descripsion.setText(null);
                pagos.setText(null);

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
                    }
                    total_costo.setText(String.format("%.2f", total));
                }
            }
        });

        //Proceso para eliminar
        eliminador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "" + tablaDinamica.getCount(), Toast.LENGTH_LONG).show();
            }
        });

        //Proceso para guardar
        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        //insertarTratamiento("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=10", tablaDinamica.getCellData(i, 1), tablaDinamica.getCellData(i, 2), tablaDinamica.getCellData(i, 0));
                        insertarTratamiento("https://diegosistemas.xyz/DHR/Normal/seguimiento.php?estado=2",tablaDinamica.getCellData(i,0), tablaDinamica.getCellData(i,1));
                    }
                }

                switch (mOpcion){
                    case 1:
                        Ing_HFoto ingHFoto = new Ing_HFoto();
                        ingHFoto.ObtenerOpcion(1);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                        transaction.replace(R.id.contenedor, ingHFoto);
                        transaction.commit();
                        break;

                    case 2:
                        Seguimiento seguimiento = new Seguimiento();
                        FragmentTransaction transaction2 = getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                        transaction2.replace(R.id.contenedor, seguimiento);
                        transaction2.commit();
                        break;
                }

            }
        });

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void insertarTratamiento(String URL, final String aux1, final String aux2) {
        final String[] id = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("desc", aux1);
                parametros.put("monto", aux2);
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }
}
