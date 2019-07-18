package com.example.dentalhistoryrecorder.OpcionIngreso.Normal;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IngDetalle extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText motivo, medico, referente;
    private TextView fecha, titulo_fecha;
    private Button calendario;
    private FloatingActionButton guardador;
    private String idPaciente;
    private static final String TAG = "MyActivity";
    SharedPreferences preferencias, preferencias2;
    RequestQueue requestQueue;
    private String iddPaciente;
    private int idPacienteExis;

    public IngDetalle() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ing_detalle, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());

        //Barra de Titulo
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle de la Ficha");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        //Detalle de la ficha
        fecha = (TextView) view.findViewById(R.id.fecha);
        fecha.setTypeface(face);
        motivo = (TextInputEditText) view.findViewById(R.id.motivo);
        motivo.setTypeface(face);
        medico = (TextInputEditText) view.findViewById(R.id.medico);
        medico.setTypeface(face);
        referente = (TextInputEditText) view.findViewById(R.id.referente);
        referente.setTypeface(face);
        calendario = view.findViewById(R.id.obFecha);
        guardador = view.findViewById(R.id.guardador_dt);
        titulo_fecha = view.findViewById(R.id.titulo_fecha);
        titulo_fecha.setTypeface(face);

        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int a = calendar.get(Calendar.YEAR);
        String fe = String.valueOf(dia) + "/" + String.valueOf(mes) + "/" + String.valueOf(a);
        fecha.setText(fe);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Agregar agregar = new Agregar();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, agregar);
                transaction.commit();
            }
        });

        //Obtener Calendario
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth ,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dat = dayOfMonth + "/" + monthOfYear + "/" + year;
                        fecha.setText(dat);
                    }
                }, yy, mm, dd);

                datePicker.show();
            }
        });

        //Persistencia de Datos
        //preferencias = getActivity().getSharedPreferences("datosdetalle", Context.MODE_PRIVATE);
        preferencias2 = getActivity().getSharedPreferences("ids", Context.MODE_PRIVATE);


        //final SharedPreferences.Editor escritor = preferencias.edit();
        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idPacienteExis == 0){
                    insertarFicha("https://diegosistemas.xyz/DHR/Normal/ficha.php?estado=2");
                    Toast.makeText(getActivity(),"Entre",Toast.LENGTH_LONG).show();
                }
                else {
                    insertarFichaExistente("https://diegosistemas.xyz/DHR/Normal/ficha.php?estado=3");
                    Toast.makeText(getActivity(),"No Entre",Toast.LENGTH_LONG).show();
                }
                IngHMedico ingHMedico = new IngHMedico();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, ingHMedico);
                transaction.commit();
            }
        });

        return view;
    }


    //Insertar Ficha
    public void insertarFicha(String URL) {
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
                parametros.put("fecha", fecha.getText().toString());
                parametros.put("medico", medico.getText().toString());
                parametros.put("motivo", motivo.getText().toString());
                parametros.put("referente", referente.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user",preferencias2.getString("idUsuario","1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    //Insertar Ficha
    public void insertarFichaExistente(String URL) {
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
                parametros.put("id", String.valueOf(idPacienteExis));
                parametros.put("fecha", fecha.getText().toString());
                parametros.put("medico", medico.getText().toString());
                parametros.put("motivo", motivo.getText().toString());
                parametros.put("referente", referente.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user",preferencias2.getString("idUsuario","1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void obtenerPaciente(int id){
        idPacienteExis = id;
    }
}