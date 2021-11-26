package com.sistemasdt.dhr.Rutas.Citas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.sistemasdt.dhr.Rutas.Citas.Adaptador.AdaptadorCita;
import com.sistemasdt.dhr.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.ServiciosAPI.QuerysCitas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ListadoCitas extends Fragment {
    Toolbar toolbar;
    TextView fecha, hora, realizado, pendiente;
    ImageView BtnFecha, BtnHora;
    private FloatingActionButton agregar, buscar;
    public static final String TAG = "example_dialog";
    private CheckBox ckbRealizado, ckbPendiente, ckbHora;

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

    private boolean boolRealizado = true;
    private boolean boolPendiente = true;

    public ListadoCitas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_citas, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Citas Disponibles");
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.opcion_nuevo:
                        Citas citas = new Citas();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, citas);
                        transaction.commit();
                        return true;

                    case R.id.opcion_filtrar:
                        MenuItem searchItem = item;
                        SearchView searchView = (SearchView) searchItem.getActionView();

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                adapter.getFilter().filter(newText);
                                return false;
                            }
                        });
                        return true;

                    case R.id.opcion_actualizar:
                        obtenerCitas();
                        return true;

                    default:
                        return false;
                }
            }
        });

        lista_pacientes = view.findViewById(R.id.listaCitas);

//        agregar = view.findViewById(R.id.crearCita);
//        agregar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AgregarCita agregarCita = new AgregarCita();
//                AgregarCita.display(getActivity().getSupportFragmentManager());
//            }
//        });
//
//        buscar = view.findViewById(R.id.consultarCita);
//        buscar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        obtenerCitas();

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

    public void obtenerCitas() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_CITA", 0);

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.obtenerListadoCitas(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONArray jsonArray = new JSONArray(object.toString());
                        final ArrayList<ItemCita> lista = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            lista.add(new ItemCita(
                                    jsonArray.getJSONObject(i).getString("ID_CITA"),
                                    jsonArray.getJSONObject(i).getString("FECHA"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE_PACIENTE"),
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    (jsonArray.getJSONObject(i).getInt("REALIZADO") > 0) ? true : false
                            ));
                        }
                        lista_pacientes.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorCita(lista);
                        lista_pacientes.setLayoutManager(layoutManager);
                        lista_pacientes.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    obtenerCitas();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}