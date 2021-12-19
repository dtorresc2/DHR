package com.sistemasdt.dhr.Rutas.Catalogos.Servicios;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.ServiciosAPI.QuerysServicios;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Servicios extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText descripcionServicio, montoServicio;
    private TextInputLayout layoutDescripcion, layoutMonto;
    private RadioButton trueServicio, falseServicio;
    private FloatingActionButton guardadorServicio;
    private TextView tituloServicio;

    private boolean modoEdicion;
    private Typeface typeface;
    private int ID_SERVICIO;

    public Servicios() {
        modoEdicion = false;
    }

    public void editarServicio(int id) {
        modoEdicion = true;
        ID_SERVICIO = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servicios, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        if (!modoEdicion)
            toolbar.setTitle("Servicio Nuevo");
        else
            toolbar.setTitle("Edicion de Servicio");

        toolbar.setNavigationOnClickListener(view1 -> {
            ListadoServicios listadoServicios = new ListadoServicios();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, listadoServicios);
            transaction.commit();
        });

        descripcionServicio = view.findViewById(R.id.descripcionServicio);
        descripcionServicio.setTypeface(typeface);
        descripcionServicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (descripcionRequerida()) {
                    validarDescripcion();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        montoServicio = view.findViewById(R.id.montoServicio);
        montoServicio.setTypeface(typeface);
        montoServicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (montoRequerido()) {
                    validarMonto();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tituloServicio = view.findViewById(R.id.tituloEstadoServicio);
        tituloServicio.setTypeface(typeface);

        trueServicio = view.findViewById(R.id.trueServicio);
        trueServicio.setTypeface(typeface);

        falseServicio = view.findViewById(R.id.falseServicio);
        falseServicio.setTypeface(typeface);

        if (modoEdicion) {
            obtenerServicio();
        }

        layoutDescripcion = view.findViewById(R.id.layoutDescripcion);
        layoutDescripcion.setTypeface(typeface);

        layoutMonto = view.findViewById(R.id.layoutMonto);
        layoutMonto.setTypeface(typeface);

        guardadorServicio = view.findViewById(R.id.grabarServicio);
        guardadorServicio.setOnClickListener(view12 -> {
            if (!descripcionRequerida() || !validarDescripcion() || !montoRequerido() || !validarMonto())
                return;

            if (!modoEdicion)
                registrarServicio();
            else
                actualizarServicio();
        });

        return view;
    }

    private void registrarServicio() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("DESCRIPCION", descripcionServicio.getText().toString().trim());
            jsonBody.put("MONTO", montoServicio.getText().toString().trim());
            jsonBody.put("ESTADO", (trueServicio.isChecked() == true) ? 1 : 0);
            jsonBody.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysServicios.registrarServicio(jsonBody, new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Servicio Registrado")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(typeface)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("CREACION", "SERVICIOS", "Se registro un servicio");

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    ListadoServicios listadoServicios = new ListadoServicios();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, listadoServicios);
                    transaction.commit();
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void actualizarServicio() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("DESCRIPCION", descripcionServicio.getText().toString().trim());
            jsonBody.put("MONTO", montoServicio.getText().toString().trim());
            jsonBody.put("ESTADO", (trueServicio.isChecked() == true) ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysServicios.actualizarServicio(ID_SERVICIO, jsonBody, new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Servicio Actualizado")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(typeface)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("ACTUALIZACION", "SERVICIOS", "Se actualizo el servicio #" + ID_SERVICIO);

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    ListadoServicios listadoServicios = new ListadoServicios();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, listadoServicios);
                    transaction.commit();
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerServicio() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_SERVICIO", ID_SERVICIO);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.obtenerListadoServicios(jsonObject, new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    descripcionServicio.setText(jsonObject.getString("DESCRIPCION"));
                    montoServicio.setText(String.format("%.2f", jsonObject.getDouble("MONTO")));
                    boolean habilitado = ((jsonObject.getInt("ESTADO")) > 0 ? true : false);
                    trueServicio.setChecked(habilitado);
                    falseServicio.setChecked(!habilitado);
                } catch (JSONException e) {
                    Log.i("SERVICIO", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Log.i("SERVICIO", e.toString());
                e.printStackTrace();
            }
        });
    }

    //    VALIDACIONES
    private boolean descripcionRequerida() {
        String textoCodigo = descripcionServicio.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            layoutDescripcion.setError("Campo requerido");
            return false;
        } else {
            layoutDescripcion.setError(null);
            return true;
        }
    }

    private boolean montoRequerido() {
        String textCorreo = montoServicio.getText().toString().trim();
        if (textCorreo.isEmpty()) {
            layoutMonto.setError("Campo requerido");
            return false;
        } else {
            layoutMonto.setError(null);
            return true;
        }
    }

    private boolean validarDescripcion() {
        String textoDescripcion = descripcionServicio.getText().toString().trim();
        Pattern patron = Pattern.compile("^[a-zA-Z0-9 ]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            layoutDescripcion.setError(null);
            return true;
        } else {
            layoutDescripcion.setError("Descripcion invalida");
            return false;
        }
    }

    private boolean validarMonto() {
        String textoDescripcion = montoServicio.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+(\\.[0-9]{2})$");
        if (patron.matcher(textoDescripcion).matches()) {
            layoutMonto.setError(null);
            return true;
        } else {
            layoutMonto.setError("Monto invalido, debe usar ####.##");
            return false;
        }
    }
}