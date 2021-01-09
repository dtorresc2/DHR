package com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.dentalhistoryrecorder.Componentes.MenusInferiores.MenuInferior;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysServicios;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoServicios extends Fragment {
    private RecyclerView mRecyclerView;
    private ServiciosAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private boolean estadoServicio = false;
    private ArrayList<ItemServicio> listaServicios;

    public ListadoServicios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_listado_servicios, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Servicios");
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
                        Servicios servicios = new Servicios();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, servicios);
                        transaction.commit();
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
                        listarServicios();
                        return true;

                    default:
                        return false;
                }
            }
        });

        listaServicios = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listado_servicios);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
//        mAdapter = new ServiciosAdapter(listaServicios);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);

        listarServicios();

        return view;
    }

    public void realizarAccion(int opcion, final int ID) {
        switch (opcion) {
            case 1:
                Servicios servicios = new Servicios();
                servicios.editarServicio(ID);
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, servicios);
                transaction.commit();
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Servicios");
                builder.setMessage("¿Desea deshabilitar el servicio?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        actualizarEstado(ID);
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case 3:
                break;
            default:
                return;
        }
    }

    public void listarServicios() {
        listaServicios.clear();
        estadoServicio = false;

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
                        public void onItemClick(final int position) {
                            MenuInferior menuInferior = new MenuInferior();
                            menuInferior.show(getFragmentManager(), "MenuInferior");
                            menuInferior.recibirTitulo(listaServicios.get(position).getDescripcionServicio());
                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
                                    estadoServicio = listaServicios.get(position).getEstadoServicio();
                                    realizarAccion(opcion, listaServicios.get(position).getCodigoServicio());
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

    public void actualizarEstado(int ID) {
        if (estadoServicio) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysServicios querysServicios = new QuerysServicios(getContext());
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("ESTADO", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            querysServicios.actualizarEstado(ID, jsonBody, new QuerysServicios.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    estadoServicio = false;

                    Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setText("Deshabilitado Correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(typeface)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            listarServicios();
                        }
                    }, 1000);
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                }
            });
        } else {
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
            Alerter.create(getActivity())
                    .setTitle("El servicio esta deshabilitado")
                    .setIcon(R.drawable.logonuevo)
                    .setTextTypeface(typeface)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.FondoSecundario)
                    .show();
        }
    }
}