package com.example.dentalhistoryrecorder.OpcionIngreso.Especial;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionConsulta.Items;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IngresoVisitas extends Fragment {
    EditText descripcion, fecha;
    Toolbar toolbar;
    ImageButton selectorFecha;
    Button agregar, quitar;
    FloatingActionButton siguiente;
    TextView titulo;
    TableLayout tableLayout;
    private String[] header = {"Fecha", "Descripsion", "Costo"};
    private TablaDinamica tablaDinamica;
    private SharedPreferences preferencias;
    private ArrayList<String[]> rows = new ArrayList<>();
    private int contador = 0;
    private RequestQueue requestQueue;
    private static final String TAG = "MyActivity";

    public IngresoVisitas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingreso_visitas, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Visitas");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Agregar agregar = new Agregar();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, agregar);
                transaction.commit();
            }
        });

        preferencias = getActivity().getSharedPreferences("Terapia", Context.MODE_PRIVATE);

        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        descripcion = view.findViewById(R.id.descEspecial);
        descripcion.setTypeface(face);

        fecha = view.findViewById(R.id.terapia);
        fecha.setText(dd + "/" + mm + "/" + yy);
        fecha.setTypeface(face);

        titulo = view.findViewById(R.id.tituloVisitas);
        titulo.setTypeface(face);

        selectorFecha = view.findViewById(R.id.obtenerFecha);
        agregar = view.findViewById(R.id.agregarVisita);
        agregar.setTypeface(face);
        quitar = view.findViewById(R.id.quitarVisita);
        quitar.setTypeface(face);
        siguiente = view.findViewById(R.id.siguiente);

        tableLayout = view.findViewById(R.id.tablaVisitas);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        selectorFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dat = dayOfMonth + "/" + monthOfYear + "/" + year;
                        fecha.setText(dat);
                    }
                }, yy, mm, dd);

                datePicker.show();
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!descripcion.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty()) {
                    String[] item = new String[]{
                            fecha.getText().toString(),
                            descripcion.getText().toString(),
                            preferencias.getString("costoVisita", "0.00")
                    };
                    tablaDinamica.addItem(item);
                    descripcion.setText(null);
                } else {
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

        quitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tablaDinamica.getCount() > 0) {
                    contador = tablaDinamica.getCount();
                    final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                    d.setCancelable(false);
                    d.setView(dialogView);
                    final AlertDialog alertDialog = d.create();
                    final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    TextView textView = dialogView.findViewById(R.id.titulo_dialogo);
                    textView.setTypeface(face2);

                    Button aceptar = dialogView.findViewById(R.id.aceptar);
                    aceptar.setTypeface(face2);

                    Button cancelar = dialogView.findViewById(R.id.cancelar);
                    cancelar.setTypeface(face2);

                    final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(contador);

                    aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tablaDinamica.removeRow(numberPicker.getValue());
                            alertDialog.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Se Elimino Una Fila")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face2)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    });

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                } else {
                    Alerter.create(getActivity())
                            .setTitle("No Hay Filas En La Tabla")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        agregarVisitas("https://diegosistemas.xyz/DHR/Especial/ingresoE.php?estado=3", tablaDinamica.getCellData(i, 0), tablaDinamica.getCellData(i, 1), tablaDinamica.getCellData(i, 2));
                    }

                    ContratoVisita contratoVisita = new ContratoVisita();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, contratoVisita);
                    transaction.commit();
                } else {
                    Alerter.create(getActivity())
                            .setTitle("No Ingreso Visitas")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void agregarVisitas(String URL, final String fecha, final String desc, final String costo) {
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
                parametros.put("fecha", fecha);
                parametros.put("desc", desc);
                parametros.put("costo", costo);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}
