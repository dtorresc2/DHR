package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

import android.graphics.Typeface;
import android.icu.util.ValueIterator;
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

import java.util.regex.Pattern;

public class Piezas extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText nombrePieza, numeroPieza;
    private RadioButton truePieza, falsePieza;
    private FloatingActionButton guardadorPieza;
    private TextView tituloPieza;

    private boolean modoEdicion;
    private Typeface typeface;
    private int ID_PIEZA;

    public Piezas() {
        modoEdicion = false;
    }

    public void editarPieza(int id) {
        modoEdicion = true;
        ID_PIEZA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_piezas, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        if (!modoEdicion)
            toolbar.setTitle("Pieza Nueva");
        else
            toolbar.setTitle("Pieza #" + ID_PIEZA);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListadoPiezas listadoPiezas = new ListadoPiezas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoPiezas);
                transaction.commit();
            }
        });

        nombrePieza = view.findViewById(R.id.nombrePieza);
        nombrePieza.setTypeface(typeface);
        nombrePieza.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nombreRequerido())
                    validarNombre();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numeroPieza = view.findViewById(R.id.numeroPieza);
        numeroPieza.setTypeface(typeface);
        numeroPieza.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (numeroRequerido())
                    validarNumero();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tituloPieza = view.findViewById(R.id.tituloEstadoPieza);
        tituloPieza.setTypeface(typeface);

        truePieza = view.findViewById(R.id.truePieza);
        truePieza.setTypeface(typeface);

        falsePieza = view.findViewById(R.id.falsePieza);
        falsePieza.setTypeface(typeface);

        guardadorPieza = view.findViewById(R.id.grabarPieza);
        guardadorPieza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nombreRequerido() || !numeroRequerido() || !validarNombre() || !validarNumero())
                    return;
            }
        });

        return view;
    }

    //    VALIDACIONES
    private boolean nombreRequerido() {
        String textoCodigo = nombrePieza.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            nombrePieza.setError("Campo requerido");
            return false;
        } else {
            nombrePieza.setError(null);
            return true;
        }
    }

    private boolean numeroRequerido() {
        String textoCodigo = numeroPieza.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            numeroPieza.setError("Campo requerido");
            return false;
        } else {
            numeroPieza.setError(null);
            return true;
        }
    }

    private boolean validarNombre() {
        String textoDescripcion = nombrePieza.getText().toString().trim();
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            nombrePieza.setError(null);
            return true;
        } else {
            nombrePieza.setError("Nombre invalido");
            return false;
        }
    }

    private boolean validarNumero() {
        String textoDescripcion = numeroPieza.getText().toString().trim();
        Pattern patron = Pattern.compile("^[0-9]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            numeroPieza.setError(null);
            return true;
        } else {
            numeroPieza.setError("Numero invalido");
            return false;
        }
    }
}