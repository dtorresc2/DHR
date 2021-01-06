package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.Componentes.MenusInferiores.MenuInferior;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysPacientes;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ListadoPacientes extends Fragment {
    private EditText pnombre, papellido;
    private FloatingActionButton buscar;
    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private PacienteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RequestQueue requestQueue;
    private static final String TAG = "MyActivity";
    SharedPreferences preferencias;
    private int mOpcion = 0;
    private TextView etiquetaN, etiquetaE;

    private ArrayList<ItemPaciente> listaPacientes;

    public ListadoPacientes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_listado_pacientes, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Pacientes");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
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
                        Pacientes pacientes = new Pacientes();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, pacientes);
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
                        obtenerPacientes();
                        return true;

                    default:
                        return false;
                }
            }
        });

        listaPacientes = new ArrayList<>();

        buscar = view.findViewById(R.id.consultador);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRecyclerView = view.findViewById(R.id.lista_pacientes);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        obtenerPacientes();
        return view;
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public void obtenerPacientes() {
        listaPacientes.clear();
//        estadoServicio = false;

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        querysPacientes.obtenerPacientes(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaPacientes.add(new ItemPaciente(
                                jsonArray.getJSONObject(i).getInt("ID_PACIENTE"),
                                jsonArray.getJSONObject(i).getString("NOMBRE"),
                                jsonArray.getJSONObject(i).getString("TELEFONO"),
                                jsonArray.getJSONObject(i).getString("DPI"),
                                jsonArray.getJSONObject(i).getInt("EDAD"),
                                jsonArray.getJSONObject(i).getString("FECHA_NACIMIENTO"),
                                (jsonArray.getJSONObject(i).getInt("ESTADO") > 0) ? true : false,
                                jsonArray.getJSONObject(i).getDouble("DEBE"),
                                jsonArray.getJSONObject(i).getDouble("HABER"),
                                jsonArray.getJSONObject(i).getDouble("SALDO")
                        ));
                    }

                    mAdapter = new PacienteAdapter(listaPacientes);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new PacienteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final int position) {
                            MenuInferior menuInferior = new MenuInferior();
                            menuInferior.show(getFragmentManager(), "MenuInferior");
                            menuInferior.recibirTitulo("Paciente #", listaPacientes.get(position).getCodigo());
                            menuInferior.eventoClick(new MenuInferior.MenuInferiorListener() {
                                @Override
                                public void onButtonClicked(int opcion) {
//                                    estadoServicio = listaServicios.get(position).getEstadoServicio();
//                                    realizarAccion(opcion, listaServicios.get(position).getCodigoServicio());
                                }
                            });
                        }
                    });

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Catalogos catalogos = new Catalogos();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, catalogos);
                    transaction.commit();
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
