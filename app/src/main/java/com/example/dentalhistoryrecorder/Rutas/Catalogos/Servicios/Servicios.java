package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.itextpdf.text.xml.simpleparser.EntitiesToUnicode;

import java.util.regex.Pattern;

public class Servicios extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText descripcionServicio, montoServicio;
    private RadioButton trueServicio, falseServicio;
    private FloatingActionButton guardadorServicio;
    private TextView tituloServicio;

    private boolean modoEdicion;
//    private Typeface typeface;

    public Servicios() {
        modoEdicion = false;
    }

    public void editarServicio(int id) {
        modoEdicion = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servicios, container, false);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Servicio Nuevo");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListadoServicios listadoServicios = new ListadoServicios();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoServicios);
                transaction.commit();
            }
        });

        descripcionServicio = view.findViewById(R.id.descripcionServicio);
        descripcionServicio.setTypeface(typeface);

        descripcionServicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (descripcionRequerida()) {
                    validarDescripcion();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        montoServicio = view.findViewById(R.id.montoServicio);
        montoServicio.setTypeface(typeface);

        montoServicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                montoRequerido();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tituloServicio = view.findViewById(R.id.tituloEstadoServicio);
        tituloServicio.setTypeface(typeface);

        trueServicio = view.findViewById(R.id.trueServicio);
        trueServicio.setTypeface(typeface);

        falseServicio = view.findViewById(R.id.falseServicio);
        falseServicio.setTypeface(typeface);

        guardadorServicio = view.findViewById(R.id.grabarServicio);
        guardadorServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!descripcionRequerida() || !validarDescripcion() || !montoRequerido())
                    return;
            }
        });

        return view;
    }

//    VALIDACIONES
    private boolean descripcionRequerida() {
        String textoCodigo = descripcionServicio.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            descripcionServicio.setError("Campo requerido");
            return false;
        } else {
            descripcionServicio.setError(null);
            return true;
        }
    }

    private boolean montoRequerido() {
        String textCorreo = montoServicio.getText().toString().trim();
        if (textCorreo.isEmpty()) {
            montoServicio.setError("Campo requerido");
            return false;
        } else {
            montoServicio.setError(null);
            return true;
        }
    }

    private boolean validarDescripcion() {
        String textoDescripcion = descripcionServicio.getText().toString().trim();
        Pattern patron = Pattern.compile("^[a-zA-Z0-9 ]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            descripcionServicio.setError(null);
            return true;
        }
        else {
            descripcionServicio.setError("Descripcion invalida");
            return false;
        }
    }

//    private boolean validacionPass() {
//        String textPass = pass.getText().toString().trim();
//        if (textPass.isEmpty()) {
//            pass.setError("Campo requerido");
//            return false;
//        } else {
//            pass.setError(null);
//            return true;
//        }
//    }
}