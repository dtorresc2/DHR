package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

import android.content.Context;
import android.content.SharedPreferences;

import com.sistemasdt.dhr.ServiciosAPI.QuerysBitacora;

import org.json.JSONException;
import org.json.JSONObject;

public class FuncionesBitacora {
    private Context mContext;

    public FuncionesBitacora(Context context) {
        mContext = context;
    }

    public void registrarBitacora(String evento, String seccion, String accion) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        QuerysBitacora querysBitacora = new QuerysBitacora(mContext);
        JSONObject jsonBodyAux = new JSONObject();
        try {
            jsonBodyAux.put("EVENTO", evento);
            jsonBodyAux.put("ACCION", accion);
            jsonBodyAux.put("SECCION", seccion);
            jsonBodyAux.put("FECHA", " ");
            jsonBodyAux.put("ID_CUENTA", sharedPreferences.getInt("ID_CUENTA", 0));
            jsonBodyAux.put("ID_USUARIO", sharedPreferences.getInt("ID_USUARIO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysBitacora.registrarBitacora(jsonBodyAux, new QuerysBitacora.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
