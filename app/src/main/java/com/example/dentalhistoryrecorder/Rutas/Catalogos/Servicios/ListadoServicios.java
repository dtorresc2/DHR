package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysCuentas;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysServicios;

import org.json.JSONException;
import org.json.JSONObject;

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
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, false));
        listaServicios.add(new ItemServicio(1, "Hola Es una prueba", 25.00, false));

        mRecyclerView = view.findViewById(R.id.listado_servicios);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ServiciosAdapter(listaServicios);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.obtenerListadoServicios(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(getContext(), object.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        return view;
    }

}