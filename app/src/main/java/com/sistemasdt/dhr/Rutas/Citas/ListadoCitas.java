package com.sistemasdt.dhr.Rutas.Citas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuCitas;
import com.sistemasdt.dhr.Rutas.Citas.Adaptador.AdaptadorCita;
import com.sistemasdt.dhr.R;

import com.sistemasdt.dhr.ServiciosAPI.QuerysCitas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoCitas extends Fragment {
    private Toolbar toolbar;
    private RecyclerView lista_pacientes;
    private AdaptadorCita adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ListadoCitas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listado_citas, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Citas Disponibles");
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.opcion_nuevo:
                    Citas citas = new Citas();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, citas);
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
                    obtenerCitas();
                    return true;

                default:
                    return false;
            }
        });

        lista_pacientes = view.findViewById(R.id.listaCitas);

        obtenerCitas();

        return view;
    }


    public void obtenerCitas() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_CITA", 0);

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.obtenerListadoCitas(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONArray jsonArray = new JSONArray(object.toString());
                        final ArrayList<ItemCita> lista = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            lista.add(new ItemCita(
                                    jsonArray.getJSONObject(i).getString("ID_CITA"),
                                    jsonArray.getJSONObject(i).getString("FECHA"),
                                    jsonArray.getJSONObject(i).getString("NOMBRE_PACIENTE"),
                                    jsonArray.getJSONObject(i).getString("DESCRIPCION"),
                                    jsonArray.getJSONObject(i).getInt("REALIZADO") > 0 ? true : false
                            ));
                        }
                        lista_pacientes.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorCita(lista);
                        lista_pacientes.setLayoutManager(layoutManager);
                        lista_pacientes.setAdapter(adapter);

                        adapter.setOnItemClickListener(position -> {
                            MenuCitas menuCitas = new MenuCitas();
                            menuCitas.show(getActivity().getSupportFragmentManager(), "MenuCitas");
                            menuCitas.recibirTitulo(lista.get(position).getMdescripcion());
                            menuCitas.recibirEstado(lista.get(position).getMrealizado());
                            menuCitas.eventoClick(opcion -> {
//                                    estadoFicha = lista.get(position).getEstado();
                                realizarAccion(opcion, Integer.parseInt(lista.get(position).getMidCitas()), position, lista.get(position).getMrealizado());
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
                    obtenerCitas();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void realizarAccion(int opcion, int ID, final int posicion, boolean estado) {
        switch (opcion) {
            case 1:
                Citas citas = new Citas();
                citas.activarModoEdicion(ID);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, citas);
                transaction.commit();
                break;

            case 2:
                if (estado) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Listado de Citas");
                    builder.setMessage("¿Desea terminar la cita?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstadoCita(ID, estado));
                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Listado de Citas");
                    builder.setMessage("¿Desea completar la cita?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> actualizarEstadoCita(ID, estado));
                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;

            case 4:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Citas");
                builder.setMessage("¿Desea eliminar la cita?");
                builder.setPositiveButton("ACEPTAR", (dialog, id) -> eliminarCita(ID));
                builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                break;
        }
    }

    private void eliminarCita(int ID) {
        try {
            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_CITA", ID);

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.eliminarCita(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Citas")
                            .setText("Cita eliminada correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    obtenerCitas();
                }

                @Override
                public void onFailure(Exception e) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Fallo al eliminar la cita")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizarEstadoCita(int ID, boolean estado) {
        try {
            final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
            jsonObject.put("ID_CITA", ID);
            jsonObject.put("REALIZADO", estado == true ? "0" : "1");

            QuerysCitas querysCitas = new QuerysCitas(getContext());
            querysCitas.actualizarEstado(jsonObject, new QuerysCitas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Citas")
                            .setText("Cita actualizada correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    obtenerCitas();
                }

                @Override
                public void onFailure(Exception e) {
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Fallo al actualizar la cita")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}