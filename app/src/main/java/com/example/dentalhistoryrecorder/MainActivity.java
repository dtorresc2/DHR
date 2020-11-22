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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferencias = MainActivity.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/bahnschrift.ttf");
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if ((preferencias.getBoolean("recordar", false) == true) || (networkInfo != null && networkInfo.isConnected())) {
                    iniciarSesion("http://dhr.sistemasdt.xyz/sesiones.php");

                    /*editor.putString("correo", correo.getText().toString());
                    editor.putString("pass", pass.getText().toString());*/
                } else {
                    //editor.putBoolean("recordar", false);
                    if (networkInfo == null || !networkInfo.isConnected()){
                        Alerter.create(MainActivity.this)
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face2)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }

                    Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }
        }, 3000);
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
                            if (idUsuarios > 0) {
                                int mobil = jsonArray.getJSONObject(i).getInt("movil");
                                switch (mobil) {
                                    case 0:
                                        //El usuario NO tiene permiso;
                                        Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/bahnschrift.ttf");
                                        Alerter.create(MainActivity.this)
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
                                        SharedPreferences preferencias = MainActivity.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferencias.edit();
                                        editor.putString("idUsuario", String.valueOf(idUsuarios));

                                        Intent intent = new Intent(MainActivity.this, Principal.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                        finish();
                                        break;
                                }
                            } else {
                                Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/bahnschrift.ttf");
                                Alerter.create(MainActivity.this)
                                        .setTitle("Error")
                                        .setText("Credenciales Incorrectas")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face2)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.AzulOscuro)
                                        .show();

                                Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
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
                SharedPreferences preferencias2 = MainActivity.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("correo",""));
                parametros.put("pass", preferencias2.getString("pass",""));
                return parametros;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
