package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class QuerysFichas {

    Context mContext;
    private QuerysFichas.VolleyOnEventListener<String> mCallBack;

    public QuerysFichas(Context context) {
        mContext = context;
    }

    public interface VolleyOnEventListener<T> {
        void onSuccess(T object);

        void onSuccessBitmap(Bitmap object);

        void onFailure(Exception e);
    }

    public void obtenerFotosFicha(String URL, QuerysFichas.VolleyOnEventListener callback) {
        mCallBack = callback;

        ImageRequest imageRequest = new ImageRequest(URL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mCallBack.onSuccessBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBack.onFailure(error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(imageRequest);
    }

    public void registrarFicha(final JSONObject jsonBody, QuerysFichas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "fichas", new Response.Listener<String>() {
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

    public void obtenerFichas(int id, QuerysFichas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "fichas/" + id + "/usuario", new Response.Listener<String>() {
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

    public void obtenerFichaEspecifica(int id, QuerysFichas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "fichas/" + id, new Response.Listener<String>() {
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

    public void actualizarFicha(int id, final JSONObject jsonBody, QuerysFichas.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, mContext.getResources().getString(R.string.API) + "fichas/" + id, new Response.Listener<String>() {
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

}
