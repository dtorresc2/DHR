package com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sistemasdt.dhr.Componentes.MenusInferiores.MenuInferiorFicha;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Adaptadores.AdaptadorEvaluacion;
import com.sistemasdt.dhr.Rutas.Fichas.FichaEspecial.Adaptadores.ItemEvaluacion;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Adaptadores.AdaptadorConsultaFicha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Items.ItemsFichas;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysEvaluaciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListadoEvaluaciones extends Fragment {
    private Toolbar toolbar;
    private RecyclerView listafichas;

    private AdaptadorEvaluacion adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemEvaluacion> lista;
    private boolean estadoFicha = false;
    private FloatingActionButton botonConsultaAvanzada;

    private TextInputLayout layoutSaldo;
    TextInputEditText saldo;

    public ListadoEvaluaciones() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listado_evaluaciones, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Evaluaciones");

        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.inflateMenu(R.menu.opciones_toolbar_catalogos);

        toolbar.setNavigationOnClickListener(view1 -> {
            MenuFichas menuFichas = new MenuFichas();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.contenedor, menuFichas);
            transaction.commit();
        });

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.opcion_nuevo:
                    FichaEvaluacion fichaEvaluacion = new FichaEvaluacion();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.contenedor, fichaEvaluacion);
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
                    obtenerEvaluaciones();
                    return true;

                default:
                    return false;

            }
        });

        botonConsultaAvanzada = view.findViewById(R.id.botonConsultaAvanzada);
        botonConsultaAvanzada.setOnClickListener(v -> {

        });

        lista = new ArrayList<>();
        listafichas = view.findViewById(R.id.lista_fichas);
        listafichas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        obtenerEvaluaciones();

        return view;
    }

    public void obtenerEvaluaciones() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        QuerysEvaluaciones querysEvaluaciones = new QuerysEvaluaciones(getContext());
        querysEvaluaciones.obtenerEvaluaciones(new QuerysEvaluaciones.ManejadorQuery() {
            @Override
            public void onSuccess(ArrayList arrayList) {
                lista = arrayList;
                adapter = new AdaptadorEvaluacion(lista);
                listafichas.setLayoutManager(layoutManager);
                listafichas.setAdapter(adapter);

                adapter.setOnItemClickListener(position -> {
                    MenuInferiorFicha menuInferiorFicha = new MenuInferiorFicha();
                    menuInferiorFicha.recibirTitulo(lista.get(position).getMotivo());
                    estadoFicha = lista.get(position).getEstado();
                    menuInferiorFicha.recibirEstado(estadoFicha);
                    menuInferiorFicha.show(getActivity().getSupportFragmentManager(), "MenuInferiorEvaluacion");
//                    menuInferiorFicha.eventoClick(opcion -> realizarAccion(opcion, lista.get(position).getId()));
                });

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerEvaluaciones();
            }
        });
    }
}