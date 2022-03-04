package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class QuerysEvaluaciones {
    Context mContext;
    QuerysEvaluaciones.ManejadorQuery mCallback;
    final SharedPreferences preferenciasUsuario;

    public interface ManejadorQuery<T> {
        void onSuccess(T object);

        void onFailure(Exception e);
    }

    public QuerysEvaluaciones(Context context) {
        mContext = context;
        preferenciasUsuario = mContext.getSharedPreferences("sesion", Context.MODE_PRIVATE);
    }


    public void obtenerEvaluaciones(QuerysEvaluaciones.ManejadorQuery callback) {
        mCallback = callback;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_EVALUACION", 0);

            ServicioGeneral servicioGeneral = new ServicioGeneral(mContext);
            servicioGeneral.realizarConsulta("evaluaciones/consulta", jsonObject, new ServicioGeneral.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONArray jsonArray = new JSONArray(object.toString());
                        mCallback.onSuccess(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mCallback.onFailure(e);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    mCallback.onFailure(e);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            mCallback.onFailure(e);
        }
    }

}
