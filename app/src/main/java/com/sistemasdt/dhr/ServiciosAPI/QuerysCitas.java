package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class QuerysCitas {
    Context mContext;
    private QuerysCitas.VolleyOnEventListener<String> mCallBack;

    public QuerysCitas(Context context) {
        mContext = context;
    }

    public interface VolleyOnEventListener<T> {
        void onSuccess(T object);

        void onFailure(Exception e);
    }

    public void obtenerListadoCitas(final JSONObject jsonBody, QuerysCitas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "citas/consulta",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
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

    public void registrarCita(final JSONObject jsonBody, QuerysCitas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "citas/registro",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
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

    public void actualizarCita(final JSONObject jsonBody, QuerysCitas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "citas/actualiza",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
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

    public void actualizarEstado(final JSONObject jsonBody, QuerysCitas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "citas/actualiza/estado",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
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

    public void eliminarCita(final JSONObject jsonBody, QuerysCitas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "citas/elimina",
                response -> mCallBack.onSuccess(response), error -> mCallBack.onFailure(error)) {
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
}
