package com.sistemasdt.dhr.OpcionIngreso.Especial;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.R;
import com.tapadoo.alerter.Alerter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IngCostos extends Fragment {
    EditText enganche, costo, terapia;
    FloatingActionButton agregar;
    Toolbar toolbar;
    RequestQueue requestQueue;
    private SharedPreferences preferencias;
    private static final String TAG = "MyActivity";
    private String idUsuario;


    public IngCostos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ing_costos, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Costos");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuFichas menuFichas = new MenuFichas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichas);
                transaction.commit();
            }
        });

        preferencias = getActivity().getSharedPreferences("Terapia", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferencias.edit();

        enganche = view.findViewById(R.id.enganche);
        enganche.setTypeface(face);

        costo = view.findViewById(R.id.costoVisita);
        costo.setTypeface(face);

        terapia = view.findViewById(R.id.terapia);
        terapia.setTypeface(face);

        agregar = view.findViewById(R.id.guardador_hm);

        agregar.setOnClickListener(new View.OnClickListener() {
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

                if (!terapia.getText().toString().isEmpty() && !enganche.getText().toString().isEmpty() && !costo.getText().toString().isEmpty()){
                    editor.putString("costoVisita",costo.getText().toString());
                    editor.commit();

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        crearFicha("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=1");
                        IngresoVisitas ingresoVisitas = new IngresoVisitas();
                        ingresoVisitas.ObtenerOpcion(1);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                        transaction.replace(R.id.contenedor, ingresoVisitas);
                        transaction.commit();

                    } else {
                        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face2)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }
                }
                else {
                    Alerter.create(getActivity())
                            .setTitle("Hay Campos Vacios")
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

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void crearFicha(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                agregarCostos("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=2");
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
                parametros.put("paciente", idUsuario);
                parametros.put("desc", "Evaluacion MyObrace");
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));

                Calendar calendar = Calendar.getInstance();
                final int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int a = calendar.get(Calendar.YEAR);

                String dat = dia + "/" + mes + "/" + a;

                parametros.put("fecha", dat);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void agregarCostos(String URL) {
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
                parametros.put("enganche", enganche.getText().toString());
                parametros.put("costo", costo.getText().toString());
                parametros.put("terapia", terapia.getText().toString());
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void ObtenerPaciente(String id){
        idUsuario = id;
    }
}
