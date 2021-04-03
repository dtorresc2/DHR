package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


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
}
