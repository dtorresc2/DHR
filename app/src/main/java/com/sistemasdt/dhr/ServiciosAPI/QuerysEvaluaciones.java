package com.sistemasdt.dhr.ServiciosAPI;

import android.content.Context;
import android.content.SharedPreferences;

import com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Adaptadores.ItemEvaluacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class QuerysEvaluaciones {
    Context mContext;
    QuerysEvaluaciones.ManejadorQuery mCallback;
    final SharedPreferences preferenciasUsuario;

    public interface ManejadorQuery<T> {
        void onSuccess(ArrayList<ItemEvaluacion> arrayList);

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
                        ArrayList<ItemEvaluacion> lista = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            lista.add(new ItemEvaluacion(
                                    jsonArray.getJSONObject(i).getInt("ID_EVALUACION"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE_PACIENTE"),
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    jsonArray.getJSONObject(i).getString("FECHA"),
                                    Double.parseDouble(jsonArray.getJSONObject(i).getString("DEBE")),
                                    Double.parseDouble(jsonArray.getJSONObject(i).getString("HABER")),
                                    Double.parseDouble(jsonArray.getJSONObject(i).getString("SALDO")),
                                    (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                            ));
                        }

                        mCallback.onSuccess(lista);
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
