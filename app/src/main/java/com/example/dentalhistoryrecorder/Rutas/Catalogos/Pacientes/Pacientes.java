package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas.ItemCuenta;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Pacientes extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText primerNombre, edad, telefono, ocupacion, fechap, dpi;
    private TextInputLayout nombreLayout, edadLayout, fechaLayout, dpiLayout, ocupacionLayout, telLayout;
    private RadioButton sexo, sexof, truePaciente, falsePaciente;
    private TextView tituloEstado, tituloGenero;
    private FloatingActionButton agregador;
    private ImageButton fecha;
    private Typeface face;
    private ArrayList<ItemPaciente> mListadoPacientes;
    private boolean modoEdicion;
    private int ID_PACIENTE = 0;

    public Pacientes() {
        modoEdicion = true;
    }

    public void enviarPacientes(ArrayList<ItemPaciente> listadoPacientes) {
        mListadoPacientes = listadoPacientes;
    }

    public void editarPaciente(int id) {
        modoEdicion = true;
        ID_PACIENTE = id;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pacientes, container, false);
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        if (!modoEdicion)
            toolbar.setTitle("Paciente Nuevo");
        else
            toolbar.setTitle("Paciente #" + ID_PACIENTE);

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

                registrarPaciente();
            }
        });
        return view;
    }

    private void registrarPaciente() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        JSONObject jsonBody = new JSONObject();
        String[] auxFecha = fechap.getText().toString().split("/");
        String fechaBD = auxFecha[2] + "/" + auxFecha[1] + "/" + auxFecha[0];

        try {
            jsonBody.put("NOMBRE", primerNombre.getText().toString().trim());
            jsonBody.put("EDAD", edad.getText().toString().trim());
            jsonBody.put("OCUPACION", ocupacion.getText().toString().trim());
            jsonBody.put("SEXO", (truePaciente.isChecked() == true) ? 1 : 0);
            jsonBody.put("TELEFONO", telefono.getText().toString().trim());
            jsonBody.put("FECHA_NACIMIENTO", fechaBD);
            jsonBody.put("DPI", dpi.getText().toString().trim());
            jsonBody.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysPacientes.registrarPaciente(jsonBody, new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Paciente registrado")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ListadoPacientes listadoPacientes = new ListadoPacientes();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, listadoPacientes);
                        transaction.commit();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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