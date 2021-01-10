package com.example.dentalhistoryrecorder.Componentes.Dialogos.Bitacora;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysBitacoras;

import org.json.JSONException;
import org.json.JSONObject;

public class FuncionesBitacora {
    private Context mContext;

    public FuncionesBitacora(Context context){
        mContext = context;
    }

    public void registrarBitacora(String accion) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        QuerysBitacoras querysBitacoras = new QuerysBitacoras(mContext);
        JSONObject jsonBodyAux = new JSONObject();
        try {
            jsonBodyAux.put("ACCION", accion);
            jsonBodyAux.put("FECHA", " ");
            jsonBodyAux.put("ID_CUENTA", sharedPreferences.getInt("ID_CUENTA", 0));
            jsonBodyAux.put("ID_USUARIO", sharedPreferences.getInt("ID_USUARIO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysBitacoras.registrarBitacora(jsonBodyAux, new QuerysBitacoras.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
