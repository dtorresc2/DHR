package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdon;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdonDos;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialMedico.HistorialMed;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto.HistorialFotografico;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha.Pagos;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.ListadoFichasNormales.ListadoFichas;


public class MenuFichaNormal extends Fragment {
    private Toolbar toolbar;
    private GridLayout menu;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_ficha_normal, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ficha General");
        toolbar.setNavigationIcon(R.drawable.ic_atras);

        toolbar.setNavigationOnClickListener(view1 -> {
            ListadoFichas listadoFichas = new ListadoFichas();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, listadoFichas);
            transaction.commit();
        });

        menu = view.findViewById(R.id.menuingeso);
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
                            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FICHA", Context.MODE_PRIVATE);

                            Ficha ficha = new Ficha();
                            ficha.activarModoEdicion(sharedPreferences.getInt("ID_FICHA", 0));
                            FragmentTransaction transaction2 = getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction2.replace(R.id.contenedor, ficha);
                            transaction2.commit();
                            break;

                        case 2:
                            HistorialMed historialMed = new HistorialMed();
                            FragmentTransaction transaction3 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction3.replace(R.id.contenedor, historialMed);
                            transaction3.commit();

                            break;

                        case 3:
                            HistorialOdon historialOdon = new HistorialOdon();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction.replace(R.id.contenedor, historialOdon);
                            transaction.commit();
                            break;

                        case 4:
                            HistorialOdonDos historialOdonDos = new HistorialOdonDos();
                            FragmentTransaction transaction4 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction4.replace(R.id.contenedor, historialOdonDos);
                            transaction4.commit();
                            break;

                        case 5:
                            Pagos pagos = new Pagos();
                            FragmentTransaction transaction6 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction6.replace(R.id.contenedor, pagos);
                            transaction6.commit();

                            break;

                        case 6:
                            HistorialFotografico historialFotografico = new HistorialFotografico();
                            FragmentTransaction transaction5 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction5.replace(R.id.contenedor, historialFotografico);
                            transaction5.commit();
                            break;
                    }
                }
            });
        }
    }
}