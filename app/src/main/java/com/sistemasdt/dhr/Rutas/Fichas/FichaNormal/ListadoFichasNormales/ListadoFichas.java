package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.ListadoFichasNormales;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferior;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorFicha;
import com.sistemasdt.dhr.R;

import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.Pacientes;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Adaptadores.AdaptadorConsultaFicha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Items.ItemsFichas;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ListadoFichas extends Fragment {
    private RecyclerView listafichas;
    private Toolbar toolbar;
    private AdaptadorConsultaFicha adapter;
    private RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;
    private static final String TAG = "MyActivity";
    private String idPaciente;
    ArrayList<ItemsFichas> lista;
    private boolean estadoFicha = false;


    private int mOpcion = 0;

    @SuppressLint("ValidFragment")
    public ListadoFichas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_fichas, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Fichas Generales");

        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setNavigationOnClickListener(view1 -> {
            MenuFichas menuFichas = new MenuFichas();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, menuFichas);
            transaction.commit();
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.opcion_nuevo:
                        Ficha ficha = new Ficha();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, ficha);
                        transaction.commit();
                        return true;

                    case R.id.opcion_filtrar:
                        MenuItem searchItem = item;
                        SearchView searchView = (SearchView) searchItem.getActionView();

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                adapter.getFilter().filter(newText);
                                return false;
                            }
                        });
                        return true;

                    case R.id.opcion_actualizar:
                        obtenerFichas();
                        return true;

                    default:
                        return false;

                }
            }
        });

        lista = new ArrayList<>();
        listafichas = view.findViewById(R.id.lista_fichas);
        listafichas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        obtenerFichas();
        return view;
    }

    public void obtenerFichas() {
        lista.clear();
        estadoFicha = false;

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysFichas querysFichas = new QuerysFichas(getContext());
        querysFichas.obtenerFichas(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        lista.add(new ItemsFichas(
                                jsonArray.getJSONObject(i).getInt("ID_FICHA"),
                                jsonArray.getJSONObject(i).getString("NOMBRE"),
                                jsonArray.getJSONObject(i).getString("MOTIVO"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("DEBE")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("HABER")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("SALDO")),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false
                        ));
                    }

                    adapter = new AdaptadorConsultaFicha(lista);
                    listafichas.setLayoutManager(layoutManager);
                    listafichas.setAdapter(adapter);

                    adapter.setOnItemClickListener(new AdaptadorConsultaFicha.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
//                            MenuInferior menuInferior = new MenuInferior();
//                            menuInferior.show(getActivity().getSupportFragmentManager(), "MenuInferior");
//                            menuInferior.recibirTitulo(lista.get(position).getMotivo());
//                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
//                                @Override
//                                public void onButtonClicked(int opcion) {
//                                    estadoFicha = lista.get(position).getEstado();
//                                    realizarAccion(opcion, lista.get(position).getId(), position);
//                                }
//                            });

                            MenuInferiorFicha menuInferiorFicha = new MenuInferiorFicha();
                            menuInferiorFicha.show(getActivity().getSupportFragmentManager(), "MenuInferiorFicha");
                            menuInferiorFicha.recibirTitulo(lista.get(position).getMotivo());
                            estadoFicha = lista.get(position).getEstado();
                            menuInferiorFicha.recibirEstado(estadoFicha);
                            menuInferiorFicha.eventoClick(opcion -> realizarAccion(opcion, lista.get(position).getId(), position));
                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerFichas();
            }
        });
    }

    public void realizarAccion(int opcion, int ID, final int posicion) {
        switch (opcion) {
            case 1:
                final SharedPreferences preferenciasFicha = getActivity().getSharedPreferences("FICHA", Context.MODE_PRIVATE);
                final SharedPreferences.Editor escritor = preferenciasFicha.edit();
                escritor.putInt("ID_FICHA", ID);
                escritor.commit();

                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, menuFichaNormal);
                transaction.commit();
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Fichas");
                builder.setMessage("¿Desea deshabilitar la ficha?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        actualizarEstado(posicion);
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
}
