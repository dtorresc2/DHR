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
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        toolbar.setTitle("Pacientes");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoPacientes listadoPacientes = new ListadoPacientes();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoPacientes);
                transaction.commit();
            }
        });

        primerNombre = view.findViewById(R.id.primerNombre);
        primerNombre.setTypeface(face);
        primerNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nombreRequerido()) {
                    validarNombre();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edad = view.findViewById(R.id.edad);
        edad.setTypeface(face);
        edad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edadRequerida()) {
                    validarEdad();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        telefono = view.findViewById(R.id.telefono);
        telefono.setTypeface(face);
        telefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                telefonoRequerido();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ocupacion = view.findViewById(R.id.ocupacion);
        ocupacion.setTypeface(face);

        sexo = view.findViewById(R.id.masculino);
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
        dia--;
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
                dd--;

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
                if (!nombreRequerido() || !telefonoRequerido() || !edadRequerida() || !validarNombre() || !validarEdad())
                    return;

                String[] auxFecha = fechap.getText().toString().split("/");
                Toast.makeText(getContext(), auxFecha[2] + "-" + auxFecha[1] + "-" + auxFecha[0], Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    // VALIDACIONES
    private boolean nombreRequerido() {
        String texto = primerNombre.getText().toString().trim();
        if (texto.isEmpty()) {
            nombreLayout.setError("Campo requerido");
            return false;
        } else {
            nombreLayout.setError(null);
            return true;
        }
    }

    private boolean telefonoRequerido() {
        String texto = telefono.getText().toString().trim();
        if (texto.isEmpty()) {
            telLayout.setError("Campo requerido");
            return false;
        } else {
            telLayout.setError(null);
            return true;
        }
    }

    private boolean edadRequerida() {
        String texto = edad.getText().toString().trim();
        if (texto.isEmpty()) {
            edadLayout.setError("Campo requerido");
            return false;
        } else {
            edadLayout.setError(null);
            return true;
        }
    }

    private boolean validarNombre() {
        String texto = primerNombre.getText().toString().trim();
        Pattern patron = Pattern.compile("^[A-Z][a-z]+([' ']?[A-Z][a-z]+){0,3}$");
        if (patron.matcher(texto).matches()) {
            nombreLayout.setError(null);
            return true;
        } else {
            nombreLayout.setError("Nombre invalido");
            return false;
        }
    }

    private boolean validarEdad() {
        String texto = edad.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]{1,3}$");
        if (patron.matcher(texto).matches()) {
            edadLayout.setError(null);
            return true;
        } else {
            edadLayout.setError("Edad invalida");
            return false;
        }
    }
}