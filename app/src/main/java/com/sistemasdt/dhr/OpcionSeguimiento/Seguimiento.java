package com.sistemasdt.dhr.OpcionSeguimiento;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdonDos;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha.Pagos;

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

        tratamiento = view.findViewById(R.id.segTratamiento);
        tratamiento.setTypeface(face);

//        pagos = view.findViewById(R.id.segPagos);
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
                            HistorialOdonDos historialOdonDos = new HistorialOdonDos();
//                            historialOdonDos.ObtenerOpcion(2);
                            FragmentTransaction transaction3 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction3.replace(R.id.contenedor, historialOdonDos);
                            transaction3.commit();
                            break;

                        case 2:
                            Pagos pagos = new Pagos();
                            pagos.ObtenerOpcion(2);
                            FragmentTransaction transaction2 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction2.replace(R.id.contenedor, pagos);
                            transaction2.commit();
                            break;

                        case 3:
                            HistorialFotografico ingHFoto = new HistorialFotografico();
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
