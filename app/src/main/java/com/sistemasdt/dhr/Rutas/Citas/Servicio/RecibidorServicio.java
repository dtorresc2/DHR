package com.sistemasdt.dhr.Rutas.Citas.Servicio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class RecibidorServicio extends BroadcastReceiver {
    private static final int PERIOD_MS = 10000;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleJob(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, NotificacionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(335, serviceComponent);
        builder.setMinimumLatency(PERIOD_MS);
        builder.setOverrideDeadline(PERIOD_MS);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
