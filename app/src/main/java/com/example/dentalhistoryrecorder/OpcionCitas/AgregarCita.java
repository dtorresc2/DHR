package com.example.dentalhistoryrecorder.OpcionCitas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes.PacienteAdapter;
import com.example.dentalhistoryrecorder.R;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AgregarCita extends DialogFragment {
    private Toolbar toolbar;
    public static final String TAG = "example_dialog";
    ImageView BtnFecha, BtnHora;
    TextView fecha, hora, paciente;
    private EditText pnombre, papellido, descripcion;
    private FloatingActionButton buscar;

    private RecyclerView lista_pacientes;
    private PacienteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    final int horas = c.get(Calendar.HOUR);
    final int minutos = c.get(Calendar.MINUTE);
    final int meridiano = c.get(Calendar.AM_PM);
    private String ampm;
    private String id;

    public AgregarCita(){

    }

    public static AgregarCita display(FragmentManager fragmentManager) {
        AgregarCita agregarCita = new AgregarCita();
        agregarCita.show(fragmentManager, TAG);
        return agregarCita;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_citasnuevas, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Cita Nueva");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        toolbar.inflateMenu(R.menu.opciones_toolbarcitas);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_aceptar:
                        if (!paciente.getText().toString().isEmpty() && !descripcion.getText().toString().isEmpty()){
                            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                            progressDialog.setMessage("Cargando...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    //insertarCitas("https://diegosistemas.xyz/DHR/Citas/agregarCitas.php?estado=1");
                                    progressDialog.dismiss();
                                    dismiss();
                                }
                            }, 1000);

                            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                            if (networkInfo != null && networkInfo.isConnected()) {
                                insertarCitas("http://dhr.sistemasdt.xyz/Citas/agregarCita.php?estado=1");
                            }
                            else{
                                final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                                Alerter.create(getActivity())
                                        .setTitle("Error")
                                        .setText("Fallo en Conexion a Internet")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face3)
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

                        return true;

                    default:
                        return false;
                }
            }
        });

        fecha = view.findViewById(R.id.fecha);
        fecha.setTypeface(face);
        hora = view.findViewById(R.id.hora);
        hora.setTypeface(face);

        if (meridiano == 0){
            ampm = " AM";
        }
        else {
            ampm = " PM";
        }

        mes++;
        String mesFormateado = (mes < 10)? "0" + mes :String.valueOf(mes);
        String minutoFormateado = (minutos < 10)? "0" + minutos :String.valueOf(minutos);

        fecha.setText(dia + "/" + mesFormateado + "/" + anio);
        hora.setText(horas + ":" + minutoFormateado + ampm );

        pnombre = view.findViewById(R.id.pnom_bus);
        pnombre.setTypeface(face);
        papellido = view.findViewById(R.id.pape_bus);
        papellido.setTypeface(face);
//        paciente = view.findViewById(R.id.paciente);
//        paciente.setTypeface(face);
        descripcion = view.findViewById(R.id.descripcion);
        descripcion.setTypeface(face);

        buscar = view.findViewById(R.id.consultador);

        lista_pacientes = view.findViewById(R.id.lista_pacientes);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consultarPaciente("http://192.168.56.1/DHR/IngresoN/consultaficha.php?db=u578331993_clinc&user=root");
                //consultarPaciente("https://diegosistemas.xyz/DHR/Normal/consultaficha.php?estado=1");
                if (!pnombre.getText().toString().isEmpty() && !papellido.getText().toString().isEmpty()) {
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
                        obtenerPacientes("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=8");
                    }
                    else{
                        final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face3)
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

        BtnFecha = view.findViewById(R.id.obtenerFecha);
        BtnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        });

        BtnHora = view.findViewById(R.id.obtenerHora);
        BtnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });

        return view;
    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Formateo el hora obtenido: antepone el 0 si son menores de 10
                        //String horaFormateada =  (hourOfDay < 10)? "0" + hourOfDay : String.valueOf(hourOfDay);
                        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                        String minutoFormateado = (minute < 10)? "0" + minute :String.valueOf(minute);
                        //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                        String AM_PM;
                        if(hourOfDay < 12) {
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

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10)? "0" + dayOfMonth :String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10)? "0" + mesActual :String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        fecha.setText(diaFormateado + "/" + mesFormateado + "/" + year);
                    }
                    //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                    /**
                     *También puede cargar los valores que usted desee
                     */
                },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void consultarPaciente(String URL) {
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

//                            lista.add(new ItemPaciente(id, nom, contN, contE, edad, fecha));
                        }
                        lista_pacientes.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new PacienteAdapter(lista);
                        lista_pacientes.setLayoutManager(layoutManager);
                        lista_pacientes.setAdapter(adapter);
                        pnombre.setText(null);
                        papellido.setText(null);
                        adapter.setOnItemClickListener(new PacienteAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(final int position) {
//                                paciente.setText(lista.get(position).getMnombre());
//                                id = lista.get(position).getMid();
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
                parametros.put("pnombre", pnombre.getText().toString());
                parametros.put("papellido", papellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void obtenerPacientes(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.parseInt(response) > 0) {

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        consultarPaciente("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=1");
                    }
                    else{
                        final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face3)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }

                } else {
                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("NO se encontraron coincidencias")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face2)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                    pnombre.setText(null);
                    papellido.setText(null);
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
                parametros.put("pnombre", pnombre.getText().toString());
                parametros.put("papellido", papellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void insertarCitas(String URL) {
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
                parametros.put("desc", descripcion.getText().toString());
                parametros.put("fecha", fecha.getText().toString());
                parametros.put("hora", hora.getText().toString());
                parametros.put("id", id);
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }
}
