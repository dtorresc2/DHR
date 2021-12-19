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
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
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
        toolbar.setTitle("Listado de Piezas");
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
                    Piezas piezas = new Piezas();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, piezas);
                transaction.commit();
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Piezas");
                builder.setMessage(estadoPieza ? "¿Desea deshabilitar la pieza?" : "¿Desea habilitar la pieza?");
                builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstadoPieza(ID));
                builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case 3:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                alertDialogBuilder.setIcon(R.drawable.logonuevo);
                alertDialogBuilder.setTitle("Listado de Piezas");
                alertDialogBuilder.setMessage("¿Desea eliminar la pieza?");
                alertDialogBuilder.setPositiveButton("ACEPTAR", (dialog1, which) -> eliminarPieza(ID));
                alertDialogBuilder.setNegativeButton("CANCELAR", (dialog1, id) -> {
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            default:
                return;
        }
    }

    public void listarPiezas() {
        listaPiezas.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_PIEZA", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        querysPiezas.obtenerListadoPiezas(jsonObject, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaPiezas.add(new ItemPieza(
                                jsonArray.getJSONObject(i).getInt("ID_PIEZA"),
                                jsonArray.getJSONObject(i).getInt("NUMERO"),
                                jsonArray.getJSONObject(i).getString("NOMBRE"),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false,
                                jsonArray.getJSONObject(i).getInt("FICHAS_NORMALES")
                        ));
                    }
                    mAdapter = new PiezasAdapter(listaPiezas);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(position -> {
                        MenuInferior menuInferior = new MenuInferior();
                        menuInferior.show(getActivity().getSupportFragmentManager(), "MenuInferior");
                        menuInferior.recibirTitulo(listaPiezas.get(position).getNombrePieza());
                        menuInferior.recibirCantiadFichas(listaPiezas.get(position).getNumeroFichas());
                        menuInferior.recibirEstado(listaPiezas.get(position).getEstadoPieza());

                        menuInferior.eventoClick(opcion -> {
                            estadoPieza = listaPiezas.get(position).getEstadoPieza();
                            realizarAccion(opcion, listaPiezas.get(position).getCodigoPieza());
                        });
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

    private void actualizarEstadoPieza(final int ID) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("ID_PIEZA", ID);
            jsonBody.put("ESTADO", estadoPieza == true ? "0" : "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysPiezas.actualizarEstadoPieza(jsonBody, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

                Alerter.create(getActivity())
                        .setTitle("Actualizado correctamente")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(typeface)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("ACTUALIZACION", "PIEZAS", "Se actualizo el estado de la pieza #" + ID);

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    ListadoPiezas listadoPiezas = new ListadoPiezas();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, listadoPiezas);
                    transaction.commit();
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void eliminarPieza(final int ID) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysPiezas querysPiezas = new QuerysPiezas(getContext());
        querysPiezas.eliminarPieza(ID, new QuerysPiezas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle("Piezas")
                        .setText("Pieza eliminada correctamente")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                progressDialog.dismiss();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("ELIMINACION", "PIEZAS", "Se elimino la pieza #" + ID);

                listarPiezas();
            }

            @Override
            public void onFailure(Exception e) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("Fallo al eliminar la pieza")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
                progressDialog.dismiss();

            }
        });
    }
}