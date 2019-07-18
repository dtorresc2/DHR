package com.example.dentalhistoryrecorder.OpcionSeguimiento;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Consultar;
import com.example.dentalhistoryrecorder.OpcionIngreso.Normal.IngHOdon;
import com.example.dentalhistoryrecorder.OpcionIngreso.Normal.Ing_HFoto;
import com.example.dentalhistoryrecorder.R;

public class Seguimiento extends Fragment {
    private Toolbar toolbar;
    private GridLayout menu;
    private TextView tratamiento, pagos, fotografias;

    public Seguimiento() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seguimiento, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Seguimiento de Fichas");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consultar consultar = new Consultar();
                consultar.ObtenerOpcion(2);
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction2.replace(R.id.contenedor, consultar);
                transaction2.commit();
            }
        });

        tratamiento = view.findViewById(R.id.segTratamiento);
        tratamiento.setTypeface(face);

        pagos = view.findViewById(R.id.segPagos);
        pagos.setTypeface(face);

        fotografias = view.findViewById(R.id.segFotografias);
        fotografias.setTypeface(face);

        menu = view.findViewById(R.id.menuSeguimiento);
        setSingleEvent(menu);


        return view;
    }

    private void setSingleEvent(GridLayout singleEvent) {
        for (int i = 0; i < singleEvent.getChildCount(); i++) {
            CardView cardView = (CardView) singleEvent.getChildAt(i);
            final int dato = i + 1;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (dato) {
                        case 1:
                            IngHOdon ingHOdon = new IngHOdon();
                            ingHOdon.ObtenerOpcion(2);
                            FragmentTransaction transaction3 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction3.replace(R.id.contenedor, ingHOdon);
                            transaction3.commit();
                            break;

                        case 2:
                            SegPagos segPagos = new SegPagos();
                            FragmentTransaction transaction2 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction2.replace(R.id.contenedor, segPagos);
                            transaction2.commit();
                            break;

                        case 3:
                            Ing_HFoto ingHFoto = new Ing_HFoto();
                            ingHFoto.ObtenerOpcion(2);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction.replace(R.id.contenedor, ingHFoto);
                            transaction.commit();
                            break;

                    }
                }
            });
        }
    }
}
