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

public class HMedico2 extends Fragment {
    private Toolbar toolbar;
    private CheckBox corazon, artritris, tuberculosis, f_reuma, pres_alta, pres_baja, diabetes, anemia, epilepsia;
    private EditText otro;
    RequestQueue requestQueue;
    private FloatingActionButton guardador;
    private static final String TAG = "MyActivity";
    public HMedico2() {
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
        View view = inflater.inflate(R.layout.fragment_hmedico2, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Medico (2/2)");
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

        guardador = view.findViewById(R.id.guardador_hm2);

        corazon = view.findViewById(R.id.corazon);
        corazon.setTypeface(face);
        artritris = view.findViewById(R.id.artritris);
        artritris.setTypeface(face);
        tuberculosis = view.findViewById(R.id.tuberculosis);
        tuberculosis.setTypeface(face);
        f_reuma = view.findViewById(R.id.fiebre);
        f_reuma.setTypeface(face);
        pres_alta = view.findViewById(R.id.presion_alta);
        pres_alta.setTypeface(face);
        pres_baja = view.findViewById(R.id.presion_baja);
        pres_baja.setTypeface(face);
        diabetes = view.findViewById(R.id.diabetes);
        diabetes.setTypeface(face);
        anemia = view.findViewById(R.id.anemia);
        anemia.setTypeface(face);
        epilepsia = view.findViewById(R.id.epilepsia);
        epilepsia.setTypeface(face);
        otro = view.findViewById(R.id.otro_hm);
        otro.setTypeface(face);

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insertarPadecimientos("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=7");

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

                insertarPadecimientos("https://diegosistemas.xyz/DHR/Normal/ficha.php?estado=7");
                HOdon2 ihOdon2 = new HOdon2();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, ihOdon2);
                transaction.commit();
            }
        });

        return view;
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void insertarPadecimientos(String URL) {
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
                parametros.put("corazon",String.valueOf((corazon.isChecked()) ? 1 : 0));
                parametros.put("artritis",String.valueOf((artritris.isChecked()) ? 1 : 0));
                parametros.put("tuberculosis",String.valueOf((corazon.isChecked()) ? 1 : 0));
                parametros.put("fiebrereu",String.valueOf((f_reuma.isChecked()) ? 1 : 0));
                parametros.put("presionalta",String.valueOf((pres_alta.isChecked()) ? 1 : 0));
                parametros.put("presionbaja",String.valueOf((pres_baja.isChecked()) ? 1 : 0));
                parametros.put("diabetes",String.valueOf((diabetes.isChecked()) ? 1 : 0));
                parametros.put("anemia",String.valueOf((anemia.isChecked()) ? 1 : 0));
                parametros.put("epilepsia",String.valueOf((epilepsia.isChecked()) ? 1 : 0));
                parametros.put("otros",otro.getText().toString());
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
