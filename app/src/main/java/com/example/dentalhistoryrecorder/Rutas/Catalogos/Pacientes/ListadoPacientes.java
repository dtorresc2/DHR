package com.example.dentalhistoryrecorder.Rutas.Catalogos.Pacientes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.consultarFichas;
import com.example.dentalhistoryrecorder.OpcionIngreso.Especial.IngCostos;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Catalogos;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas.Piezas;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ItemServicio;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
//                        listarPiezas();
                        return true;

                    default:
                        return false;
                }
            }
        });

        listaPacientes = new ArrayList<>();
        listaPacientes.clear();

        buscar = view.findViewById(R.id.consultador);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRecyclerView = view.findViewById(R.id.lista_pacientes);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        listaPacientes.add(new ItemPaciente(1,"Diego Torres", "2", "2",123,"20/10/2020", true, 0.00, 0.00, 0.00));
        listaPacientes.add(new ItemPaciente(1,"Diego Torres", "2", "2",123,"20/10/2020", true, 0.00, 0.00, 0.00));
        listaPacientes.add(new ItemPaciente(1,"Diego Torres", "2", "2",123,"20/10/2020", true, 0.00, 0.00, 0.00));
        listaPacientes.add(new ItemPaciente(1,"Diego Torres", "2", "2",123,"20/10/2020", true, 0.00, 0.00, 0.00));

        mAdapter = new PacienteAdapter(listaPacientes);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        obtenerPacientes();

        return view;
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public void obtenerPacientes() {
        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
        querysPacientes.obtenerPacientes(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysPacientes.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
//                Toast.makeText(getContext(), object.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
