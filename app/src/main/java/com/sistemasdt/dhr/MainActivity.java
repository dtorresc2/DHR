package com.sistemasdt.dhr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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

                if (preferencias.getBoolean("recordar", false)) {
                    if (preferencias.getInt("ID_USUARIO", 0) != 0 && preferencias.getInt("ID_CUENTA", 0) != 0) {
                        Intent intent = new Intent(MainActivity.this, Principal.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                } else {
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
                parametros.put("user", preferencias2.getString("correo", ""));
                parametros.put("pass", preferencias2.getString("pass", ""));
                return parametros;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void pruebaAPI(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("ID_USUARIO", "1");
                    jsonBody.put("USUARIO", "diegot");
                    jsonBody.put("PASSWORD", "321");
                    final String mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return null;

//                try {
//
//                } catch (UnsupportedEncodingExcept ion uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
//                    return null;
//                }
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
