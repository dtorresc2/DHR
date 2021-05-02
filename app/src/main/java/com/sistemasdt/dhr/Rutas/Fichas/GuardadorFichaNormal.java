package com.sistemasdt.dhr.Rutas.Fichas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sistemasdt.dhr.R;

public class GuardadorFichaNormal extends Fragment {
    public GuardadorFichaNormal() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guardador_ficha_normal, container, false);
        return view;
    }
}