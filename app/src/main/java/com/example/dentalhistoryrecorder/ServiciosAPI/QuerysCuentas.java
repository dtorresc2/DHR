package com.example.dentalhistoryrecorder.ServiciosAPI;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
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

    public void pruebaAPI(String URL, VolleyOnEventListener callback, final int numero) {
        mCallBack = callback;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
//                Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                mCallBack.onSuccess(response + " " + numero);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
                mCallBack.onFailure(error);
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


        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
}

