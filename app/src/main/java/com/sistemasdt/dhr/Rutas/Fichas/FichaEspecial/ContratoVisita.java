package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha.Pagos;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.Componentes.Pizarron.Lienzo;
import com.sistemasdt.dhr.R;
import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public class ContratoVisita extends Fragment {
    private Toolbar toolbar;
    private TextView titulo, parrafo1, parrafo2, parrafo3, parrafo4, parrafo5, parrafo6, parrafo7;
    private FloatingActionButton siguiente;
    private ImageView firma;
    private Lienzo lienzo;

    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;

    public ContratoVisita() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_contrato_visita, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Contrato de Compromiso");

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                FichaEvaluacion fichaEvaluacion = new FichaEvaluacion();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, fichaEvaluacion);
                transaction.commit();
            } else {
                ListadoEvaluaciones listadoEvaluaciones = new ListadoEvaluaciones();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, listadoEvaluaciones);
                transaction.commit();
            }
        });

        titulo = view.findViewById(R.id.tituloContrato);

        parrafo1 = view.findViewById(R.id.parrafo1);
        parrafo2 = view.findViewById(R.id.parrafo2);
        parrafo3 = view.findViewById(R.id.parrafo3);
        parrafo4 = view.findViewById(R.id.parrafo4);
        parrafo5 = view.findViewById(R.id.parrafo5);
        parrafo6 = view.findViewById(R.id.parrafo6);
        parrafo7 = view.findViewById(R.id.parrafo7);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parrafo1.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            parrafo2.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            parrafo3.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            parrafo4.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            parrafo5.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            parrafo6.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            parrafo7.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        siguiente = view.findViewById(R.id.siguiente);

        firma = view.findViewById(R.id.firma);
        firma.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
            View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_firma, null);
            ImageButton limpiar = viewCuadro.findViewById(R.id.limpiar);

            TextView titulo = viewCuadro.findViewById(R.id.TituloFirmaDialogo);
            Button cancelar = viewCuadro.findViewById(R.id.cancelar);
            final Button aceptar = viewCuadro.findViewById(R.id.aceptar);

            lienzo = viewCuadro.findViewById(R.id.lienzo);

            builder.setView(viewCuadro);
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);

            limpiar.setOnClickListener(v1 -> lienzo.limpiarCanvas());

            cancelar.setOnClickListener(v13 -> dialog.dismiss());

            aceptar.setOnClickListener(v14 -> {
                try {
                    lienzo.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(lienzo.getDrawingCache());
                    firma.setImageBitmap(bitmap);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            dialog.show();
        });

        siguiente.setOnClickListener(v -> {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.dismiss(), 1000);

            lienzo.setDrawingCacheEnabled(true);
            Bitmap bitmap_aux = Bitmap.createBitmap(lienzo.getDrawingCache());
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
            byte[] b = salida.toByteArray();

            String codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);
        });

        return view;
    }
}
