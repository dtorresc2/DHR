package com.sistemasdt.dhr;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private JobScheduler jobScheduler;
    private final int ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS = 335;
    private final int ID_SERVICIO_NOTIFICACIONES_CITAS_DIA = 336;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //MANEJADOR DE SERVICIOS
        jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);


        new Handler().postDelayed(() -> {
            SharedPreferences preferencias = MainActivity.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();

            if (preferencias.getBoolean("recordar", false)) {
                if (preferencias.getInt("ID_USUARIO", 0) != 0 && preferencias.getInt("ID_CUENTA", 0) != 0) {
                    Intent intent = new Intent(MainActivity.this, Principal.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    //FINALIZANDO SERVICIOS
                    jobScheduler.cancel(ID_SERVICIO_NOTIFICACIONES_CITAS_DIA);
                    jobScheduler.cancel(ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS);
                    editor.putBoolean("SERVICIO_CITAS_DIARIAS", false);
                    editor.putBoolean("SERVICIO_CITAS_CERCANAS", false);
                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            } else {
                //FINALIZANDO SERVICIOS
                jobScheduler.cancel(ID_SERVICIO_NOTIFICACIONES_CITAS_DIA);
                jobScheduler.cancel(ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS);
                editor.putBoolean("SERVICIO_CITAS_DIARIAS", false);
                editor.putBoolean("SERVICIO_CITAS_CERCANAS", false);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 3000);
    }
}
