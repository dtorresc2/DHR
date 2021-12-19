package com.sistemasdt.dhr.Rutas.Citas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuCitas;
import com.sistemasdt.dhr.Rutas.Citas.Adaptador.AdaptadorCita;
import com.sistemasdt.dhr.R;

import com.sistemasdt.dhr.ServiciosAPI.QuerysCitas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListadoCitas extends Fragment {
    private Toolbar toolbar;
    private RecyclerView lista_pacientes;
    private AdaptadorCita adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton botonConsultaAvanzada;

    public ListadoCitas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listado_citas, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Citas Disponibles");
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.opcion_nuevo:
                    Citas citas = new Citas();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, citas);
                    transaction.commit();
                    return true;

                case R.id.opcion_filtrar:
                    MenuItem searchItem = item;
                    SearchView searchView = (SearchView) searchItem.getActionView();

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            adapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                    return true;

                case R.id.opcion_actualizar:
                    obtenerCitas();
                    return true;

                default:
                    return false;
            }
        });

        botonConsultaAvanzada = view.findViewById(R.id.botonConsultaAvanzada);
        botonConsultaAvanzada.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_citas_consulta_avanzada, null);
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

            ImageView botonCerrar = viewCuadro.findViewById(R.id.botonCerrar);
            TextView tituloDialogo = viewCuadro.findViewById(R.id.tituloDialogoBA);
            tituloDialogo.setTypeface(typeface);

            TextView tituloCita = viewCuadro.findViewById(R.id.tituloEstado);
            tituloCita.setTypeface(typeface);

            RadioButton citaTrue = viewCuadro.findViewById(R.id.citaTrue);
            citaTrue.setTypeface(typeface);
            RadioButton citaFalse = viewCuadro.findViewById(R.id.citaFalse);
            citaFalse.setTypeface(typeface);

            CheckBox checkCita = viewCuadro.findViewById(R.id.checkCita);
            checkCita.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    citaTrue.setEnabled(true);
                    citaFalse.setEnabled(true);
                } else {
                    citaTrue.setEnabled(false);
                    citaFalse.setEnabled(false);
                }
            });

            // FECHA INICIAL
            TextView tituloFecha = viewCuadro.findViewById(R.id.tituloFecha);
            tituloFecha.setTypeface(typeface);

            TextView tituloFechaInicial = viewCuadro.findViewById(R.id.tituloFechaInicial);
            tituloFechaInicial.setTypeface(typeface);

            TextView fechaInicialTexto = viewCuadro.findViewById(R.id.fechaInicial);
            fechaInicialTexto.setTypeface(typeface);

            ImageView obtenerFechaInicial = viewCuadro.findViewById(R.id.obtenerFechaInicial);
            obtenerFechaInicial.setOnClickListener(v12 -> {
                Calendar calendarAux = Calendar.getInstance();
                calendarAux.set(Calendar.DATE, calendarAux.getActualMinimum(Calendar.DATE));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (datePick, year, month, dayOfMonth) -> {
                            final int mesActual = month + 1;
                            String diaFormateado = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                            String mesFormateado = (mesActual < 10) ? "0" + mesActual : String.valueOf(mesActual);
                            fechaInicialTexto.setText(diaFormateado + "/" + mesFormateado + "/" + year);

                        }, calendarAux.get(Calendar.YEAR) - 1, calendarAux.get(Calendar.MONTH), calendarAux.get(Calendar.DAY_OF_YEAR));

                datePickerDialog.show();
            });

            // FECHA FINAL
            TextView tituloFechaFinal = viewCuadro.findViewById(R.id.tituloFechaFinal);
            tituloFechaFinal.setTypeface(typeface);

            TextView fechaFinalTexto = viewCuadro.findViewById(R.id.fechaFinal);
            fechaFinalTexto.setTypeface(typeface);

            ImageView obtenerFechaFinal = viewCuadro.findViewById(R.id.obtenerFechaFinal);
            obtenerFechaFinal.setOnClickListener(v13 -> {
                Calendar calendarAux = Calendar.getInstance();
                calendarAux.set(Calendar.DATE, calendarAux.getActualMaximum(Calendar.DATE));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (datePick, year, month, dayOfMonth) -> {
                            final int mesActual = month + 1;
                            String diaFormateado = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                            String mesFormateado = (mesActual < 10) ? "0" + mesActual : String.valueOf(mesActual);
                            fechaFinalTexto.setText(diaFormateado + "/" + mesFormateado + "/" + year);

                        }, calendarAux.get(Calendar.YEAR) - 1, calendarAux.get(Calendar.MONTH), calendarAux.get(Calendar.DAY_OF_YEAR));

                datePickerDialog.show();
            });

            CheckBox checkFecha = viewCuadro.findViewById(R.id.checkFecha);


            Button botonConsultaAvanzada = viewCuadro.findViewById(R.id.botonConsultar);
            botonConsultaAvanzada.setTypeface(typeface);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatoFechaGeneral = new SimpleDateFormat("dd/MM/yyyy");

            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));

            fechaInicialTexto.setText(formatoFechaGeneral.format(calendar.getTime()));

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

            fechaFinalTexto.setText(formatoFechaGeneral.format(calendar.getTime()));

            builder.setCancelable(false);
            builder.setView(viewCuadro);
            AlertDialog dialog = builder.create();

            botonConsultaAvanzada.setOnClickListener(v14 -> {
                if (checkCita.isChecked() || checkFecha.isChecked()) {
                    try {
                        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));

                        if (checkCita.isChecked())
                            jsonObject.put("REALIZADO", citaTrue.isChecked() ? 1 : 0);

                        if (checkFecha.isChecked()) {
                            Date fechaInicialAux = new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialTexto.getText().toString());
                            Date fechaFinalAux = new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalTexto.getText().toString());
                            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

                            if (fechaInicialAux.equals(fechaFinalAux) || fechaInicialAux.before(fechaFinalAux)) {
                                jsonObject.put("FECHA_INICIAL", formatoFecha.format(fechaInicialAux));
                                jsonObject.put("FECHA_FINAL", formatoFecha.format(fechaFinalAux));
                            } else {
                                AlertDialog.Builder builderAux = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                                builderAux.setIcon(R.drawable.logonuevo);
                                builderAux.setTitle("Listado de Citas");
                                builderAux.setMessage("Rango de Fechas Incorrecto");
                                builderAux.setPositiveButton("Aceptar",
                                        (dialog1, which) -> {
                                        });


                                AlertDialog alertDialog = builderAux.create();
                                alertDialog.show();
                                return;
                            }
                        }

                        obtenerConsultaAvanzada(jsonObject);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                } else {
                    AlertDialog.Builder builderAux = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builderAux.setIcon(R.drawable.logonuevo);
                    builderAux.setTitle("Listado de Citas");
                    builderAux.setMessage("Seleccione al menos una opcion");
                    builderAux.setPositiveButton("Aceptar",
                            (dialog1, which) -> {
                            });


                    AlertDialog alertDialog = builderAux.create();
                    alertDialog.show();
                }
            });

            botonCerrar.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });

        lista_pacientes = view.findViewById(R.id.listaCitas);

        obtenerCitas();

        return view;
    }

    private void obtenerCitas() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
            jsonObject.put("FECHA_INICIAL", simpleDateFormat.format(calendar.getTime()));
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            jsonObject.put("FECHA_FINAL", simpleDateFormat.format(calendar.getTime()));

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.obtenerListadoCitasFiltrado(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONArray jsonArray = new JSONArray(object.toString());
                        final ArrayList<ItemCita> lista = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            lista.add(new ItemCita(
                                    jsonArray.getJSONObject(i).getString("ID_CITA"),
                                    jsonArray.getJSONObject(i).getString("FECHA"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE_PACIENTE"),
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    jsonArray.getJSONObject(i).getInt("REALIZADO") > 0 ? true : false
                            ));
                        }
                        lista_pacientes.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorCita(lista);
                        lista_pacientes.setLayoutManager(layoutManager);
                        lista_pacientes.setAdapter(adapter);

                        adapter.setOnItemClickListener(position -> {
                            MenuCitas menuCitas = new MenuCitas();
                            menuCitas.show(getActivity().getSupportFragmentManager(), "MenuCitas");
                            menuCitas.recibirTitulo(lista.get(position).getMdescripcion());
                            menuCitas.recibirEstado(lista.get(position).getMrealizado());
                            menuCitas.eventoClick(opcion -> {
                                realizarAccion(opcion, Integer.parseInt(lista.get(position).getMidCitas()), position, lista.get(position).getMrealizado());
                            });
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    obtenerCitas();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void realizarAccion(int opcion, int ID, final int posicion, boolean estado) {
        switch (opcion) {
            case 1:
                Citas citas = new Citas();
                citas.activarModoEdicion(ID);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, citas);
                transaction.commit();
                break;

            case 2:
                if (estado) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Listado de Citas");
                    builder.setMessage("¿Desea terminar la cita?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstadoCita(ID, estado));
                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Listado de Citas");
                    builder.setMessage("¿Desea completar la cita?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstadoCita(ID, estado));
                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;

            case 4:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Citas");
                builder.setMessage("¿Desea eliminar la cita?");
                builder.setPositiveButton("ACEPTAR", (dialog, id) -> eliminarCita(ID));
                builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            default:
                break;
        }
    }

    private void eliminarCita(int ID) {
        try {
            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_CITA", ID);

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.eliminarCita(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Citas")
                            .setText("Cita eliminada correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    obtenerCitas();
                }

                @Override
                public void onFailure(Exception e) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Fallo al eliminar la cita")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizarEstadoCita(int ID, boolean estado) {
        try {
            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_CITA", ID);
            jsonObject.put("REALIZADO", estado == true ? "0" : "1");

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.actualizarEstado(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Citas")
                            .setText("Cita actualizada correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    obtenerCitas();
                }

                @Override
                public void onFailure(Exception e) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Fallo al actualizar la cita")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerConsultaAvanzada(JSONObject consulta) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysCitas querysCitasAux = new QuerysCitas(getContext());
        querysCitasAux.consultaAvanzada(consulta, new QuerysCitas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    final ArrayList<ItemCita> lista = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        lista.add(new ItemCita(
                                jsonArray.getJSONObject(i).getString("ID_CITA"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                jsonArray.getJSONObject(i).getString("NOMBRE_PACIENTE"),
                                jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                jsonArray.getJSONObject(i).getInt("REALIZADO") > 0 ? true : false
                        ));
                    }
                    lista_pacientes.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    adapter = new AdaptadorCita(lista);
                    lista_pacientes.setLayoutManager(layoutManager);
                    lista_pacientes.setAdapter(adapter);

                    adapter.setOnItemClickListener(position -> {
                        MenuCitas menuCitas = new MenuCitas();
                        menuCitas.show(getActivity().getSupportFragmentManager(), "MenuCitas");
                        menuCitas.recibirTitulo(lista.get(position).getMdescripcion());
                        menuCitas.recibirEstado(lista.get(position).getMrealizado());
                        menuCitas.eventoClick(opcion -> {
                            realizarAccion(opcion, Integer.parseInt(lista.get(position).getMidCitas()), position, lista.get(position).getMrealizado());
                        });
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerConsultaAvanzada(consulta);
            }
        });
    }
}