package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.R;
import com.tapadoo.alerter.Alerter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FichaEvaluacion extends Fragment {
    private EditText enganche, costo, terapia;
    private FloatingActionButton agregar;
    private Toolbar toolbar;
    private SharedPreferences preferencias;

    public FichaEvaluacion() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ficha_evaluacion, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Costos");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(v -> {
            ListadoEvaluaciones listadoEvaluaciones = new ListadoEvaluaciones();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
            transaction.replace(R.id.contenedor, listadoEvaluaciones);
            transaction.commit();
        });

        preferencias = getActivity().getSharedPreferences("Terapia", Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = preferencias.edit();

        enganche = view.findViewById(R.id.enganche);
        enganche.setTypeface(face);

        costo = view.findViewById(R.id.costoVisita);
        costo.setTypeface(face);

        terapia = view.findViewById(R.id.terapia);
        terapia.setTypeface(face);

        agregar = view.findViewById(R.id.guardador_hm);

        agregar.setOnClickListener(v -> {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.dismiss(), 1000);
        });

        return view;
    }
}
