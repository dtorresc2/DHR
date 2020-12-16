package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalhistoryrecorder.R;

import java.util.ArrayList;

public class ListadoServicios extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ListadoServicios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_servicios, container, false);

        ArrayList<ItemServicio> listaServicios = new ArrayList<>();
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, true));
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, true));
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, true));
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, true));
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, true));

        return view;
    }
}