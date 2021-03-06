package com.sistemasdt.dhr.OpcionConsulta.Normal;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sistemasdt.dhr.OpcionConsulta.Especial.FichasEspeciales;
import com.sistemasdt.dhr.OpcionConsulta.Normal.Adaptadores.AdaptadorConsultaFicha;
import com.sistemasdt.dhr.OpcionSeguimiento.Seguimiento;
import com.sistemasdt.dhr.OpcionSeguimiento.SeguimientoEspecial;
import com.sistemasdt.dhr.R;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class consultarFichas extends Fragment {
    private RecyclerView listafichas;
    private EditText nombre;
    private Toolbar toolbar;
    private AdaptadorConsultaFicha adapter;
    private RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;
    private static final String TAG = "MyActivity";
    private String idPaciente;
    ArrayList<ItemsFichas> lista = new ArrayList<>();
    SharedPreferences preferencias, preferencias2;
    private int mOpcion = 0;

    @SuppressLint("ValidFragment")
    public consultarFichas(String id, int opcion) {
        // Required empty public constructor
        idPaciente = id;
        mOpcion = opcion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultar_fichas, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Fichas");
        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);

        nombre = view.findViewById(R.id.nombre_con);
        nombre.setTypeface(face);
        nombre.setEnabled(false);
        nombre.setText(preferencias.getString("nombre", ""));
        listafichas = view.findViewById(R.id.lista_fichas);

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

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (mOpcion < 4){
                consultarFichas("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=2");
            }
            else {
                consultarFichasEspeciales("http://dhr.sistemasdt.xyz/Especial/consultaE.php?estado=1");
            }

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

        return view;
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void consultarFichas(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    final ArrayList<ItemsFichas> lista = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("idFicha");
                            String motivo = "Motivo: " + jsonArray.getJSONObject(i).getString("motivo");
                            String medico = "Medico: " + jsonArray.getJSONObject(i).getString("medico");
                            String fecha = "Fecha: " + jsonArray.getJSONObject(i).getString("fecha");
                            lista.add(new ItemsFichas(id, motivo, medico, fecha));
                        }

                        listafichas.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorConsultaFicha(lista);
                        listafichas.setLayoutManager(layoutManager);
                        listafichas.setAdapter(adapter);
                        adapter.setOnItemClickListener(new AdaptadorConsultaFicha.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                SharedPreferences.Editor escritor2 = preferencias.edit();
                                escritor2.putString("idficha", lista.get(position).getId());
                                escritor2.commit();

                                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {
                                    switch (mOpcion) {
                                        case 1:
                                            FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                            Historiales historiales = new Historiales();
                                            transaction.replace(R.id.contenedor, historiales);
                                            transaction.commit();
                                            break;

                                        case 2:
                                            FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                            Seguimiento seguimiento = new Seguimiento();
                                            transaction2.replace(R.id.contenedor, seguimiento);
                                            transaction2.commit();
                                            break;
                                    }
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
                        });
                    }

                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }
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
                parametros.put("id", idPaciente);
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarFichasEspeciales(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    final ArrayList<ItemsFichas> lista = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("idProgramaCostos");
                            String fecha = "Fecha: " + jsonArray.getJSONObject(i).getString("fecha");
                            String descripsion = "Descripsion: " + jsonArray.getJSONObject(i).getString("descripsion");
                            String terapia = "Terapia Completa: " + jsonArray.getJSONObject(i).getString("terapia");
                            lista.add(new ItemsFichas(id, terapia, descripsion, fecha));
                        }

                        listafichas.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorConsultaFicha(lista);
                        listafichas.setLayoutManager(layoutManager);
                        listafichas.setAdapter(adapter);
                        adapter.setOnItemClickListener(new AdaptadorConsultaFicha.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                SharedPreferences.Editor escritor2 = preferencias.edit();
                                escritor2.putString("idficha", lista.get(position).getId());
                                escritor2.commit();

                                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {
                                    switch (mOpcion) {
                                        case 4:
                                            FragmentTransaction transaction3 = getFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                            FichasEspeciales fichasEspeciales = new FichasEspeciales();
                                            transaction3.replace(R.id.contenedor, fichasEspeciales);
                                            transaction3.commit();
                                            break;

                                        case 5:
                                            FragmentTransaction transaction4 = getFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                            SeguimientoEspecial seguimientoEspecial = new SeguimientoEspecial();
                                            transaction4.replace(R.id.contenedor, seguimientoEspecial);
                                            transaction4.commit();
                                            break;

                                        default:
                                            break;
                                    }
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
                        });
                    }

                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }
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
                parametros.put("id", idPaciente);
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}
