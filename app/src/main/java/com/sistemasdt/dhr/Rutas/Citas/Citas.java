package com.sistemasdt.dhr.Rutas.Citas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysCitas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Citas extends Fragment {
    private boolean MODO_EDICION = false;
    private int ID_CITA = 0;
    private int ID_PACIENTE = 0;

    private ImageView BtnFecha, BtnHora;
    private TextView fecha, hora, descripcion, paciente;
    private TextView tituloPaciente, tituloFecha, tituloHora;
    private Toolbar toolbar;
    private TextInputLayout layoutDescripcion;
    private FloatingActionButton guardador;

    ArrayList<String> listaPacientes;
    ArrayList<ItemPaciente> listaPacientesGeneral;

    //Calendario para obtener fecha & hora
    public final Calendar calendar = Calendar.getInstance();

    //Variables para obtener la fecha
//    int mes = c.get(Calendar.MONTH);
//    final int dia = c.get(Calendar.DAY_OF_MONTH);
//    final int anio = c.get(Calendar.YEAR);
//
//    final int horas = c.get(Calendar.HOUR);
//    final int minutos = c.get(Calendar.MINUTE);
//    final int meridiano = c.get(Calendar.AM_PM);


    private String ampm;

    public Citas() {
        MODO_EDICION = false;
    }

    public void activarModoEdicion(int id) {
        MODO_EDICION = true;
        ID_CITA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_citas, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        //Barra de Titulo
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Cita Nueva");
        toolbar.setNavigationIcon(R.drawable.ic_atras);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoCitas listadoCitas = new ListadoCitas();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, listadoCitas);
                transaction.commit();
            }
        });

        fecha = view.findViewById(R.id.fecha);
        fecha.setTypeface(face);
        hora = view.findViewById(R.id.hora);
        hora.setTypeface(face);

        layoutDescripcion = view.findViewById(R.id.layoutDescripcionCita);
        layoutDescripcion.setTypeface(face);
        descripcion = view.findViewById(R.id.descripcion);
        descripcion.setTypeface(face);

        guardador = view.findViewById(R.id.guardadorCita);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        fecha.setText(formatoFecha.format(c.getTime()));
        hora.setText(formatoHora.format(c.getTime()));

        listaPacientes = new ArrayList<>();
        listaPacientesGeneral = new ArrayList<>();

        BtnFecha = view.findViewById(R.id.obtenerFecha);
        BtnFecha.setOnClickListener(v -> {
            obtenerFecha();
        });

        BtnHora = view.findViewById(R.id.obtenerHora);
        BtnHora.setOnClickListener(v -> {
            obtenerHora();
        });

        obtenerPacientes();

        paciente = view.findViewById(R.id.paciente);
        paciente.setTypeface(face);

        paciente.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialogo_busqueda);
            dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            EditText editText = dialog.findViewById(R.id.buscador);
            editText.setTypeface(face);

            TextView textView = dialog.findViewById(R.id.tituloDialogo);
            textView.setTypeface(face);

            ListView listView = dialog.findViewById(R.id.lista_items);

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaPacientes);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                String filter = adapter.getItem(position).toLowerCase().trim();
                paciente.setText(adapter.getItem(position));

                for (ItemPaciente item : listaPacientesGeneral) {
                    if (item.getNombre().toLowerCase().trim().contains(filter)) {
                        ID_PACIENTE = listaPacientesGeneral.get(listaPacientesGeneral.indexOf(item)).getCodigo();
                    }
                }

                dialog.dismiss();
            });
        });

        guardador.setOnClickListener(v -> {
            if (!textoRequerido())
                return;

            if (ID_PACIENTE == 0) {
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("No ha seleccionado un paciente")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
                return;
            }
            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            if (!MODO_EDICION) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                progressDialog.setMessage("Cargando...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                try {
                    Date initDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fecha.getText().toString() + " " + hora.getText().toString());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String fechaMYSQL = formatter.format(initDate);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ID_PACIENTE", ID_PACIENTE);
                    jsonObject.put("FECHA", fechaMYSQL);
                    jsonObject.put("DESCRIPCION", descripcion.getText().toString());
                    jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
                    jsonObject.put("REALIZADO", 0);

                    QuerysCitas querysCitas = new QuerysCitas(getContext());
                    querysCitas.registrarCita(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            Alerter.create(getActivity())
                                    .setTitle("Citas")
                                    .setText("Cita registrada correctamente")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.FondoSecundario)
                                    .show();

                            progressDialog.dismiss();

                            ListadoCitas listadoCitas = new ListadoCitas();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                            transaction.replace(R.id.contenedor, listadoCitas);
                            transaction.commit();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressDialog.dismiss();

                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo al registrar la cita")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    });

                } catch (ParseException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void obtenerHora() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog,
                (view, hourOfDay, minute) -> {
                    String minutoFormateado = (minute < 10) ? "0" + minute : String.valueOf(minute);
                    hora.setText(hourOfDay + ":" + minutoFormateado);
                }, calendar.get(Calendar.HOUR) + 12, calendar.get(Calendar.MINUTE), true);

        recogerHora.show();
    }

    private void obtenerFecha() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog,
                (view, year, month, dayOfMonth) -> {
                    final int mesActual = month + 1;
                    String diaFormateado = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10) ? "0" + mesActual : String.valueOf(mesActual);
                    fecha.setText(diaFormateado + "/" + mesFormateado + "/" + year);
                }, calendar.get(Calendar.YEAR) - 1, calendar.get(Calendar.MONTH) + 2, calendar.get(Calendar.DAY_OF_YEAR));

        recogerFecha.show();
    }

    public void obtenerPacientes() {
        listaPacientes.clear();
        listaPacientesGeneral.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        querysPacientes.obtenerPacientes(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getInt("ESTADO") == 1) {
                            ItemPaciente paciente = new ItemPaciente(
                                    jsonArray.getJSONObject(i).getInt("ID_PACIENTE"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE"),
                                    jsonArray.getJSONObject(i).getString("TELEFONO"),
                                    jsonArray.getJSONObject(i).getString("DPI"),
                                    jsonArray.getJSONObject(i).getInt("EDAD"),
                                    jsonArray.getJSONObject(i).getString("FECHA_NACIMIENTO"),
                                    (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false,
                                    jsonArray.getJSONObject(i).getDouble("DEBE"),
                                    jsonArray.getJSONObject(i).getDouble("HABER"),
                                    jsonArray.getJSONObject(i).getDouble("SALDO"),
                                    jsonArray.getJSONObject(i).getString("OCUPACION"),
                                    (jsonArray.getJSONObject(i).getInt("SEXO") > 0) ? true : false
                            );

                            listaPacientes.add(paciente.getNombre());
                            listaPacientesGeneral.add(paciente);
                        }
                    }

//                    cargarDatos();

                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerPacientes();
            }
        });
    }

    // VALIDACIONES
    private boolean textoRequerido() {
        String texto = descripcion.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutDescripcion.setError("Campo requerido");
            return false;
        } else {
            layoutDescripcion.setError(null);
            return true;
        }
    }

}