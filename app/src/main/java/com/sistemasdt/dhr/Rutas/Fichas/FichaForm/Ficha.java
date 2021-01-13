package com.sistemasdt.dhr.Rutas.Fichas.FichaForm;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class Ficha extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText motivo, medico, referente;
    private TextView fecha, tituloPaciente;
    private ImageButton calendario;
    private FloatingActionButton guardador;
    private int ID_PACIENTE = 0;
    private TextInputLayout motivoLayout, medicoLayout, referenteLayout;
    private TextView paciente;
    ArrayList<String> listaPacientes;
    ArrayList<ItemPaciente> listaPacientesGeneral;

    public Ficha() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ficha, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        //Barra de Titulo
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ficha");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuFichas menuFichas = new MenuFichas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichas);
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
        fecha.setText(dat);

        //Obtener Calendario
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);
                dd--;

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        R.style.progressDialog
                        , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear++;
                        String mesAux = (monthOfYear > 9) ? String.valueOf(monthOfYear) : "0" + monthOfYear;
                        String diaAux = (dayOfMonth > 9) ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;

                        String dat = diaAux + "/" + mesAux + "/" + year;
                        fecha.setText(dat);
                    }
                }, yy, mm, dd);

//                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
//                        android.R.style.Theme_Holo_Dialog_MinWidth
//                        , new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        monthOfYear++;
//                        String mesAux = (monthOfYear > 9) ? String.valueOf(monthOfYear) : "0" + monthOfYear;
//                        String diaAux = (dayOfMonth > 9) ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;
//
//                        String dat = diaAux + "/" + mesAux + "/" + year;
//                        fecha.setText(dat);
//                    }
//                }, yy, mm, dd);

                datePicker.show();
            }
        });

        listaPacientes = new ArrayList<>();
        listaPacientesGeneral = new ArrayList<>();

        obtenerPacientes();

        paciente = view.findViewById(R.id.paciente);
        paciente.setTypeface(face);

        paciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

//                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listaPacientes) {
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
//                        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/bahnschrift.ttf");
//                        TextView textView = view.findViewById(android.R.id.text1);
//                        textView.setTypeface(typeface);
//                        return view;
//                    }
//                };

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

                listView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String filter = adapter.getItem(position).toLowerCase().trim();
                        paciente.setText(adapter.getItem(position));

                        for (ItemPaciente item : listaPacientesGeneral) {
                            if (item.getNombre().toLowerCase().equals(filter)) {
                                ID_PACIENTE = listaPacientesGeneral.get(listaPacientesGeneral.indexOf(item)).getCodigo();
                                String codigoPaciente = String.valueOf(listaPacientesGeneral.get(listaPacientesGeneral.indexOf(item)).getCodigo());
                                Toast.makeText(getContext(), codigoPaciente, Toast.LENGTH_LONG).show();
                            }
                        }

                        dialog.dismiss();
                    }
                });
            }
        });

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Toast.makeText(getContext(), "Hola", Toast.LENGTH_SHORT).show();
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

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        querysPacientes.obtenerPacientes(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
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