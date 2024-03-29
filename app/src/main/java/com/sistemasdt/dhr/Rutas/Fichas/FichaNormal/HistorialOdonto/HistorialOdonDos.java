package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.Rutas.Catalogos.Piezas.ItemPieza;
import com.sistemasdt.dhr.Rutas.Catalogos.Servicios.ItemServicio;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha.Pagos;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPiezas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysServicios;
import com.tapadoo.alerter.Alerter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class HistorialOdonDos extends Fragment {
    private Toolbar toolbar;
    private Button listador;
    private TableLayout tableLayout;
    private String[] header = {"Pieza", "Descripcion", "Costo"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private TextView pieza, servicio;
    private TextView titulo_diag, titulo_pres, total_costo, titulo_costo, titulo_pagos, total_pagos;
    private TablaDinamica tablaDinamica;
    private double total;
    private FloatingActionButton agregador;
    private TextInputEditText desc_servicio, monto;
    private TextInputLayout layoutServicio, layoutMonto;
    private LinearLayout layoutTotalPagos;

    ArrayList<String> listaPiezas;
    ArrayList<ItemPieza> listaPiezasGenenal;

    ArrayList<String> listaServicios;
    ArrayList<ItemServicio> listaServiciosGeneral;

    private ArrayList<ItemTratamiento> listaTratamientos = new ArrayList<>();

    int ID_PIEZA = 0;
    int ID_SERVICIO = 0;

    int POSICION = 0;
    boolean modoEdicionTratamiento = false;

    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;
    private int ID_ODONTO = 0;
    private double TOTAL_PAGOS = 0;

    public HistorialOdonDos() {
        MODO_EDICION = false;
    }

    public void activarModoEdicion(int id) {
        MODO_EDICION = true;
        ID_FICHA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_historial_odon_dos, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);

        layoutTotalPagos = view.findViewById(R.id.layoutTotalPagos);
        titulo_pagos = view.findViewById(R.id.totalPagos);
        titulo_pagos.setTypeface(face);
        total_pagos = view.findViewById(R.id.totalPagos);
        total_pagos.setTypeface(face);

        //Encabezado
        if (!MODO_EDICION) {
            toolbar.setTitle("Historial Odontodologico (2/2)");
            toolbar.setNavigationIcon(R.drawable.ic_atras);
            layoutTotalPagos.setVisibility(View.GONE);

        } else {
            toolbar.setTitle("Tratamientos");
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
            layoutTotalPagos.setVisibility(View.VISIBLE);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                HistorialOdon historialOdon = new HistorialOdon();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                fragmentTransaction.replace(R.id.contenedor, historialOdon);
                fragmentTransaction.commit();
            } else {
                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichaNormal);
                transaction.commit();
            }
        });

        desc_servicio = view.findViewById(R.id.desc_servicio);
        desc_servicio.setTypeface(face);
        desc_servicio.addTextChangedListener(new TextWatcher() {
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

        monto = view.findViewById(R.id.monto);
        monto.setTypeface(face);
        monto.addTextChangedListener(new TextWatcher() {
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

        layoutServicio = view.findViewById(R.id.layoutServicio);
        layoutServicio.setTypeface(face);

        layoutMonto = view.findViewById(R.id.layoutMonto);
        layoutMonto.setTypeface(face);

        listaPiezas = new ArrayList<>();
        listaPiezasGenenal = new ArrayList<>();

        listaServicios = new ArrayList<>();
        listaServiciosGeneral = new ArrayList<>();

        pieza = view.findViewById(R.id.pieza);
        pieza.setTypeface(face);
        pieza.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialogo_busqueda);
            dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            EditText editText = dialog.findViewById(R.id.buscador);
            editText.setTypeface(face);

            TextView textView = dialog.findViewById(R.id.tituloDialogo);
            textView.setTypeface(face);
            textView.setText("Seleccione una pieza");

            ListView listView = dialog.findViewById(R.id.lista_items);

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaPiezas);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                String filter = adapter.getItem(position).toLowerCase().trim();
                pieza.setText(adapter.getItem(position));

                for (ItemPieza item : listaPiezasGenenal) {
                    if (item.getNombrePieza().toLowerCase().trim().contains(filter)) {
                        ID_PIEZA = listaPiezasGenenal.get(listaPiezasGenenal.indexOf(item)).getCodigoPieza();
                    }
                }

                dialog.dismiss();
            });
        });

        servicio = view.findViewById(R.id.servicio);
        servicio.setTypeface(face);
        servicio.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialogo_busqueda);
            dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            EditText editText = dialog.findViewById(R.id.buscador);
            editText.setTypeface(face);

            TextView textView = dialog.findViewById(R.id.tituloDialogo);
            textView.setTypeface(face);
            textView.setText("Seleccione un servicio");

            ListView listView = dialog.findViewById(R.id.lista_items);

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaServicios);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view12, position, id) -> {
                String filter = adapter.getItem(position).toLowerCase().trim();
                servicio.setText(adapter.getItem(position));

                for (ItemServicio item : listaServiciosGeneral) {
                    if (item.getDescripcionServicio().toLowerCase().trim().contains(filter)) {
                        ID_SERVICIO = listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getCodigoServicio();
                        desc_servicio.setText(listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getDescripcionServicio());
                        monto.setText(String.format("%.2f", listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getMontoServicio()));
                    }
                }

                dialog.dismiss();
            });

        });

        tableLayout = view.findViewById(R.id.table);

        titulo_diag = view.findViewById(R.id.titulo_diagnostico);
        titulo_diag.setTypeface(face);
        titulo_pres = view.findViewById(R.id.titulo_presupuesto);
        titulo_pres.setTypeface(face);

        total_costo = view.findViewById(R.id.tota_costo);
        total_costo.setTypeface(face);
        titulo_costo = view.findViewById(R.id.titulo_costo);
        titulo_costo.setTypeface(face);
        agregador = view.findViewById(R.id.guardador_hd2);

        //Instancias
        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);
        tablaDinamica.setOnItemClickListener(position -> {
            MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
            menuInferiorDos.show(getActivity().getSupportFragmentManager(), "MenuInferior");
            menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(position, 1));
            menuInferiorDos.eventoClick(opcion -> {
                int index = position - 1;
                switch (opcion) {
                    case 1:
                        // Editar Tratamiento
                        modoEdicionTratamiento = true;
                        listador.setText("ACTUALIZAR TRATAMIENTO");

                        ID_PIEZA = listaTratamientos.get(index).getPieza();
                        ID_SERVICIO = listaTratamientos.get(index).getServicio();
                        desc_servicio.setText(listaTratamientos.get(index).getDescripcionServicio());
                        monto.setText(String.format("%.2f", listaTratamientos.get(index).getMonto()));
                        servicio.setText(listaTratamientos.get(index).getDescripcionServicio());
                        pieza.setText(tablaDinamica.getCellData(position, 0));

                        for (ItemServicio item : listaServiciosGeneral) {
                            if (item.getCodigoServicio() == ID_SERVICIO) {
                                servicio.setText(listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getDescripcionServicio());
                            }
                        }

                        POSICION = index;
                        break;

                    case 2:
                        // Eliminar Tratamiento
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                        builder.setIcon(R.drawable.logonuevo);
                        builder.setTitle("Historial Odontodologico");
                        builder.setMessage("¿Desea eliminar el tratamiento?");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                            tablaDinamica.removeRow(position);
                            total = 0;
                            listaTratamientos.remove(position - 1);

                            if (tablaDinamica.getCount() > 0) {
                                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                    total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                                }
                                total_costo.setText(String.format("%.2f", total));
                            } else {
                                total_costo.setText("0.00");
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

        //Proceso para listar
        listador = view.findViewById(R.id.guardador_hm);
        listador.setTypeface(face);
        listador.setOnClickListener(v -> {
            if (!descripcionRequerida() || !montoRequerido() || !validarMonto())
                return;

            if (ID_PIEZA > 0 && ID_SERVICIO > 0) {
                total = 0;

                String[] item = new String[]{
                        pieza.getText().toString(),
                        desc_servicio.getText().toString(),
                        monto.getText().toString()
                };

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                if (!modoEdicionTratamiento) {
                    tablaDinamica.addItem(item);
                    listaTratamientos.add(new ItemTratamiento(
                            ID_PIEZA,
                            ID_SERVICIO,
                            desc_servicio.getText().toString(),
                            Double.parseDouble(monto.getText().toString()),
                            date
                    ));
                } else {
                    if (listaTratamientos.size() > 0) {
                        listaTratamientos.set(POSICION, new ItemTratamiento(
                                ID_PIEZA,
                                ID_SERVICIO,
                                desc_servicio.getText().toString(),
                                Double.parseDouble(monto.getText().toString()),
                                date
                        ));

                        // Reinciar Tabla
                        tablaDinamica.removeAll();
                        tablaDinamica.addHeader(header);
                        tablaDinamica.fondoHeader(R.color.AzulOscuro);

                        for (ItemTratamiento tratamiento : listaTratamientos) {
                            String descPieza = "";

                            for (ItemPieza PIEZA : listaPiezasGenenal) {
                                if (tratamiento.getPieza() == PIEZA.getCodigoPieza()) {
                                    descPieza = PIEZA.getNombrePieza();
                                }
                            }

                            tablaDinamica.addItem(new String[]{
                                    descPieza,
                                    tratamiento.getDescripcionServicio(),
                                    String.format("%.2f", tratamiento.getMonto())
                            });
                        }

                        modoEdicionTratamiento = false;
                        POSICION = 0;
                        listador.setText("AGREGAR TRATAMIENTO");
                    }

                    ID_SERVICIO = 0;
                    desc_servicio.setText(null);
                    monto.setText(null);
                    servicio.setText("Seleccione Servicios");

                    layoutMonto.setError(null);
                    layoutServicio.setError(null);
                }

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                    }
                    total_costo.setText(String.format("%.2f", total));
                }
            } else {
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("Hay campos vacios o incorrectos")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
            }
        });

        // Proceso para guardar
        agregador.setOnClickListener(v -> {
            if (listaTratamientos.size() > 0) {
                if (!MODO_EDICION) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("HOD2", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Set<String> set = new HashSet<>();

                    for (ItemTratamiento item : listaTratamientos) {
                        String cadena = item.getPieza() + ";" + item.getServicio() + ";" + item.getDescripcionServicio() + ";" + item.getMonto() + ";" + item.getFechaRegistro() + ";";
                        set.add(cadena);
                    }

                    editor.putStringSet("listaTratamientos", set);
                    editor.apply();

                    final SharedPreferences preferenciasFicha2 = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor2 = preferenciasFicha2.edit();
                    escritor2.putString("NO_TRATAMIENTOS", String.valueOf(listaTratamientos.size()));
                    escritor2.commit();

                    Pagos pagos = new Pagos();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, pagos);
                    transaction.commit();
                } else {
                    if (total >= TOTAL_PAGOS) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                        progressDialog.setMessage("Cargando...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        try {
                            JSONArray jsonArray = new JSONArray();
                            for (ItemTratamiento item : listaTratamientos) {
                                String fechaMYSQL;

                                if (item.getFechaRegistro().contains("/")) {
                                    Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(item.getFechaRegistro());
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    fechaMYSQL = formatter.format(initDate);
                                } else {
                                    fechaMYSQL = item.getFechaRegistro();
                                }

                                JSONObject rowJSON = new JSONObject();
                                rowJSON.put("PLAN", item.getDescripcionServicio());
                                rowJSON.put("COSTO", item.getMonto());
                                rowJSON.put("FECHA", fechaMYSQL);
                                rowJSON.put("ID_PIEZA", item.getPieza());
                                rowJSON.put("ID_SERVICIO", item.getServicio());
                                rowJSON.put("ID_HISTORIAL_ODONTO", ID_ODONTO);

                                jsonArray.put(rowJSON);
                            }

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("TRATAMIENTOS", jsonArray);

                            QuerysFichas querysFichas = new QuerysFichas(getContext());
                            querysFichas.actualizarTratamientos(ID_ODONTO, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    progressDialog.dismiss();

                                    Alerter.create(getActivity())
                                            .setTitle("Tratamientos")
                                            .setText("Actualizados correctamente")
                                            .setIcon(R.drawable.logonuevo)
                                            .setTextTypeface(face)
                                            .enableSwipeToDismiss()
                                            .setBackgroundColorRes(R.color.FondoSecundario)
                                            .show();

                                    FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                                    funcionesBitacora.registrarBitacora("ACTUALIZACION", "TRATAMIENTOS", "Se actualizo la ficha #" + ID_FICHA);

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
                                            .setText("Fallo al actualizar los tratamientos")
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
                    } else {
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("El total de tratamientos debe ser mayor a los pagos")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }
                }
            } else {
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("No ha agregado tratamientos")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
            }
        });

        obtenerPiezas();
        obtenerServicios();

        if (MODO_EDICION) {
            cargarTotalPagos();
        }

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    public void cargarTratamientos() {
        // Reinciar Tabla
        listaTratamientos.clear();
        tablaDinamica.removeAll();
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        if (!MODO_EDICION) {
            SharedPreferences preferences = getActivity().getSharedPreferences("HOD2", Context.MODE_PRIVATE);
            Set<String> set = preferences.getStringSet("listaTratamientos", null);

            if (set != null) {
                ArrayList<String> listaAuxiliar = new ArrayList<>(set);

                for (String item : listaAuxiliar) {
                    String cadenaAuxiliar[] = item.split(";");

                    listaTratamientos.add(new ItemTratamiento(
                            Integer.parseInt(cadenaAuxiliar[0]),
                            Integer.parseInt(cadenaAuxiliar[1]),
                            cadenaAuxiliar[2],
                            Double.parseDouble(cadenaAuxiliar[3]),
                            cadenaAuxiliar[4]
                    ));

                    String descPieza = "";
                    for (ItemPieza aux : listaPiezasGenenal) {
                        if (aux.getCodigoPieza() == Integer.parseInt(cadenaAuxiliar[0])) {
                            descPieza = aux.getNombrePieza();
                        }
                    }

                    tablaDinamica.addItem(new String[]{
                            descPieza,
                            cadenaAuxiliar[2],
                            String.format("%.2f", Double.parseDouble(cadenaAuxiliar[3]))
                    });
                }

                total = 0;

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                    }
                    total_costo.setText(String.format("%.2f", total));
                } else {
                    total_costo.setText("0.00");
                }
            }
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.obtenerHistorialOdontodologico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject(object.toString());
                        ID_ODONTO = jsonObject.getInt("ID_HISTORIAL_ODONTO");

                        QuerysFichas querysFichas1 = new QuerysFichas(getContext());
                        querysFichas1.obtenerTratamientos(ID_ODONTO, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object) {
                                try {
                                    JSONArray jsonArray = new JSONArray(object.toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        listaTratamientos.add(new ItemTratamiento(
                                                jsonArray.getJSONObject(i).getInt("ID_PIEZA"),
                                                jsonArray.getJSONObject(i).getInt("ID_SERVICIO"),
                                                jsonArray.getJSONObject(i).getString("PLAN"),
                                                jsonArray.getJSONObject(i).getDouble("COSTO"),
                                                jsonArray.getJSONObject(i).getString("FECHA")
                                        ));

                                        tablaDinamica.addItem(new String[]{
                                                jsonArray.getJSONObject(i).getString("NOMBRE_PIEZA"),
                                                jsonArray.getJSONObject(i).getString("PLAN"),
                                                String.format("%.2f", jsonArray.getJSONObject(i).getDouble("COSTO"))
                                        });
                                    }

                                    total = 0;

                                    if (tablaDinamica.getCount() > 0) {
                                        for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                            total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                                        }
                                        total_costo.setText(String.format("%.2f", total));
                                    } else {
                                        total_costo.setText("0.00");
                                    }

                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressDialog.dismiss();
                                cargarTratamientos();
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

    public void obtenerPiezas() {
        listaPiezas.clear();
        listaPiezasGenenal.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_PIEZA", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        querysPiezas.obtenerListadoPiezas(jsonObject, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getInt("ESTADO") == 1) {
                            ItemPieza pieza = new ItemPieza(
                                    jsonArray.getJSONObject(i).getInt("ID_PIEZA"),
                                    jsonArray.getJSONObject(i).getInt("NUMERO"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE"),
                                    (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false,
                                    jsonArray.getJSONObject(i).getInt("FICHAS_NORMALES")
                            );

                            listaPiezas.add(pieza.getNombrePieza());
                            listaPiezasGenenal.add(pieza);
                        }
                    }

                    cargarTratamientos();

                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerPiezas();
            }
        });
    }

    private void obtenerServicios() {
        listaServicios.clear();
        listaServiciosGeneral.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_SERVICIO", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.obtenerListadoServicios(jsonObject, new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getInt("ESTADO") == 1) {
                            ItemServicio itemServicio = new ItemServicio(
                                    jsonArray.getJSONObject(i).getInt("ID_SERVICIO"),
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    jsonArray.getJSONObject(i).getDouble("MONTO"),
                                    (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false,
                                    jsonArray.getJSONObject(i).getInt("FICHAS_NORMALES")
                            );

                            listaServicios.add(itemServicio.getDescripcionServicio());
                            listaServiciosGeneral.add(itemServicio);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerServicios();
            }
        });
    }

    private void cargarTotalPagos() {
        TOTAL_PAGOS = 0;

        QuerysFichas querysFichas = new QuerysFichas(getContext());
        querysFichas.obtenerPagos(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        TOTAL_PAGOS += jsonArray.getJSONObject(i).getDouble("PAGO");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                total_pagos.setText(String.format("%.2f", TOTAL_PAGOS));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    //    VALIDACIONES
    private boolean descripcionRequerida() {
        String texto = desc_servicio.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutServicio.setError("Campo Requerido");
            return false;
        } else {
            layoutServicio.setError(null);
            return true;
        }
    }

    private boolean montoRequerido() {
        String texto = monto.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutMonto.setError("Campo Requerido");
            return false;
        } else {
            layoutMonto.setError(null);
            return true;
        }
    }

    private boolean validarMonto() {
        String texto = monto.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutMonto.setError(null);
            return true;
        } else {
            layoutMonto.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }
}