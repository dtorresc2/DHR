package com.sistemasdt.dhr.Rutas.Citas.Servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.ServiciosAPI.QuerysCitas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CitasDiaService extends JobService {
    private String CHANNEL_ID = "235";
    private String CHANNEL_NOMBRE = "DHR_CHANNEL";
    private String CHANNEL_DESCRIPCION = "CANAL PARA EL APP DHR";
    private int notificationId = 133;
    private QuerysCitas querysCitas;

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(() -> {
            mostrarNotificacion();
            jobFinished(params, false);
        });

        RecibidorServicio.servicioNumeroCitas(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void mostrarNotificacion() {
        querysCitas = new QuerysCitas(this);

        final SharedPreferences preferenciasUsuario = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatoFechaGeneral = new SimpleDateFormat("yyyy-MM-dd");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("FECHA", formatoFechaGeneral.format(calendar.getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysCitas.obtenerListadoCitasDia(jsonObject, new QuerysCitas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    if (jsonArray.length() > 0)
                        crearNotificacion(jsonArray.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void crearNotificacion(int conteo) {
        String titulo = "";
        if (conteo == 1) {
            titulo = "Tienes 1 cita para hoy";
        }
        if (conteo > 1) {
            titulo = "Tienes " + conteo + " citas para hoy";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logonuevo)
                .setContentTitle("Citas")
                .setContentText(titulo)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NOMBRE;
            String description = CHANNEL_DESCRIPCION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
