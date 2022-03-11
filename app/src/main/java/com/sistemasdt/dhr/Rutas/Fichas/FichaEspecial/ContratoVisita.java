package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sistemasdt.dhr.Componentes.Pizarron.Lienzo;
import com.sistemasdt.dhr.R;

import java.io.ByteArrayOutputStream;

public class ContratoVisita extends Fragment {
    private Toolbar toolbar;
    private TextView titulo, parrafo1, parrafo2, parrafo3, parrafo4, parrafo5, parrafo6, parrafo7;
    private FloatingActionButton siguiente;
    private ImageView firma, firmaPaciente;
    private Lienzo lienzo;
    private Lienzo lienzoPaciente;

    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;

    public ContratoVisita() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_contrato_visita, container, false);

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

            View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_firma, null);
            ImageButton limpiar = viewCuadro.findViewById(R.id.limpiar);

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

        firmaPaciente = view.findViewById(R.id.firmaPaciente);
        firmaPaciente.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_firma, null);
            ImageButton limpiar = viewCuadro.findViewById(R.id.limpiar);

            Button cancelar = viewCuadro.findViewById(R.id.cancelar);
            final Button aceptar = viewCuadro.findViewById(R.id.aceptar);

            lienzoPaciente = viewCuadro.findViewById(R.id.lienzo);

            builder.setView(viewCuadro);
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);

            limpiar.setOnClickListener(v1 -> lienzoPaciente.limpiarCanvas());

            cancelar.setOnClickListener(v13 -> dialog.dismiss());

            aceptar.setOnClickListener(v14 -> {
                try {
                    lienzoPaciente.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(lienzoPaciente.getDrawingCache());
                    firmaPaciente.setImageBitmap(bitmap);
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

            // OBTENER FIRMA DEL ODONTOLOGO
            lienzo.setDrawingCacheEnabled(true);
            Bitmap bitmap_aux = Bitmap.createBitmap(lienzo.getDrawingCache());
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
            byte[] b = salida.toByteArray();
            String FIRMA_ODONTOLOGO = Base64.encodeToString(b, Base64.DEFAULT);

            // OBTENER FIRMA DEL PACIENTE
            lienzoPaciente.setDrawingCacheEnabled(true);
            bitmap_aux = Bitmap.createBitmap(lienzoPaciente.getDrawingCache());
            salida = new ByteArrayOutputStream();
            bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
            b = salida.toByteArray();
            String FIRMA_PACIENTE = Base64.encodeToString(b, Base64.DEFAULT);

            progressDialog.dismiss();
        });

        return view;
    }
}