package com.sistemasdt.dhr.OpcionSeguimiento;


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

import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.sistemasdt.dhr.OpcionIngreso.Especial.IngresoPagosVisitas;
import com.sistemasdt.dhr.OpcionIngreso.Especial.IngresoVisitas;
import com.sistemasdt.dhr.R;

public class SeguimientoEspecial extends Fragment {
    private Toolbar toolbar;
    private GridLayout menu;
    private TextView visitas, pagos, fotografias;

    public SeguimientoEspecial() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_seguimiento_especial, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Seguimiento de Fichas Especiales");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoPacientes listadoPacientes = new ListadoPacientes();
                listadoPacientes.ObtenerOpcion(2);
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction2.replace(R.id.contenedor, listadoPacientes);
                transaction2.commit();
            }
        });

        visitas = view.findViewById(R.id.segVisitas);
        visitas.setTypeface(face);

        pagos = view.findViewById(R.id.segPagos);
        pagos.setTypeface(face);

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
                            IngresoVisitas ingresoVisitas = new IngresoVisitas();
                            ingresoVisitas.ObtenerOpcion(2);
                            FragmentTransaction transaction3 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction3.replace(R.id.contenedor, ingresoVisitas);
                            transaction3.commit();
                            break;

                        case 2:
                            IngresoPagosVisitas ingresoPagosVisitas = new IngresoPagosVisitas();
                            ingresoPagosVisitas.ObtenerOpcion(2);
                            FragmentTransaction transaction2 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction2.replace(R.id.contenedor, ingresoPagosVisitas);
                            transaction2.commit();
                            break;

                    }
                }
            });
        }
    }
}
