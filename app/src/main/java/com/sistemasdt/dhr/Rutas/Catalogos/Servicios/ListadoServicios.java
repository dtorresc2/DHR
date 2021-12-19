package com.sistemasdt.dhr.Rutas.Catalogos.Servicios;

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
import com.sistemasdt.dhr.ServiciosAPI.QuerysServicios;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_listado_servicios, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setTitle("Listado de Servicios");
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
                    Servicios servicios = new Servicios();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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
        });

        listaServicios = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.listado_servicios);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        listarServicios();

        return view;
    }

    public void realizarAccion(int opcion, final int ID) {
        switch (opcion) {
            case 1:
                Servicios servicios = new Servicios();
                servicios.editarServicio(ID);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, servicios);
                transaction.commit();
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Servicios");
                builder.setMessage(estadoServicio ? "¿Desea deshabilitar el servicio?" : "¿Desea habilitar el servicio?");
                builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstado(ID));
                builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case 3:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                alertDialogBuilder.setIcon(R.drawable.logonuevo);
                alertDialogBuilder.setTitle("Listado de Piezas");
                alertDialogBuilder.setMessage("¿Desea eliminar el servicio?");
                alertDialogBuilder.setPositiveButton("ACEPTAR", (dialog1, which) -> eliminarServicio(ID));
                alertDialogBuilder.setNegativeButton("CANCELAR", (dialog1, id) -> {
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            default:
                return;
        }
    }

    public void listarServicios() {
        listaServicios.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_SERVICIO", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.obtenerListadoServicios(jsonObject, new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaServicios.add(new ItemServicio(
                                jsonArray.getJSONObject(i).getInt("ID_SERVICIO"),
                                jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                jsonArray.getJSONObject(i).getDouble("MONTO"),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false,
                                jsonArray.getJSONObject(i).getInt("FICHAS_NORMALES")
                        ));
                    }
                    mAdapter = new ServiciosAdapter(listaServicios);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(position -> {
                        MenuInferior menuInferior = new MenuInferior();
                        menuInferior.show(getActivity().getSupportFragmentManager(), "MenuInferior");
                        menuInferior.recibirTitulo(listaServicios.get(position).getDescripcionServicio());
                        menuInferior.recibirCantiadFichas(listaServicios.get(position).getCantidadFichasNormales());
                        menuInferior.recibirEstado(listaServicios.get(position).getEstadoServicio());

                        menuInferior.eventoClick(opcion -> {
                            estadoServicio = listaServicios.get(position).getEstadoServicio();
                            realizarAccion(opcion, listaServicios.get(position).getCodigoServicio());
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
                listarServicios();
            }
        });
    }

    public void actualizarEstado(final int ID) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("ESTADO", estadoServicio == true ? "0" : "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysServicios.actualizarEstado(ID, jsonBody, new QuerysServicios.VolleyOnEventListener() {
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
                funcionesBitacora.registrarBitacora("ACTUALIZACION", "SERVICIOS", "Se actualizo el estado del servicio #" + ID);

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    listarServicios();
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void eliminarServicio(final int ID) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysServicios querysServicios = new QuerysServicios(getContext());
        querysServicios.eliminarServicio(ID, new QuerysServicios.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle("Servicios")
                        .setText("Servicio eliminado correctamente")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                progressDialog.dismiss();

                FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                funcionesBitacora.registrarBitacora("ELIMINACION", "SERVICIOS", "Se elimino el servicio #" + ID);

                listarServicios();
            }

            @Override
            public void onFailure(Exception e) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("Fallo al eliminar el servicio")
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