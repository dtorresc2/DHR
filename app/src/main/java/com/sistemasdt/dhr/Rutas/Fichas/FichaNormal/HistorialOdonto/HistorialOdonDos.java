package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.Rutas.Catalogos.Piezas.ItemPieza;
import com.sistemasdt.dhr.Rutas.Catalogos.Servicios.ItemServicio;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPiezas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysServicios;
import com.tapadoo.alerter.Alerter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class HistorialOdonDos extends Fragment {
    private Toolbar toolbar;
    private Button listador;
    private TableLayout tableLayout;
    private String[] header = {"Pieza", "Descripcion", "Costo"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private TextView pieza, servicio;
    private TextView titulo_diag, titulo_pres, total_costo, titulo_costo;
    private TablaDinamica tablaDinamica;
    private double total;
    private FloatingActionButton agregador;
    private int mOpcion = 0;
    private TextInputEditText desc_servicio, monto;
    private TextInputLayout layoutServicio, layoutMonto;

    ArrayList<String> listaPiezas;
    ArrayList<ItemPieza> listaPiezasGenenal;

    ArrayList<String> listaServicios;
    ArrayList<ItemServicio> listaServiciosGeneral;

    private ArrayList<ItemTratamiento> listaTratamientos = new ArrayList<>();

    int ID_PIEZA = 0;
    int ID_SERVICIO = 0;

    int POSICION = 0;
    boolean modoEdicionTratamiento = false;

    public HistorialOdonDos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_historial_odon_dos, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

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

        listaServicios = new ArrayList<>();
        listaServiciosGeneral = new ArrayList<>();

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
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialogo_busqueda);
                dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.buscador);
                editText.setTypeface(face);

                TextView textView = dialog.findViewById(R.id.tituloDialogo);
                textView.setTypeface(face);
                textView.setText("Seleccione un servicio");

                ListView listView = dialog.findViewById(R.id.lista_items);

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaServicios);
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
                        servicio.setText(adapter.getItem(position));

                        for (ItemServicio item : listaServiciosGeneral) {
                            if (item.getDescripcionServicio().toLowerCase().trim().contains(filter)) {
                                ID_SERVICIO = listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getCodigoServicio();
                                desc_servicio.setText(listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getDescripcionServicio());
                                monto.setText(String.format("%.2f", listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getMontoServicio()));
                            }
                        }

                        dialog.dismiss();
                    }
                });

            }
        });

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
        tablaDinamica.setOnItemClickListener(new TablaDinamica.OnClickListener() {
            @Override
            public void onItemClick(final int position) {
                MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
                menuInferiorDos.show(getFragmentManager(), "MenuInferior");
                menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(position, 1));
                menuInferiorDos.eventoClick(new MenuInferiorDos.MenuInferiorListener() {
                    @Override
                    public void onButtonClicked(int opcion) {
                        int index = position - 1;
                        switch (opcion) {
                            case 1:
                                // Editar Tratamiento
                                modoEdicionTratamiento = true;
                                listador.setText("ACTUALIZAR TRATAMIENTO");

                                ID_PIEZA = listaTratamientos.get(index).getPieza();
                                ID_SERVICIO = listaTratamientos.get(index).getServicio();
                                desc_servicio.setText(listaTratamientos.get(index).getDescripcionServicio());
                                monto.setText(String.format("%.2f", listaTratamientos.get(index).getMonto()));
                                servicio.setText(listaTratamientos.get(index).getDescripcionServicio());
                                pieza.setText(tablaDinamica.getCellData(position, 0));

                                for (ItemServicio item : listaServiciosGeneral) {
                                    if (item.getCodigoServicio() == ID_SERVICIO) {
                                        servicio.setText(listaServiciosGeneral.get(listaServiciosGeneral.indexOf(item)).getDescripcionServicio());
                                    }
                                }

                                POSICION = index;
                                break;
                            case 2:
                                // Eliminar Tratamiento
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                                builder.setIcon(R.drawable.logonuevo);
                                builder.setTitle("Historial Odontodologico");
                                builder.setMessage("¿Desea eliminar el tratamiento?");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tablaDinamica.removeRow(position);
                                        total = 0;
                                        listaTratamientos.remove(position - 1);

                                        if (tablaDinamica.getCount() > 0) {
                                            for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                                total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                                            }
                                            total_costo.setText(String.format("%.2f", total));
                                        } else {
                                            total_costo.setText("0.00");
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                break;
                        }
                    }
                });
            }
        });

        //Proceso para listar
        listador = view.findViewById(R.id.guardador_hm);
        listador.setTypeface(face);
        listador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!descripcionRequerida() || !montoRequerido() || !validarMonto())
                    return;

                if (ID_PIEZA > 0 && ID_SERVICIO > 0) {
                    total = 0;

                    String[] item = new String[]{
                            pieza.getText().toString(),
                            desc_servicio.getText().toString(),
                            monto.getText().toString()
                    };

                    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    if (!modoEdicionTratamiento) {
                        tablaDinamica.addItem(item);
                        listaTratamientos.add(new ItemTratamiento(
                                ID_PIEZA,
                                ID_SERVICIO,
                                desc_servicio.getText().toString(),
                                Double.parseDouble(monto.getText().toString()),
                                date
                        ));
                    } else {
                        if (listaTratamientos.size() > 0) {
                            listaTratamientos.set(POSICION, new ItemTratamiento(
                                    ID_PIEZA,
                                    ID_SERVICIO,
                                    desc_servicio.getText().toString(),
                                    Double.parseDouble(monto.getText().toString()),
                                    date
                            ));

                            // Reinciar Tabla
                            tablaDinamica.removeAll();
                            tablaDinamica.addHeader(header);
//                        tablaDinamica.addData(getClients());
                            tablaDinamica.fondoHeader(R.color.AzulOscuro);

                            for (ItemTratamiento tratamiento : listaTratamientos) {
                                String descPieza = "";

                                for (ItemPieza PIEZA : listaPiezasGenenal) {
                                    if (tratamiento.getPieza() == PIEZA.getCodigoPieza()) {
                                        descPieza = PIEZA.getNombrePieza();
                                    }
                                }

                                tablaDinamica.addItem(new String[]{
                                        descPieza,
                                        tratamiento.getDescripcionServicio(),
                                        String.format("%.2f", tratamiento.getMonto())
                                });
                            }

                            modoEdicionTratamiento = false;
                            POSICION = 0;
                            listador.setText("AGREGAR TRATAMIENTO");
                        }

                        ID_SERVICIO = 0;
                        desc_servicio.setText(null);
                        monto.setText(null);
                        servicio.setText("Seleccione Servicio");

                        layoutMonto.setError(null);
                        layoutServicio.setError(null);

                        if (tablaDinamica.getCount() > 0) {
                            for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                            }
                            total_costo.setText(String.format("%.2f", total));
                        }
                    }
                } else {
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Hay campos vacios o incorrectos")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        // Proceso para guardar
        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listaTratamientos.size() > 0) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("HOD2", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Set<String> set = new HashSet<>();

                    for (ItemTratamiento item : listaTratamientos) {
                        String cadena = item.getPieza() + ";" + item.getServicio() + ";" + item.getDescripcionServicio() + ";" + item.getMonto() + ";" + item.getFechaRegistro() + ";";
                        set.add(cadena);
                    }

                    editor.putStringSet("listaTratamientos", set);
                    editor.apply();

                    final SharedPreferences preferenciasFicha2 = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor2 = preferenciasFicha2.edit();
                    escritor2.putString("NO_TRATAMIENTOS", String.valueOf(listaTratamientos.size()));
                    escritor2.putString("NO_PAGOS", String.valueOf(0));
                    escritor2.commit();

                    HistorialFotografico historialFotografico = new HistorialFotografico();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, historialFotografico);
                    transaction.commit();

                } else {
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("No ha agregado tratamientos")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        obtenerPiezas();
        obtenerServicios();
//        cargarTratamientos();

        return view;
    }

    private ArrayList<String[]> getClients() {

        return rows;
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public void cargarTratamientos() {
        SharedPreferences preferences = getActivity().getSharedPreferences("HOD2", Context.MODE_PRIVATE);
        Set<String> set = preferences.getStringSet("listaTratamientos", null);

        // Reinciar Tabla
        listaTratamientos.clear();
        tablaDinamica.removeAll();
//        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        if (set != null) {
            ArrayList<String> listaAuxiliar = new ArrayList<>(set);

            for (String item : listaAuxiliar) {
                String cadenaAuxiliar[] = item.split(";");

                listaTratamientos.add(new ItemTratamiento(
                        Integer.parseInt(cadenaAuxiliar[0]),
                        Integer.parseInt(cadenaAuxiliar[1]),
                        cadenaAuxiliar[2],
                        Double.parseDouble(cadenaAuxiliar[3]),
                        cadenaAuxiliar[4]
                ));

                String descPieza = "";
                for (ItemPieza aux : listaPiezasGenenal) {
                    if (aux.getCodigoPieza() == Integer.parseInt(cadenaAuxiliar[0])) {
                        descPieza = aux.getNombrePieza();
                    }
                }

                tablaDinamica.addItem(new String[]{
                        descPieza,
                        cadenaAuxiliar[2],
                        String.format("%.2f", Double.parseDouble(cadenaAuxiliar[3]))
                });
            }

            total = 0;

            if (tablaDinamica.getCount() > 0) {
                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                    total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                }
                total_costo.setText(String.format("%.2f", total));
            } else {
                total_costo.setText("0.00");
            }
        }
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

                    cargarTratamientos();

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

    private void obtenerServicios() {
        listaServicios.clear();
        listaServiciosGeneral.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.obtenerListadoServicios(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getInt("ESTADO") == 1) {
                            ItemServicio itemServicio = new ItemServicio(
                                    jsonArray.getJSONObject(i).getInt("ID_SERVICIO"),
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    jsonArray.getJSONObject(i).getDouble("MONTO"),
                                    (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                            );

                            listaServicios.add(itemServicio.getDescripcionServicio());
                            listaServiciosGeneral.add(itemServicio);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerServicios();
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