package com.sistemasdt.dhr.Rutas.Citas.Servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sistemasdt.dhr.R;

public class CitasDiaService extends JobService {
    private String CHANNEL_ID = "235";
    private String CHANNEL_NOMBRE = "DHR_CHANNEL";
    private String CHANNEL_DESCRIPCION = "CANAL PARA EL APP DHR";
    private int notificationId = 133;

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logonuevo)
                .setContentTitle("Citas")
                .setContentText("Tienes 2 cita para hoy")
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
