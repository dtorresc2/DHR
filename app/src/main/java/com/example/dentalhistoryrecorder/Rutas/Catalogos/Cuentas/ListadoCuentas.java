package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalhistoryrecorder.Componentes.MenuInferior;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas.PiezasAdapter;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ItemServicio;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ServiciosAdapter;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysCuentas;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysServicios;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ListadoCuentas extends Fragment {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private CuentasAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ItemCuenta> listaCuentas;
    private boolean estadoCuenta = false;

    public ListadoCuentas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_cuentas, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Cuentas");
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
                switch (menuItem.getItemId()) {
                    case R.id.opcion_nuevo:
                        return true;

                    case R.id.opcion_filtrar:
                        MenuItem searchItem = menuItem;
                        SearchView searchView = (SearchView) searchItem.getActionView();

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                mAdapter.getFilter().filter(newText);
                                return false;
                            }
                        });
                        return true;

                    case R.id.opcion_actualizar:
                        obtenerCuentas();
                        return true;

                    default:
                        return false;
                }
            }
        });

        listaCuentas = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listado_cuentas);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        obtenerCuentas();
        return view;
    }

    public void obtenerCuentas(){
        listaCuentas.clear();
        estadoCuenta = false;

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysCuentas querysCuentas = new QuerysCuentas(getContext());
        querysCuentas.obtenerCuentas(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysCuentas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaCuentas.add(new ItemCuenta(
                                jsonArray.getJSONObject(i).getInt("ID_CUENTA"),
                                jsonArray.getJSONObject(i).getString("USUARIO")
                        ));
                    }
                    mAdapter = new CuentasAdapter(listaCuentas);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new CuentasAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(final int position) {
                            MenuInferior menuInferior = new MenuInferior();
                            menuInferior.show(getFragmentManager(), "MenuInferior");
                            menuInferior.recibirTitulo("Servicio #", listaCuentas.get(position).getCodigoCuenta());
                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
//                                    estadoCuenta = listaServicios.get(position).getEstadoServicio();
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