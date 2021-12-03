package com.sistemasdt.dhr.Rutas.Catalogos.Cuentas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorDos;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Catalogos;
import com.sistemasdt.dhr.ServiciosAPI.QuerysCuentas;
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

        toolbar.setNavigationOnClickListener(view1 -> {
            Catalogos catalogos = new Catalogos();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, catalogos);
            transaction.commit();
        });

        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.opcion_nuevo:
                    Cuentas cuentas = new Cuentas();
                    cuentas.enviarCuentas(listaCuentas);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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

                    mAdapter.setOnItemClickListener(position -> {
                        MenuInferiorDos menuInferiorDos = new MenuInferiorDos();
                        menuInferiorDos.show(getActivity().getSupportFragmentManager(), "MenuInferior");
                        menuInferiorDos.recibirTitulo(listaCuentas.get(position).getUsuarioCuenta());
                        menuInferiorDos.eventoClick(opcion -> realizarAccion(opcion, listaCuentas.get(position).getCodigoCuenta()));
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerCuentas();
            }
        });
    }

    public void eliminarCuenta(final int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
        builder.setIcon(R.drawable.logonuevo);
        builder.setTitle("Listado de Cuentas");
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

                            FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                            funcionesBitacora.registrarBitacora("ELIMINACION", "CUENTAS", "Se elimino la cuenta #" + ID);

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
        builder.setNegativeButton("CANCELAR", (dialog, id) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}