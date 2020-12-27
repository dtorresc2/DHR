package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;

import java.util.ArrayList;

public class ListadoPiezas extends Fragment {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private PiezasAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ItemPieza> listaPiezas;

    public ListadoPiezas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_piezas, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Piezas");
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Catalogos catalogos = new Catalogos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, catalogos);
                transaction.commit();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        listaPiezas = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listado_piezas);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        listaPiezas.add(new ItemPieza(1, 23, "Muela", true));
        listaPiezas.add(new ItemPieza(1, 23, "Muela", true));
        listaPiezas.add(new ItemPieza(1, 23, "Muela", true));
        listaPiezas.add(new ItemPieza(1, 23, "Muela", true));

        mAdapter = new PiezasAdapter(listaPiezas);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}