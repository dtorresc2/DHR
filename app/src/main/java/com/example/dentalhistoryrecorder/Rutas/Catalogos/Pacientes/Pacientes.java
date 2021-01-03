package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionIngreso.Normal.IngDetalle;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Pacientes extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText primerNombre, edad, telefono, ocupacion, fechap, dpi;
    private TextInputLayout nombreLayout, edadLayout, fechaLayout, dpiLayout, ocupacionLayout, telLayout;
    private RadioButton sexo, sexof, truePaciente, falsePaciente;
    private TextView tituloEstado, tituloGenero;
    private FloatingActionButton agregador;
    private static final String TAG = "MyActivity";
    private ImageButton fecha;
    RequestQueue requestQueue;
    private RecyclerView listaPac;
    private PacienteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public Pacientes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pacientes, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Paciente");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Catalogos catalogos = new Catalogos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, catalogos);
                transaction.commit();
            }
        });

        primerNombre = view.findViewById(R.id.primerNombre);
        primerNombre.setTypeface(face);

        edad = view.findViewById(R.id.edad);
        edad.setTypeface(face);

        telefono = view.findViewById(R.id.telefono);
        telefono.setTypeface(face);

        ocupacion = view.findViewById(R.id.ocupacion);
        ocupacion.setTypeface(face);

        sexo = (RadioButton) view.findViewById(R.id.masculino);
        sexo.setTypeface(face);

        sexof = view.findViewById(R.id.femenino);
        sexof.setTypeface(face);

        agregador = view.findViewById(R.id.grabarPaciente);

        fecha = view.findViewById(R.id.fecha_dp);

        fechap = view.findViewById(R.id.fecha_persona);
        fechap.setTypeface(face);

        dpi = view.findViewById(R.id.dpi);
        dpi.setTypeface(face);

        nombreLayout = view.findViewById(R.id.nombreLayout);
        nombreLayout.setTypeface(face);

        edadLayout = view.findViewById(R.id.edadLayout);
        edadLayout.setTypeface(face);

        dpiLayout = view.findViewById(R.id.dpiLayout);
        dpiLayout.setTypeface(face);

        fechaLayout = view.findViewById(R.id.fechaLayout);
        fechaLayout.setTypeface(face);

        ocupacionLayout = view.findViewById(R.id.ocupacionLayout);
        ocupacionLayout.setTypeface(face);

        telLayout = view.findViewById(R.id.telefonoLayout);
        telLayout.setTypeface(face);

        truePaciente = view.findViewById(R.id.pacienteTrue);
        truePaciente.setTypeface(face);

        falsePaciente = view.findViewById(R.id.pacienteFalse);
        falsePaciente.setTypeface(face);

        tituloEstado = view.findViewById(R.id.tituloEstadoPaciente);
        tituloEstado.setTypeface(face);

        tituloGenero = view.findViewById(R.id.tituloSexo);
        tituloGenero.setTypeface(face);

        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int a = calendar.get(Calendar.YEAR);

        mes++;
        String mesAux = (mes > 9) ? String.valueOf(mes) : "0" + mes;
        String diaAux = (dia > 9) ? String.valueOf(dia) : "0" + dia;

        String dat = diaAux + "/" + mesAux + "/" + a;
        fechap.setText(dat);

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.progressDialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        String mesAux = (month > 9) ? String.valueOf(month) : "0" + month;
                        String diaAux = (dayOfMonth > 9) ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;

                        fechap.setText(diaAux + "/" + mesAux + "/" + year);
                    }
                }, yy, mm, dd);

                datePickerDialog.show();
            }
        });

        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] auxFecha = fechap.getText().toString().split("/");
                Toast.makeText(getContext(), auxFecha[2] + "-" + auxFecha[1] + "-" + auxFecha[0], Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void insertarPaciente(String URL) {
        final String[] id = new String[1];
        //id[0] = obtenerNumPacientes("https://diegosistemas.xyz/DHR/Normal/ficha.php?estado=3");
        //Log.i("Id",id[0]);
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
                //parametros.put("id", id[0]);
                parametros.put("pnombre", primerNombre.getText().toString());
//                parametros.put("snombre", segundoNombre.getText().toString());
//                parametros.put("papellido", primerApellido.getText().toString());
//                parametros.put("sapellido", segundoApellido.getText().toString());
                parametros.put("edad", edad.getText().toString());
                parametros.put("ocupacion", ocupacion.getText().toString());
                parametros.put("sexo", String.valueOf((sexo.isChecked()) ? 1 : 0));
                parametros.put("tel", telefono.getText().toString());
                parametros.put("fecha", fechap.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        //idPacientee = id[0];
    }

    public void obtenerPacientes(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.parseInt(response) > 0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_pac_exis, null);
                    listaPac = viewCuadro.findViewById(R.id.lista_pacientesExis);
                    Toolbar toolbar = viewCuadro.findViewById(R.id.toolbar2);
                    toolbar.setTitle("Pacientes");
                    toolbar.setNavigationIcon(R.drawable.ic_cerrar);

                    builder.setView(viewCuadro);
                    final AlertDialog dialog = builder.create();

                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        consultarPaciente("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=1", dialog);
                    } else {
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face2)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }

                    dialog.show();

                } else {
                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("NO se encontraron coincidencias")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face2)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
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
                parametros.put("pnombre", primerNombre.getText().toString());
//                parametros.put("papellido", primerApellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        //idPacientee = id[0];
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void consultarPaciente(String URL, final AlertDialog dialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    final ArrayList<ItemPaciente> lista = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("idPaciente");
                            Log.i(TAG, "id: " + id);
                            String nom = jsonArray.getJSONObject(i).getString("Nombre");
                            Log.i(TAG, "nombre: " + nom);
                            String contN = jsonArray.getJSONObject(i).getString("Fichas");
                            Log.i(TAG, "contadorN: " + contN);
                            String contE = jsonArray.getJSONObject(i).getString("Fichas2");
                            Log.i(TAG, "contadorE: " + contE);
                            String edad = jsonArray.getJSONObject(i).getString("edad");
                            String fecha = jsonArray.getJSONObject(i).getString("fecha_nac");

                            lista.add(new ItemPaciente(id, nom, contN, contE, edad, fecha));
                        }
                        listaPac.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new PacienteAdapter(lista);
                        listaPac.setLayoutManager(layoutManager);
                        listaPac.setAdapter(adapter);
                        adapter.setOnItemClickListener(new PacienteAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(final int position) {
                                if (Integer.parseInt(lista.get(position).getMcontadorN()) > 0) {
                                    IngDetalle ingDetalle = new IngDetalle();
                                    ingDetalle.obtenerPaciente(Integer.parseInt(lista.get(position).getMid()));

                                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                    transaction.replace(R.id.contenedor, ingDetalle);
                                    transaction.commit();
                                    dialog.dismiss();
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
                parametros.put("pnombre", primerNombre.getText().toString());
//                parametros.put("papellido", primerApellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }
}