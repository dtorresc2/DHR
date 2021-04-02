package com.sistemasdt.dhr.Rutas.Fichas.HistorialOdonto;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.sistemasdt.dhr.Rutas.Catalogos.Piezas.ItemPieza;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.OpcionSeguimiento.SegPagos;
import com.sistemasdt.dhr.OpcionSeguimiento.Seguimiento;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPacientes;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPiezas;
import com.tapadoo.alerter.Alerter;

import android.support.design.widget.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HistorialOdonDos extends Fragment {
    private Toolbar toolbar;
    ArrayList<String> listaDatos1, piezas;
    Button listador, eliminador;
    Spinner spinner;
    CheckBox dolor, gingivitis;
    ArrayAdapter<String> adaptadorSpinner;
    TableLayout tableLayout;
    RequestQueue requestQueue;
    private String[] header = {"Pieza", "Descripcion", "Costo"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private EditText tratamiento, costo, otros, desc_dolor;
    private TextView pieza, servicio;
    private TextView titulo_detalle, titulo_diag, titulo_pres, titulo_piez, total_costo, titulo_costo, celdap, celdat, celdac;
    private TablaDinamica tablaDinamica;
    private int lim;
    private double total;
    private FloatingActionButton agregador;
    private static final String TAG = "MyActivity";
    private int mOpcion = 0;
    private SharedPreferences preferencias;
    private int contador = 0;
    private ImageButton selectorPieza;

    private TextInputEditText desc_servicio, monto;
    private TextInputLayout layoutServicio, layoutMonto;

    ArrayList<String> listaPiezas;
    ArrayList<ItemPieza> listaPiezasGenenal;

    int ID_PIEZA = 0;
    int ID_SERVICIO = 0;

    public HistorialOdonDos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_historial_odon_dos, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        requestQueue = Volley.newRequestQueue(getContext());

        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);

        //Encabezado
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Odontodologico (2/2)");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistorialOdon historialOdon = new HistorialOdon();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                fragmentTransaction.replace(R.id.contenedor, historialOdon);
                fragmentTransaction.commit();
            }
        });

        desc_servicio = view.findViewById(R.id.desc_servicio);
        desc_servicio.setTypeface(face);
        desc_servicio.addTextChangedListener(new TextWatcher() {
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

        monto = view.findViewById(R.id.monto);
        monto.setTypeface(face);
        monto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (montoRequerido()) {
                    validarMonto();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        layoutServicio = view.findViewById(R.id.layoutServicio);
        layoutServicio.setTypeface(face);

        layoutMonto = view.findViewById(R.id.layoutMonto);
        layoutMonto.setTypeface(face);

        listaPiezas = new ArrayList<>();
        listaPiezasGenenal = new ArrayList<>();

        pieza = view.findViewById(R.id.pieza);
        pieza.setTypeface(face);
        pieza.setOnClickListener(new View.OnClickListener() {
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
                textView.setText("Seleccione una pieza");

                ListView listView = dialog.findViewById(R.id.lista_items);

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaPiezas);
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

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String filter = adapter.getItem(position).toLowerCase().trim();
                        pieza.setText(adapter.getItem(position));

                        for (ItemPieza item : listaPiezasGenenal) {
                            if (item.getNombrePieza().toLowerCase().trim().contains(filter)) {
                                ID_PIEZA = listaPiezasGenenal.get(listaPiezasGenenal.indexOf(item)).getCodigoPieza();
                            }
                        }

                        dialog.dismiss();
                    }
                });

            }
        });

        servicio = view.findViewById(R.id.servicio);
        servicio.setTypeface(face);
        servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        listador = view.findViewById(R.id.guardador_hm);
//        listador.setTypeface(face);

//        eliminador = view.findViewById(R.id.eliminador);
//        eliminador.setTypeface(face);

//        selectorPieza = view.findViewById(R.id.selectorPieza);
//        selectorPieza.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
//                final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
//                d.setCancelable(false);
//                d.setView(dialogView);
//                final AlertDialog alertDialog = d.create();
//
//                TextView textView = dialogView.findViewById(R.id.titulo_dialogo);
//                textView.setTypeface(face2);
//
//                Button aceptar = dialogView.findViewById(R.id.aceptar);
//                aceptar.setTypeface(face2);
//
//                Button cancelar = dialogView.findViewById(R.id.cancelar);
//                cancelar.setTypeface(face2);
//
//                final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
//                numberPicker.setMinValue(1);
//                numberPicker.setMaxValue(32);
//
//                aceptar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pieza.setText(String.valueOf(numberPicker.getValue()));
//                        alertDialog.dismiss();
//                    }
//                });
//
//                cancelar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//
//                alertDialog.show();
//            }
//        });

        tableLayout = view.findViewById(R.id.table);

        titulo_diag = view.findViewById(R.id.titulo_diagnostico);
        titulo_diag.setTypeface(face);
        titulo_pres = view.findViewById(R.id.titulo_presupuesto);
        titulo_pres.setTypeface(face);

        total_costo = view.findViewById(R.id.tota_costo);
        total_costo.setTypeface(face);
        titulo_costo = view.findViewById(R.id.titulo_costo);
        titulo_costo.setTypeface(face);
        agregador = view.findViewById(R.id.guardador_hd2);

        //Instancias
        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        final SharedPreferences.Editor escritor = preferencias.edit();

        //Proceso para listar
//        listador.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!tratamiento.getText().toString().isEmpty() && !costo.getText().toString().isEmpty()) {
//                    total = 0;
//                    String[] item = new String[]{
//                            //spinner.getSelectedItem().toString(),
//                            pieza.getText().toString(),
//                            tratamiento.getText().toString(),
//                            costo.getText().toString()
//                    };
//                    tablaDinamica.addItem(item);
//                    tratamiento.setText(null);
//                    costo.setText(null);
//
//                    if (tablaDinamica.getCount() > 0) {
//                        for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
//                            total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
//                        }
//                        total_costo.setText(String.format("%.2f", total));
//                    }
//                }
//                else {
//                    Alerter.create(getActivity())
//                            .setTitle("Error")
//                            .setText("Hay campos vacios")
//                            .setIcon(R.drawable.logonuevo)
//                            .setTextTypeface(face)
//                            .enableSwipeToDismiss()
//                            .setBackgroundColorRes(R.color.AzulOscuro)
//                            .show();
//                }
//            }
//        });

        //Proceso para eliminar
//        eliminador.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
//                if (tablaDinamica.getCount() > 0) {
//                    contador = tablaDinamica.getCount();
//                    final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
//                    LayoutInflater inflater = getActivity().getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
//                    d.setCancelable(false);
//                    d.setView(dialogView);
//                    final AlertDialog alertDialog = d.create();
//
//                    TextView textView = dialogView.findViewById(R.id.titulo_dialogo);
//                    textView.setTypeface(face2);
//
//                    Button aceptar = dialogView.findViewById(R.id.aceptar);
//                    aceptar.setTypeface(face2);
//
//                    Button cancelar = dialogView.findViewById(R.id.cancelar);
//                    cancelar.setTypeface(face2);
//
//                    final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
//                    numberPicker.setMinValue(1);
//                    numberPicker.setMaxValue(contador);
//
//                    aceptar.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            tablaDinamica.removeRow(numberPicker.getValue());
//                            alertDialog.dismiss();
//                            Alerter.create(getActivity())
//                                    .setTitle("Se Elimino Una Fila")
//                                    .setIcon(R.drawable.logonuevo)
//                                    .setTextTypeface(face2)
//                                    .enableSwipeToDismiss()
//                                    .setBackgroundColorRes(R.color.AzulOscuro)
//                                    .show();
//                        }
//                    });
//
//                    cancelar.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            alertDialog.dismiss();
//                        }
//                    });
//
//                    alertDialog.show();
//                } else {
//                    Alerter.create(getActivity())
//                            .setTitle("No Hay Filas En La Tabla")
//                            .setIcon(R.drawable.logonuevo)
//                            .setTextTypeface(face2)
//                            .enableSwipeToDismiss()
//                            .setBackgroundColorRes(R.color.AzulOscuro)
//                            .show();
//                }
//            }
//        });

        // Proceso para guardar
        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!descripcionRequerida() || !montoRequerido() || !validarMonto())
                    return;
            }
        });

        String[] item = new String[]{
                "Muela Izq",
                "Limpieza",
                "20.00"
        };
        tablaDinamica.addItem(item);

        obtenerPiezas();
        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public void obtenerPiezas() {
        listaPiezas.clear();
        listaPiezasGenenal.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        querysPiezas.obtenerListadoPiezas(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getInt("ESTADO") == 1) {
                            ItemPieza pieza = new ItemPieza(
                                    jsonArray.getJSONObject(i).getInt("ID_PIEZA"),
                                    jsonArray.getJSONObject(i).getInt("NUMERO"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE"),
                                    (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                            );

                            listaPiezas.add(pieza.getNombrePieza());
                            listaPiezasGenenal.add(pieza);
                        }
                    }

                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerPiezas();
            }
        });

    }

    //    VALIDACIONES
    private boolean descripcionRequerida() {
        String texto = desc_servicio.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutServicio.setError("Campo Requerido");
            return false;
        } else {
            layoutServicio.setError(null);
            return true;
        }
    }

    private boolean montoRequerido() {
        String texto = monto.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutMonto.setError("Campo Requerido");
            return false;
        } else {
            layoutMonto.setError(null);
            return true;
        }
    }

    private boolean validarMonto() {
        String texto = monto.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutMonto.setError(null);
            return true;
        } else {
            layoutMonto.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }

}