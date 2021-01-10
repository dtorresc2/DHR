package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class QuerysCuentas {
    Context mContext;
    private VolleyOnEventListener<String> mCallBack;

    public QuerysCuentas(Context context) {
        mContext = context;
    }

    public interface VolleyOnEventListener<T> {
        void onSuccess(T object);

        void onFailure(Exception e);
    }

    public void inicioSesion(final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "cuentas/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    final String mRequestBody = jsonBody.toString();
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void serviciosHabilitados(final int id, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "usuarios/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void obtenerCuenta(int id, int usuario, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "cuentas/" + id + "/usuario/" + usuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void obtenerCuentas(int id, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "cuentas/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void actualizarPerfil(int id, final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, mContext.getResources().getString(R.string.API) + "usuarios/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void registrarCuenta(final JSONObject jsonBody, VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "cuentas", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
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

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, mContext.getResources().getString(R.string.API) + "cuentas/" + id + "/pass", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
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

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, mContext.getResources().getString(R.string.API) + "cuentas/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
}