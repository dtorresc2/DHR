package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

public class PagosVisitas extends Fragment {
    EditText descripcion, fecha, pagos;
    Toolbar toolbar;
    Button agregar;
    FloatingActionButton siguiente;
    TextView titulo, totalGasto, tituloAbono, totalAbono;

    TableLayout tableLayout;
    private String[] header = {"Fecha", "Descripsion", "Pago"};
    private TablaDinamica tablaDinamica;
    private ArrayList<String[]> rows = new ArrayList<>();
    private RequestQueue requestQueue;
    private int contador = 0;

    private int mOpcion = 0;
    private SharedPreferences preferencias;
    private double total;

    public PagosVisitas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingreso_pagos_visitas, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Visitas");
        toolbar.setNavigationOnClickListener(v -> {
            switch (mOpcion) {
                case 1:
                    break;

                case 2:
                    break;
            }
        });

        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);

        descripcion = view.findViewById(R.id.descripcion);
        descripcion.setTypeface(face);

        pagos = view.findViewById(R.id.abono);
        pagos.setTypeface(face);

        titulo = view.findViewById(R.id.tituloAbono);
        titulo.setTypeface(face);

        agregar = view.findViewById(R.id.agregarAbono);
        agregar.setTypeface(face);

        siguiente = view.findViewById(R.id.siguiente);

        tableLayout = view.findViewById(R.id.tablaPagos);
        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        tituloAbono = view.findViewById(R.id.tituloAbono);
        tituloAbono.setTypeface(face);

        totalAbono = view.findViewById(R.id.totalAbono);
        totalAbono.setTypeface(face);

        switch (mOpcion) {
            case 1:
                totalGasto.setText(String.format("%.2f", Double.parseDouble(preferencias.getString("totalVisitas", "0.00"))));
                break;
            case 2:
                break;
        }

        agregar.setOnClickListener(v -> {
            if (!descripcion.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty() && !pagos.getText().toString().isEmpty()) {
                total = 0;
                String[] item = new String[]{
                        fecha.getText().toString(),
                        descripcion.getText().toString(),
                        pagos.getText().toString()
                };

                double aux = Double.parseDouble(totalGasto.getText().toString());

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                    }
                }

                total += Double.parseDouble(pagos.getText().toString());

                if (total <= aux) {
                    tablaDinamica.addItem(item);
                    descripcion.setText(null);
                    pagos.setText(null);
                    total = 0;
                    if (tablaDinamica.getCount() > 0) {
                        for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                            total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                        }
                        totalAbono.setText(String.format("%.2f", total));
                    }
                } else {
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("El pago es mayor a la deuda")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            } else {
                Alerter.create(getActivity())
                        .setTitle("Hay Campos Vacios")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
            }
        });

        siguiente.setOnClickListener(v -> {
        });

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    public void ObtenerOpcion(int opcion){
        mOpcion = opcion;
    }
}
