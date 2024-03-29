package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.ListadoFichasNormales;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import android.graphics.Typeface;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorFicha;
import com.sistemasdt.dhr.Componentes.PDF.Impresiones;
import com.sistemasdt.dhr.R;

import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Adaptadores.AdaptadorConsultaFicha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Items.ItemsFichas;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.tapadoo.alerter.Alerter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("ValidFragment")
public class ListadoFichas extends Fragment {
    private RecyclerView listafichas;
    private Toolbar toolbar;
    private AdaptadorConsultaFicha adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemsFichas> lista;
    private boolean estadoFicha = false;
    private FloatingActionButton botonConsultaAvanzada;

    private TextInputLayout layoutSaldo;
    TextInputEditText saldo;

    @SuppressLint("ValidFragment")
    public ListadoFichas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listado_fichas, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Fichas Generales");

        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setNavigationOnClickListener(view1 -> {
            MenuFichas menuFichas = new MenuFichas();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, menuFichas);
            transaction.commit();
        });


        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.opcion_nuevo:
                    Ficha ficha = new Ficha();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, ficha);
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
                    obtenerFichas();
                    return true;

                default:
                    return false;

            }
        });

        botonConsultaAvanzada = view.findViewById(R.id.botonConsultaAvanzada);
        botonConsultaAvanzada.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_fichas_consulta_avanzada, null);
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

            ImageView botonCerrar = viewCuadro.findViewById(R.id.botonCerrar);
            TextView tituloDialogo = viewCuadro.findViewById(R.id.tituloDialogoBA);
            tituloDialogo.setTypeface(typeface);

            TextView tituloEstadoFicha = viewCuadro.findViewById(R.id.tituloEstado);
            tituloEstadoFicha.setTypeface(typeface);

            RadioButton trueFicha = viewCuadro.findViewById(R.id.trueFicha);
            trueFicha.setTypeface(typeface);
            RadioButton falseFicha = viewCuadro.findViewById(R.id.falseFicha);
            falseFicha.setTypeface(typeface);

            CheckBox checkEstado = viewCuadro.findViewById(R.id.checkEstado);
            checkEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    trueFicha.setEnabled(true);
                    falseFicha.setEnabled(true);
                } else {
                    trueFicha.setEnabled(false);
                    falseFicha.setEnabled(false);
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

            Button botonConsultar = viewCuadro.findViewById(R.id.botonConsultar);
            botonConsultar.setTypeface(typeface);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatoFechaGeneral = new SimpleDateFormat("dd/MM/yyyy");

            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
            fechaInicialTexto.setText(formatoFechaGeneral.format(calendar.getTime()));

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            fechaFinalTexto.setText(formatoFechaGeneral.format(calendar.getTime()));

            // SALDO DE FICHA
            TextView tituloSaldo = viewCuadro.findViewById(R.id.tituloSaldo);
            tituloSaldo.setTypeface(typeface);

            RadioButton saldoIgual = viewCuadro.findViewById(R.id.saldoIgual);
            saldoIgual.setTypeface(typeface);

            RadioButton saldoMenor = viewCuadro.findViewById(R.id.saldoMenor);
            saldoMenor.setTypeface(typeface);

            RadioButton saldoMayor = viewCuadro.findViewById(R.id.saldoMayor);
            saldoMayor.setTypeface(typeface);

            layoutSaldo = viewCuadro.findViewById(R.id.saldoLayout);
            layoutSaldo.setTypeface(typeface);

            saldo = viewCuadro.findViewById(R.id.saldo);
            saldo.setTypeface(typeface);

            CheckBox checkSaldo = viewCuadro.findViewById(R.id.checkSaldo);
            checkSaldo.setTypeface(typeface);
            checkSaldo.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saldo.setEnabled(isChecked);
                saldoMenor.setEnabled(isChecked);
                saldoMayor.setEnabled(isChecked);
                saldoIgual.setEnabled(isChecked);
            });

            builder.setCancelable(false);
            builder.setView(viewCuadro);
            AlertDialog dialog = builder.create();

            botonConsultar.setOnClickListener(v14 -> {
                if (checkEstado.isChecked() || checkFecha.isChecked() || checkSaldo.isChecked()) {
                    try {
                        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));

                        if (checkEstado.isChecked()) {
                            jsonObject.put("ESTADO", trueFicha.isChecked() ? 1 : 0);
                        } else {
                            jsonObject.put("ESTADO", 2);
                        }

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
                        } else {
                            jsonObject.put("FECHA_INICIAL", "");
                            jsonObject.put("FECHA_FINAL", "");
                        }

                        if (checkSaldo.isChecked()) {
                            if (saldoIgual.isChecked())
                                jsonObject.put("TIPO_SALDO", 0);
                            if (saldoMenor.isChecked())
                                jsonObject.put("TIPO_SALDO", 1);
                            if (saldoMayor.isChecked())
                                jsonObject.put("TIPO_SALDO", 2);
                            if (!textoRequerido())
                                return;

                            jsonObject.put("SALDO", Double.parseDouble(saldo.getText().toString()));
                        } else {
                            jsonObject.put("TIPO_SALDO", 3);
                            jsonObject.put("SALDO", 0);
                        }

                        obtenerConsultaAvanzada(jsonObject);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                } else {
                    AlertDialog.Builder builderAux = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builderAux.setIcon(R.drawable.logonuevo);
                    builderAux.setTitle("Listado de Fichas");
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

        lista = new ArrayList<>();
        listafichas = view.findViewById(R.id.lista_fichas);
        listafichas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        obtenerFichas();
        return view;
    }

    public void obtenerFichas() {
        lista.clear();
        estadoFicha = false;

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
            jsonObject.put("FECHA_INICIAL", simpleDateFormat.format(calendar.getTime()));
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            jsonObject.put("FECHA_FINAL", simpleDateFormat.format(calendar.getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysFichas querysFichas = new QuerysFichas(getContext());
        querysFichas.obtenerFichasFiltradas(jsonObject, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        lista.add(new ItemsFichas(
                                jsonArray.getJSONObject(i).getInt("ID_FICHA"),
                                jsonArray.getJSONObject(i).getString("NOMBRE"),
                                jsonArray.getJSONObject(i).getString("MOTIVO"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("DEBE")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("HABER")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("SALDO")),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                        ));
                    }

                    adapter = new AdaptadorConsultaFicha(lista);
                    listafichas.setLayoutManager(layoutManager);
                    listafichas.setAdapter(adapter);

                    adapter.setOnItemClickListener(position -> {
                        MenuInferiorFicha menuInferiorFicha = new MenuInferiorFicha();
                        menuInferiorFicha.recibirTitulo(lista.get(position).getMotivo());
                        estadoFicha = lista.get(position).getEstado();
                        menuInferiorFicha.recibirEstado(estadoFicha);
                        menuInferiorFicha.show(getActivity().getSupportFragmentManager(), "MenuInferiorFicha");
                        menuInferiorFicha.eventoClick(opcion -> realizarAccion(opcion, lista.get(position).getId()));
                    });

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerFichas();
            }
        });
    }

    private void realizarAccion(int opcion, int ID) {
        switch (opcion) {
            case 1:
                final SharedPreferences preferenciasFicha = getActivity().getSharedPreferences("FICHA", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = preferenciasFicha.edit();
                escritor.putInt("ID_FICHA", ID);
                escritor.commit();

                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, menuFichaNormal);
                transaction.commit();
                break;

            case 2:
                if (estadoFicha) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Listado de Fichas");
                    builder.setMessage("¿Desea deshabilitar la ficha?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstado(ID));
                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Listado de Fichas");
                    builder.setMessage("¿Desea habilitar la ficha?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstado(ID));
                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;

            case 3:
                Impresiones impresiones = new Impresiones(getContext(), getActivity().getSupportFragmentManager());
                impresiones.generarFichaNormal(ID);
                break;

            case 4:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Fichas");
                builder.setMessage("¿Desea eliminar la ficha?");
                builder.setPositiveButton("ACEPTAR", (dialog, id) -> eliminarFicha(ID));
                builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                return;
        }
    }

    private void actualizarEstado(int ID_FICHA) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ESTADO", estadoFicha == true ? "0" : "1");

            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.actualizarEstadoFicha(ID_FICHA, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Fichas")
                            .setText("Ficha actualizada correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                    funcionesBitacora.registrarBitacora("ACTUALIZACION", "FICHA NORMAL", "Se el estado de la ficha #" + ID_FICHA);

                    obtenerFichas();
                }

                @Override
                public void onFailure(Exception e) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Fichas")
                            .setText("Fallo al actualizar la ficha")
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

    private void eliminarFicha(int ID) {
        QuerysFichas querysFichas = new QuerysFichas(getContext());
        querysFichas.eliminarFicha(ID, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle("Fichas")
                        .setText("Ficha eliminada correctamente")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("ELIMINACION", "FICHA NORMAL", "Se elimino la ficha #" + ID);

                obtenerFichas();
            }

            @Override
            public void onFailure(Exception e) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("Fallo al eliminar la ficha")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
            }
        });
    }

    private void obtenerConsultaAvanzada(JSONObject consulta) {
        lista.clear();
        estadoFicha = false;

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysFichas querysFichas = new QuerysFichas(getContext());
        querysFichas.consultaAvanzada(consulta, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        lista.add(new ItemsFichas(
                                jsonArray.getJSONObject(i).getInt("ID_FICHA"),
                                jsonArray.getJSONObject(i).getString("NOMBRE"),
                                jsonArray.getJSONObject(i).getString("MOTIVO"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("DEBE")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("HABER")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("SALDO")),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                        ));
                    }

                    adapter = new AdaptadorConsultaFicha(lista);
                    listafichas.setLayoutManager(layoutManager);
                    listafichas.setAdapter(adapter);

                    adapter.setOnItemClickListener(position -> {
                        MenuInferiorFicha menuInferiorFicha = new MenuInferiorFicha();
                        menuInferiorFicha.recibirTitulo(lista.get(position).getMotivo());
                        estadoFicha = lista.get(position).getEstado();
                        menuInferiorFicha.recibirEstado(estadoFicha);
                        menuInferiorFicha.show(getActivity().getSupportFragmentManager(), "MenuInferiorFicha");
                        menuInferiorFicha.eventoClick(opcion -> realizarAccion(opcion, lista.get(position).getId()));
                    });

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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

    //    VALIDACIONES
    private boolean textoRequerido() {
        String textoCodigo = saldo.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            layoutSaldo.setError("Campo requerido");
            return false;
        } else {
            layoutSaldo.setError(null);
            return true;
        }
    }
}