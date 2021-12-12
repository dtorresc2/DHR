package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.InicioSesion;
import com.sistemasdt.dhr.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class QuerysCuentas {
    Context mContext;
    private VolleyOnEventListener<String> mCallBack;
    private String TOKEN;

    public QuerysCuentas(Context context) {
        mContext = context;
        final SharedPreferences preferenciasUsuario = mContext.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        TOKEN = preferenciasUsuario.getString("TOKEN", "");
    }

    public interface VolleyOnEventListener<T> {
        void onSuccess(T object);

        void onFailure(Exception e);
    }

    public void inicioSesion(final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "cuentas/login", response -> {
            mCallBack.onSuccess(response);
        }, error -> mCallBack.onFailure(error)) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    final String mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void serviciosHabilitados(final int id, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "usuarios/" + id,
                response -> mCallBack.onSuccess(response),
                error -> mCallBack.onFailure(error)) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void obtenerCuenta(int id, int usuario, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "cuentas/" + id + "/usuario/" + usuario,
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void obtenerCuentas(int id, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "cuentas/" + id,
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void actualizarPerfil(int id, final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, mContext.getResources().getString(R.string.API) + "usuarios/" + id,
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    final String mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void registrarCuenta(final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "cuentas",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    final String mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void actualizarPassword(int id, final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, mContext.getResources().getString(R.string.API) + "cuentas/" + id + "/pass",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    final String mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void eliminarCuenta(int id, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, mContext.getResources().getString(R.string.API) + "cuentas/" + id,
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
            //TOKEN DE AUTENTICACION
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-dhr-token", TOKEN);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
}