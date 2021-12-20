package com.sistemasdt.dhr.Rutas.Citas.Servicio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sistemasdt.dhr.R;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificacionService extends JobService {
    private String CHANNEL_ID = "234";
    private String CHANNEL_NOMBRE = "DHR_CHANNEL";
    private String CHANNEL_DESCRIPCION = "CANAL PARA EL APP DHR";
    private int notificationId = 132;

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logonuevo)
                .setContentTitle("Citas")
                .setContentText("Notificacion desde el Servicio")
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
