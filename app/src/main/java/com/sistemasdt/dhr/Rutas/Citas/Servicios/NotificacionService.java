package com.sistemasdt.dhr.Rutas.Citas.Servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Citas.ItemCita;
import com.sistemasdt.dhr.ServiciosAPI.QuerysCitas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificacionService extends JobService {
    private String CHANNEL_ID = "235";
    private String CHANNEL_NOMBRE = "DHR_CHANNEL";
    private String CHANNEL_DESCRIPCION = "CANAL PARA EL APP DHR";
    private int notificationId = 132;
    private QuerysCitas querysCitas;

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(() -> {
            mostrarNotificacion();
            jobFinished(params, false);
        });

        RecibidorServicio.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void mostrarNotificacion() {
        querysCitas = new QuerysCitas(this);

        final SharedPreferences preferenciasUsuario = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatoFechaGeneral = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("FECHA", formatoFechaGeneral.format(calendar.getTime()));
            jsonObject.put("LIMITE", 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysCitas.obtenerListadoCitasCercanas(jsonObject, new QuerysCitas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        crearNotificacion(new ItemCita(
                                jsonArray.getJSONObject(i).getString("ID_CITA"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                jsonArray.getJSONObject(i).getString("NOMBRE_PACIENTE"),
                                jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                jsonArray.getJSONObject(i).getInt("REALIZADO") > 0 ? true : false
                        ));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void crearNotificacion(ItemCita itemCita) {
        String[] aux = itemCita.getMfecha().split(" ");
        String titulo = "Con " + itemCita.getMnombre() + " a las: " + aux[1] + " " + aux[2];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logonuevo)
                .setContentTitle("Tienes una cita cercana")
                .setContentText(titulo)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(itemCita.getMidCitas()), builder.build());
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
