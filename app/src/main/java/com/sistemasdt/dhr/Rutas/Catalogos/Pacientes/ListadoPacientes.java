package com.sistemasdt.dhr.Rutas.Catalogos.Pacientes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferior;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Catalogos;
import com.sistemasdt.dhr.ServiciosAPI.QuerysPacientes;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoPacientes extends Fragment {
    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private PacienteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RequestQueue requestQueue;
    private static final String TAG = "MyActivity";
    SharedPreferences preferencias;
    private int mOpcion = 0;
    private boolean estadoPaciente = false;

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

        toolbar.setNavigationOnClickListener(view1 -> {
            Catalogos catalogos = new Catalogos();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, catalogos);
            transaction.commit();
        });

        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.opcion_nuevo:
                    Pacientes pacientes = new Pacientes();
                    pacientes.enviarPacientes(listaPacientes);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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
        });

        listaPacientes = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.lista_pacientes);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        obtenerPacientes();
        return view;
    }

    public void realizarAccion(int opcion, int ID, final int posicion) {
        switch (opcion) {
            case 1:
                Pacientes pacientes = new Pacientes();
                pacientes.editarPaciente(ID);
                pacientes.enviarPacientes(listaPacientes);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, pacientes);
                transaction.commit();
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Listado de Pacientes");
                builder.setMessage("¿Desea deshabilitar al paciente?");
                builder.setPositiveButton("ACEPTAR", (dialog, id) -> {
                    // User cancelled the dialog
                    actualizarEstado(posicion);
                });
                builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    // User cancelled the dialog
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

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public void obtenerPacientes() {
        listaPacientes.clear();
        estadoPaciente = false;

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
                                jsonArray.getJSONObject(i).getDouble("SALDO"),
                                jsonArray.getJSONObject(i).getString("OCUPACION"),
                                (jsonArray.getJSONObject(i).getInt("SEXO") > 0) ? true : false
                        ));
                    }

                    mAdapter = new PacienteAdapter(listaPacientes);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(position -> {
                        MenuInferior menuInferior = new MenuInferior();
                        menuInferior.show(getActivity().getSupportFragmentManager(), "MenuInferior");
                        menuInferior.recibirTitulo(listaPacientes.get(position).getNombre());
                        menuInferior.eventoClick(opcion -> {
                            estadoPaciente = listaPacientes.get(position).getEstado();
                            realizarAccion(opcion, listaPacientes.get(position).getCodigo(), position);
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
                obtenerPacientes();
            }
        });
    }

    public void actualizarEstado(final int ID) {
        if (estadoPaciente) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysPacientes querysPacientes = new QuerysPacientes(getContext());
            JSONObject jsonBody = new JSONObject();

            String[] auxFecha = listaPacientes.get(ID).getFecha().split("/");
            String fechaBD = auxFecha[2] + "/" + auxFecha[1] + "/" + auxFecha[0];

            try {
                jsonBody.put("ESTADO", false);
                jsonBody.put("NOMBRE", listaPacientes.get(ID).getNombre());
                jsonBody.put("EDAD", listaPacientes.get(ID).getEdad());
                jsonBody.put("OCUPACION", listaPacientes.get(ID).getOcupacion());
                jsonBody.put("TELEFONO", listaPacientes.get(ID).getTelefono());
                jsonBody.put("FECHA_NACIMIENTO", fechaBD);
                jsonBody.put("DPI", listaPacientes.get(ID).getDpi());
                jsonBody.put("SEXO", listaPacientes.get(ID).getGenero());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            querysPacientes.actualizarPaciente(listaPacientes.get(ID).getCodigo(), jsonBody, new QuerysPacientes.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    estadoPaciente = false;

                    Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setText("Deshabilitado Correctamente")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(typeface)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.FondoSecundario)
                            .show();

                    FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                    funcionesBitacora.registrarBitacora("ACTUALIZACION", "PACIENTES", "Se actualizo el estado del paciente #" + listaPacientes.get(ID).getCodigo());

                    new Handler().postDelayed(() -> {
                        progressDialog.dismiss();
                        obtenerPacientes();
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
                    .setTitle("El paciente esta deshabilitado")
                    .setIcon(R.drawable.logonuevo)
                    .setTextTypeface(typeface)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.FondoSecundario)
                    .show();
        }
    }

}
