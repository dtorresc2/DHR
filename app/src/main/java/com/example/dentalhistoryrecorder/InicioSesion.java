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
    private TextInputEditText correo, pass;
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
        correo = (TextInputEditText) findViewById(R.id.correo);
        correo.setTypeface(typeface);
        pass = (TextInputEditText) findViewById(R.id.pass);
        pass.setTypeface(typeface);
        boton = (Button) findViewById(R.id.aceptador);
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
                if (!correo.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("ID_USUARIO", "1");
                        jsonBody.put("USUARIO", "diegot");
                        jsonBody.put("PASSWORD", "321");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final QuerysCuentas querysCuentas = new QuerysCuentas(getApplicationContext());
                    querysCuentas.inicioSesion(jsonBody, new QuerysCuentas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            try {
                                JSONObject jsonObject = new JSONObject(object.toString());
//                                Toast.makeText(getApplicationContext(), jsonObject.getString("ID"), Toast.LENGTH_SHORT).show();
                                querysCuentas.serviciosHabilitados(jsonObject.getInt("ID"), new QuerysCuentas.VolleyOnEventListener() {
                                    @Override
                                    public void onSuccess(Object object) {
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
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    });
                } else {
                    Alerter.create(InicioSesion.this)
                            .setTitle("Error")
                            .setText("Faltan datos")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(typeface)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });
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
