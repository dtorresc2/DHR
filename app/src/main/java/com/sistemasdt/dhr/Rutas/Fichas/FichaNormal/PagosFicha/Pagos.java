package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.OpcionSeguimiento.Seguimiento;
import com.sistemasdt.dhr.Rutas.Catalogos.Piezas.ItemPieza;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdonDos;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.ItemTratamiento;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.ListadoFichasNormales.ListadoFichas;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Pagos extends Fragment {
    private TableLayout tableLayout;
    RequestQueue requestQueue;
    private Button listador, eliminador;
    private String[] header = {"Descripcion", "Pago"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private TablaDinamica tablaDinamica;
    private double total;
    private FloatingActionButton agregador;
    private static final String TAG = "MyActivity";
    private Toolbar toolbar;
    private SharedPreferences preferencias;
    private int contador = 0;
    private TextView tituloGasto, totalGasto;

    private TextView tituloTotalGatos, totalGastos, tituloPagos, totalPagos, tituloTotalPagos;
    private TextInputEditText descripcionPago, cantidadPago;
    private TextInputLayout descripcionPagoLayout, cantidadPagoLayout;
    private ArrayList<ItemPago> listaPagos = new ArrayList<>();
    double totalTratamientos = 0;

    private boolean MODO_EDICION = false;
    private boolean modoEdicionPago = false;
    int POSICION = 0;

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
        toolbar.setNavigationIcon(R.drawable.ic_atras);

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
//                ListadoFichas listadoFichas = new ListadoFichas();
                HistorialOdonDos historialOdonDos = new HistorialOdonDos();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction2.replace(R.id.contenedor, historialOdonDos);
                transaction2.commit();
            }
        });


        listador = view.findViewById(R.id.agregarPago);
        listador.setTypeface(face);

        tableLayout = view.findViewById(R.id.tablaPagos);

        descripcionPago = view.findViewById(R.id.descPagos);
        descripcionPago.setTypeface(face);
        descripcionPago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descripcionRequerida();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cantidadPago = view.findViewById(R.id.costoPagos);
        cantidadPago.setTypeface(face);
        cantidadPago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (montoRequerido()) {
                    validarMonto();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        agregador = view.findViewById(R.id.guardadorPagos);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);
        tablaDinamica.setOnItemClickListener(new TablaDinamica.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
                menuInferiorDos.show(getFragmentManager(), "MenuInferior");
                menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(position, 0));
                menuInferiorDos.eventoClick(new MenuInferiorDos.MenuInferiorListener() {
                    @Override
                    public void onButtonClicked(int opcion) {
                        int index = position - 1;
                        switch (opcion) {
                            case 1:
                                // Editar Pago
                                modoEdicionPago = true;
                                listador.setText("ACTUALIZAR PAGO");

                                descripcionPago.setText(listaPagos.get(index).getDescripcionPago());
                                cantidadPago.setText(String.format("%.2f", listaPagos.get(index).getMonto()));

                                POSICION = index;

//                                if (tablaDinamica.getCount() > 0) {
//                                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
//                                        total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
//                                    }
//                                    totalPagos.setText(String.format("%.2f", total));
//                                } else {
//                                    totalPagos.setText("0.00");
//                                }
                                break;
                            case 2:
                                // Eliminar Tratamiento
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), R.style.progressDialog);
                                builder.setIcon(R.drawable.logonuevo);
                                builder.setTitle("Pagos");
                                builder.setMessage("¿Desea eliminar el pago?");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tablaDinamica.removeRow(position);
                                        total = 0;
                                        listaPagos.remove(position - 1);

                                        if (tablaDinamica.getCount() > 0) {
                                            for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                                total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
                                            }
                                            totalPagos.setText(String.format("%.2f", total));
                                        } else {
                                            totalPagos.setText("0.00");
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                break;
                        }
                    }
                });
            }
        });

        listador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!descripcionRequerida() || !montoRequerido() || !validarMonto())
                    return;

                total = 0;

                String[] item = new String[]{
                        descripcionPago.getText().toString(),
                        cantidadPago.getText().toString()
                };

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                if (!modoEdicionPago) {
                    tablaDinamica.addItem(item);
                    listaPagos.add(new ItemPago(
                            descripcionPago.getText().toString(),
                            Double.parseDouble(cantidadPago.getText().toString()),
                            date
                    ));
                } else {
                    if (listaPagos.size() > 0) {
                        listaPagos.set(POSICION, new ItemPago(
                                descripcionPago.getText().toString(),
                                Double.parseDouble(cantidadPago.getText().toString()),
                                date
                        ));

                        // Reinciar Tabla
                        tablaDinamica.removeAll();
                        tablaDinamica.addHeader(header);
                        tablaDinamica.addData(getClients());
                        tablaDinamica.fondoHeader(R.color.AzulOscuro);

                        modoEdicionPago = false;
                        POSICION = 0;
                        listador.setText("AGREGAR TRATAMIENTO");
                    }
                }

                descripcionPago.setText(null);
                cantidadPago.setText(null);

                descripcionPagoLayout.setError(null);
                cantidadPagoLayout.setError(null);

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
                    }
                    totalPagos.setText(String.format("%.2f", total));
                }
            }
        });

        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalTratamientos >= total) {


                    SharedPreferences preferences = getActivity().getSharedPreferences("PAGOS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Set<String> set = new HashSet<>();

                    for (ItemPago item : listaPagos) {
                        String cadena = item.getDescripcionPago() + ";" + item.getMonto() + ";" + item.getFecha() + ";";
                        set.add(cadena);
                    }

                    editor.putStringSet("listaPagos", set);
                    editor.apply();

                    final SharedPreferences preferenciasFicha2 = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor2 = preferenciasFicha2.edit();
                    escritor2.putString("NO_PAGOS", String.valueOf(listaPagos.size()));
                    escritor2.commit();

                    HistorialFotografico historialFotografico = new HistorialFotografico();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, historialFotografico);
                    transaction.commit();
                } else {
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("El pago no puede ser mayor a la deuda")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        cargarPagos();
        cargarTotalTratamientos();
        return view;
    }

    public void cargarPagos() {
        SharedPreferences preferences = getActivity().getSharedPreferences("PAGOS", Context.MODE_PRIVATE);
        Set<String> set = preferences.getStringSet("listaPagos", null);

        // Reinciar Tabla
        listaPagos.clear();
        tablaDinamica.removeAll();
//        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        if (set != null) {
            ArrayList<String> listaAuxiliar = new ArrayList<>(set);

            for (String item : listaAuxiliar) {
                String cadenaAuxiliar[] = item.split(";");

                listaPagos.add(new ItemPago(
                        cadenaAuxiliar[0],
                        Double.parseDouble(cadenaAuxiliar[1]),
                        cadenaAuxiliar[2]
                ));

                tablaDinamica.addItem(new String[]{
                        cadenaAuxiliar[0],
                        String.format("%.2f", Double.parseDouble(cadenaAuxiliar[1]))
                });
            }

            total = 0;

            if (tablaDinamica.getCount() > 0) {
                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                    total += Double.parseDouble(tablaDinamica.getCellData(i, 1));
                }
                totalPagos.setText(String.format("%.2f", total));
            } else {
                totalPagos.setText("0.00");
            }
        }
    }

    private void cargarTotalTratamientos() {
        ArrayList<String> listaTratramientos;
        totalTratamientos = 0;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HOD2", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("listaTratamientos", null);

        if (set == null) {
            listaTratramientos = new ArrayList<>();
        } else {
            listaTratramientos = new ArrayList<>(set);
        }

        for (String item : listaTratramientos) {
            String cadenaAuxiliar[] = item.split(";");
            totalTratamientos += Double.parseDouble(cadenaAuxiliar[3]);
        }

        totalGastos.setText(String.format("%.2f", totalTratamientos));
    }

    private ArrayList<String[]> getClients() {
        rows.clear();
        for (ItemPago pago : listaPagos) {
            rows.add(new String[]{
                    pago.getDescripcionPago(),
                    String.format("%.2f", pago.getMonto())
            });
        }
        return rows;
    }

    public void ObtenerOpcion(int opcion) {
    }

    //    VALIDACIONES
    private boolean descripcionRequerida() {
        String texto = descripcionPago.getText().toString().trim();
        if (texto.isEmpty()) {
            descripcionPagoLayout.setError("Campo Requerido");
            return false;
        } else {
            descripcionPagoLayout.setError(null);
            return true;
        }
    }

    private boolean montoRequerido() {
        String texto = cantidadPago.getText().toString().trim();
        if (texto.isEmpty()) {
            cantidadPagoLayout.setError("Campo Requerido");
            return false;
        } else {
            cantidadPagoLayout.setError(null);
            return true;
        }
    }

    private boolean validarMonto() {
        String texto = cantidadPago.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            cantidadPagoLayout.setError(null);
            return true;
        } else {
            cantidadPagoLayout.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }
}
