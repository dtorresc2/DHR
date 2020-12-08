package com.example.dentalhistoryrecorder;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Adaptadores.AdaptadorConsultaFicha;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Historiales;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.ItemsFichas;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysCuentas;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InicioSesion extends AppCompatActivity {
    private TextInputEditText correo, pass, usuario;
    private Button boton;
    private TextView titulo, version;
    private CheckBox recordatorio;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_inicio_sesion);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/bahnschrift.ttf");
        correo = findViewById(R.id.correo);
        correo.setTypeface(typeface);
        pass = findViewById(R.id.pass);
        pass.setTypeface(typeface);
        usuario = findViewById(R.id.codigoUsuario);
        usuario.setTypeface(typeface);
        boton = findViewById(R.id.aceptador);
        boton.setTypeface(typeface);
        titulo = findViewById(R.id.titulo_inicio);
        titulo.setTypeface(typeface);
        version = findViewById(R.id.titulo_version);
        version.setTypeface(typeface);
        recordatorio = findViewById(R.id.recordar);
        recordatorio.setTypeface(typeface);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validacionCodigo() || !validacionCorreo() || !validacionPass())
                    return;

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("ID_USUARIO", usuario.getText().toString().trim());
                    jsonBody.put("USUARIO", correo.getText().toString().trim());
                    jsonBody.put("PASSWORD", pass.getText().toString().trim());
//                    jsonBody.put("ID_USUARIO", "1001");
//                    jsonBody.put("USUARIO", "diegot");
//                    jsonBody.put("PASSWORD", "321");
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final QuerysCuentas querysCuentas = new QuerysCuentas(getApplicationContext());
                querysCuentas.inicioSesion(jsonBody, new QuerysCuentas.VolleyOnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        try {
                            JSONObject jsonObject = new JSONObject(object.toString());

                            if (jsonObject.getInt("ID_USUARIO") < 1) {
                                Alerter.create(InicioSesion.this)
                                        .setTitle("Error")
                                        .setText("Credenciales Incorrectas")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(typeface)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.AzulOscuro)
                                        .show();
                                return;
                            }

                            querysCuentas.serviciosHabilitados(jsonObject.getInt("ID_USUARIO"), new QuerysCuentas.VolleyOnEventListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(object.toString());
                                        if (jsonObject.getInt("ID_USUARIO") != 1) {
                                            Alerter.create(InicioSesion.this)
                                                    .setTitle("Error")
                                                    .setText("No tienene acceso")
                                                    .setIcon(R.drawable.logonuevo)
                                                    .setTextTypeface(typeface)
                                                    .enableSwipeToDismiss()
                                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                                    .show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getApplicationContext(), object.toString(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Alerter.create(InicioSesion.this)
                                .setTitle("Error")
                                .setText("Fallo al obtener datos")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(typeface)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.FondoSecundario)
                                .show();
                    }
                });

//                else {
//                    Alerter.create(InicioSesion.this)
//                            .setTitle("Error")
//                            .setText("Faltan datos")
//                            .setIcon(R.drawable.logonuevo)
//                            .setTextTypeface(typeface)
//                            .enableSwipeToDismiss()
//                            .setBackgroundColorRes(R.color.AzulOscuro)
//                            .show();
//                }
            }
        });
    }

    private boolean validacionCodigo() {
        String textoCodigo = usuario.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            usuario.setError("Campo requerido");
            return false;
        } else {
            usuario.setError(null);
            return true;
        }
    }

    private boolean validacionCorreo() {
        String textCorreo = correo.getText().toString().trim();
        if (textCorreo.isEmpty()) {
            correo.setError("Campo requerido");
            return false;
        } else {
            correo.setError(null);
            return true;
        }
    }

    private boolean validacionPass() {
        String textPass = pass.getText().toString().trim();
        if (textPass.isEmpty()) {
            pass.setError("Campo requerido");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    private void iniciarSesion(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int idUsuarios = jsonArray.getJSONObject(i).getInt("idUsuarios");

                            final ProgressDialog progressDialog = new ProgressDialog(InicioSesion.this, R.style.progressDialog);
                            progressDialog.setMessage("Autenticando...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 500);

                            if (idUsuarios > 0) {
                                int mobil = jsonArray.getJSONObject(i).getInt("movil");
                                switch (mobil) {
                                    case 0:
                                        //El usuario NO tiene permiso;
                                        Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/bahnschrift.ttf");
                                        Alerter.create(InicioSesion.this)
                                                .setTitle("Error")
                                                .setText("La Cuenta NO Tiene Permiso")
                                                .setIcon(R.drawable.logonuevo)
                                                .setTextTypeface(face2)
                                                .enableSwipeToDismiss()
                                                .setBackgroundColorRes(R.color.AzulOscuro)
                                                .show();

                                        break;
                                    case 1:
                                        //El usuario TIENE acceso
                                        SharedPreferences preferencias = InicioSesion.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferencias.edit();
                                        editor.putString("idUsuario", String.valueOf(idUsuarios));

                                        if (recordatorio.isChecked()) {
                                            editor.putBoolean("recordar", true);
                                            editor.putString("correo", correo.getText().toString());
                                            editor.putString("pass", pass.getText().toString());
                                        } else {
                                            editor.putBoolean("recordar", false);
                                            editor.putString("correo", correo.getText().toString());
                                            editor.putString("pass", pass.getText().toString());
                                        }

                                        editor.commit();

                                        Intent intent = new Intent(InicioSesion.this, Principal.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                        finish();
                                        break;
                                }
                            } else {
                                Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/bahnschrift.ttf");
                                Alerter.create(InicioSesion.this)
                                        .setTitle("Error")
                                        .setText("El Usuario o Contraseña erroneos")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face2)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.AzulOscuro)
                                        .show();
                            }
                        }
                    }

                } catch (JSONException e) {
                    Log.i("Error", "" + e);
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user", correo.getText().toString());
                parametros.put("pass", pass.getText().toString());
                return parametros;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
