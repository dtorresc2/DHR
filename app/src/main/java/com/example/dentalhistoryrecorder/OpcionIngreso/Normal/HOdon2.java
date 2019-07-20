package com.example.dentalhistoryrecorder.OpcionIngreso.Normal;

import android.app.ProgressDialog;
import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.R;

import java.util.HashMap;
import java.util.Map;

public class HOdon2 extends Fragment {
    private Toolbar toolbar;
    RequestQueue requestQueue;
    private FloatingActionButton guardador;
    private static final String TAG = "MyActivity";
    CheckBox dolor, gingivitis;
    private EditText otros, desc_dolor;
    public HOdon2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hodon2, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Odontodologico (1/2)");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "ATRAS", Toast.LENGTH_SHORT).show();
                Agregar agregar = new Agregar();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, agregar);
                transaction.commit();
            }
        });

        dolor = view.findViewById(R.id.dolor);
        dolor.setTypeface(face);
        gingivitis = view.findViewById(R.id.gingivitis);
        gingivitis.setTypeface(face);
        otros = view.findViewById(R.id.otros_ho);
        otros.setTypeface(face);
        desc_dolor = view.findViewById(R.id.desc_dolor);

        guardador = view.findViewById(R.id.guardador_hd);

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insertarHOdonto("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=8");

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

                insertarHOdonto("https://diegosistemas.xyz/DHR/Normal/ficha.php?estado=8");
                IngHOdon ingHOdon = new IngHOdon();
                ingHOdon.ObtenerOpcion(1);
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, ingHOdon);
                transaction.commit();
            }
        });

        return view;
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void insertarHOdonto(String URL) {
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
                parametros.put("dolor",String.valueOf((dolor.isChecked()) ? 1 : 0));
                parametros.put("descdolor", desc_dolor.getText().toString());
                parametros.put("gingivitis",String.valueOf((gingivitis.isChecked()) ? 1 : 0));
                parametros.put("otros", otros.getText().toString());
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
