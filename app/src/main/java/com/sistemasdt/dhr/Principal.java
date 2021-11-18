package com.sistemasdt.dhr;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.sistemasdt.dhr.Rutas.Citas.ListadoCitas;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.sistemasdt.dhr.Rutas.Inicio.Inicio;
import com.sistemasdt.dhr.OpcionSeguimiento.Seguimiento;
import com.sistemasdt.dhr.Rutas.Catalogos.Catalogos;


public class Principal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bottomNavigationView = findViewById(R.id.menuinferior);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_fichas);
    }

    MenuFichas menuFichasFragment = new MenuFichas();
    ListadoCitas listadoCitasFragment = new ListadoCitas();
    Ficha ficha = new Ficha();
    Inicio inicioFragment = new Inicio();
    Catalogos catalogosFragment = new Catalogos();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_fichas:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, menuFichasFragment).commit();
                return true;
            case R.id.navigation_citas:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, listadoCitasFragment).commit();
                return true;
            case R.id.navigation_finanzas:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, ficha).commit();
                return true;
            case R.id.navigation_perfil:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, inicioFragment).commit();
                return true;
            case R.id.navigation_catalogos:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, catalogosFragment).commit();
                return true;
        }
        return false;
    }
}
