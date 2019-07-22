package com.example.dentalhistoryrecorder.OpcionCitas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionCitas.Adaptador.AdaptadorCita;
import com.example.dentalhistoryrecorder.OpcionConsulta.Items;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Adaptadores.AdaptadorConsulta;
import com.example.dentalhistoryrecorder.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Citas extends Fragment {
    Toolbar toolbar;
    TextView fecha, hora, realizado, pendiente;
    ImageView BtnFecha, BtnHora;
    private FloatingActionButton agregar, buscar;
    public static final String TAG = "example_dialog";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    int mes = c.get(Calendar.MONTH) + 1;
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    final int horas = c.get(Calendar.HOUR);
    final int minutos = c.get(Calendar.MINUTE);
    final int meridiano = c.get(Calendar.AM_PM);
    private String ampm;

    private RecyclerView lista_pacientes;
    private AdaptadorCita adapter;
    private RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;

    public Citas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_citas, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Citas");

        fecha = view.findViewById(R.id.etiquetaFecha);
        fecha.setTypeface(face);
        hora = view.findViewById(R.id.etiquetaHora);
        hora.setTypeface(face);
        realizado = view.findViewById(R.id.etiquetaR);
        realizado.setTypeface(face);
        pendiente = view.findViewById(R.id.etiquetaP);
        pendiente.setTypeface(face);

        if (meridiano == 0) {
            ampm = " AM";
        } else {
            ampm = " PM";
        }

        String mesFormateado = (mes < 10) ? "0" + mes : String.valueOf(mes);
        String minutoFormateado = (minutos < 10) ? "0" + minutos : String.valueOf(minutos);

        fecha.setText(dia + "/" + mesFormateado + "/" + anio);
        hora.setText(horas + ":" + minutoFormateado + ampm);

        lista_pacientes = view.findViewById(R.id.listaCitas);

        agregar = view.findViewById(R.id.crearCita);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarCita agregarCita = new AgregarCita();
                AgregarCita.display(getFragmentManager());
            }
        });

        buscar = view.findViewById(R.id.consultarCita);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String tiempo = "12:00 AM";
                String[] parts = tiempo.split(":");
                String part1 = parts[0]; // 123
                String part2 = parts[1]; // 654321

                String[] partes = part2.split(" ");

                String tiempo2 = "12:00 AM";
                String[] parts2 = tiempo.split(":");
                String part3 = parts2[0]; // 123
                String part4 = parts2[1]; // 654321

                String[] partes2 = part2.split(" ");
                Toast.makeText(getActivity(), part1 + " " + partes[0], Toast.LENGTH_LONG).show();*/
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
                obtenerCitas("https://diegosistemas.xyz/DHR/Citas/consultarCita.php?estado=1");
            }
        });

        BtnFecha = view.findViewById(R.id.iconoFecha);
        BtnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        });

        BtnHora = view.findViewById(R.id.iconoHora);
        BtnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });

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
        obtenerCitas("https://diegosistemas.xyz/DHR/Citas/consultarCita.php?estado=1");

        return view;
    }

    private void obtenerHora() {
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Formateo el hora obtenido: antepone el 0 si son menores de 10
                        //String horaFormateada =  (hourOfDay < 10)? "0" + hourOfDay : String.valueOf(hourOfDay);
                        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                        String minutoFormateado = (minute < 10) ? "0" + minute : String.valueOf(minute);
                        //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                        String AM_PM;
                        if (hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            hourOfDay -= 12;
                        }
                        //Muestro la hora con el formato deseado
                        hora.setText(hourOfDay + ":" + minutoFormateado + " " + AM_PM);
                    }
                    //Estos valores deben ir en ese orden
                    //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
                    //Pero el sistema devuelve la hora en formato 24 horas
                }, horas, minutos, false);

        recogerHora.show();
    }

    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10) ? "0" + mesActual : String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        fecha.setText(diaFormateado + "/" + mesFormateado + "/" + year);


                    }
                    //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                    /**
                     *También puede cargar los valores que usted desee
                     */
                }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void consultarCitas(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    final ArrayList<ItemCita> lista = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String fecha = jsonArray.getJSONObject(i).getString("fecha");
                            String hora = jsonArray.getJSONObject(i).getString("hora");
                            String nombre = jsonArray.getJSONObject(i).getString("nombre");
                            String descripsion = jsonArray.getJSONObject(i).getString("descripsion");
                            String realizado = jsonArray.getJSONObject(i).getString("realizado");
                            lista.add(new ItemCita(hora, fecha, nombre, descripsion, realizado));
                        }
                        lista_pacientes.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorCita(lista);
                        lista_pacientes.setLayoutManager(layoutManager);
                        lista_pacientes.setAdapter(adapter);
                        adapter.setOnItemClickListener(new AdaptadorCita.OnItemClickListener() {
                            @Override
                            public void onItemClick(final int position) {
                                Toast.makeText(getContext(), "" + position, Toast.LENGTH_LONG).show();
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
                parametros.put("fecha", fecha.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void obtenerCitas(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.parseInt(response) > 0) {
                    consultarCitas("https://diegosistemas.xyz/DHR/Citas/consultarCita.php?estado=2");
                } else {
                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("No hay citas programadas")
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
                parametros.put("fecha", fecha.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

}
