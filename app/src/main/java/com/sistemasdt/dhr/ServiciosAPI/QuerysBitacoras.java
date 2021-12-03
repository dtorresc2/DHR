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

public class QuerysBitacoras {
    Context mContext;
    private QuerysBitacoras.VolleyOnEventListener<String> mCallBack;

    public interface VolleyOnEventListener<T> {
        void onSuccess(T object);

        void onFailure(Exception e);
    }

    public QuerysBitacoras(Context context){
        mContext = context;
    }

    public void obtenerBitacora(int id, QuerysBitacoras.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "bitacora/" + id,
                response -> mCallBack.onSuccess(response),
                error -> mCallBack.onFailure(error)) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void registrarBitacora(final JSONObject jsonBody, QuerysBitacoras.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + "bitacora",
                response -> mCallBack.onSuccess(response),
                error -> mCallBack.onFailure(error)) {
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
