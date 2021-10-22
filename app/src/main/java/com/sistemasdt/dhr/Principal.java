package com.sistemasdt.dhr;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.sistemasdt.dhr.Rutas.Citas.Citas;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.sistemasdt.dhr.Rutas.Inicio.Inicio;
import com.sistemasdt.dhr.OpcionSeguimiento.Seguimiento;
import com.sistemasdt.dhr.Rutas.Catalogos.Catalogos;

import java.util.Timer;

public class Principal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    //Typeface face = Typeface.createFromAsset(getAssets(),"fonts/bahnschrift.ttf");
    Timer timer = new Timer();
    final Handler handler = new Handler();
    private int notifiacionID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bottomNavigationView = findViewById(R.id.menuinferior);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_fichas);

        /*TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AsyncTask mytask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //notificationDialog();
                            }
                        });
                        return null;
                    }
                };
                mytask.execute();
            }
        };
        timer.schedule(task, 0, 7000);*/


    }

    MenuFichas menuFichasFragment = new MenuFichas();
    Citas citasFragment = new Citas();
    ListadoPacientes listadoPacientesFragment = new ListadoPacientes();
    ListadoPacientes listadoPacientesFragment2 = new ListadoPacientes();
    Inicio inicioFragment = new Inicio();
    Seguimiento seguimientoFragment = new Seguimiento();

    Catalogos catalogosFragment = new Catalogos();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_fichas:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, menuFichasFragment).commit();
                return true;
            case R.id.navigation_citas:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, citasFragment).commit();
                return true;
            case R.id.navigation_finanzas:
//                listadoPacientesFragment.ObtenerOpcion(1);
//                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, listadoPacientesFragment).commit();
                Ficha ficha = new Ficha();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, ficha).commit();
                return true;
            case R.id.navigation_perfil:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, inicioFragment).commit();
                return true;
            case R.id.navigation_catalogos:
//                listadoPacientesFragment2.ObtenerOpcion(2);
//                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, listadoPacientesFragment2).commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, catalogosFragment).commit();
                return true;
        }
        return false;
    }

    private void notificationDialog() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            //notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logonuevo)
                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("DHR")
                .setContentText("Tienes una Cita")
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notifiacionID)
                .setSmallIcon(R.drawable.ic_cam)
                .setContentTitle("DHR")
                .setContentText("Tienes una cita")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent resultIntent = new Intent(this, Principal.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notifiacionID, builder.build());*/
    }
}
