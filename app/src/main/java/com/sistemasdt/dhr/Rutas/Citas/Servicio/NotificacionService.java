package com.sistemasdt.dhr.Rutas.Citas.Servicio;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificacionService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(() -> {
            Toast.makeText(this, "Mensaje desde el Servicio", Toast.LENGTH_SHORT).show();
            jobFinished(params, false);
        });

        RecibidorServicio.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
