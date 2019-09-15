package com.example.dentalhistoryrecorder.OpcionConsulta.Normal;

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
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.dentalhistoryrecorder.InicioSesion;
import com.example.dentalhistoryrecorder.OpcionConsulta.Items;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Adaptadores.AdaptadorConsulta;
import com.example.dentalhistoryrecorder.OpcionIngreso.Especial.IngCostos;
import com.example.dentalhistoryrecorder.R;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Consultar extends Fragment {
    private EditText pnombre, papellido;
    private FloatingActionButton buscar;
    private Toolbar toolbar;

    private RecyclerView lista_pacientes;
    private AdaptadorConsulta adapter;
    private RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;
    private static final String TAG = "MyActivity";
    SharedPreferences preferencias;
    private int mOpcion = 0;
    private TextView etiquetaN, etiquetaE;

    public Consultar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_consultar, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        preferencias = getActivity().getSharedPreferences("Consultar", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbar);

        switch (mOpcion) {
            case 1:
                toolbar.setTitle("Consulta de Fichas");
                break;

            case 2:
                toolbar.setTitle("Seguimiento de  Fichas");
                break;

            case 3:
                toolbar.setTitle("Evaluacion Myobrace");
                break;

            default:
                toolbar.setTitle("Consulta");
                break;
        }

        pnombre = view.findViewById(R.id.pnom_bus);
        pnombre.setTypeface(face);
        papellido = view.findViewById(R.id.pape_bus);
        papellido.setTypeface(face);

        buscar = view.findViewById(R.id.consultador);

        lista_pacientes = view.findViewById(R.id.lista_pacientes);


        etiquetaN = view.findViewById(R.id.etiquetaN);
        etiquetaN.setTypeface(face);
        etiquetaE = view.findViewById(R.id.etiquetaE);
        etiquetaE.setTypeface(face);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consultarPaciente("http://192.168.56.1/DHR/IngresoN/consultaficha.php?db=u578331993_clinc&user=root");
                //consultarPaciente("https://diegosistemas.xyz/DHR/Normal/consultaficha.php?estado=1");
                if (!pnombre.getText().toString().isEmpty() && !papellido.getText().toString().isEmpty()) {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        obtenerPacientes("https://diegosistemas.xyz/DHR/Normal/consultaficha.php?estado=8");
                    } else {
                        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face2)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }
                }
                else {
                    Alerter.create(getActivity())
                            .setTitle("Hay Campos Vacios")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            }
        });

        return view;
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void consultarPaciente(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    final ArrayList<Items> lista = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("idPaciente");
                            Log.i(TAG, "id: " + id);
                            String nom = jsonArray.getJSONObject(i).getString("Nombre");
                            Log.i(TAG, "nombre: " + nom);
                            String contN = jsonArray.getJSONObject(i).getString("Fichas");
                            Log.i(TAG, "contadorN: " + contN);
                            String contE = jsonArray.getJSONObject(i).getString("Fichas2");
                            Log.i(TAG, "contadorE: " + contE);
                            String edad = jsonArray.getJSONObject(i).getString("edad");
                            String fecha = jsonArray.getJSONObject(i).getString("fecha_nac");

                            lista.add(new Items(id, nom, contN, contE, edad, fecha));
                        }
                        lista_pacientes.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        adapter = new AdaptadorConsulta(lista);
                        lista_pacientes.setLayoutManager(layoutManager);
                        lista_pacientes.setAdapter(adapter);
                        pnombre.setText(null);
                        papellido.setText(null);
                        adapter.setOnItemClickListener(new AdaptadorConsulta.OnItemClickListener() {
                            @Override
                            public void onItemClick(final int position) {
                                //Toast.makeText(getActivity(),"Posicion: " + position,Toast.LENGTH_LONG).show();
                                if (mOpcion != 3) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                                    View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_fichas, null);
                                    Button cancelar = viewCuadro.findViewById(R.id.cancelar_cfichas);
                                    cancelar.setTypeface(face2);
                                    TextView tituloN = viewCuadro.findViewById(R.id.textView2);
                                    tituloN.setTypeface(face2);
                                    TextView tituloE = viewCuadro.findViewById(R.id.textView3);
                                    tituloE.setTypeface(face2);

                                    ImageView normales = viewCuadro.findViewById(R.id.entrarNormales);
                                    ImageView especiales = viewCuadro.findViewById(R.id.entrarEspeciales);

                                    builder.setView(viewCuadro);
                                    final AlertDialog dialog = builder.create();

                                    normales.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (Integer.parseInt(lista.get(position).getMcontadorN()) > 0) {

                                                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                                if (networkInfo != null && networkInfo.isConnected()) {
                                                    consultarFichas consultarFichas1 = new consultarFichas(lista.get(position).getMid(), mOpcion);
                                                    SharedPreferences.Editor escritor2 = preferencias.edit();
                                                    escritor2.putString("nombre", lista.get(position).getMnombre());
                                                    escritor2.putString("edad", lista.get(position).getMedad());
                                                    escritor2.commit();
                                                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                                    transaction.replace(R.id.contenedor, consultarFichas1);
                                                    transaction.commit();
                                                    dialog.dismiss();
                                                } else {
                                                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                                                    Alerter.create(getActivity())
                                                            .setTitle("Error")
                                                            .setText("Fallo en Conexion a Internet")
                                                            .setIcon(R.drawable.logonuevo)
                                                            .setTextTypeface(face2)
                                                            .enableSwipeToDismiss()
                                                            .setBackgroundColorRes(R.color.AzulOscuro)
                                                            .show();
                                                }
                                            }
                                        }
                                    });

                                    especiales.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (Integer.parseInt(lista.get(position).getMcontadorE()) > 0) {

                                                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                                if (networkInfo != null && networkInfo.isConnected()) {
                                                    if (mOpcion != 2){
                                                        consultarFichas consultarFichas1 = new consultarFichas(lista.get(position).getMid(), 4);
                                                        SharedPreferences.Editor escritor2 = preferencias.edit();
                                                        escritor2.putString("nombre", lista.get(position).getMnombre());
                                                        escritor2.putString("edad", lista.get(position).getMedad());
                                                        escritor2.commit();
                                                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                                        transaction.replace(R.id.contenedor, consultarFichas1);
                                                        transaction.commit();
                                                        dialog.dismiss();
                                                    }
                                                    else {
                                                        consultarFichas consultarFichas1 = new consultarFichas(lista.get(position).getMid(), 5);
                                                        SharedPreferences.Editor escritor2 = preferencias.edit();
                                                        escritor2.putString("nombre", lista.get(position).getMnombre());
                                                        escritor2.putString("edad", lista.get(position).getMedad());
                                                        escritor2.commit();
                                                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                                                        transaction.replace(R.id.contenedor, consultarFichas1);
                                                        transaction.commit();
                                                        dialog.dismiss();
                                                    }
                                                } else {
                                                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                                                    Alerter.create(getActivity())
                                                            .setTitle("Error")
                                                            .setText("Fallo en Conexion a Internet")
                                                            .setIcon(R.drawable.logonuevo)
                                                            .setTextTypeface(face2)
                                                            .enableSwipeToDismiss()
                                                            .setBackgroundColorRes(R.color.AzulOscuro)
                                                            .show();
                                                }
                                            }
                                        }
                                    });

                                    cancelar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();
                                }
                                else {
                                    IngCostos ingCostos = new IngCostos();
                                    ingCostos.ObtenerPaciente(lista.get(position).getMid());
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                                    transaction.replace(R.id.contenedor, ingCostos);
                                    transaction.commit();
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("pnombre", pnombre.getText().toString());
                parametros.put("papellido", papellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public void obtenerPacientes(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.parseInt(response) > 0) {

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        consultarPaciente("https://diegosistemas.xyz/DHR/Normal/consultaficha.php?estado=1");
                    } else {
                        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle("Error")
                                .setText("Fallo en Conexion a Internet")
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face2)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                    }

                } else {
                    Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                    Alerter.create(getActivity())
                            .setTitle("NO se encontraron coincidencias")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face2)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                    pnombre.setText(null);
                    papellido.setText(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("pnombre", pnombre.getText().toString());
                parametros.put("papellido", papellido.getText().toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }
}
