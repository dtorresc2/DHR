package com.example.dentalhistoryrecorder.OpcionIngreso.Normal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
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
import com.example.dentalhistoryrecorder.OpcionSeguimiento.SegPagos;
import com.example.dentalhistoryrecorder.OpcionSeguimiento.Seguimiento;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

import android.support.design.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IngHOdon extends Fragment {
    private Toolbar toolbar;
    ArrayList<String> listaDatos1, piezas;
    Button listador, eliminador;
    Spinner spinner;
    CheckBox dolor, gingivitis;
    ArrayAdapter<String> adaptadorSpinner;
    TableLayout tableLayout;
    RequestQueue requestQueue;
    private String[] header = {"Pieza", "Descripsion", "Costo"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private EditText tratamiento, costo, otros, desc_dolor;
    private TextView titulo_detalle, titulo_diag, titulo_pres, titulo_piez, total_costo, titulo_costo, celdap, celdat, celdac;
    private TablaDinamica tablaDinamica;
    private int lim;
    private double total;
    private FloatingActionButton agregador;
    private static final String TAG = "MyActivity";
    private int mOpcion = 0;
    private SharedPreferences preferencias;
    private int contador = 0;

    public IngHOdon() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ing_hodon, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());

        preferencias = getActivity().getSharedPreferences("Consultar", Context.MODE_PRIVATE);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        spinner = view.findViewById(R.id.ing_piezas);
        listador = view.findViewById(R.id.guardador_hm);
        listador.setTypeface(face);
        eliminador = view.findViewById(R.id.eliminador);
        eliminador.setTypeface(face);

        tableLayout = view.findViewById(R.id.table);

        tratamiento = view.findViewById(R.id.tratamiento);
        tratamiento.setTypeface(face);
        costo = view.findViewById(R.id.costo);
        costo.setTypeface(face);

        titulo_diag = view.findViewById(R.id.titulo_diagnostico);
        titulo_diag.setTypeface(face);
        titulo_pres = view.findViewById(R.id.titulo_presupuesto);
        titulo_pres.setTypeface(face);
        titulo_piez = view.findViewById(R.id.titulo_pieza);
        titulo_piez.setTypeface(face);
        total_costo = view.findViewById(R.id.tota_costo);
        total_costo.setTypeface(face);
        titulo_costo = view.findViewById(R.id.titulo_costo);
        titulo_costo.setTypeface(face);
        agregador = view.findViewById(R.id.guardador_hd2);

        //Instancias
        llenarPiezas();
        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);
        //tablaDinamica.fondoData(R.color.FondoSecundario);

        //Encabezado
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

        //Recuperacion de Datos
        /*SharedPreferences preferencias = getActivity().getSharedPreferences("datosdentales", Context.MODE_PRIVATE);
        dolor.setChecked(preferencias.getBoolean("dolor", false));
        gingivitis.setChecked(preferencias.getBoolean("gingi", false));
        desc_dolor.setText(preferencias.getString("descdolor", ""));
        otros.setText(preferencias.getString("otro", ""));
        lim = preferencias.getInt("lim", 0);

        final SharedPreferences.Editor escritor = preferencias.edit();

        if (lim > 0) {
            for (int i = 0; i < lim; i++) {
                String[] item = new String[]{
                        preferencias.getString("pieza" + i, ""),
                        preferencias.getString("trat" + i, ""),
                        preferencias.getString("cost" + i, "")
                };
                tablaDinamica.addItem(item);
            }
        }*/

        //Proceso para listar
        listador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = 0;
                String[] item = new String[]{
                        spinner.getSelectedItem().toString(),
                        tratamiento.getText().toString(),
                        costo.getText().toString()
                };
                tablaDinamica.addItem(item);
                tratamiento.setText(null);
                costo.setText(null);

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                    }
                    total_costo.setText(String.format("%.2f", total));
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
                /*escritor.putBoolean("dolor", dolor.isChecked());
                escritor.putString("descdolor", desc_dolor.getText().toString());
                escritor.putBoolean("gingi", gingivitis.isChecked());
                escritor.putString("otros", otros.getText().toString());
                escritor.putInt("lim", tablaDinamica.getCount());

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        escritor.putString("pieza" + (i - 1), tablaDinamica.getCellData(i, 0));
                        escritor.putString("trat" + (i - 1), tablaDinamica.getCellData(i, 1));
                        escritor.putString("cost" + (i - 1), tablaDinamica.getCellData(i, 2));
                    }
                }
                escritor.commit();*/

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        //insertarTratamiento("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=10", tablaDinamica.getCellData(i, 1), tablaDinamica.getCellData(i, 2), tablaDinamica.getCellData(i, 0));

                        switch (mOpcion) {
                            case 1:
                                insertarTratamiento("https://diegosistemas.xyz/DHR/Normal/ficha.php?estado=10", tablaDinamica.getCellData(i, 1), tablaDinamica.getCellData(i, 2), tablaDinamica.getCellData(i, 0));
                                break;
                            case 2:
                                agregarTratamiento("https://diegosistemas.xyz/DHR/Normal/seguimiento.php?estado=1", tablaDinamica.getCellData(i, 1), tablaDinamica.getCellData(i, 2), tablaDinamica.getCellData(i, 0));
                                Toast.makeText(getActivity(), preferencias.getString("idficha", ""), Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    switch (mOpcion) {
                        case 1:
                        /*Ing_HFoto ingHFoto = new Ing_HFoto();
                        ingHFoto.ObtenerOpcion(1);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                        transaction.replace(R.id.contenedor, ingHFoto);
                        transaction.commit();*/

                            SegPagos segPagos = new SegPagos();
                            segPagos.ObtenerOpcion(1);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction.replace(R.id.contenedor, segPagos);
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
                else {
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

    public void llenarPiezas() {
        piezas = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            piezas.add(String.valueOf(i));
        }
        adaptadorSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, piezas);
        spinner.setAdapter(adaptadorSpinner);
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void insertarTratamiento(String URL, final String aux1, final String aux2, final String aux3) {
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
                parametros.put("plan", aux1);
                parametros.put("costo", aux2);
                parametros.put("pieza", aux3);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void agregarTratamiento(String URL, final String aux1, final String aux2, final String aux3) {
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
                parametros.put("plan", aux1);
                parametros.put("costo", aux2);
                parametros.put("pieza", aux3);
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}