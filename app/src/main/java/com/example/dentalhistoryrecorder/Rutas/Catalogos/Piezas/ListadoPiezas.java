package com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.dentalhistoryrecorder.Componentes.MenuInferior;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ItemServicio;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ServiciosAdapter;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysPiezas;

import org.json.JSONArray;
import org.json.JSONException;

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

        listarPiezas();

        return view;
    }

    public void listarPiezas() {
        listaPiezas.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        querysPiezas.obtenerListadoPiezas(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaPiezas.add(new ItemPieza(
                                jsonArray.getJSONObject(i).getInt("ID_PIEZA"),
                                jsonArray.getJSONObject(i).getInt("NUMERO"),
                                jsonArray.getJSONObject(i).getString("NOMBRE"),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                        ));
                    }
                    mAdapter = new PiezasAdapter(listaPiezas);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

//                    mAdapter.setOnItemClickListener(new ServiciosAdapter.OnClickListener() {
//                        @Override
//                        public void onItemClick(final int position) {
//                            MenuInferior menuInferior = new MenuInferior();
//                            menuInferior.show(getFragmentManager(), "MenuInferior");
//                            menuInferior.recibirTitulo("Servicio #", listaServicios.get(position).getCodigoServicio());
//                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
//                                @Override
//                                public void onButtonClicked(int opcion) {
//                                    estadoServicio = listaServicios.get(position).getEstadoServicio();
//                                    realizarAccion(opcion, listaServicios.get(position).getCodigoServicio());
//                                }
//                            });
//                        }
//                    });

                    mAdapter.setOnItemClickListener(new PiezasAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            MenuInferior menuInferior = new MenuInferior();
                            menuInferior.show(getFragmentManager(), "MenuInferior");
                            menuInferior.recibirTitulo("Pieza #", listaPiezas.get(position).getNumeroPieza());
                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
                                    Toast.makeText(getContext(), String.valueOf(opcion), Toast.LENGTH_SHORT).show();
//                                    estadoServicio = listaServicios.get(position).getEstadoServicio();
//                                    realizarAccion(opcion, listaServicios.get(position).getCodigoServicio());
                                }
                            });
                        }
                    });

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Catalogos catalogos = new Catalogos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, catalogos);
                transaction.commit();
                e.printStackTrace();
            }
        });
    }
}