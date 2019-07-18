package com.example.dentalhistoryrecorder;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.dentalhistoryrecorder.OpcionCitas.Citas;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Consultar;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.OpcionInicio.Inicio;
import com.example.dentalhistoryrecorder.OpcionSeguimiento.Seguimiento;

public class Principal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    //Typeface face = Typeface.createFromAsset(getAssets(),"fonts/bahnschrift.ttf");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bottomNavigationView = findViewById(R.id.menuinferior);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    Agregar agregarFragment = new Agregar();
    Citas citasFragment = new Citas();
    Consultar consultarFragment = new Consultar();
    Consultar consultarFragment2 = new Consultar();
    Inicio inicioFragment = new Inicio();
    Seguimiento seguimientoFragment = new Seguimiento();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_agregar:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.contenedor,agregarFragment).commit();
                return true;
            case R.id.navigation_citas:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.contenedor,citasFragment).commit();
                return true;
            case R.id.navigation_consultar:
                consultarFragment.ObtenerOpcion(1);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.contenedor,consultarFragment).commit();
                return true;
            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.contenedor,inicioFragment).commit();
                return true;
            case R.id.navigation_seguimiento:
                consultarFragment2.ObtenerOpcion(2);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.contenedor,consultarFragment2).commit();
                return true;
        }
        return false;
    }
}
