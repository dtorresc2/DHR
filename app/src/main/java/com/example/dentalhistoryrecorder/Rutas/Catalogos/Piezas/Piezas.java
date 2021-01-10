package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dentalhistoryrecorder.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ListadoServicios;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysPiezas;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysServicios;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Piezas extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText nombrePieza, numeroPieza;
    private RadioButton truePieza, falsePieza;
    private FloatingActionButton guardadorPieza;
    private TextView tituloPieza;

    private boolean modoEdicion;
    private Typeface typeface;
    private int ID_PIEZA;

    public Piezas() {
        modoEdicion = false;
    }

    public void editarPieza(int id) {
        modoEdicion = true;
        ID_PIEZA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_piezas, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        if (!modoEdicion)
            toolbar.setTitle("Pieza Nueva");
        else
            toolbar.setTitle("Pieza #" + ID_PIEZA);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListadoPiezas listadoPiezas = new ListadoPiezas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoPiezas);
                transaction.commit();
            }
        });

        nombrePieza = view.findViewById(R.id.nombrePieza);
        nombrePieza.setTypeface(typeface);
        nombrePieza.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nombreRequerido())
                    validarNombre();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numeroPieza = view.findViewById(R.id.numeroPieza);
        numeroPieza.setTypeface(typeface);
        numeroPieza.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (numeroRequerido())
                    validarNumero();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tituloPieza = view.findViewById(R.id.tituloEstadoPieza);
        tituloPieza.setTypeface(typeface);

        truePieza = view.findViewById(R.id.truePieza);
        truePieza.setTypeface(typeface);

        falsePieza = view.findViewById(R.id.falsePieza);
        falsePieza.setTypeface(typeface);

        if (modoEdicion) {
            obtenerPieza();
        }

        guardadorPieza = view.findViewById(R.id.grabarPieza);
        guardadorPieza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nombreRequerido() || !numeroRequerido() || !validarNombre() || !validarNumero())
                    return;

                if (!modoEdicion)
                    registrarPieza();
                else
                    actualizarPieza();
            }
        });

        return view;
    }

    private void obtenerPieza() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        querysPiezas.obtenerPiezaEspecifica(ID_PIEZA, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    nombrePieza.setText(jsonObject.getString("NOMBRE"));
                    numeroPieza.setText(jsonObject.getString("NUMERO"));
                    boolean habilitado = ((jsonObject.getInt("ESTADO")) > 0 ? true : false);
                    truePieza.setChecked(habilitado);
                    falsePieza.setChecked(!habilitado);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.i("PIEZA", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Log.i("PIEZA", e.toString());
                e.printStackTrace();
            }
        });
    }

    private void registrarPieza() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("NUMERO", numeroPieza.getText().toString().trim());
            jsonBody.put("NOMBRE", nombrePieza.getText().toString().trim());
            jsonBody.put("ESTADO", (truePieza.isChecked() == true) ? 1 : 0);
            jsonBody.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysPiezas.registrarPieza(jsonBody, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Pieza Registrada")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(typeface)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("Se registro una pieza");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ListadoPiezas listadoPiezas = new ListadoPiezas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, listadoPiezas);
                        transaction.commit();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPieza() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("NUMERO", numeroPieza.getText().toString().trim());
            jsonBody.put("NOMBRE", nombrePieza.getText().toString().trim());
            jsonBody.put("ESTADO", (truePieza.isChecked() == true) ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysPiezas.actualizarPieza(ID_PIEZA, jsonBody, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Pieza Actualizada")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(typeface)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("Se actualizo la pieza #" + ID_PIEZA);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ListadoPiezas listadoPiezas = new ListadoPiezas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, listadoPiezas);
                        transaction.commit();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    VALIDACIONES
    private boolean nombreRequerido() {
        String textoCodigo = nombrePieza.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            nombrePieza.setError("Campo requerido");
            return false;
        } else {
            nombrePieza.setError(null);
            return true;
        }
    }

    private boolean numeroRequerido() {
        String textoCodigo = numeroPieza.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            numeroPieza.setError("Campo requerido");
            return false;
        } else {
            numeroPieza.setError(null);
            return true;
        }
    }

    private boolean validarNombre() {
        String textoDescripcion = nombrePieza.getText().toString().trim();
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            nombrePieza.setError(null);
            return true;
        } else {
            nombrePieza.setError("Nombre invalido");
            return false;
        }
    }

    private boolean validarNumero() {
        String textoDescripcion = numeroPieza.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            numeroPieza.setError(null);
            return true;
        } else {
            numeroPieza.setError("Numero invalido");
            return false;
        }
    }
}