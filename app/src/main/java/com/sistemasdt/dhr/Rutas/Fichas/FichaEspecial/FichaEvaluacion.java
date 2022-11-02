package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class FichaEvaluacion extends Fragment {
    private TextInputEditText enganche, costo, terapia, descripcion;
    private TextInputLayout layoutEnganche, layoutCosto, layoutTerapia, layoutDescripcion;
    private FloatingActionButton agregar;
    private Toolbar toolbar;

    private TextView paciente;
    private ArrayList<String> listaPacientes;
    private ArrayList<ItemPaciente> listaPacientesGeneral;
    private int ID_PACIENTE = 0;
    private boolean MODO_EDICION = false;

    public FichaEvaluacion() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ficha_evaluacion, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Evaluacion");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(v -> {
            ListadoEvaluaciones listadoEvaluaciones = new ListadoEvaluaciones();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
            transaction.replace(R.id.contenedor, listadoEvaluaciones);
            transaction.commit();
        });

        layoutEnganche = view.findViewById(R.id.layoutEnganche);
        layoutEnganche.setTypeface(face);

        layoutCosto = view.findViewById(R.id.layoutCostoVisita);
        layoutCosto.setTypeface(face);

        layoutTerapia = view.findViewById(R.id.layoutTerapia);
        layoutTerapia.setTypeface(face);

        layoutDescripcion = view.findViewById(R.id.layoutDescripcionEspecial);
        layoutDescripcion.setTypeface(face);

        enganche = view.findViewById(R.id.enganche);
        enganche.setTypeface(face);
        enganche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (engancheRequerido())
                    validarEnganche();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        costo = view.findViewById(R.id.costoVisita);
        costo.setTypeface(face);
        costo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (costoRequerido())
                    validarCosto();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        terapia = view.findViewById(R.id.terapia);
        terapia.setTypeface(face);
        terapia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (terapiaRequerida())
                    validarTerapia();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        descripcion = view.findViewById(R.id.descripcionEspecial);
        descripcion.setTypeface(face);
        descripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descripcionRequerida();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        agregar = view.findViewById(R.id.guardador_hm);

        listaPacientes = new ArrayList<>();
        listaPacientesGeneral = new ArrayList<>();

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

        agregar.setOnClickListener(v -> {
            if (!descripcionRequerida() || !engancheRequerido() || !validarEnganche() || !costoRequerido() ||
                    !validarCosto() || !terapiaRequerida() || !validarTerapia())
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
                final SharedPreferences preferenciasFicha = getActivity().getSharedPreferences("EVALUACION", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = preferenciasFicha.edit();
                escritor.putString("ID_PACIENTE", String.valueOf(ID_PACIENTE));
                escritor.putString("PACIENTE", paciente.getText().toString());
                escritor.putString("DESCRIPCION", descripcion.getText().toString());
                escritor.putString("ENGANCHE", enganche.getText().toString());
                escritor.putString("COSTO_VISITA", costo.getText().toString());
                escritor.putString("TERAPIA", terapia.getText().toString());
                escritor.commit();

                final SharedPreferences preferenciasFicha2 = getActivity().getSharedPreferences("RESUMEN_EVALUACION", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor2 = preferenciasFicha2.edit();
                escritor2.putString("PACIENTE", paciente.getText().toString());
                escritor2.commit();

                Contrato contrato = new Contrato();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, contrato);
                transaction.commit();
            }
        });

        obtenerPacientes();

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

    private boolean descripcionRequerida() {
        String texto = descripcion.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutDescripcion.setError("Campo Requerido");
            return false;
        } else {
            layoutDescripcion.setError(null);
            return true;
        }
    }

    private boolean engancheRequerido() {
        String texto = enganche.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutEnganche.setError("Campo Requerido");
            return false;
        } else {
            layoutEnganche.setError(null);
            return true;
        }
    }

    private boolean validarEnganche() {
        String texto = enganche.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutEnganche.setError(null);
            return true;
        } else {
            layoutEnganche.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }

    private boolean costoRequerido() {
        String texto = costo.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutCosto.setError("Campo Requerido");
            return false;
        } else {
            layoutCosto.setError(null);
            return true;
        }
    }

    private boolean validarCosto() {
        String texto = costo.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutCosto.setError(null);
            return true;
        } else {
            layoutCosto.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }

    private boolean terapiaRequerida() {
        String texto = terapia.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutTerapia.setError("Campo Requerido");
            return false;
        } else {
            layoutTerapia.setError(null);
            return true;
        }
    }

    private boolean validarTerapia() {
        String texto = terapia.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutTerapia.setError(null);
            return true;
        } else {
            layoutTerapia.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }

    public void cargarDatos() {
        if (!MODO_EDICION) {
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EVALUACION", Context.MODE_PRIVATE);
            ID_PACIENTE = Integer.parseInt(sharedPreferences.getString("ID_PACIENTE", "0"));
            paciente.setText(sharedPreferences.getString("PACIENTE", "Seleccione paciente"));
//            if (sharedPreferences.contains("FECHA")) {
//                fecha.setText(sharedPreferences.getString("FECHA", "-"));
//            }
            descripcion.setText(sharedPreferences.getString("DESCRIPCION", ""));
            enganche.setText(sharedPreferences.getString("ENGANCHE", ""));
            costo.setText(sharedPreferences.getString("COSTO_VISITA", ""));
            terapia.setText(sharedPreferences.getString("TERAPIA", ""));
        } else {
//            QuerysFichas querysFichas = new QuerysFichas(getContext());
//            querysFichas.obtenerFichaEspecifica(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
//                @Override
//                public void onSuccess(Object object) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(object.toString());
//                        fecha.setText(jsonObject.getString("FECHA"));
//                        medico.setText(jsonObject.getString("MEDICO"));
//                        motivo.setText(jsonObject.getString("MOTIVO"));
//                        referente.setText(jsonObject.getString("REFERENTE"));
//                        ID_PACIENTE = jsonObject.getInt("ID_PACIENTE");
//
//                        for (ItemPaciente item : listaPacientesGeneral) {
//                            if (item.getCodigo() == ID_PACIENTE) {
//                                paciente.setText(listaPacientesGeneral.get(listaPacientesGeneral.indexOf(item)).getNombre());
//                            }
//                        }
//
//                    } catch (JSONException e) {
//                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    cargarDatos();
//                }
//            });
        }
    }
}
