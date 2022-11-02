package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.PacienteAdapter;
import com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Items.ItemPagoEvaluacion;
import com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Items.ItemVisita;
import com.tapadoo.alerter.Alerter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class PagosVisitas extends Fragment {
    private TextInputEditText descripcion, fecha, pago;
    private TextInputLayout layoutDescripcion, layoutPagos;

    private Toolbar toolbar;
    private Button agregar;
    private FloatingActionButton siguiente;
    private TextView titulo, totalGasto, tituloAbono, totalAbono;

    private TableLayout tableLayout;
    private String[] header = {"Fecha", "Descripción", "Pago"};
    private TablaDinamica tablaDinamica;
    private ArrayList<String[]> rows = new ArrayList<>();
    private ArrayList<ItemPagoEvaluacion> listaPagosEvaluacion = new ArrayList<>();

    boolean modoEdicionTratamiento = false;

    int POSICION = 0;
    private double TOTAL_VISITA = 0.00;
    private SharedPreferences preferencias;
    private double total;
    private boolean MODO_EDICION = false;

    public PagosVisitas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingreso_pagos_visitas, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("PAGO DE VISITAS");
        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                Visitas visitas = new Visitas();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, visitas);
                transaction.commit();
            } else {
                ListadoEvaluaciones listadoEvaluaciones = new ListadoEvaluaciones();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, listadoEvaluaciones);
                transaction.commit();
            }
        });

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        preferencias = getActivity().getSharedPreferences("VISITAS", Context.MODE_PRIVATE);

        layoutDescripcion = view.findViewById(R.id.layoutDescripcion);
        descripcion = view.findViewById(R.id.descripcion);
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

        layoutPagos = view.findViewById(R.id.layoutAbono);
        pago = view.findViewById(R.id.abono);
        pago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (montoRequerido())
                    validarMonto();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        titulo = view.findViewById(R.id.tituloAbono);

        agregar = view.findViewById(R.id.agregarAbono);
        siguiente = view.findViewById(R.id.siguiente);

        tableLayout = view.findViewById(R.id.tablaPagos);
        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);
        tablaDinamica.setOnItemClickListener(position -> {
            MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
            menuInferiorDos.show(getActivity().getSupportFragmentManager(), "MenuInferior");
            if (tablaDinamica.getCount() > 1)
                menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(position, 1));
            else
                menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(1, 1));

            menuInferiorDos.eventoClick(opcion -> {
                int index = position - 1;
                switch (opcion) {
                    case 1:
                        // Editar Pago
                        modoEdicionTratamiento = true;
                        agregar.setText("ACTUALIZAR PAGO");
                        ItemPagoEvaluacion aux = listaPagosEvaluacion.get(index);

                        descripcion.setText(aux.getDescripcion());
                        pago.setText(String.format("%.2f", aux.getPago()));

                        POSICION = index;
                        break;

                    case 2:
                        // Eliminar Pago
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                        builder.setIcon(R.drawable.logonuevo);
                        builder.setTitle("Pagos de Evaluación");
                        builder.setMessage("¿Desea eliminar el pago?");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                            tablaDinamica.removeRow(position);
                            total = 0;
                            listaPagosEvaluacion.remove(position - 1);

                            if (tablaDinamica.getCount() > 0) {
                                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                    total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                                }
                                totalAbono.setText(String.format("%.2f", total));
                            } else {
                                totalAbono.setText("0.00");
                            }
                        });

                        builder.setNegativeButton("Cancelar", (dialog, which) -> {

                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
            });

        });

        tituloAbono = view.findViewById(R.id.tituloAbono);
        totalAbono = view.findViewById(R.id.totalAbono);

        totalGasto = view.findViewById(R.id.totalVisita);
        totalGasto.setText(String.format("%.2f", Double.parseDouble(preferencias.getString("TOTAL_VISITAS", "0.00"))));

        agregar.setOnClickListener(v -> {
            if (!descripcionRequerida() || !montoRequerido() || !validarMonto())
                return;

            total = 0;

            String fechaOriginal = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Date initDate;
            String fechaFormateada = null;
            try {
                initDate = new SimpleDateFormat("yyyy-MM-dd").parse(fechaOriginal);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                fechaFormateada = formatter.format(initDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String[] item = new String[]{
                    fechaFormateada,
                    descripcion.getText().toString(),
                    pago.getText().toString()
            };


            TOTAL_VISITA = Double.parseDouble(preferencias.getString("TOTAL_VISITAS", "0.00"));

            if (tablaDinamica.getCount() > 0) {
                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                    total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                }
            }

            total += Double.parseDouble(pago.getText().toString());

            if (total <= TOTAL_VISITA) {
                if (!modoEdicionTratamiento) {
                    tablaDinamica.addItem(item);
                    listaPagosEvaluacion.add(new ItemPagoEvaluacion(
                            fechaFormateada,
                            descripcion.getText().toString(),
                            Double.parseDouble(pago.getText().toString())
                    ));
                } else {
                    if (listaPagosEvaluacion.size() > 0) {
                        listaPagosEvaluacion.set(POSICION, new ItemPagoEvaluacion(
                                fechaFormateada,
                                descripcion.getText().toString(),
                                Double.parseDouble(pago.getText().toString())
                        ));

                        // Reinciar Tabla
                        tablaDinamica.removeAll();
                        tablaDinamica.addHeader(header);
                        tablaDinamica.fondoHeader(R.color.AzulOscuro);

                        for (ItemPagoEvaluacion itemPagoEvaluacion : listaPagosEvaluacion) {
                            tablaDinamica.addItem(new String[]{
                                    fechaFormateada,
                                    itemPagoEvaluacion.getDescripcion(),
                                    String.format("%.2f", itemPagoEvaluacion.getPago())
                            });
                        }
                    }

                    modoEdicionTratamiento = false;
                    POSICION = 0;
                    agregar.setText("AGREGAR PAGO");
                }

                descripcion.setText(null);
                layoutDescripcion.setError(null);
                pago.setText(null);
                layoutPagos.setError(null);

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                    }
                }
                totalAbono.setText(String.format("%.2f", total));
            } else {
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("El pago es mayor a la deuda")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
            }
        });

        siguiente.setOnClickListener(v -> {
        });

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    public void ObtenerOpcion(int opcion) {
    }

    //    VALIDACIONES
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

    private boolean montoRequerido() {
        String texto = pago.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutPagos.setError("Campo Requerido");
            return false;
        } else {
            layoutPagos.setError(null);
            return true;
        }
    }

    private boolean validarMonto() {
        String texto = pago.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutPagos.setError(null);
            return true;
        } else {
            layoutPagos.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }

}
