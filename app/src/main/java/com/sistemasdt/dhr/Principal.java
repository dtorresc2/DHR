package com.sistemasdt.dhr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.sistemasdt.dhr.Rutas.Citas.ListadoCitas;
import com.sistemasdt.dhr.Rutas.Citas.Servicios.RecibidorServicio;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Rutas.Inicio.Inicio;
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

        //Inicializador del Servicios
        final SharedPreferences preferencias = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        if (!preferencias.contains("SERVICIO_CITAS_CERCANAS")) {
            RecibidorServicio.scheduleJob(this);
            editor.putBoolean("SERVICIO_CITAS_CERCANAS", true);
            editor.commit();
        }

        if (!preferencias.contains("SERVICIO_CITAS_DIARIAS")) {
            RecibidorServicio.servicioNumeroCitas(this);
            editor.putBoolean("SERVICIO_CITAS_DIARIAS", true);
            editor.commit();
        }
    }

    MenuFichas menuFichasFragment = new MenuFichas();
    ListadoCitas listadoCitasFragment = new ListadoCitas();
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
//                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.contenedor, ficha).commit();
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
