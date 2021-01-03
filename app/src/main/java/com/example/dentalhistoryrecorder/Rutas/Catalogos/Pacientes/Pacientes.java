package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionConsulta.Items;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Adaptadores.AdaptadorConsulta;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.OpcionIngreso.Normal.IngDetalle;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Pacientes extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText primerNombre, segundoNombre, primerApellido, segundoApellido;
    private TextInputEditText edad, telefono, ocupacion, fechap;
    private RadioButton sexo, sexof;
    private Switch existente;
    private TextView etiqueta;
    private String dato1, dato2, dato3, dato4, dato5, dato6, dato7, dato8;
    private ArrayList<String> lista1 = new ArrayList<String>();
    private final Bundle bundle = new Bundle();
    private FloatingActionButton agregador, buscador;
    private EditText prua;
    private static final String TAG = "MyActivity";
    private ImageButton fecha;
    RequestQueue requestQueue;
    private RecyclerView listaPac;
    private AdaptadorConsulta adapter;
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
        //Barra de Titulo
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Datos Personales");

        //Datos Personales
//        existente = (Switch) view.findViewById(R.id.existente);
//        existente.setTypeface(face);
        primerNombre = view.findViewById(R.id.primerNombre);
        primerNombre.setTypeface(face);
//        segundoNombre = view.findViewById(R.id.segundoNombre);
//        segundoNombre.setTypeface(face);
//        primerApellido = view.findViewById(R.id.primerApellido);
//        primerApellido.setTypeface(face);
//        segundoApellido = view.findViewById(R.id.segundoApellido);
//        segundoApellido.setTypeface(face);
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
        agregador = view.findViewById(R.id.agregar_dp);
        fecha = view.findViewById(R.id.fecha_dp);
        fechap = view.findViewById(R.id.fecha_persona);
        fechap.setTypeface(face);
        buscador = view.findViewById(R.id.buscar_dp);

        Calendar calendar = Calendar.getInstance();
        final int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int a = calendar.get(Calendar.YEAR);

        String dat = dia + "/" + mes + "/" + a;
        fechap.setText(dat);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "ATRAS", Toast.LENGTH_SHORT).show();
                Catalogos catalogos = new Catalogos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, catalogos);
                transaction.commit();
            }
        });

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dat = dayOfMonth + "/" + monthOfYear
                                + "/" + year;
                        fechap.setText(dat);
                    }
                }, yy, mm, dd);

                datePicker.show();
            }
        });

        //Persistencia de Datos
        /*SharedPreferences preferencias = getActivity().getSharedPreferences("datospersonales", Context.MODE_PRIVATE);
        primerNombre.setText(preferencias.getString("pnombre", " "));
        segundoNombre.setText(preferencias.getString("snombre", " "));
        primerApellido.setText(preferencias.getString("papellido", " "));
        segundoApellido.setText(preferencias.getString("sapellido", " "));
        telefono.setText(preferencias.getString("telefono", " "));
        ocupacion.setText(preferencias.getString("ocupacion", " "));
        sexo.setChecked(preferencias.getBoolean("sexo", true));
        edad.setText(String.valueOf(preferencias.getInt("edad", 0)));*/

        //Guardador de datos
        //final SharedPreferences.Editor escritor = preferencias.edit();

        //Guardando datos temporalmente
        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!existente.isChecked()) {
                    //Toast.makeText(getActivity(), "Ingresado Correctamente", Toast.LENGTH_SHORT).show();
                    /*escritor.putString("pnombre", primerNombre.getText().toString());
                    escritor.putString("snombre", segundoNombre.getText().toString());
                    escritor.putString("papellido", primerApellido.getText().toString());
                    escritor.putString("sapellido", segundoApellido.getText().toString());
                    escritor.putString("telefono", telefono.getText().toString());
                    escritor.putString("ocupacion", ocupacion.getText().toString());
                    escritor.putInt("edad", Integer.parseInt(edad.getText().toString()));
                    escritor.putBoolean("sexo", sexo.isChecked());
                    escritor.commit();*/

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

                    boolean validado = false;
                    if (!primerNombre.getText().toString().isEmpty()) {
                        validado = true;
                        if (!segundoNombre.getText().toString().isEmpty()) {
                            validado = true;
                            if (!primerApellido.getText().toString().isEmpty()) {
                                validado = true;
                                if (!segundoApellido.getText().toString().isEmpty()) {
                                    validado = true;

                                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    if (networkInfo != null && networkInfo.isConnected()) {
                                        insertarPaciente("http://dhr.sistemasdt.xyz/Normal/ficha.php?estado=1");

                                        IngDetalle ingDetalle = new IngDetalle();
                                        ingDetalle.obtenerPaciente(0);
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                                        transaction.replace(R.id.contenedor, ingDetalle);
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
                            }
                        }
                    }

                    if (validado == false) {
                        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle("Faltan Campos")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face2)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }
                }
            }
        });

        existente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existente.isChecked()) {
                    segundoNombre.setEnabled(false);
                    segundoApellido.setEnabled(false);
                    edad.setEnabled(false);
                    telefono.setEnabled(false);
                    ocupacion.setEnabled(false);
                    sexo.setEnabled(false);
                    sexof.setEnabled(false);

                } else {
                    primerApellido.setText(null);
                    primerNombre.setText(null);
                    segundoNombre.setEnabled(true);
                    segundoApellido.setEnabled(true);
                    edad.setEnabled(true);
                    telefono.setEnabled(true);
                    ocupacion.setEnabled(true);
                    sexo.setEnabled(true);
                    sexof.setEnabled(true);
                }
            }
        });

        buscador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!primerNombre.getText().toString().isEmpty() && !primerApellido.getText().toString().isEmpty() && existente.isChecked()) {
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
                    }, 3000);

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        obtenerPacientes("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=8");
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
                parametros.put("snombre", segundoNombre.getText().toString());
                parametros.put("papellido", primerApellido.getText().toString());
                parametros.put("sapellido", segundoApellido.getText().toString());
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
                parametros.put("papellido", primerApellido.getText().toString());
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
                    final ArrayList<Items> lista = new ArrayList<>();
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

                            lista.add(new Items(id, nom, contN, contE, edad, fecha));
                        }
                        listaPac.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorConsulta(lista);
                        listaPac.setLayoutManager(layoutManager);
                        listaPac.setAdapter(adapter);
                        adapter.setOnItemClickListener(new AdaptadorConsulta.OnItemClickListener() {
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
                parametros.put("papellido", primerApellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }
}