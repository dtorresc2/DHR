package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ServicioGeneral {
    Context mContext;
    private ServicioGeneral.VolleyOnEventListener<String> mCallBack;

    private String TOKEN;

    public interface VolleyOnEventListener<T> {
        void onSuccess(T object);

        void onFailure(Exception e);
    }

    public ServicioGeneral(Context context) {
        mContext = context;
        final SharedPreferences preferenciasUsuario = mContext.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        TOKEN = preferenciasUsuario.getString("TOKEN", "");
    }

    public void realizarConsulta(final String ruta, final JSONObject jsonBody, ServicioGeneral.VolleyOnEventListener callback) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mContext.getResources().getString(R.string.API) + ruta,
                response -> mCallBack.onSuccess(response),
                error -> mCallBack.onFailure(error)) {

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
}
