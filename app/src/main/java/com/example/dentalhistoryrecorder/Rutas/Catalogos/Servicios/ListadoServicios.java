package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.ItemsFichas;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysCuentas;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysServicios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoServicios extends Fragment  {
    private RecyclerView mRecyclerView;
    private ServiciosAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;

    public ListadoServicios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_servicios, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Listado de Servicios");
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
                switch (menuItem.getItemId()){
                    case R.id.opcion_nuevo:
                        Servicios servicios = new Servicios();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, servicios);
                        transaction.commit();
                        return true;

                    default:
                        return false;
                }
            }
        });

        final ArrayList<ItemServicio> listaServicios = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listado_servicios);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
//        mAdapter = new ServiciosAdapter(listaServicios);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.obtenerListadoServicios(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaServicios.add(new ItemServicio(
                                jsonArray.getJSONObject(i).getInt("ID_SERVICIO"),
                                jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                jsonArray.getJSONObject(i).getDouble("MONTO"),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                        ));
                    }
                    mAdapter = new ServiciosAdapter(listaServicios);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new ServiciosAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            MenuInferior menuInferior = new MenuInferior();
                            menuInferior.show(getFragmentManager(), "MenuInferior");
                            menuInferior.recibirTitulo("Servicio #", listaServicios.get(position).getCodigoServicio());
                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
                                    Toast.makeText(getContext(), String.valueOf(opcion), Toast.LENGTH_SHORT).show();
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

        return view;
    }
}