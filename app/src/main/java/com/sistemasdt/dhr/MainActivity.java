package com.sistemasdt.dhr;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.sistemasdt.dhr.Rutas.Citas.Servicio.NotificacionService;
import com.sistemasdt.dhr.Rutas.Citas.Servicio.RecibidorServicio;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Handler().postDelayed(() -> {
            SharedPreferences preferencias = MainActivity.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);

            if (preferencias.getBoolean("recordar", false)) {
                if (preferencias.getInt("ID_USUARIO", 0) != 0 && preferencias.getInt("ID_CUENTA", 0) != 0) {
                    Intent intent = new Intent(MainActivity.this, Principal.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();

                    //Inicializador del Servicio
                    RecibidorServicio.scheduleJob(this);

                } else {
                    Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            } else {
                Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 3000);
    }
}
