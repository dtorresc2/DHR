package com.sistemasdt.dhr.Rutas.Catalogos.Piezas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferior;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Catalogos;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPiezas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoPiezas extends Fragment {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private PiezasAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ItemPieza> listaPiezas;
    private boolean estadoPieza = false;
    private ItemPieza itemPieza;

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
                switch (menuItem.getItemId()) {
                    case R.id.opcion_nuevo:
                        Piezas piezas = new Piezas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, piezas);
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
                        listarPiezas();
                        return true;

                    default:
                        return false;
                }
            }
        });

        listaPiezas = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listado_piezas);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        listarPiezas();

        return view;
    }

    public void realizarAccion(int opcion, final int ID) {
        switch (opcion) {
            case 1:
                Piezas piezas = new Piezas();
                piezas.editarPieza(ID);
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, piezas);
                transaction.commit();
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Piezas");
                builder.setMessage("¿Desea deshabilitar la pieza?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        deshabilitarPieza(ID);
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

                    mAdapter.setOnItemClickListener(new PiezasAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(final int position) {
                            MenuInferior menuInferior = new MenuInferior();
                            menuInferior.show(getFragmentManager(), "MenuInferior");
                            menuInferior.recibirTitulo(listaPiezas.get(position).getNombrePieza());
                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
                                    estadoPieza = listaPiezas.get(position).getEstadoPieza();
                                    itemPieza = listaPiezas.get(position);
                                    realizarAccion(opcion, listaPiezas.get(position).getCodigoPieza());
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                listarPiezas();
            }
        });
    }

    private void deshabilitarPieza(final int ID) {
        if (estadoPieza) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("NUMERO", itemPieza.getNumeroPieza());
                jsonBody.put("NOMBRE", itemPieza.getNombrePieza());
                jsonBody.put("ESTADO", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            querysPiezas.actualizarPieza(ID, jsonBody, new QuerysPiezas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

                    Alerter.create(getActivity())
                            .setTitle("Deshabilitado correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(typeface)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                    funcionesBitacora.registrarBitacora("Se deshabilito la pieza #" + ID);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            ListadoPiezas listadoPiezas = new ListadoPiezas();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction.replace(R.id.contenedor, listadoPiezas);
                            transaction.commit();
                        }
                    }, 1000);
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
//                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
            Alerter.create(getActivity())
                    .setTitle("La pieza esta deshabilitada")
                    .setIcon(R.drawable.logonuevo)
                    .setTextTypeface(typeface)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.FondoSecundario)
                    .show();
        }
    }
}