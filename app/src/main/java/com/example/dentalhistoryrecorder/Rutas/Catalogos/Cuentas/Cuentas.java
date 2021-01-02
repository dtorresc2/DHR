package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalhistoryrecorder.R;

public class Cuentas extends Fragment {
    public Cuentas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cuentas, container, false);
        return view;
    }
}