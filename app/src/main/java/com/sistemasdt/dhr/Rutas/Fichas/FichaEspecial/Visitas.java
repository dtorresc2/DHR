package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

public class Visitas extends Fragment {
    private TextInputEditText descripcion, fecha, costo_visita;
    private TextInputLayout layoutDescripcion, layoutCostoVisita;

    private Toolbar toolbar;
    private ImageButton selectorFecha;
    private Button agregar;
    private FloatingActionButton siguiente;
    private TextView titulo, tituloGasto, totalGasto;

    private double COSTO_VISITA = 0;
    private double TERAPIA_COMPLETA = 0;

    private TableLayout tableLayout;
    private String[] header = {"Fecha", "Descripcion", "Costo"};
    private TablaDinamica tablaDinamica;
    private ArrayList<String[]> rows = new ArrayList<>();

    private double total;

    private boolean MODO_EDICION = false;

    public Visitas() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_visitas, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Visitas");
        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                Evaluacion evaluacion = new Evaluacion();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, evaluacion);
                transaction.commit();
            } else {
                ListadoEvaluaciones listadoEvaluaciones = new ListadoEvaluaciones();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, listadoEvaluaciones);
                transaction.commit();
            }
        });

        descripcion = view.findViewById(R.id.descEspecial);
        titulo = view.findViewById(R.id.tituloVisitas);
        selectorFecha = view.findViewById(R.id.obtenerFecha);
        agregar = view.findViewById(R.id.agregarVisita);
        siguiente = view.findViewById(R.id.siguiente);

        costo_visita = view.findViewById(R.id.visitaIndividual);
        layoutCostoVisita = view.findViewById(R.id.layoutVisitaInd);

        if (!MODO_EDICION) {
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EVALUACION", Context.MODE_PRIVATE);
            COSTO_VISITA = Double.parseDouble(sharedPreferences.getString("COSTO_VISITA", "0"));
            TERAPIA_COMPLETA = Double.parseDouble(sharedPreferences.getString("TERAPIA", "0"));

            costo_visita.setText(String.format("%.2f", COSTO_VISITA));
        }

        tableLayout = view.findViewById(R.id.tablaVisitas);
        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        tituloGasto = view.findViewById(R.id.tituloGasto);
        totalGasto = view.findViewById(R.id.totalGasto);
        totalGasto.setTypeface(face);

        agregar.setOnClickListener(v -> {
//            if (!descripcion.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty()) {
//                total = 0;
//                String[] item = new String[]{
//                        fecha.getText().toString(),
//                        descripcion.getText().toString(),
//                        "0.00"
//                };
//                tablaDinamica.addItem(item);
//                descripcion.setText(null);
//
//                if (tablaDinamica.getCount() > 0) {
//                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
//                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
//                    }
//                    totalGasto.setText(String.format("%.2f", total));
//                }
//
//
//            } else {
//                Alerter.create(getActivity())
//                        .setTitle("Hay Campos Vacios")
//                        .setIcon(R.drawable.logonuevo)
//                        .setTextTypeface(face)
//                        .enableSwipeToDismiss()
//                        .setBackgroundColorRes(R.color.AzulOscuro)
//                        .show();
//            }
        });

//        quitar.setOnClickListener(v -> {
//            if (tablaDinamica.getCount() > 0) {
//                contador = tablaDinamica.getCount();
//                final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
//                LayoutInflater inflater1 = getActivity().getLayoutInflater();
//                View dialogView = inflater1.inflate(R.layout.number_picker_dialog, null);
//                d.setCancelable(false);
//                d.setView(dialogView);
//                final AlertDialog alertDialog = d.create();
//                final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
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
//                numberPicker.setMaxValue(contador);
//
//                aceptar.setOnClickListener(v1 -> {
//                    tablaDinamica.removeRow(numberPicker.getValue());
//                    alertDialog.dismiss();
//                    Alerter.create(getActivity())
//                            .setTitle("Se Elimino Una Fila")
//                            .setIcon(R.drawable.logonuevo)
//                            .setTextTypeface(face2)
//                            .enableSwipeToDismiss()
//                            .setBackgroundColorRes(R.color.AzulOscuro)
//                            .show();
//                });
//
//                cancelar.setOnClickListener(v12 -> alertDialog.dismiss());
//
//                alertDialog.show();
//            } else {
//                Alerter.create(getActivity())
//                        .setTitle("No Hay Filas En La Tabla")
//                        .setIcon(R.drawable.logonuevo)
//                        .setTextTypeface(face)
//                        .enableSwipeToDismiss()
//                        .setBackgroundColorRes(R.color.AzulOscuro)
//                        .show();
//            }
//        });

        siguiente.setOnClickListener(v -> {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.dismiss(), 1000);

            if (tablaDinamica.getCount() > 0) {

            }
        });

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }
}
