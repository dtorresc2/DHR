package com.sistemasdt.dhr;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sistemasdt.dhr.ServiciosAPI.QuerysCuentas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;


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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ProgressDialog progressDialog = new ProgressDialog(InicioSesion.this, R.style.progressDialog);
                progressDialog.setMessage("Autenticando...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                final QuerysCuentas querysCuentas = new QuerysCuentas(getApplicationContext());
                querysCuentas.inicioSesion(jsonBody, new QuerysCuentas.VolleyOnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(object.toString());

                            if (!jsonObject.has("ID_USUARIO") || jsonObject.getInt("ESTADO") == 0) {
                                Alerter.create(InicioSesion.this)
                                        .setTitle("Error")
                                        .setText("Credenciales Incorrectas")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(typeface)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.FondoSecundario)
                                        .show();
                                return;
                            }

                            final int id_usuario = jsonObject.getInt("ID_USUARIO");
                            final int id_cuenta = jsonObject.getInt("ID_CUENTA");

                            querysCuentas.serviciosHabilitados(id_usuario, new QuerysCuentas.VolleyOnEventListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(object.toString());
                                        if (jsonObject.getInt("MOVIL") != 1) {
                                            Alerter.create(InicioSesion.this)
                                                    .setTitle("Error")
                                                    .setText("No tienene acceso")
                                                    .setIcon(R.drawable.logonuevo)
                                                    .setTextTypeface(typeface)
                                                    .enableSwipeToDismiss()
                                                    .setBackgroundColorRes(R.color.FondoSecundario)
                                                    .show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    SharedPreferences preferencias = InicioSesion.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferencias.edit();
                                    editor.putInt("ID_USUARIO", id_usuario);
                                    editor.putInt("ID_CUENTA", id_cuenta);

                                    if (recordatorio.isChecked()) {
                                        editor.putBoolean("recordar", true);
                                        editor.putString("codigo", usuario.getText().toString());
                                        editor.putString("correo", correo.getText().toString());
                                        editor.putString("pass", pass.getText().toString());
                                    } else {
                                        editor.putBoolean("recordar", false);
                                        editor.putString("codigo", usuario.getText().toString());
                                        editor.putString("correo", correo.getText().toString());
                                        editor.putString("pass", pass.getText().toString());
                                    }

                                    editor.commit();

                                    Intent intent = new Intent(InicioSesion.this, Principal.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                    finish();
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
                                    progressDialog.dismiss();
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
                        progressDialog.dismiss();
                    }
                });
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
}
