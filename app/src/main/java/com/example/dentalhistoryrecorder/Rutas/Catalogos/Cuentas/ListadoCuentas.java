package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

import com.example.dentalhistoryrecorder.Componentes.MenusInferiores.MenuInferiorCuentas;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysCuentas;
import com.tapadoo.alerter.Alerter;

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
    private Typeface typeface;

    public ListadoCuentas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_cuentas, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

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
                        Cuentas cuentas = new Cuentas();
                        cuentas.enviarCuentas(listaCuentas);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, cuentas);
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

    public void realizarAccion(int opcion, int ID) {
        switch (opcion) {
            case 1:
                Cuentas cuentas = new Cuentas();
                cuentas.editarCuenta(ID);
                cuentas.enviarCuentas(listaCuentas);
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, cuentas);
                transaction.commit();
                break;
            case 2:
                eliminarCuenta(ID);
                break;
            default:
                return;
        }
    }

    public void obtenerCuentas() {
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
            public void onSuccess(final Object object) {
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
                            MenuInferiorCuentas menuInferiorCuentas = new MenuInferiorCuentas();
                            menuInferiorCuentas.show(getFragmentManager(), "MenuInferior");
                            menuInferiorCuentas.recibirTitulo(listaCuentas.get(position).getUsuarioCuenta());
                            menuInferiorCuentas.eventoClick(new MenuInferiorCuentas.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
                                    realizarAccion(opcion, listaCuentas.get(position).getCodigoCuenta());
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

    public void eliminarCuenta(final int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
        builder.setIcon(R.drawable.logonuevo);
        builder.setTitle("Eliminar Cuenta");
        builder.setMessage("Desea eliminar la cuenta?");
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                if (ID != preferenciasUsuario.getInt("ID_CUENTA", 0)) {
                    QuerysCuentas querysCuentas = new QuerysCuentas(getContext());
                    querysCuentas.eliminarCuenta(ID, new QuerysCuentas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            Alerter.create(getActivity())
                                    .setTitle("Cuenta eliminada")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(typeface)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.FondoSecundario)
                                    .show();

                            obtenerCuentas();
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                }
                else {
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("No puede eliminar la cuenta en uso")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(typeface)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}