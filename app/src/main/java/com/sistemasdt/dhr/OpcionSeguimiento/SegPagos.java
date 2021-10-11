package com.sistemasdt.dhr.OpcionSeguimiento;


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
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

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
    private int contador = 0;
    private TextView tituloGasto, totalGasto;

    public SegPagos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seg_pagos, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Pagos");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mOpcion) {
                    case 1:
                        MenuFichas menuFichas = new MenuFichas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction.replace(R.id.contenedor, menuFichas);
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


        listador = view.findViewById(R.id.guardador_hm);
        listador.setTypeface(face);
        eliminador = view.findViewById(R.id.eliminador);
        eliminador.setTypeface(face);

        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);

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

        tituloGasto = view.findViewById(R.id.tituloGasto);
        tituloGasto.setTypeface(face);
        totalGasto = view.findViewById(R.id.totalGasto);
        totalGasto.setTypeface(face);

        switch (mOpcion) {
            case 1:
                totalGasto.setText(String.format("%.2f", Double.parseDouble(preferencias.getString("totalOdon", "0.00"))));
                break;
            case 2:
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    consultarTratamiento("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=10", preferencias.getString("idficha", ""));

                }
                else{
                    final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Fallo en Conexion a Internet")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face3)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
                break;
        }

        agregador = view.findViewById(R.id.guardador_hd2);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        listador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!descripsion.getText().toString().isEmpty() && !pagos.getText().toString().isEmpty()) {
                    total = 0;
                    String[] item = new String[]{
                            descripsion.getText().toString(),
                            pagos.getText().toString()
                    };

                    double aux = Double.parseDouble(totalGasto.getText().toString());

                    if (tablaDinamica.getCount() > 0) {
                        for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                            total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
                        }
                    }

                    total += Double.parseDouble(pagos.getText().toString());

                    if (total <= aux) {
                        tablaDinamica.addItem(item);
                        descripsion.setText(null);
                        pagos.setText(null);
                        total = 0;

                        if (tablaDinamica.getCount() > 0) {
                            for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
                            }
                            total_costo.setText(String.format("%.2f", total));
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
                            .setTitle("Error")
                            .setText("Hay campos vacios")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        //Proceso para eliminar
        eliminador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                if (tablaDinamica.getCount() > 0) {
                    contador = tablaDinamica.getCount();
                    final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                    d.setCancelable(false);
                    d.setView(dialogView);
                    final AlertDialog alertDialog = d.create();

                    TextView textView = dialogView.findViewById(R.id.titulo_dialogo);
                    textView.setTypeface(face2);

                    Button aceptar = dialogView.findViewById(R.id.aceptar);
                    aceptar.setTypeface(face2);

                    Button cancelar = dialogView.findViewById(R.id.cancelar);
                    cancelar.setTypeface(face2);

                    final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(contador);

                    aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tablaDinamica.removeRow(numberPicker.getValue());
                            alertDialog.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Se Elimino Una Fila")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face2)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    });

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                } else {
                    Alerter.create(getActivity())
                            .setTitle("No Hay Filas En La Tabla")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face2)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        //Proceso para guardar
        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                progressDialog.setMessage("Cargando...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        //insertarTratamiento("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=10", tablaDinamica.getCellData(i, 1), tablaDinamica.getCellData(i, 2), tablaDinamica.getCellData(i, 0));
                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            switch (mOpcion) {
                                case 1:
                                    insertarTratamiento("http://dhr.sistemasdt.xyz/Normal/ficha.php?estado=12", tablaDinamica.getCellData(i, 0), tablaDinamica.getCellData(i, 1));
                                    break;

                                case 2:
                                    insertarTratamiento("http://dhr.sistemasdt.xyz/Normal/seguimiento.php?estado=2", tablaDinamica.getCellData(i, 0), tablaDinamica.getCellData(i, 1));
                                    break;
                            }

                            switch (mOpcion) {
                                case 1:
                                    HistorialFotografico ingHFoto = new HistorialFotografico();
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
                        else{
                            final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo en Conexion a Internet")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face3)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    }


                } else {
                    Alerter.create(getActivity())
                            .setTitle("No Hay Filas En La Tabla")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
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

    //ListadoPacientes Historial Odontodologico - Tratamiento
    public void consultarTratamiento(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                totalGasto.setText(String.format("%.2f", Double.parseDouble(response)));
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}
