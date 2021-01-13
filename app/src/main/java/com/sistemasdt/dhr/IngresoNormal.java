package com.sistemasdt.dhr;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Rutas.Fichas.FichaForm.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialMedico.HistorialMed;
import com.sistemasdt.dhr.OpcionIngreso.Normal.IngHOdon;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.Pacientes;
import com.sistemasdt.dhr.OpcionIngreso.Normal.Ing_HFoto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IngresoNormal extends Fragment {
    private Toolbar toolbar;
    private GridLayout menu;
    private int edad, lim_od, fotos_guardadas;
    private String pnombre, snombre, papellido, sapellido, tel, ocupacion, dato;
    private String fecha, motivo, medico, referente;
    private boolean existentedp, sexodp;
    private boolean hospi, alergia, medic, tratamiento, hemorragia;
    private FloatingActionButton guardador;
    private boolean sex;
    private String desc_h, desc_al, desc_medic, otro;
    private boolean dolor, gingivitis;
    private String otro_hd, desc_dolor, idPaciente, idFicha, idHisMed;
    private ArrayList<Boolean> padecimientos = new ArrayList<Boolean>();
    private ArrayList<String[]> tabla_costos = new ArrayList<String[]>();
    private ArrayList<Bitmap> lista_fotos = new ArrayList<Bitmap>();
    private static final String TAG = "MyActivity";
    private SharedPreferences almacen;
    RequestQueue requestQueue;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingreso_normal, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        almacen = getActivity().getSharedPreferences("ids", Context.MODE_PRIVATE);
        //Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/bahnschrift.ttf");
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ingreso de Pacientes");
        menu = (GridLayout) view.findViewById(R.id.menuingeso);
        setSingleEvent(menu);
        guardador = view.findViewById(R.id.guardarBD);
        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Hola123",Toast.LENGTH_LONG).show();
                //Informacion - Datos Personales ---------------------------------------------------
                SharedPreferences preferencias = getActivity().getSharedPreferences("datospersonales", Context.MODE_PRIVATE);
                pnombre = preferencias.getString("pnombre", " ");
                snombre = preferencias.getString("snombre", " ");
                papellido = preferencias.getString("papellido", " ");
                sapellido = preferencias.getString("sapellido", " ");
                tel = preferencias.getString("telefono", " ");
                ocupacion = preferencias.getString("ocupacion", " ");
                sex = preferencias.getBoolean("sexo", true);
                edad = preferencias.getInt("edad", 0);

                //Insercion de Pacientes
                insertarPaciente("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=1");
                SharedPreferences.Editor editor = preferencias.edit();
                editor.clear().commit();

                //Informacion - Detalle ------------------------------------------------------------
                SharedPreferences preferencias2 = getActivity().getSharedPreferences
                        ("datosdetalle", Context.MODE_PRIVATE);
                fecha = preferencias2.getString("fecha", " ");
                motivo = preferencias2.getString("motivo", " ");
                medico = preferencias2.getString("medico", " ");
                referente = preferencias2.getString("referente", " ");

                editor = preferencias2.edit();
                editor.clear().commit();

                //Insertar Ficha
                insertarFicha("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=2");


                //Informacion - Historial Medico ---------------------------------------------------
                SharedPreferences preferencias3 = getActivity().getSharedPreferences("datoshm"
                        , Context.MODE_PRIVATE);
                hospi = preferencias3.getBoolean("hospi", false);
                alergia = preferencias3.getBoolean("alergia", false);
                medic = preferencias3.getBoolean("medic", false);
                tratamiento = preferencias3.getBoolean("trat", false);
                desc_h = preferencias3.getString("deshospi", " ");
                desc_al = preferencias3.getString("descaler", " ");
                desc_medic = preferencias3.getString("desmedic", " ");
                hemorragia = preferencias3.getBoolean("hemo", false);

                //Insertar HistorialMedico
                insertarHMedico("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=4");

                padecimientos.add(preferencias3.getBoolean("corazon", false));
                padecimientos.add(preferencias3.getBoolean("artri", false));
                padecimientos.add(preferencias3.getBoolean("tuber", false));
                padecimientos.add(preferencias3.getBoolean("fr", false));
                padecimientos.add(preferencias3.getBoolean("presA", false));
                padecimientos.add(preferencias3.getBoolean("presB", false));
                padecimientos.add(preferencias3.getBoolean("diab", false));
                padecimientos.add(preferencias3.getBoolean("anem", false));
                padecimientos.add(preferencias3.getBoolean("epile", false));
                otro = preferencias3.getString("otro", " ");

                insertarPadecimientos("http://192.168.56.1:80/DHR/IngresoN/ficha.php?db=u578331993_clinc&user=root&estado=7");

                editor = preferencias3.edit();
                editor.clear().commit();
                //padecimientos.clear();

                //Informacion - Historial Odontodologico -------------------------------------------
                SharedPreferences preferencias4 = getActivity().getSharedPreferences("datosdentales"
                        , Context.MODE_PRIVATE);
                dolor = preferencias4.getBoolean("dolor", false);
                gingivitis = preferencias4.getBoolean("gingi", false);
                desc_dolor = preferencias4.getString("descdolor", "");
                otro_hd = preferencias4.getString("otro", "");
                lim_od = preferencias4.getInt("lim", 0);

                if (lim_od > 0) {
                    for (int i = 0; i < lim_od; i++) {
                        String[] fila = new String[]{
                                preferencias4.getString("pieza" + i, ""),
                                preferencias4.getString("trat" + i, ""),
                                preferencias4.getString("cost" + i, "")
                        };
                        tabla_costos.add(fila);
                    }
                }
                //Toast.makeText(getActivity(), "" + tabla_costos.size(), Toast.LENGTH_LONG).show();

                editor = preferencias4.edit();
                editor.clear().commit();

                SharedPreferences preferencias5 = getActivity().getSharedPreferences("datosfotos"
                        , Context.MODE_PRIVATE);

                fotos_guardadas = preferencias.getInt("fotos", 0);

                if (fotos_guardadas > 0) {
                    for (int i = 0; i < fotos_guardadas; i++) {
                        String codigo_foto = preferencias.getString("foto" + i, "");
                        if (!codigo_foto.equalsIgnoreCase("")) {
                            byte[] b = Base64.decode(codigo_foto, Base64.DEFAULT);
                            Bitmap imagen_codificada = BitmapFactory.decodeByteArray(b, 0, b.length);
                            lista_fotos.add(imagen_codificada);
                        }
                    }
                }

                editor = preferencias5.edit();
                editor.clear().commit();

            }
        });

        return view;
    }

    private void setSingleEvent(GridLayout singleEvent) {
        for (int i = 0; i < singleEvent.getChildCount(); i++) {
            CardView cardView = (CardView) singleEvent.getChildAt(i);
            final int dato = i + 1;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (dato) {
                        case 1:
                            Pacientes pacientes = new Pacientes();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction.replace(R.id.contenedor, pacientes);
                            transaction.commit();
                            break;

                        case 2:
                            Ficha ficha = new Ficha();
                            FragmentTransaction transaction2 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction2.replace(R.id.contenedor, ficha);
                            transaction2.commit();
                            break;

                        case 3:
                            HistorialMed historialMed = new HistorialMed();
                            FragmentTransaction transaction3 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction3.replace(R.id.contenedor, historialMed);
                            transaction3.commit();
                            break;

                        case 4:
                            IngHOdon ingHOdon = new IngHOdon();
                            FragmentTransaction transaction4 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction4.replace(R.id.contenedor, ingHOdon);
                            transaction4.commit();
                            break;

                        case 5:
                            Ing_HFoto ing_hFoto = new Ing_HFoto();
                            FragmentTransaction transaction5 = getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction5.replace(R.id.contenedor, ing_hFoto);
                            transaction5.commit();
                            break;

                        case 6:
                            SharedPreferences.Editor editor = almacen.edit();
                            padecimientos.clear();
                            editor.clear().commit();
                            break;
                    }
                }
            });
        }
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void insertarPaciente(String URL) {
        final String[] id = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                JSONObject objeto = null;
                /*try {
                    jsonArray = new JSONArray(response);
                    id[0] = jsonArray.getJSONObject(0).getString("idPaciente");

                    SharedPreferences.Editor escritor = almacen.edit();
                    escritor.putString("idPaciente", id[0] + 1);
                    escritor.commit();
                    Log.i(TAG, "ID2: " + id[0]);
                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }*/
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
                parametros.put("pnombre", pnombre.trim());
                parametros.put("snombre", snombre.trim());
                parametros.put("papellido", papellido.trim());
                parametros.put("sapellido", sapellido.trim());
                parametros.put("edad", String.valueOf(edad).trim());
                parametros.put("ocupacion", ocupacion.trim());
                parametros.put("sexo", String.valueOf(sex).trim());
                parametros.put("tel", tel.trim());
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        //idPacientee = id[0];
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void insertarFicha(String URL) {
        final String[] id = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                /*try {
                    jsonArray = new JSONArray(response);
                    id[0] = jsonArray.getJSONObject(0).getString("idFicha");

                    SharedPreferences.Editor escritor = almacen.edit();
                    escritor.putString("idFicha", id[0]);
                    escritor.commit();

                    Log.i(TAG, "idFicha1: " + id[0]);
                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }*/
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
                parametros.put("fecha", fecha);
                parametros.put("medico", medico);
                parametros.put("motivo", motivo);
                parametros.put("referente", referente);
                int idd = Integer.parseInt(almacen.getString("idPaciente", "0")) + 1;
                Log.i(TAG, "idPaciente: " + idd);
                //parametros.put("id", String.valueOf(Integer.parseInt(idPaciente + 1)));
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        /*SharedPreferences.Editor escritor = almacen.edit();
        escritor.putString("idFicha", id[0]);
        escritor.commit();*/
        //idFichaa = id[0];
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void insertarHMedico(String URL) {
        final String[] id = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                JSONObject objeto = null;
                /*try {
                    jsonArray = new JSONArray(response);
                    id[0] = jsonArray.getJSONObject(0).getString("idHistorialMed");

                    SharedPreferences.Editor escritor = almacen.edit();
                    escritor.putString("idHistorialMed", id[0]);
                    escritor.commit();
                    Log.i(TAG, "ID3: " + id[0]);
                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }*/
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
                parametros.put("hospitalizado", String.valueOf((hospi) ? 1 : 0));
                parametros.put("desc_hos", desc_h);
                parametros.put("tratmed", String.valueOf((tratamiento) ? 1 : 0));
                parametros.put("alergia", String.valueOf((alergia) ? 1 : 0));
                parametros.put("desc_al", desc_al);
                parametros.put("hemorragia", String.valueOf((hemorragia) ? 1 : 0));
                parametros.put("medicamento", String.valueOf((medic) ? 1 : 0));
                parametros.put("desc_med", desc_medic);
                int idd = Integer.parseInt(almacen.getString("idFicha", "0")) + 1;
                Log.i(TAG, "idFicha2: " + idd);
                //parametros.put("idficha", String.valueOf(Integer.parseInt(idFicha + 1)));
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        //idHisMedd = id[0];
    }

    //Insertar Datos Personales y Obtener ID Pacientes ----------------------------------------------
    public void insertarPadecimientos(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                parametros.put("corazon",String.valueOf((padecimientos.get(0)) ? 1 : 0));
                parametros.put("artritis",String.valueOf((padecimientos.get(1)) ? 1 : 0));
                parametros.put("tuberculosis",String.valueOf((padecimientos.get(2)) ? 1 : 0));
                parametros.put("fiebrereu",String.valueOf((padecimientos.get(3)) ? 1 : 0));
                parametros.put("presionalta",String.valueOf((padecimientos.get(4)) ? 1 : 0));
                parametros.put("presionbaja",String.valueOf((padecimientos.get(5)) ? 1 : 0));
                parametros.put("diabetes",String.valueOf((padecimientos.get(6)) ? 1 : 0));
                parametros.put("anemia",String.valueOf((padecimientos.get(7)) ? 1 : 0));
                parametros.put("epilepsia",String.valueOf((padecimientos.get(8)) ? 1 : 0));
                parametros.put("otros",otro);
                padecimientos.clear();

                int idd = Integer.parseInt(almacen.getString("idHistorialMed", "0")) + 1;
                Log.i(TAG, "idFicha3: " + idd);
                //parametros.put("id", String.valueOf(Integer.parseInt(idHisMed + 1)));
                return parametros;
            }

        };
        //RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    /*
                padecimientos.add(preferencias3.getBoolean("corazon", false));
                padecimientos.add(preferencias3.getBoolean("artri", false));
                padecimientos.add(preferencias3.getBoolean("tuber", false));
                padecimientos.add(preferencias3.getBoolean("fr", false));
                padecimientos.add(preferencias3.getBoolean("presA", false));
                padecimientos.add(preferencias3.getBoolean("presB", false));
                padecimientos.add(preferencias3.getBoolean("diab", false));
                padecimientos.add(preferencias3.getBoolean("anem", false));
                padecimientos.add(preferencias3.getBoolean("epile", false));
                otro = preferencias3.getString("otro", " ");
    */

}