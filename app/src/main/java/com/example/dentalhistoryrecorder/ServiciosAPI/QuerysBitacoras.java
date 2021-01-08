package com.example.dentalhistoryrecorder.ServiciosAPI;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.R;

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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mContext.getResources().getString(R.string.API) + "bitacora/" + id, new Response.Listener<String>() {
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
