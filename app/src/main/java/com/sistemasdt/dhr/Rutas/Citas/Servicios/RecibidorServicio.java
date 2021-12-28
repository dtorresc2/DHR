package com.sistemasdt.dhr.Rutas.Citas.Servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class RecibidorServicio extends BroadcastReceiver {
    private static final int PERIOD_MS = 60000;
    private static final int PERIOD_MS2 = 120000;
    private static final int ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS = 335;
    private static final int ID_SERVICIO_NOTIFICACIONES_CITAS_DIA = 336;

    private boolean SERVICIO_NOTIFICACIONES_CITAS_CERCANAS = false;
    private boolean SERVICIO_NOTIFICACIONES_CITAS_DIA = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences preferencias = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SERVICIO_NOTIFICACIONES_CITAS_CERCANAS = preferencias.getBoolean("SERVICIO_CITAS_CERCANAS", false);
        SERVICIO_NOTIFICACIONES_CITAS_DIA = preferencias.getBoolean("SERVICIO_CITAS_DIARIAS", false);

        if (SERVICIO_NOTIFICACIONES_CITAS_CERCANAS)
            scheduleJob(context);

        if (SERVICIO_NOTIFICACIONES_CITAS_DIA)
            servicioNumeroCitas(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, NotificacionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS, serviceComponent);
        builder.setMinimumLatency(PERIOD_MS);
        builder.setOverrideDeadline(PERIOD_MS);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void servicioNumeroCitas(Context context) {
        Calendar objCalendar = Calendar.getInstance();
        objCalendar.set(Calendar.HOUR_OF_DAY, 9);
        objCalendar.set(Calendar.MINUTE, 0);
        objCalendar.set(Calendar.SECOND, 0);
        objCalendar.set(Calendar.MILLISECOND, 0);
        objCalendar.set(Calendar.AM_PM, Calendar.PM);

        ComponentName serviceComponent = new ComponentName(context, CitasDiaService.class);
        JobInfo.Builder builder = new JobInfo.Builder(ID_SERVICIO_NOTIFICACIONES_CITAS_DIA, serviceComponent);
        builder.setMinimumLatency(PERIOD_MS2);
        builder.setOverrideDeadline(PERIOD_MS2);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
