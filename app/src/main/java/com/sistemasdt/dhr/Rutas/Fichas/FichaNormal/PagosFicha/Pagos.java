package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdonDos;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Pagos extends Fragment {
    private TableLayout tableLayout;
    RequestQueue requestQueue;
    private Button listador;
    private String[] header = {"Descripcion", "Pago"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private TablaDinamica tablaDinamica;
    private double total;
    private FloatingActionButton agregador;
    private Toolbar toolbar;

    private TextView tituloTotalGatos, totalGastos, tituloPagos, totalPagos, tituloTotalPagos;
    private TextInputEditText descripcionPago, cantidadPago;
    private TextInputLayout descripcionPagoLayout, cantidadPagoLayout;
    private ArrayList<ItemPago> listaPagos = new ArrayList<>();
    double totalTratamientos = 0;

    private boolean modoEdicionPago = false;
    int POSICION = 0;

    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;
    private int ID_ODONTO = 0;
    private double TOTAL_GASTOS = 0;

    public Pagos() {
        MODO_EDICION = false;
    }

    public void activarModoEdicion(int id) {
        MODO_EDICION = true;
        ID_FICHA = id;
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

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                HistorialOdonDos historialOdonDos = new HistorialOdonDos();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                fragmentTransaction.replace(R.id.contenedor, historialOdonDos);
                fragmentTransaction.commit();
            } else {
                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichaNormal);
                transaction.commit();
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
        tablaDinamica.setOnItemClickListener(position -> {
            MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
            menuInferiorDos.show(getActivity().getSupportFragmentManager(), "MenuInferior");
            menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(position, 0));
            menuInferiorDos.eventoClick(opcion -> {
                int index = position - 1;
                switch (opcion) {
                    case 1:
                        // Editar Pago
                        modoEdicionPago = true;
                        listador.setText("ACTUALIZAR PAGO");

                        descripcionPago.setText(listaPagos.get(index).getDescripcionPago());
                        cantidadPago.setText(String.format("%.2f", listaPagos.get(index).getMonto()));

                        POSICION = index;
                        break;
                    case 2:
                        // Eliminar Tratamiento
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                        builder.setIcon(R.drawable.logonuevo);
                        builder.setTitle("Pagos");
                        builder.setMessage("¿Desea eliminar el pago?");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
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
                        });

                        builder.setNegativeButton("Cancelar", (dialog, which) -> {

                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
            });
        });

        listador.setOnClickListener(v -> {
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
                    tablaDinamica.fondoHeader(R.color.AzulOscuro);

                    for (ItemPago itemPago : listaPagos) {
                        tablaDinamica.addItem(new String[]{
                                itemPago.getDescripcionPago(),
                                String.format("%.2f", itemPago.getMonto())
                        });
                    }

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
        });

        agregador.setOnClickListener(v -> {
            if (totalTratamientos >= total) {
                if (!MODO_EDICION) {
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
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, historialFotografico);
                    transaction.commit();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (ItemPago item : listaPagos) {
                            String fechaMYSQL;

                            if (item.getFecha().contains("/")) {
                                Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(item.getFecha());
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                fechaMYSQL = formatter.format(initDate);
                            } else {
                                fechaMYSQL = item.getFecha();
                            }

                            JSONObject rowJSON = new JSONObject();
                            rowJSON.put("PAGO", item.getMonto());
                            rowJSON.put("DESCRIPCION", item.getDescripcionPago());
                            rowJSON.put("FECHA", fechaMYSQL);
                            rowJSON.put("ID_FICHA", ID_FICHA);

                            jsonArray.put(rowJSON);
                        }

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("PAGOS", jsonArray);

                        QuerysFichas querysFichas = new QuerysFichas(getContext());
                        querysFichas.actualizarPagos(ID_FICHA, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Pagos")
                                        .setText("Actualizados correctamente")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.FondoSecundario)
                                        .show();

                                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                                funcionesBitacora.registrarBitacora("ACTUALIZACION", "PAGOS - FICHA NORMAL", "Se actualizo la ficha #" + ID_FICHA);

                                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                                transaction.replace(R.id.contenedor, menuFichaNormal);
                                transaction.commit();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Error")
                                        .setText("Fallo al actualizar los PAGOS")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.AzulOscuro)
                                        .show();
                            }
                        });

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
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
        });

        cargarPagos();
        cargarTotalTratamientos();
        return view;
    }

    public void cargarPagos() {
        // Reinciar Tabla
        listaPagos.clear();
        tablaDinamica.removeAll();
//        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        if (!MODO_EDICION) {
            SharedPreferences preferences = getActivity().getSharedPreferences("PAGOS", Context.MODE_PRIVATE);
            Set<String> set = preferences.getStringSet("listaPagos", null);

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
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.obtenerPagos(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONArray jsonArray = new JSONArray(object.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            listaPagos.add(new ItemPago(
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    jsonArray.getJSONObject(i).getDouble("PAGO"),
                                    jsonArray.getJSONObject(i).getString("FECHA")
                            ));

                            tablaDinamica.addItem(new String[]{
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    String.format("%.2f", jsonArray.getJSONObject(i).getDouble("PAGO"))
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

                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    cargarPagos();
                }
            });
        }
    }

    private void cargarTotalTratamientos() {
        totalTratamientos = 0;
        if (!MODO_EDICION) {
            ArrayList<String> listaTratramientos;

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
        } else {
            QuerysFichas querysTratamientos = new QuerysFichas(getContext());
            querysTratamientos.obtenerHistorialOdontodologico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject(object.toString());
                        ID_ODONTO = jsonObject.getInt("ID_HISTORIAL_ODONTO");

                        QuerysFichas querysFichas1 = new QuerysFichas(getContext());
                        querysFichas1.obtenerTratamientos(ID_ODONTO, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object2) {
                                try {
                                    JSONArray jsonArray = new JSONArray(object2.toString());

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        totalTratamientos += jsonArray.getJSONObject(i).getDouble("COSTO");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                totalGastos.setText(String.format("%.2f", totalTratamientos));
                            }

                            @Override
                            public void onFailure(Exception e) {
                                totalGastos.setText(String.format("%.2f", totalTratamientos));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }

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
