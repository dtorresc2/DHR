package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.AlertDialog;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.Rutas.Catalogos.Piezas.ItemPieza;
import com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Items.ItemVisita;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.ItemTratamiento;
import com.tapadoo.alerter.Alerter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    int POSICION = 0;

    private TableLayout tableLayout;
    private String[] header = {"Fecha", "Descripcion", "Costo"};
    private TablaDinamica tablaDinamica;
    private ArrayList<String[]> rows = new ArrayList<>();
    private ArrayList<ItemVisita> listaVisitas = new ArrayList<>();
    boolean modoEdicionTratamiento = false;

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

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        descripcion = view.findViewById(R.id.descEspecial);
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
        layoutDescripcion = view.findViewById(R.id.layoutDescEspecial);

        titulo = view.findViewById(R.id.tituloVisitas);
        selectorFecha = view.findViewById(R.id.obtenerFecha);
        agregar = view.findViewById(R.id.agregarVisita);
        siguiente = view.findViewById(R.id.siguiente);

        costo_visita = view.findViewById(R.id.visitaIndividual);
        costo_visita.addTextChangedListener(new TextWatcher() {
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
        tablaDinamica.addData(getRows());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);
        tablaDinamica.setOnItemClickListener(position -> {
            MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
            menuInferiorDos.show(getActivity().getSupportFragmentManager(), "MenuInferior");
            menuInferiorDos.recibirTitulo(tablaDinamica.getCellData(position, 1));
            menuInferiorDos.eventoClick(opcion -> {
                int index = position - 1;
                switch (opcion) {
                    case 1:
                        // Editar Tratamiento
                        modoEdicionTratamiento = true;
                        agregar.setText("ACTUALIZAR VISITA");
                        ItemVisita aux = listaVisitas.get(index);

                        descripcion.setText(aux.getDescripcion());
                        costo_visita.setText(String.format("%.2f", aux.getCosto()));
                        COSTO_VISITA = Double.parseDouble(aux.getCosto().toString());

                        POSICION = index;
                        break;

                    case 2:
                        // Eliminar Tratamiento
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                        builder.setIcon(R.drawable.logonuevo);
                        builder.setTitle("Visitas");
                        builder.setMessage("¿Desea eliminar la visita?");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                            tablaDinamica.removeRow(position);
                            total = 0;
                            listaVisitas.remove(position - 1);

                            if (tablaDinamica.getCount() > 0) {
                                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                    total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                                }
                                totalGasto.setText(String.format("%.2f", total));
                            } else {
                                totalGasto.setText("0.00");
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

        tituloGasto = view.findViewById(R.id.tituloGasto);
        totalGasto = view.findViewById(R.id.totalGasto);
        totalGasto.setTypeface(face);

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
                    String.format("%.2f", COSTO_VISITA)
            };

            if (!modoEdicionTratamiento) {
                tablaDinamica.addItem(item);
                listaVisitas.add(new ItemVisita(
                        fechaFormateada,
                        descripcion.getText().toString(),
                        COSTO_VISITA
                ));
            } else {
                if (listaVisitas.size() > 0) {
                    listaVisitas.set(POSICION, new ItemVisita(
                            fechaFormateada,
                            descripcion.getText().toString(),
                            COSTO_VISITA
                    ));

                    // Reinciar Tabla
                    tablaDinamica.removeAll();
                    tablaDinamica.addHeader(header);
                    tablaDinamica.fondoHeader(R.color.AzulOscuro);

                    for (ItemVisita itemVisita : listaVisitas) {
                        tablaDinamica.addItem(new String[]{
                                fechaFormateada,
                                itemVisita.getDescripcion(),
                                String.format("%.2f", itemVisita.getCosto())
                        });
                    }
                }

                modoEdicionTratamiento = false;
                POSICION = 0;
                agregar.setText("AGREGAR TRATAMIENTO");
            }

            descripcion.setText(null);
            layoutDescripcion.setError(null);

            if (tablaDinamica.getCount() > 1) {
                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                    total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                }
                totalGasto.setText(String.format("%.2f", total));
            }
        });

        siguiente.setOnClickListener(v -> {
//            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
//            progressDialog.setMessage("Cargando...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//            Handler handler = new Handler();
//            handler.postDelayed(() -> progressDialog.dismiss(), 1000);

            if (tablaDinamica.getCount() > 0) {
                if (!MODO_EDICION) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("VISITAS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Set<String> set = new HashSet<>();

                    for (ItemVisita itemVisita : listaVisitas) {
                        String cadena = itemVisita.getFecha() + ";" + itemVisita.getCosto() + ";" + itemVisita.getDescripcion() + ";";
                        set.add(cadena);
                    }

                    editor.putStringSet("LISTA_VISITAS", set);
                    editor.apply();

                    PagosVisitas pagosVisitas = new PagosVisitas();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, pagosVisitas);
                    transaction.commit();
                }
            }
        });

        cargarVisitas();
        return view;
    }

    public void cargarVisitas() {
        // Reinciar Tabla
        listaVisitas.clear();
        tablaDinamica.removeAll();
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        if (!MODO_EDICION) {
            SharedPreferences preferences = getActivity().getSharedPreferences("VISITAS", Context.MODE_PRIVATE);
            Set<String> set = preferences.getStringSet("LISTA_VISITAS", null);

            if (set != null) {
                ArrayList<String> listaAuxiliar = new ArrayList<>(set);

                for (String item : listaAuxiliar) {
                    String cadenaAuxiliar[] = item.split(";");

                    listaVisitas.add(new ItemVisita(
                            cadenaAuxiliar[0],
                            cadenaAuxiliar[2],
                            Double.parseDouble(cadenaAuxiliar[1])
                    ));

                    tablaDinamica.addItem(new String[]{
                            cadenaAuxiliar[0],
                            cadenaAuxiliar[2],
                            String.format("%.2f", Double.parseDouble(cadenaAuxiliar[1]))
                    });
                }

                total = 0;

                if (tablaDinamica.getCount() > 0) {
                    for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                        total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                    }
                    totalGasto.setText(String.format("%.2f", total));
                } else {
                    totalGasto.setText("0.00");
                }
            }
        }
    }

    private ArrayList<String[]> getRows() {
        return rows;
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
        String texto = costo_visita.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutCostoVisita.setError("Campo Requerido");
            return false;
        } else {
            layoutCostoVisita.setError(null);
            return true;
        }
    }

    private boolean validarMonto() {
        String texto = costo_visita.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(texto).matches()) {
            layoutCostoVisita.setError(null);
            return true;
        } else {
            layoutCostoVisita.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }
}
