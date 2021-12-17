package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ItemPaciente;
import com.sistemasdt.dhr.ServiciosAPI.QuerysBitacora;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DialogoBitacora extends DialogFragment {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private BitacoraAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ItemBitacora> listaBitacora;
    private FloatingActionButton consultaAvanzada;
    private ArrayList<String> listaEventos;
    private ArrayList<String> listaSecciones;
    private ArrayList<String> listaCuentas;

    public DialogoBitacora() {
    }

    public static DialogoBitacora display(FragmentManager fragmentManager) {
        DialogoBitacora dialogoBitacora = new DialogoBitacora();
        dialogoBitacora.show(fragmentManager, "Dialogo DialogoBitacora");
        return dialogoBitacora;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_bitacora, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Bitacora del Sistema");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));

        toolbar.inflateMenu(R.menu.opciones_toolbarcitas);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_aceptar:
                    dismiss();
                    return true;

                default:
                    return false;
            }
        });

        consultaAvanzada = view.findViewById(R.id.botonConsultaAvanzada);
        consultaAvanzada.setOnClickListener(v -> {
            listaEventos = new ArrayList<>();
            listaSecciones = new ArrayList<>();
            listaCuentas = new ArrayList<>();

            obtenerSecciones();
            obtenerEventos();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_bitacora_consulta_avanzada, null);
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
            ImageView botonCerrar = viewCuadro.findViewById(R.id.botonCerrar);

            // FECHA INICIAL
            CheckBox checkFecha = viewCuadro.findViewById(R.id.checkFecha);

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

            // EVENTO
            TextView tituloEvento = viewCuadro.findViewById(R.id.tituloOpcionEvento);
            tituloEvento.setTypeface(typeface);

            TextView evento = viewCuadro.findViewById(R.id.evento);
            evento.setBackgroundColor(getResources().getColor(R.color.Gris));
            evento.setTypeface(typeface);

            CheckBox checkEvento = viewCuadro.findViewById(R.id.checkEvento);
            checkEvento.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    evento.setBackgroundColor(getResources().getColor(R.color.AzulOscuro));
                } else {
                    evento.setBackgroundColor(getResources().getColor(R.color.Gris));
                }
            });

            evento.setOnClickListener(v14 -> {
                if (checkEvento.isChecked()) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialogo_busqueda);
                    dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText editText = dialog.findViewById(R.id.buscador);
                    editText.setTypeface(typeface);

                    TextView textView = dialog.findViewById(R.id.tituloDialogo);
                    textView.setText("Seleccione un Evento");
                    textView.setTypeface(typeface);

                    ListView listView = dialog.findViewById(R.id.lista_items);

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaEventos);
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
                        evento.setText(adapter.getItem(position));
                        dialog.dismiss();
                    });
                }
            });

            // SECCION
            TextView tituloSeccion = viewCuadro.findViewById(R.id.tituloOpcionSeccion);
            tituloSeccion.setTypeface(typeface);

            TextView seccion = viewCuadro.findViewById(R.id.seccion);
            seccion.setBackgroundColor(getResources().getColor(R.color.Gris));
            seccion.setTypeface(typeface);

            CheckBox checkSeccion = viewCuadro.findViewById(R.id.checkSeccion);
            checkSeccion.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    seccion.setBackgroundColor(getResources().getColor(R.color.AzulOscuro));
                } else {
                    seccion.setBackgroundColor(getResources().getColor(R.color.Gris));
                }
            });

            seccion.setOnClickListener(v14 -> {
                if (checkSeccion.isChecked()) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialogo_busqueda);
                    dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText editText = dialog.findViewById(R.id.buscador);
                    editText.setTypeface(typeface);

                    TextView textView = dialog.findViewById(R.id.tituloDialogo);
                    textView.setText("Seleccione una Seccion");
                    textView.setTypeface(typeface);

                    ListView listView = dialog.findViewById(R.id.lista_items);

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaSecciones);
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
                        seccion.setText(adapter.getItem(position));
                        dialog.dismiss();
                    });
                }
            });

            // CUENTA
            TextView tituloCuenta = viewCuadro.findViewById(R.id.tituloOpcionCuenta);
            tituloCuenta.setTypeface(typeface);

            TextView cuenta = viewCuadro.findViewById(R.id.cuenta);
            cuenta.setBackgroundColor(getResources().getColor(R.color.Gris));
            cuenta.setTypeface(typeface);

            CheckBox checkCuenta = viewCuadro.findViewById(R.id.checkCuenta);
            checkCuenta.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    cuenta.setBackgroundColor(getResources().getColor(R.color.AzulOscuro));
                } else {
                    cuenta.setBackgroundColor(getResources().getColor(R.color.Gris));
                }
            });

            cuenta.setOnClickListener(v14 -> {
                if (checkCuenta.isChecked()) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialogo_busqueda);
                    dialog.getWindow().setLayout(view.getWidth() - 50, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText editText = dialog.findViewById(R.id.buscador);
                    editText.setTypeface(typeface);

                    TextView textView = dialog.findViewById(R.id.tituloDialogo);
                    textView.setText("Seleccione una Cuenta");
                    textView.setTypeface(typeface);

                    ListView listView = dialog.findViewById(R.id.lista_items);

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaCuentas);
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
                }
            });

            // INICIALIZAR FECHAS
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatoFechaGeneral = new SimpleDateFormat("dd/MM/yyyy");

            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));

            fechaInicialTexto.setText(formatoFechaGeneral.format(calendar.getTime()));

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

            fechaFinalTexto.setText(formatoFechaGeneral.format(calendar.getTime()));

            // ASIGNAR BOTON CONSULTA AVANZADA
            Button botonConsultaAvanzada = viewCuadro.findViewById(R.id.botonConsultar);
            botonConsultaAvanzada.setTypeface(typeface);

            builder.setCancelable(false);
            builder.setView(viewCuadro);
            AlertDialog dialog = builder.create();

            botonConsultaAvanzada.setOnClickListener(v15 -> {

            });

            botonCerrar.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });

        listaBitacora = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.listaBitacora);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        obtenerBitacora();
        return view;
    }

    public void obtenerBitacora() {
        listaBitacora.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
            jsonObject.put("FECHA_INICIAL", simpleDateFormat.format(calendar.getTime()));
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            jsonObject.put("FECHA_FINAL", simpleDateFormat.format(calendar.getTime()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysBitacora querysBitacora = new QuerysBitacora(getContext());
        querysBitacora.obtenerBitacoraFiltrada(jsonObject, new QuerysBitacora.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaBitacora.add(new ItemBitacora(
                                jsonArray.getJSONObject(i).getString("ACCION"),
                                jsonArray.getJSONObject(i).getString("EVENTO"),
                                jsonArray.getJSONObject(i).getString("SECCION"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                jsonArray.getJSONObject(i).getString("USUARIO")
                        ));
                    }
                    mAdapter = new BitacoraAdapter(listaBitacora);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerBitacora();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void obtenerSecciones() {
        listaSecciones.clear();

        QuerysBitacora querysBitacora = new QuerysBitacora(getContext());
        querysBitacora.obtenerSecciones(new QuerysBitacora.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaSecciones.add(jsonArray.getJSONObject(i).getString("SECCION"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void obtenerEventos() {
        listaEventos.clear();

        QuerysBitacora querysBitacora = new QuerysBitacora(getContext());
        querysBitacora.obtenerEventos(new QuerysBitacora.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaEventos.add(jsonArray.getJSONObject(i).getString("EVENTO"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
