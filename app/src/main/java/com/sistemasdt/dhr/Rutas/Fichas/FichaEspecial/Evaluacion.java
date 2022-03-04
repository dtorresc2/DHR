package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;


public class Evaluacion extends Fragment {
    Toolbar toolbar;
    TextView titulo1, titulo2, titulo3, titulo4, titulo5, titulo6, titulo7, titulo8, titulo9, titulo10, titulo11, titulo12;
    CheckBox ad1, ad2, ad3, ad4, ad5;
    CheckBox fas1, fas2, fas3;
    CheckBox fai1, fai2, fai3;
    CheckBox oc1, oc2, oc3, oc4, oc5;
    CheckBox df1, df2, df3, df4;
    CheckBox rp1, rp2, rp3, rp4, rp5;
    CheckBox len1, len2, len3;
    CheckBox de1, de2;
    CheckBox lm1, lm2;
    CheckBox h1, h2, h3, h4, h5;
    CheckBox tmj1, tmj2, tmj3, tmj4, tmj5, tmj6, tmj7;
    EditText descripcion;
    FloatingActionButton siguiente;
    private static final String TAG = "MyActivity";
    RequestQueue requestQueue;

    public Evaluacion() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_evaluacion, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Evaluacion");
        toolbar.setNavigationOnClickListener(v -> {
            MenuFichas menuFichas = new MenuFichas();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
            transaction.replace(R.id.contenedor, menuFichas);
            transaction.commit();
        });

        //Alineacion Dental
        titulo1 = view.findViewById(R.id.titulo1);
        titulo1.setTypeface(face);
        ad1 = view.findViewById(R.id.ad1);
        ad1.setTypeface(face);
        ad2 = view.findViewById(R.id.ad2);
        ad2.setTypeface(face);
        ad3 = view.findViewById(R.id.ad3);
        ad3.setTypeface(face);
        ad4 = view.findViewById(R.id.ad4);
        ad4.setTypeface(face);
        ad5 = view.findViewById(R.id.ad5);
        ad5.setTypeface(face);


        //Forma del Arco Superior
        titulo2 = view.findViewById(R.id.titulo2);
        titulo2.setTypeface(face);
        fas1 = view.findViewById(R.id.fas1);
        fas1.setTypeface(face);
        fas2 = view.findViewById(R.id.fas2);
        fas2.setTypeface(face);
        fas3 = view.findViewById(R.id.fas3);
        fas3.setTypeface(face);

        //Forma del Arco Inferior
        titulo3 = view.findViewById(R.id.titulo3);
        titulo3.setTypeface(face);
        fai1 = view.findViewById(R.id.fai1);
        fai1.setTypeface(face);
        fai2 = view.findViewById(R.id.fai2);
        fai2.setTypeface(face);
        fai3 = view.findViewById(R.id.fai3);
        fai3.setTypeface(face);

        //Oclusion
        titulo4 = view.findViewById(R.id.titulo4);
        titulo4.setTypeface(face);
        oc1 = view.findViewById(R.id.oc1);
        oc1.setTypeface(face);
        oc2 = view.findViewById(R.id.oc2);
        oc2.setTypeface(face);
        oc3 = view.findViewById(R.id.oc3);
        oc3.setTypeface(face);
        oc4 = view.findViewById(R.id.oc4);
        oc4.setTypeface(face);
        oc5 = view.findViewById(R.id.oc5);
        oc5.setTypeface(face);

        //Desarrollo Facial
        titulo5 = view.findViewById(R.id.titulo5);
        titulo5.setTypeface(face);
        df1 = view.findViewById(R.id.df1);
        df1.setTypeface(face);
        df2 = view.findViewById(R.id.df2);
        df2.setTypeface(face);
        df3 = view.findViewById(R.id.df3);
        df3.setTypeface(face);
        df4 = view.findViewById(R.id.df4);
        df4.setTypeface(face);

        //Respiracion y Postura
        titulo6 = view.findViewById(R.id.titulo6);
        titulo6.setTypeface(face);
        rp1 = view.findViewById(R.id.rp1);
        rp1.setTypeface(face);
        rp2 = view.findViewById(R.id.rp2);
        rp2.setTypeface(face);
        rp3 = view.findViewById(R.id.rp3);
        rp3.setTypeface(face);
        rp4 = view.findViewById(R.id.rp4);
        rp4.setTypeface(face);
        rp5 = view.findViewById(R.id.rp5);
        rp5.setTypeface(face);

        //Lengua
        titulo7 = view.findViewById(R.id.titulo7);
        titulo7.setTypeface(face);
        len1 = view.findViewById(R.id.len1);
        len1.setTypeface(face);
        len2 = view.findViewById(R.id.len2);
        len2.setTypeface(face);
        len3 = view.findViewById(R.id.len3);
        len3.setTypeface(face);

        //Deglucion
        titulo8 = view.findViewById(R.id.titulo8);
        titulo8.setTypeface(face);
        de1 = view.findViewById(R.id.de1);
        de1.setTypeface(face);
        de2 = view.findViewById(R.id.de2);
        de2.setTypeface(face);

        //Labios y Mejilla
        titulo9 = view.findViewById(R.id.titulo9);
        titulo9.setTypeface(face);
        lm1 = view.findViewById(R.id.lm1);
        lm1.setTypeface(face);
        lm2 = view.findViewById(R.id.lm2);
        lm2.setTypeface(face);

        //Habitos
        titulo10 = view.findViewById(R.id.titulo10);
        titulo10.setTypeface(face);
        h1 = view.findViewById(R.id.h1);
        h1.setTypeface(face);
        h2 = view.findViewById(R.id.h2);
        h2.setTypeface(face);
        h3 = view.findViewById(R.id.h3);
        h3.setTypeface(face);
        h4 = view.findViewById(R.id.h4);
        h4.setTypeface(face);
        h5 = view.findViewById(R.id.h5);
        h5.setTypeface(face);

        //TMJ
        titulo11 = view.findViewById(R.id.titulo11);
        titulo11.setTypeface(face);
        tmj1 = view.findViewById(R.id.tmj1);
        tmj1.setTypeface(face);
        tmj2 = view.findViewById(R.id.tmj2);
        tmj2.setTypeface(face);
        tmj3 = view.findViewById(R.id.tmj3);
        tmj3.setTypeface(face);
        tmj4 = view.findViewById(R.id.tmj4);
        tmj4.setTypeface(face);
        tmj5 = view.findViewById(R.id.tmj5);
        tmj5.setTypeface(face);
        tmj6 = view.findViewById(R.id.tmj6);
        tmj6.setTypeface(face);
        tmj7 = view.findViewById(R.id.tmj7);
        tmj7.setTypeface(face);

        //Otros
        titulo12 = view.findViewById(R.id.titulo12);
        titulo12.setTypeface(face);
        descripcion = view.findViewById(R.id.otrosEvaluacion);
        descripcion.setTypeface(face);

        siguiente = view.findViewById(R.id.siguiente);
        siguiente.setOnClickListener(new View.OnClickListener() {
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

                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    crearEvaluacion("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=6");
                    MenuFichas menuFichas = new MenuFichas();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, menuFichas);
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
        });

        return view;
    }

    public void crearEvaluacion(String URL) {
        final String[] id = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                agregarAlineacionD("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=7");
                agregarArcoS("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=8");
                agregarArcoI("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=9");
                agregarOclucion("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=10");
                agregarDesarrollo("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=11");
                agregarRespiracionPos("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=12");
                agregarLengua("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=13");
                agregarDeglucion("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=14");
                agregarLabios("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=15");
                agregarHabitos("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=16");
                agregarTMJ("http://dhr.sistemasdt.xyz/Especial/ingresoE.php?estado=17");
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
                parametros.put("desc", descripcion.getText().toString());
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }


    public void agregarAlineacionD(String URL) {
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
                parametros.put("ad1", String.valueOf((ad1.isChecked()) ? 1 : 0));
                parametros.put("ad2", String.valueOf((ad2.isChecked()) ? 1 : 0));
                parametros.put("ad3", String.valueOf((ad3.isChecked()) ? 1 : 0));
                parametros.put("ad4", String.valueOf((ad4.isChecked()) ? 1 : 0));
                parametros.put("ad5", String.valueOf((ad5.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarArcoS(String URL) {
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
                parametros.put("fas1", String.valueOf((fas1.isChecked()) ? 1 : 0));
                parametros.put("fas2", String.valueOf((fas2.isChecked()) ? 1 : 0));
                parametros.put("fas3", String.valueOf((fas3.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarArcoI(String URL) {
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
                parametros.put("fai1", String.valueOf((fai1.isChecked()) ? 1 : 0));
                parametros.put("fai2", String.valueOf((fai2.isChecked()) ? 1 : 0));
                parametros.put("fai3", String.valueOf((fai3.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarOclucion(String URL) {
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
                parametros.put("oc1", String.valueOf((oc1.isChecked()) ? 1 : 0));
                parametros.put("oc2", String.valueOf((oc2.isChecked()) ? 1 : 0));
                parametros.put("oc3", String.valueOf((oc3.isChecked()) ? 1 : 0));
                parametros.put("oc4", String.valueOf((oc4.isChecked()) ? 1 : 0));
                parametros.put("oc5", String.valueOf((oc5.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarDesarrollo(String URL) {
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
                parametros.put("df1", String.valueOf((df1.isChecked()) ? 1 : 0));
                parametros.put("df2", String.valueOf((df2.isChecked()) ? 1 : 0));
                parametros.put("df3", String.valueOf((df3.isChecked()) ? 1 : 0));
                parametros.put("df4", String.valueOf((df4.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarRespiracionPos(String URL) {
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
                parametros.put("rp1", String.valueOf((rp1.isChecked()) ? 1 : 0));
                parametros.put("rp2", String.valueOf((rp2.isChecked()) ? 1 : 0));
                parametros.put("rp3", String.valueOf((rp3.isChecked()) ? 1 : 0));
                parametros.put("rp4", String.valueOf((rp4.isChecked()) ? 1 : 0));
                parametros.put("rp5", String.valueOf((rp5.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarLengua(String URL) {
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
                parametros.put("len1", String.valueOf((len1.isChecked()) ? 1 : 0));
                parametros.put("len2", String.valueOf((len2.isChecked()) ? 1 : 0));
                parametros.put("len3", String.valueOf((len3.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarDeglucion(String URL) {
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
                parametros.put("de1", String.valueOf((de1.isChecked()) ? 1 : 0));
                parametros.put("de2", String.valueOf((de2.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarLabios(String URL) {
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
                parametros.put("lm1", String.valueOf((lm1.isChecked()) ? 1 : 0));
                parametros.put("lm2", String.valueOf((lm2.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarHabitos(String URL) {
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
                parametros.put("h1", String.valueOf((h1.isChecked()) ? 1 : 0));
                parametros.put("h2", String.valueOf((h2.isChecked()) ? 1 : 0));
                parametros.put("h3", String.valueOf((h3.isChecked()) ? 1 : 0));
                parametros.put("h4", String.valueOf((h4.isChecked()) ? 1 : 0));
                parametros.put("h5", String.valueOf((h5.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarTMJ(String URL) {
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
                parametros.put("tmj1", String.valueOf((tmj1.isChecked()) ? 1 : 0));
                parametros.put("tmj2", String.valueOf((tmj2.isChecked()) ? 1 : 0));
                parametros.put("tmj3", String.valueOf((tmj3.isChecked()) ? 1 : 0));
                parametros.put("tmj4", String.valueOf((tmj4.isChecked()) ? 1 : 0));
                parametros.put("tmj5", String.valueOf((tmj5.isChecked()) ? 1 : 0));
                parametros.put("tmj6", String.valueOf((tmj6.isChecked()) ? 1 : 0));
                parametros.put("tmj7", String.valueOf((tmj7.isChecked()) ? 1 : 0));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}