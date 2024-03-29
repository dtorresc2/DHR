package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialMedico.HistorialMed;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
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

public class Ficha extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText motivo, medico, referente;
    private TextView fecha, tituloPaciente;
    private ImageButton calendario;
    private FloatingActionButton guardador;
    private int ID_PACIENTE = 0;
    private TextInputLayout motivoLayout, medicoLayout, referenteLayout, fechaLayout;
    private TextView paciente;
    ArrayList<String> listaPacientes;
    ArrayList<ItemPaciente> listaPacientesGeneral;
    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;

    ActivityResultLauncher<String> lanzadorPermisos;

    public Ficha() {
        // Required empty public constructor
        MODO_EDICION = false;

        // INICIALIZADOR DE ACTIVIDAD PARA PERMISOS
        lanzadorPermisos = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {

                    }
                });
    }

    public void activarModoEdicion(int id) {
        MODO_EDICION = true;
        ID_FICHA = id;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ficha, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

//        lanzadorPermisos.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        lanzadorPermisos.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        //Barra de Titulo
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ficha");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                MenuFichas menuFichas = new MenuFichas();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichas);
                transaction.commit();
            } else {
                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichaNormal);
                transaction.commit();
            }
        });

        //Detalle de la ficha
        fecha = view.findViewById(R.id.fecha);
        fecha.setTypeface(face);
        motivo = view.findViewById(R.id.motivo);
        motivo.setTypeface(face);
        medico = view.findViewById(R.id.medico);
        medico.setTypeface(face);
        referente = view.findViewById(R.id.referente);
        referente.setTypeface(face);
        calendario = view.findViewById(R.id.obFecha);
        guardador = view.findViewById(R.id.guardador_dt);

        motivoLayout = view.findViewById(R.id.layoutMotivo);
        motivoLayout.setTypeface(face);

        medicoLayout = view.findViewById(R.id.layoutMedico);
        medicoLayout.setTypeface(face);

        referenteLayout = view.findViewById(R.id.layoutReferente);
        referenteLayout.setTypeface(face);

        fechaLayout = view.findViewById(R.id.fechaLayout);
        fechaLayout.setTypeface(face);

        tituloPaciente = view.findViewById(R.id.tituloPaciente);
        tituloPaciente.setTypeface(face);

        motivo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                motivoRequerido();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int a = calendar.get(Calendar.YEAR);
        mes++;
        dia--;
        String mesAux = (mes > 9) ? String.valueOf(mes) : "0" + mes;
        String diaAux = (dia > 9) ? String.valueOf(dia) : "0" + dia;

        final String dat = diaAux + "/" + mesAux + "/" + a;

        if (!MODO_EDICION)
            fecha.setText(dat);

        //Obtener Calendario
        calendario.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int yy = calendario.get(Calendar.YEAR);
            int mm = calendario.get(Calendar.MONTH);
            int dd = calendario.get(Calendar.DAY_OF_MONTH);
            dd--;

            DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                    R.style.progressDialog
                    , (view12, year, monthOfYear, dayOfMonth) -> {
                monthOfYear++;
                String mesAux1 = (monthOfYear > 9) ? String.valueOf(monthOfYear) : "0" + monthOfYear;
                String diaAux1 = (dayOfMonth > 9) ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;

                String dat1 = diaAux1 + "/" + mesAux1 + "/" + year;
                fecha.setText(dat1);
            }, yy, mm, dd);


            datePicker.show();
        });

        listaPacientes = new ArrayList<>();
        listaPacientesGeneral = new ArrayList<>();

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
            if (!motivoRequerido())
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

            if (!MODO_EDICION) {
                final SharedPreferences preferenciasFicha = getActivity().getSharedPreferences("FICHA", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = preferenciasFicha.edit();
                escritor.putString("ID_PACIENTE", String.valueOf(ID_PACIENTE));
                escritor.putString("PACIENTE", paciente.getText().toString());
                escritor.putString("FECHA", fecha.getText().toString());
                escritor.putString("MEDICO", medico.getText().toString());
                escritor.putString("MOTIVO", motivo.getText().toString());
                escritor.putString("REFERENTE", referente.getText().toString());
                escritor.commit();

                final SharedPreferences preferenciasFicha2 = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor2 = preferenciasFicha2.edit();
                escritor2.putString("PACIENTE", paciente.getText().toString());
                escritor2.commit();

                HistorialMed historialMed = new HistorialMed();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, historialMed);
                transaction.commit();
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                progressDialog.setMessage("Cargando...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                try {
                    Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(fecha.getText().toString());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaMYSQL = formatter.format(initDate);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ID_PACIENTE", ID_PACIENTE);
                    jsonObject.put("FECHA", fechaMYSQL);
                    jsonObject.put("MEDICO", medico.getText().toString());
                    jsonObject.put("MOTIVO", motivo.getText().toString());
                    jsonObject.put("REFERENTE", referente.getText().toString());

                    QuerysFichas querysFichas = new QuerysFichas(getContext());
                    querysFichas.actualizarFicha(ID_FICHA, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            progressDialog.dismiss();

                            Alerter.create(getActivity())
                                    .setTitle("Ficha")
                                    .setText("Actualizada correctamente")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.FondoSecundario)
                                    .show();

                            FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                            funcionesBitacora.registrarBitacora("ACTUALIZACION", "FICHA NORMAL", "Se actualizo la ficha #" + ID_FICHA);

                            MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction.replace(R.id.contenedor, menuFichaNormal);
                            transaction.commit();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressDialog.dismiss();

                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo al actualizar la ficha")
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

    public void obtenerPacientes() {
        listaPacientes.clear();
        listaPacientesGeneral.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_PACIENTE", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        querysPacientes.obtenerPacientes(jsonObject, new QuerysPacientes.VolleyOnEventListener() {
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
                                    (jsonArray.getJSONObject(i).getInt("SEXO") > 0) ? true : false,
                                    jsonArray.getJSONObject(i).getInt("FICHAS_NORMALES"),
                                    jsonArray.getJSONObject(i).getInt("CITAS")
                                    );

                            listaPacientes.add(paciente.getNombre());
                            listaPacientesGeneral.add(paciente);
                        }
                    }

                    cargarDatos();

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

    public void cargarDatos() {
        if (!MODO_EDICION) {
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FICHA", Context.MODE_PRIVATE);
            ID_PACIENTE = Integer.parseInt(sharedPreferences.getString("ID_PACIENTE", "0"));
            paciente.setText(sharedPreferences.getString("PACIENTE", "Seleccione paciente"));
            if (sharedPreferences.contains("FECHA")) {
                fecha.setText(sharedPreferences.getString("FECHA", "-"));
            }
            medico.setText(sharedPreferences.getString("MEDICO", ""));
            motivo.setText(sharedPreferences.getString("MOTIVO", ""));
            referente.setText(sharedPreferences.getString("REFERENTE", ""));
        } else {
            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.obtenerFichaEspecifica(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject(object.toString());
                        fecha.setText(jsonObject.getString("FECHA"));
                        medico.setText(jsonObject.getString("MEDICO"));
                        motivo.setText(jsonObject.getString("MOTIVO"));
                        referente.setText(jsonObject.getString("REFERENTE"));
                        ID_PACIENTE = jsonObject.getInt("ID_PACIENTE");

                        for (ItemPaciente item : listaPacientesGeneral) {
                            if (item.getCodigo() == ID_PACIENTE) {
                                paciente.setText(listaPacientesGeneral.get(listaPacientesGeneral.indexOf(item)).getNombre());
                            }
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    cargarDatos();
                }
            });
        }
    }

    // VALIDACIONES
    private boolean motivoRequerido() {
        String texto = motivo.getText().toString().trim();
        if (texto.isEmpty()) {
            motivoLayout.setError("Campo requerido");
            return false;
        } else {
            motivoLayout.setError(null);
            return true;
        }
    }
}