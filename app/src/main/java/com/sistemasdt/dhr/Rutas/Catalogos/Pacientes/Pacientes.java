package com.sistemasdt.dhr.Rutas.Catalogos.Pacientes;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

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

import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
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
        modoEdicion = false;
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

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        if (!modoEdicion)
            toolbar.setTitle("Paciente Nuevo");
        else
            toolbar.setTitle("Paciente #" + ID_PACIENTE);

        toolbar.setNavigationOnClickListener(v -> {
            ListadoPacientes listadoPacientes = new ListadoPacientes();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, listadoPacientes);
            transaction.commit();
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
                    if (validarNombre()) {
                        validarUsuarioRepetido();
                    }
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

        fecha.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int yy = calendario.get(Calendar.YEAR);
            int mm = calendario.get(Calendar.MONTH);
            int dd = calendario.get(Calendar.DAY_OF_MONTH);
            dd--;

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.progressDialog, (view1, year, month, dayOfMonth) -> {
                month++;
                String mesAux1 = (month > 9) ? String.valueOf(month) : "0" + month;
                String diaAux1 = (dayOfMonth > 9) ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;

                fechap.setText(diaAux1 + "/" + mesAux1 + "/" + year);
            }, yy, mm, dd);

            datePickerDialog.show();
        });

        if (modoEdicion) {
            obtenerPaciente();
        }

        agregador.setOnClickListener(v -> {
            if (!nombreRequerido() || !telefonoRequerido() || !edadRequerida() || !validarNombre() || !validarEdad() || !validarUsuarioRepetido())
                return;

            if (!modoEdicion)
                registrarPaciente();
            else
                actualizarPaciente();
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
            jsonBody.put("SEXO", (sexo.isChecked() == true) ? 1 : 0);
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

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("CREACION", "PACIENTES", "Se registro un paciente");

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    ListadoPacientes listadoPacientes = new ListadoPacientes();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, listadoPacientes);
                    transaction.commit();
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPaciente() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        JSONObject jsonBody = new JSONObject();
        String[] auxFecha = fechap.getText().toString().split("/");
        String fechaBD = auxFecha[2] + "/" + auxFecha[1] + "/" + auxFecha[0];

        try {
            jsonBody.put("NOMBRE", primerNombre.getText().toString().trim());
            jsonBody.put("EDAD", edad.getText().toString().trim());
            jsonBody.put("OCUPACION", ocupacion.getText().toString().trim());
            jsonBody.put("SEXO", (sexo.isChecked() == true) ? 1 : 0);
            jsonBody.put("ESTADO", (truePaciente.isChecked() == true) ? 1 : 0);
            jsonBody.put("TELEFONO", telefono.getText().toString().trim());
            jsonBody.put("FECHA_NACIMIENTO", fechaBD);
            jsonBody.put("DPI", dpi.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysPacientes.actualizarPaciente(ID_PACIENTE, jsonBody, new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Paciente actualizado")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("ACTUALIZACION", "PACIENTES", "Se actualizo el paciente #" + ID_PACIENTE);

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    ListadoPacientes listadoPacientes = new ListadoPacientes();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, listadoPacientes);
                    transaction.commit();
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerPaciente() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_PACIENTE", ID_PACIENTE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        querysPacientes.obtenerPacientes(jsonObject, new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    primerNombre.setText(jsonObject.getString("NOMBRE"));
                    edad.setText(jsonObject.getString("EDAD"));
                    ocupacion.setText(jsonObject.getString("OCUPACION"));
                    telefono.setText(jsonObject.getString("TELEFONO"));
                    fechap.setText(jsonObject.getString("FECHA_NACIMIENTO"));
                    dpi.setText(jsonObject.getString("DPI"));
                    boolean habilitado = ((jsonObject.getInt("SEXO")) > 0 ? true : false);
                    sexo.setChecked(habilitado);
                    sexof.setChecked(!habilitado);

                    habilitado = ((jsonObject.getInt("ESTADO")) > 0 ? true : false);
                    truePaciente.setChecked(habilitado);
                    falsePaciente.setChecked(!habilitado);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.i("SERVICIO", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Log.i("SERVICIO", e.toString());
                e.printStackTrace();
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

    private boolean validarUsuarioRepetido() {
        String texto = primerNombre.getText().toString().trim();
        ArrayList<ItemPaciente> listaConsultada = new ArrayList<>();

        for (ItemPaciente item : mListadoPacientes) {
            if (item.getNombre().equals(texto) && item.getCodigo() != ID_PACIENTE) {
                listaConsultada.add(item);
            }
        }

        if (listaConsultada.size() == 0) {
            nombreLayout.setError(null);
            return true;
        } else {
            nombreLayout.setError("Paciente duplicado");
            return false;
        }
    }
}