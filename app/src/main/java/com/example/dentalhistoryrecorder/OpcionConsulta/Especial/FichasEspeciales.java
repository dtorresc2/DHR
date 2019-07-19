package com.example.dentalhistoryrecorder.OpcionConsulta.Especial;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionConsulta.Normal.Consultar;
import com.example.dentalhistoryrecorder.PDF.LectorPDF;
import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Tabla.TablaDinamica;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FichasEspeciales extends Fragment {
    EditText enganche, costo, terapia;
    FloatingActionButton agregar;
    Toolbar toolbar;
    RequestQueue requestQueue;
    private SharedPreferences preferencias;
    private static final String TAG = "MyActivity";
    private String idUsuario;
    private TextView separador1, separador2, separador3;

    private TableLayout tableLayout;
    private String[] header = {"Fecha", "Descripsion", "Costo"};
    private TablaDinamica tablaDinamica;
    private ArrayList<String[]> rows = new ArrayList<>();

    private TableLayout tableLayout2;
    private String[] header2 = {"Fecha", "Descripsion", "Costo"};
    private TablaDinamica tablaDinamica2;
    private ArrayList<String[]> rows2 = new ArrayList<>();

    private int contador = 0;
    private byte fotoFirma[];

    TextView parrafo1, parrafo2, parrafo3, parrafo4, parrafo5, parrafo6, parrafo7, parrafo8;
    ImageView firma;

    TextView titulo1, titulo2, titulo3, titulo4, titulo5, titulo6, titulo7, titulo8, titulo9, titulo10, titulo11, titulo12, titulo13;
    CheckBox ad1, ad2, ad3, ad4, ad5;
    CheckBox fas1, fas2, fas3;
    CheckBox fai1, fai2, fai3;
    CheckBox oc1, oc2, oc3, oc4, oc5;
    CheckBox df1, df2, df3, df4;
    CheckBox rp1, rp2, rp3, rp4, rp5;
    CheckBox len1, len2, len3;
    CheckBox de1, de2;
    CheckBox lm1, lm2;
    CheckBox h1, h2, h3, h4, h5;
    CheckBox tmj1, tmj2, tmj3, tmj4, tmj5, tmj6, tmj7;
    EditText descripcion;

    private static final int CODIGO_SOLICITUD_PERMISO = 123;

    public FichasEspeciales() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fichas_especiales, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());

        preferencias = getActivity().getSharedPreferences("Consultar", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ficha Especial");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consultar consultar = new Consultar();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, consultar);
                transaction.commit();
            }
        });
        toolbar.inflateMenu(R.menu.opciones_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_exportar:
                        if (checkearPermiso()) {
                            generarPDF();
                        } else {
                            //Nuestra app no tiene permiso, entonces debo solicitar el mismo
                            solicitarPermiso();
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);

        //Detalle de Ficha

        separador1 = view.findViewById(R.id.tituloControlD);
        separador1.setTypeface(face);

        enganche = view.findViewById(R.id.enganche);
        enganche.setTypeface(face);

        costo = view.findViewById(R.id.costoVisita);
        costo.setTypeface(face);

        terapia = view.findViewById(R.id.terapia);
        terapia.setTypeface(face);

        //Control de Visitas
        separador2 = view.findViewById(R.id.tituloControlV);
        separador2.setTypeface(face);

        tableLayout = view.findViewById(R.id.tablaVisitas);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        titulo13 = view.findViewById(R.id.titulo_pagosCons);
        titulo13.setTypeface(face);

        tableLayout2 = view.findViewById(R.id.tablaPagos);

        tablaDinamica2 = new TablaDinamica(tableLayout2, getContext());
        tablaDinamica2.addHeader(header2);
        tablaDinamica2.addData(getClients2());
        tablaDinamica2.fondoHeader(R.color.AzulOscuro);


        //Contrato de Compromiso
        separador3 = view.findViewById(R.id.tituloContrato);
        separador3.setTypeface(face);

        parrafo1 = view.findViewById(R.id.parrafo1);
        parrafo1.setTypeface(face);

        parrafo2 = view.findViewById(R.id.parrafo2);
        parrafo2.setTypeface(face);

        parrafo3 = view.findViewById(R.id.parrafo3);
        parrafo3.setTypeface(face);

        parrafo4 = view.findViewById(R.id.parrafo4);
        parrafo4.setTypeface(face);

        parrafo5 = view.findViewById(R.id.parrafo5);
        parrafo5.setTypeface(face);

        parrafo6 = view.findViewById(R.id.parrafo6);
        parrafo6.setTypeface(face);

        parrafo7 = view.findViewById(R.id.parrafo7);
        parrafo7.setTypeface(face);

        parrafo8 = view.findViewById(R.id.parrafo8);
        parrafo8.setTypeface(face);

        firma = view.findViewById(R.id.firma);

        //Evaluacion

        //Alineacion Dental
        titulo1 = view.findViewById(R.id.titulo1);
        titulo1.setTypeface(face);
        ad1 = view.findViewById(R.id.ad1);
        ad1.setTypeface(face);
        ad2 = view.findViewById(R.id.ad2);
        ad2.setTypeface(face);
        ad3 = view.findViewById(R.id.ad3);
        ad3.setTypeface(face);
        ad4 = view.findViewById(R.id.ad4);
        ad4.setTypeface(face);
        ad5 = view.findViewById(R.id.ad5);
        ad5.setTypeface(face);


        //Forma del Arco Superior
        titulo2 = view.findViewById(R.id.titulo2);
        titulo2.setTypeface(face);
        fas1 = view.findViewById(R.id.fas1);
        fas1.setTypeface(face);
        fas2 = view.findViewById(R.id.fas2);
        fas2.setTypeface(face);
        fas3 = view.findViewById(R.id.fas3);
        fas3.setTypeface(face);

        //Forma del Arco Inferior
        titulo3 = view.findViewById(R.id.titulo3);
        titulo3.setTypeface(face);
        fai1 = view.findViewById(R.id.fai1);
        fai1.setTypeface(face);
        fai2 = view.findViewById(R.id.fai2);
        fai2.setTypeface(face);
        fai3 = view.findViewById(R.id.fai3);
        fai3.setTypeface(face);

        //Oclusion
        titulo4 = view.findViewById(R.id.titulo4);
        titulo4.setTypeface(face);
        oc1 = view.findViewById(R.id.oc1);
        oc1.setTypeface(face);
        oc2 = view.findViewById(R.id.oc2);
        oc2.setTypeface(face);
        oc3 = view.findViewById(R.id.oc3);
        oc3.setTypeface(face);
        oc4 = view.findViewById(R.id.oc4);
        oc4.setTypeface(face);
        oc5 = view.findViewById(R.id.oc5);
        oc5.setTypeface(face);

        //Desarrollo Facial
        titulo5 = view.findViewById(R.id.titulo5);
        titulo5.setTypeface(face);
        df1 = view.findViewById(R.id.df1);
        df1.setTypeface(face);
        df2 = view.findViewById(R.id.df2);
        df2.setTypeface(face);
        df3 = view.findViewById(R.id.df3);
        df3.setTypeface(face);
        df4 = view.findViewById(R.id.df4);
        df4.setTypeface(face);

        //Respiracion y Postura
        titulo6 = view.findViewById(R.id.titulo6);
        titulo6.setTypeface(face);
        rp1 = view.findViewById(R.id.rp1);
        rp1.setTypeface(face);
        rp2 = view.findViewById(R.id.rp2);
        rp2.setTypeface(face);
        rp3 = view.findViewById(R.id.rp3);
        rp3.setTypeface(face);
        rp4 = view.findViewById(R.id.rp4);
        rp4.setTypeface(face);
        rp5 = view.findViewById(R.id.rp5);
        rp5.setTypeface(face);

        //Lengua
        titulo7 = view.findViewById(R.id.titulo7);
        titulo7.setTypeface(face);
        len1 = view.findViewById(R.id.len1);
        len1.setTypeface(face);
        len2 = view.findViewById(R.id.len2);
        len2.setTypeface(face);
        len3 = view.findViewById(R.id.len3);
        len3.setTypeface(face);

        //Deglucion
        titulo8 = view.findViewById(R.id.titulo8);
        titulo8.setTypeface(face);
        de1 = view.findViewById(R.id.de1);
        de1.setTypeface(face);
        de2 = view.findViewById(R.id.de2);
        de2.setTypeface(face);

        //Labios y Mejilla
        titulo9 = view.findViewById(R.id.titulo9);
        titulo9.setTypeface(face);
        lm1 = view.findViewById(R.id.lm1);
        lm1.setTypeface(face);
        lm2 = view.findViewById(R.id.lm2);
        lm2.setTypeface(face);

        //Habitos
        titulo10 = view.findViewById(R.id.titulo10);
        titulo10.setTypeface(face);
        h1 = view.findViewById(R.id.h1);
        h1.setTypeface(face);
        h2 = view.findViewById(R.id.h2);
        h2.setTypeface(face);
        h3 = view.findViewById(R.id.h3);
        h3.setTypeface(face);
        h4 = view.findViewById(R.id.h4);
        h4.setTypeface(face);
        h5 = view.findViewById(R.id.h5);
        h5.setTypeface(face);

        //TMJ
        titulo11 = view.findViewById(R.id.titulo11);
        titulo11.setTypeface(face);
        tmj1 = view.findViewById(R.id.tmj1);
        tmj1.setTypeface(face);
        tmj2 = view.findViewById(R.id.tmj2);
        tmj2.setTypeface(face);
        tmj3 = view.findViewById(R.id.tmj3);
        tmj3.setTypeface(face);
        tmj4 = view.findViewById(R.id.tmj4);
        tmj4.setTypeface(face);
        tmj5 = view.findViewById(R.id.tmj5);
        tmj5.setTypeface(face);
        tmj6 = view.findViewById(R.id.tmj6);
        tmj6.setTypeface(face);
        tmj7 = view.findViewById(R.id.tmj7);
        tmj7.setTypeface(face);

        //Otros
        titulo12 = view.findViewById(R.id.titulo12);
        titulo12.setTypeface(face);
        descripcion = view.findViewById(R.id.otrosEvaluacion);
        descripcion.setTypeface(face);

        consultarDetalle("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=2");
        consultarVisitas("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=3");
        consultarPagos("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=17");
        consultarFirma("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=4");
        consultarEvaluacion("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=5");

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    private ArrayList<String[]> getClients2() {
        return rows2;
    }

    public void consultarDetalle(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //String.format("%.2f", totV)
                            enganche.setText(String.format("%.2f",jsonArray.getJSONObject(i).getDouble("enganche")));
                            costo.setText(String.format("%.2f",jsonArray.getJSONObject(i).getDouble("costovisita")));
                            terapia.setText(String.format("%.2f",jsonArray.getJSONObject(i).getDouble("terapia")));
                        }
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
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarVisitas(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String[] item = new String[]{
                                    jsonArray.getJSONObject(i).getString("fecha"),
                                    jsonArray.getJSONObject(i).getString("descripsion"),
                                    String.format("%.2f", jsonArray.getJSONObject(i).getDouble("costo"))
                            };
                            tablaDinamica.addItem(item);
                        }

                        /*if (tablaDinamica2.getCount() > 0) {
                            for (int i = 1; i < tablaDinamica2.getCount() + 1; i++) {
                                totalPagos += Double.parseDouble(tablaDinamica2.getCellData(i, 1));
                            }
                            pagos_total.setText(String.format("%.2f", totalPagos));
                        }*/

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
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarPagos(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String[] item = new String[]{
                                    jsonArray.getJSONObject(i).getString("fecha"),
                                    jsonArray.getJSONObject(i).getString("descripsion"),
                                    String.format("%.2f", jsonArray.getJSONObject(i).getDouble("cantidad"))
                            };
                            tablaDinamica2.addItem(item);
                        }
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
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }


    public void consultarFirma(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String codigo_foto = jsonArray.getJSONObject(i).getString("firmapac");
                            if (!codigo_foto.equalsIgnoreCase("")) {
                                byte[] b = Base64.decode(codigo_foto, Base64.DEFAULT);
                                fotoFirma = b;
                                Bitmap imagen_codificada = BitmapFactory.decodeByteArray(b, 0, b.length);
                                firma.setImageBitmap(imagen_codificada);
                            }
                        }
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
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarEvaluacion(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String evaluacion = jsonArray.getJSONObject(i).getString("idEvaluacion");
                            descripcion.setText(jsonArray.getJSONObject(i).getString("descripsion"));
                            consultarAD("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=6", evaluacion);
                            consultarFAS("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=7", evaluacion);
                            consultarFAI("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=8", evaluacion);
                            consultarOclusion("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=9", evaluacion);
                            consultarDesarrollo("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=10", evaluacion);
                            consultarRespiracion("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=11", evaluacion);
                            consultarLengua("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=12", evaluacion);
                            consultarDeglucion("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=13", evaluacion);
                            consultarLabios("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=14", evaluacion);
                            consultarHabitos("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=15", evaluacion);
                            consultarTMJ("https://diegosistemas.xyz/DHR/Especial/consultaE.php?estado=16", evaluacion);
                        }
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
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarAD(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ad1.setChecked((jsonArray.getJSONObject(i).getInt("BuenaAlimentacion") > 0) ? true : false);
                            ad2.setChecked((jsonArray.getJSONObject(i).getInt("ApillamientoMaxilar") > 0) ? true : false);
                            ad3.setChecked((jsonArray.getJSONObject(i).getInt("ApillamientoMandibula") > 0) ? true : false);
                            ad4.setChecked((jsonArray.getJSONObject(i).getInt("LineaMediaCorrecta") > 0) ? true : false);
                            ad5.setChecked((jsonArray.getJSONObject(i).getInt("DiscrepanciaLM") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }


    public void consultarFAS(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            fas1.setChecked((jsonArray.getJSONObject(i).getInt("Normal") > 0) ? true : false);
                            fas2.setChecked((jsonArray.getJSONObject(i).getInt("Estrecha") > 0) ? true : false);
                            fas3.setChecked((jsonArray.getJSONObject(i).getInt("Aplanado") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarFAI(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            fai1.setChecked((jsonArray.getJSONObject(i).getInt("Normal") > 0) ? true : false);
                            fai2.setChecked((jsonArray.getJSONObject(i).getInt("Estrecha") > 0) ? true : false);
                            fai3.setChecked((jsonArray.getJSONObject(i).getInt("Aplanado") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarOclusion(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            oc1.setChecked((jsonArray.getJSONObject(i).getInt("relacion_correcta") > 0) ? true : false);
                            oc2.setChecked((jsonArray.getJSONObject(i).getInt("sobre_mordida") > 0) ? true : false);
                            oc3.setChecked((jsonArray.getJSONObject(i).getInt("resalte_dental") > 0) ? true : false);
                            oc4.setChecked((jsonArray.getJSONObject(i).getInt("mordida_abierta") > 0) ? true : false);
                            oc5.setChecked((jsonArray.getJSONObject(i).getInt("mordida_cerrada") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarDesarrollo(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            df1.setChecked((jsonArray.getJSONObject(i).getInt("des_facbueno") > 0) ? true : false);
                            df2.setChecked((jsonArray.getJSONObject(i).getInt("def_terciomed") > 0) ? true : false);
                            df3.setChecked((jsonArray.getJSONObject(i).getInt("def_terinf") > 0) ? true : false);
                            df4.setChecked((jsonArray.getJSONObject(i).getInt("crecimiento_vertical") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarRespiracion(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            rp1.setChecked((jsonArray.getJSONObject(i).getInt("res_nasallev") > 0) ? true : false);
                            rp2.setChecked((jsonArray.getJSONObject(i).getInt("res_nasfuerte") > 0) ? true : false);
                            rp3.setChecked((jsonArray.getJSONObject(i).getInt("res_bucal") > 0) ? true : false);
                            rp4.setChecked((jsonArray.getJSONObject(i).getInt("buenapostura") > 0) ? true : false);
                            rp5.setChecked((jsonArray.getJSONObject(i).getInt("incorpostura") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarLengua(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            len1.setChecked((jsonArray.getJSONObject(i).getInt("correcta_pos") > 0) ? true : false);
                            len2.setChecked((jsonArray.getJSONObject(i).getInt("incorrecta_pos") > 0) ? true : false);
                            len3.setChecked((jsonArray.getJSONObject(i).getInt("insersion") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarDeglucion(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            de1.setChecked((jsonArray.getJSONObject(i).getInt("patron_correcto") > 0) ? true : false);
                            de2.setChecked((jsonArray.getJSONObject(i).getInt("patron_incorrecto") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarLabios(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            lm1.setChecked((jsonArray.getJSONObject(i).getInt("correcta_post") > 0) ? true : false);
                            lm2.setChecked((jsonArray.getJSONObject(i).getInt("incorrecta_post") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarHabitos(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            h1.setChecked((jsonArray.getJSONObject(i).getInt("bruxismo") > 0) ? true : false);
                            h2.setChecked((jsonArray.getJSONObject(i).getInt("succion") > 0) ? true : false);
                            h3.setChecked((jsonArray.getJSONObject(i).getInt("chupete") > 0) ? true : false);
                            h4.setChecked((jsonArray.getJSONObject(i).getInt("biberón") > 0) ? true : false);
                            h5.setChecked((jsonArray.getJSONObject(i).getInt("ronca") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void consultarTMJ(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            tmj1.setChecked((jsonArray.getJSONObject(i).getInt("temporal") > 0) ? true : false);
                            tmj2.setChecked((jsonArray.getJSONObject(i).getInt("pterigoideo") > 0) ? true : false);
                            tmj3.setChecked((jsonArray.getJSONObject(i).getInt("masateros") > 0) ? true : false);
                            tmj4.setChecked((jsonArray.getJSONObject(i).getInt("cervical") > 0) ? true : false);
                            tmj5.setChecked((jsonArray.getJSONObject(i).getInt("trapecio") > 0) ? true : false);
                            tmj6.setChecked((jsonArray.getJSONObject(i).getInt("tmjclick") > 0) ? true : false);
                            tmj7.setChecked((jsonArray.getJSONObject(i).getInt("tmjdolor") > 0) ? true : false);
                        }
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
                parametros.put("id", id);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }


    //Generar PDF

    private void generarPDF() {
        try {
            BaseFont baseFont = BaseFont.createFont("assets/fonts/bahnschrift.ttf", "UTF-8", BaseFont.EMBEDDED);
            Document documento = new Document(PageSize.LETTER, 50, 50, 50, 50);
            BaseColor fondo = new BaseColor(49, 63, 76);

            Font fuentecolumna = new Font(baseFont, 12, Font.NORMAL);
            fuentecolumna.setColor(255, 255, 255);

            Font fuentecolumna2 = new Font(baseFont, 12, Font.NORMAL);
            fuentecolumna2.setColor(0, 0, 0);

            Font fuentedatos = new Font(baseFont, 11, Font.NORMAL);
            fuentedatos.setColor(49,63,76);

            Font fuentetitulo = new Font(baseFont, 14, Font.NORMAL);
            fuentedatos.setColor(49,63,76);

            //Creacion de la Carpeta - Fichas Normales
            File folder = new File(Environment.getExternalStorageDirectory().toString(), "FichasEspeciales");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File pdf = new File(folder, "Prueba_Es.pdf");
            PdfWriter escritorpdf = PdfWriter.getInstance(documento,  new FileOutputStream(pdf));

            documento.open();

            //Posicionamiento del Texto
            //Correlativo Ficha
            PdfContentByte cb = escritorpdf.getDirectContent();
            cb.beginText();
            float posy = documento.getPageSize().getHeight() - 101;
            cb.setFontAndSize(baseFont, 12);
            cb.setTextMatrix(60, posy);
            cb.showText(preferencias.getString("idficha", ""));
            cb.endText();

            //Nombre del Paciente
            PdfContentByte cb2 = escritorpdf.getDirectContent();
            cb2.beginText();
            posy = documento.getPageSize().getHeight() - 101;
            cb2.setFontAndSize(baseFont, 12);
            cb2.setTextMatrix(180, posy);
            cb2.showText(preferencias.getString("nombre", ""));
            cb2.endText();

            //Edad del paciente
            /*PdfContentByte cb3 = escritorpdf.getDirectContent();
            cb3.beginText();
            posy = documento.getPageSize().getHeight() - 101;
            cb3.setFontAndSize(baseFont, 12);
            cb3.setTextMatrix(518, posy);
            cb3.showText(edad);
            cb3.endText();*/

            //Enganche
            PdfContentByte cb4 = escritorpdf.getDirectContent();
            cb4.beginText();
            posy = documento.getPageSize().getHeight() - 450;
            cb4.setFontAndSize(baseFont, 14);
            cb4.setTextMatrix(75, posy);
            cb4.showText(enganche.getText().toString());
            cb4.endText();

            //Costo por Visita
            PdfContentByte cb5 = escritorpdf.getDirectContent();
            cb5.beginText();
            posy = documento.getPageSize().getHeight() - 450;
            cb5.setFontAndSize(baseFont, 14);
            cb5.setTextMatrix(238, posy);
            cb5.showText(costo.getText().toString());
            cb5.endText();

            //Terapia Completa
            PdfContentByte cb6 = escritorpdf.getDirectContent();
            cb6.beginText();
            posy = documento.getPageSize().getHeight() - 450;
            cb6.setFontAndSize(baseFont, 14);
            cb6.setTextMatrix(400, posy);
            cb6.showText(terapia.getText().toString());
            cb6.endText();

            documento.close();

            //Estampando en las plantillas
            //Plantillas Programa Costos

            //Programa Costos ----------------------------------------------------------------------
            File carpetaPlantillas = new File(Environment.getExternalStorageDirectory().toString(), "Plantillas");

            if (!carpetaPlantillas.exists()) {
                carpetaPlantillas.mkdirs();
            }

            File plantilla1 = new File(carpetaPlantillas, "programa_costos.pdf");
            File pdfInfo = new File(folder, "Prueba_Es.pdf");

            if (!plantilla1.exists()) {
                CopyRawToSDCard(R.raw.programa_costos, plantilla1.getAbsolutePath());
            }

            PdfReader reader = new PdfReader(plantilla1.getAbsolutePath());
            PdfReader info = new PdfReader(pdfInfo.getAbsolutePath());

            //Estampando portada
            File pdf2 = new File(folder, "FinalP.pdf");
            PdfStamper estampador = new PdfStamper(reader, new FileOutputStream(pdf2));

            //Estampando informacion
            for (int i = 1; i < reader.getNumberOfPages() + 1; i++) {
                PdfImportedPage page = estampador.getImportedPage(info, i);
                estampador.getOverContent(i).addTemplate(page, 0, 0);
            }

            estampador.close();
            info.close();
            reader.close();

            File archivo = new File("Prueba_Es.pdf");
            archivo.delete();

            //Programa Costos ----------------------------------------------------------------------
            Document documento2 = new Document(PageSize.LETTER, 50, 50, 50, 50);
            File pdf3 = new File(folder,"PruebaCom.pdf");
            PdfWriter escritorpdf1 = PdfWriter.getInstance(documento2, new FileOutputStream(pdf3));
            documento2.open();

            //Posicionamiento del Texto
            //Correlativo Ficha
            PdfContentByte cbb = escritorpdf1.getDirectContent();
            cbb.beginText();
            float posy1 = documento2.getPageSize().getHeight() - 101;
            cbb.setFontAndSize(baseFont, 12);
            cbb.setTextMatrix(60, posy1);
            cbb.showText(preferencias.getString("idficha", ""));
            cbb.endText();

            //Nombre del Paciente
            PdfContentByte cbb2 = escritorpdf1.getDirectContent();
            cbb2.beginText();
            posy1 = documento2.getPageSize().getHeight() - 101;
            cbb2.setFontAndSize(baseFont, 12);
            cbb2.setTextMatrix(180, posy1);
            cbb2.showText(preferencias.getString("nombre", ""));
            cbb2.endText();

            //Edad del paciente
            /*PdfContentByte cbb3 = escritorpdf.getDirectContent();
            cbb3.beginText();
            posy = documento.getPageSize().getHeight() - 101;
            cbb3.setFontAndSize(baseFont, 12);
            cbb3.setTextMatrix(518, posy);
            cbb3.showText(preferencias.getString("nombre", ""));
            cbb3.endText();*/

            //Firma del Paciente
            Image imagen = Image.getInstance(fotoFirma);
            posy1 = documento2.getPageSize().getHeight() - 650;
            imagen.scaleAbsolute(250, 135);
            imagen.setAbsolutePosition(350, posy1);
            documento2.add(imagen);
            documento2.close();

            //Estampando en las plantillas
            //Plantillas Contrato Compromiso

            //Contrato Compromiso ----------------------------------------------------------------------
            carpetaPlantillas = new File(Environment.getExternalStorageDirectory().toString(), "Plantillas");

            if (!carpetaPlantillas.exists()) {
                carpetaPlantillas.mkdirs();
            }

            File plantilla2 = new File(carpetaPlantillas, "compromiso.pdf");
            pdfInfo = new File(folder, "PruebaCom.pdf");

            if (!plantilla2.exists()) {
                CopyRawToSDCard(R.raw.compromiso, plantilla2.getAbsolutePath());
            }

            reader = new PdfReader(plantilla2.getAbsolutePath());
            info = new PdfReader(pdfInfo.getAbsolutePath());

            //Estampando portada
            File pdf5 = new File(folder, "FinalCon.pdf");
            estampador = new PdfStamper(reader, new FileOutputStream(pdf5));

            //Estampando informacion
            for (int i = 1; i < reader.getNumberOfPages() + 1; i++) {
                PdfImportedPage page = estampador.getImportedPage(info, i);
                estampador.getOverContent(i).addTemplate(page, 0, 0);
            }

            estampador.close();
            info.close();
            reader.close();

            archivo = new File("PruebaCom.pdf");
            archivo.delete();

            //Evaluacion ---------------------------------------------------------------------------
            Document documento3 = new Document(PageSize.LETTER, 50, 50, 50, 50);
            File pdf6 = new File(folder,"PruebaEva.pdf");
            PdfWriter escritorpdf2 = PdfWriter.getInstance(documento3, new FileOutputStream(pdf6));
            documento3.open();

            //Posicionamiento del Texto
            //Correlativo Ficha
            PdfContentByte cbbb1 = escritorpdf2.getDirectContent();
            cbbb1.beginText();
            float posy2 = documento3.getPageSize().getHeight() - 101;
            cbbb1.setFontAndSize(baseFont, 12);
            cbbb1.setTextMatrix(60, posy2);
            cbbb1.showText(preferencias.getString("idficha", ""));
            cbbb1.endText();

            //Nombre del Paciente
            PdfContentByte cbbb2 = escritorpdf2.getDirectContent();
            cbbb2.beginText();
            posy2 = documento3.getPageSize().getHeight() - 101;
            cbbb2.setFontAndSize(baseFont, 12);
            cbbb2.setTextMatrix(180, posy2);
            cbbb2.showText(preferencias.getString("nombre", ""));
            cbbb2.endText();

            //Edad del paciente
            /*PdfContentByte cb3 = escritorpdf.getDirectContent();
            cb3.beginText();
            posy = documento3.getPageSize().getHeight() - 101;
            cb3.setFontAndSize(baseFont, 12);
            cb3.setTextMatrix(518, posy);
            cb3.showText(edad);
            cb3.endText();*/

            //Datos
            //Primera Linea
            PdfContentByte cbb4 = escritorpdf2.getDirectContent();
            cbb4.beginText();
            posy2 = documento3.getPageSize().getHeight() - 170;
            cbb4.setFontAndSize(baseFont, 14);
            cbb4.setTextMatrix(139, posy2);
            String linea;
            int espacio = 0;

            if (ad1.isChecked()) {
                linea = "Si";
                espacio = 0;
            } else {
                linea = "No";
                espacio = 2;
            }

            for (int i = 0; i < 167 - espacio; i++) {
                linea += " ";
            }

            if (oc1.isChecked()) {
                linea += "Si";
            } else {
                linea += "No";
            }

            cbb4.showText(linea);
            cbb4.endText();

            //Segunda Linea
            PdfContentByte cbb5 = escritorpdf2.getDirectContent();
            cbb5.beginText();
            posy2 = documento3.getPageSize().getHeight() - 200;
            cbb5.setFontAndSize(baseFont, 14);
            cbb5.setTextMatrix(139, posy2);
            String linea2;
            espacio = 0;

            if (ad2.isChecked()) {
                linea2 = "Si";
                espacio = 0;
            } else {
                linea2 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea2 += " ";
            }

            if (fas1.isChecked()) {
                linea2 += "Si";
                espacio = 0;
            } else {
                linea2 += "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea2 += " ";
            }

            if (fai1.isChecked()) {
                linea2 += "Si";
                espacio = 0;
            } else {
                linea2 += "No";
                espacio = 3;
            }

            for (int i = 0; i < 53 - espacio; i++) {
                linea2 += " ";
            }

            if (oc2.isChecked()) {
                linea2 += "Si";
            } else {
                linea2 += "No";
            }

            cbb5.showText(linea2);
            cbb5.endText();

            //Tercera Linea
            PdfContentByte cbb6 = escritorpdf2.getDirectContent();
            cbb6.beginText();
            posy2 = documento3.getPageSize().getHeight() - 232;
            cbb6.setFontAndSize(baseFont, 14);
            cbb6.setTextMatrix(139, posy2);
            String linea3;

            if (ad3.isChecked()) {
                linea3 = "Si";
                espacio = 0;
            } else {
                linea3 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea3 += " ";
            }

            if (fas2.isChecked()) {
                linea3 += "Si";
                espacio = 0;
            } else {
                linea3 += "No";
                espacio = 3;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea3 += " ";
            }

            if (fai2.isChecked()) {
                linea3 += "Si";
                espacio = 0;
            } else {
                linea3 += "No";
                espacio = 2;
            }

            for (int i = 0; i < 53 - espacio; i++) {
                linea3 += " ";
            }

            if (oc3.isChecked()) {
                linea3 += "Si";
            } else {
                linea3 += "No";
            }

            cbb6.showText(linea3);
            cbb6.endText();

            //Cuarta Linea
            PdfContentByte cbb7 = escritorpdf2.getDirectContent();
            cbb7.beginText();
            posy2 = documento3.getPageSize().getHeight() - 264;
            cbb7.setFontAndSize(baseFont, 14);
            cbb7.setTextMatrix(139, posy2);
            String linea4;

            if (ad4.isChecked()) {
                linea4 = "Si";
                espacio = 0;
            } else {
                linea4 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea4 += " ";
            }

            if (fas3.isChecked()) {
                linea4 += "Si";
                espacio = 0;
            } else {
                linea4 += "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea4 += " ";
            }

            if (fai3.isChecked()) {
                linea4 += "Si";
                espacio = 0;
            } else {
                linea4 += "No";
                espacio = 3;
            }

            for (int i = 0; i < 53 - espacio; i++) {
                linea4 += " ";
            }

            if (oc4.isChecked()) {
                linea4 += "Si";
            } else {
                linea4 += "No";
            }

            cbb7.showText(linea4);
            cbb7.endText();

            //Quita Linea
            PdfContentByte cbb8 = escritorpdf2.getDirectContent();
            cbb8.beginText();
            posy2 = documento3.getPageSize().getHeight() - 296;
            cbb8.setFontAndSize(baseFont, 14);
            cbb8.setTextMatrix(139, posy2);
            String linea5;

            if (ad4.isChecked()) {
                linea5 = "Si";
                espacio = 0;
            } else {
                linea5 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 167 - espacio; i++) {
                linea5 += " ";
            }

            if (oc4.isChecked()) {
                linea5 += "Si";
            } else {
                linea5 += "No";
            }

            cbb8.showText(linea5);
            cbb8.endText();

            //Sexta Linea
            PdfContentByte cb9 = escritorpdf2.getDirectContent();
            cb9.beginText();
            posy2 = documento3.getPageSize().getHeight() - 375;
            cb9.setFontAndSize(baseFont, 14);
            cb9.setTextMatrix(139, posy2);
            String linea6;

            if (df1.isChecked()) {
                linea6 = "Si";
                espacio = 0;
            } else {
                linea6 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea6 += " ";
            }

            if (rp1.isChecked()) {
                linea6 += "Si";
                espacio = 0;
            } else {
                linea6 += "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea6 += " ";
            }

            if (len1.isChecked()) {
                linea6 += "Si";
                espacio = 0;
            } else {
                linea6 += "No";
                espacio = 3;
            }

            for (int i = 0; i < 53 - espacio; i++) {
                linea6 += " ";
            }

            if (de1.isChecked()) {
                linea6 += "Si";
            } else {
                linea6 += "No";
            }

            cb9.showText(linea6);
            cb9.endText();

            //Septima Linea
            PdfContentByte cb10 = escritorpdf2.getDirectContent();
            cb10.beginText();
            posy2 = documento3.getPageSize().getHeight() - 408;
            cb10.setFontAndSize(baseFont, 14);
            cb10.setTextMatrix(139, posy2);
            String linea7;

            if (df2.isChecked()) {
                linea7 = "Si";
                espacio = 0;
            } else {
                linea7 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea7 += " ";
            }

            if (rp2.isChecked()) {
                linea7 += "Si";
                espacio = 0;
            } else {
                linea7 += "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea7 += " ";
            }

            if (len2.isChecked()) {
                linea7 += "Si";
                espacio = 0;
            } else {
                linea7 += "No";
                espacio = 3;
            }

            for (int i = 0; i < 53 - espacio; i++) {
                linea7 += " ";
            }

            if (de2.isChecked()) {
                linea7 += "Si";
            } else {
                linea7 += "No";
            }

            cb10.showText(linea7);
            cb10.endText();

            //Octava Linea
            PdfContentByte cb11 = escritorpdf2.getDirectContent();
            cb11.beginText();
            posy2 = documento3.getPageSize().getHeight() - 438;
            cb11.setFontAndSize(baseFont, 14);
            cb11.setTextMatrix(139, posy2);
            String linea8;

            if (df3.isChecked()) {
                linea8 = "Si";
                espacio = 0;
            } else {
                linea8 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea8 += " ";
            }

            if (rp3.isChecked()) {
                linea8 += "Si";
                espacio = 0;
            } else {
                linea8 += "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea8 += " ";
            }

            if (len3.isSelected()) {
                linea8 += "Si";
            } else {
                linea8 += "No";
            }

            cb11.showText(linea8);
            cb11.endText();

            //Novena Linea
            PdfContentByte cb12 = escritorpdf2.getDirectContent();
            cb12.beginText();
            posy2 = documento3.getPageSize().getHeight() - 472;
            cb12.setFontAndSize(baseFont, 14);
            cb12.setTextMatrix(139, posy2);
            String linea9;

            if (df4.isChecked()) {
                linea9 = "Si";
                espacio = 0;
            } else {
                linea9 = "No";
                espacio = 2;
            }

            for (int i = 0; i < 52 - espacio; i++) {
                linea9 += " ";
            }

            if (rp4.isChecked()) {
                linea9 += "Si";
            } else {
                linea9 += "No";
            }

            cb12.showText(linea9);
            cb12.endText();

            //Decima Linea
            PdfContentByte cb13 = escritorpdf2.getDirectContent();
            cb13.beginText();
            posy2 = documento3.getPageSize().getHeight() - 505;
            cb13.setFontAndSize(baseFont, 14);
            cb13.setTextMatrix(275, posy2);
            String linea10;

            if (rp5.isChecked()) {
                linea10 = "Si";
            } else {
                linea10 = "No";
            }

            cb13.showText(linea10);
            cb13.endText();

            //Labios Mejillas
            PdfContentByte cb14 = escritorpdf2.getDirectContent();
            cb14.beginText();
            posy2 = documento3.getPageSize().getHeight() - 583;
            cb14.setFontAndSize(baseFont, 14);
            cb14.setTextMatrix(208, posy2);
            String linea11;

            if (lm1.isChecked()) {
                linea11 = "Si";
            } else {
                linea11 = "No";
            }

            cb14.showText(linea11);
            cb14.endText();

            cb14.beginText();
            posy2 = documento3.getPageSize().getHeight() - 613;
            cb14.setFontAndSize(baseFont, 14);
            cb14.setTextMatrix(208, posy2);

            if (lm2.isChecked()) {
                linea11 = "Si";
            } else {
                linea11 = "No";
            }

            cb14.showText(linea11);
            cb14.endText();

            //Habitos
            PdfContentByte cb15 = escritorpdf2.getDirectContent();
            cb15.beginText();
            posy2 = documento3.getPageSize().getHeight() - 577;
            cb15.setFontAndSize(baseFont, 12);
            cb15.setTextMatrix(343, posy2);
            String linea12;

            if (h1.isChecked()) {
                linea12 = "Si";
            } else {
                linea12 = "No";
            }

            cb15.showText(linea12);
            cb15.endText();

            cb15.beginText();
            posy2 = documento3.getPageSize().getHeight() - 607;
            cb15.setFontAndSize(baseFont, 12);
            cb15.setTextMatrix(343, posy2);

            if (h2.isChecked()) {
                linea11 = "Si";
            } else {
                linea11 = "No";
            }

            cb15.showText(linea11);
            cb15.endText();

            cb15.beginText();
            posy2 = documento3.getPageSize().getHeight() - 637;
            cb15.setFontAndSize(baseFont, 12);
            cb15.setTextMatrix(343, posy2);

            if (h3.isChecked()) {
                linea11 = "Si";
            } else {
                linea11 = "No";
            }

            cb15.showText(linea11);
            cb15.endText();

            cb15.beginText();
            posy2 = documento3.getPageSize().getHeight() - 667;
            cb15.setFontAndSize(baseFont, 12);
            cb15.setTextMatrix(343, posy2);

            if (h4.isChecked()) {
                linea11 = "Si";
            } else {
                linea11 = "No";
            }

            cb15.showText(linea11);
            cb15.endText();

            cb15.beginText();
            posy2 = documento3.getPageSize().getHeight() - 697;
            cb15.setFontAndSize(baseFont, 12);
            cb15.setTextMatrix(343, posy2);

            if (h5.isChecked()) {
                linea11 = "Si";
            } else {
                linea11 = "No";
            }

            cb15.showText(linea11);
            cb15.endText();

            //TMJ
            PdfContentByte cb16 = escritorpdf2.getDirectContent();
            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 577;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);
            String linea13;

            if (tmj1.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();


            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 597;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);

            if (tmj2.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();

            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 617;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);

            if (tmj3.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();

            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 637;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);

            if (tmj4.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();

            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 657;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);

            if (tmj5.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();

            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 677;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);

            if (tmj6.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();

            cb16.beginText();
            posy2 = documento3.getPageSize().getHeight() - 697;
            cb16.setFontAndSize(baseFont, 12);
            cb16.setTextMatrix(480, posy2);

            if (tmj7.isChecked()) {
                linea13 = "Si";
            } else {
                linea13 = "No";
            }

            cb16.showText(linea13);
            cb16.endText();

            documento3.newPage();

            Paragraph esp = new Paragraph(" ", fuentecolumna);
            documento3.add(esp);
            documento3.add(esp);

            Paragraph tit = new Paragraph(descripcion.getText().toString(), fuentecolumna2);
            documento3.add(tit);

            documento3.close();

            //Estampando en las plantillas
            //Plantillas Evaluacion

            //Evaluacion ----------------------------------------------------------------------
            carpetaPlantillas = new File(Environment.getExternalStorageDirectory().toString(), "Plantillas");

            if (!carpetaPlantillas.exists()) {
                carpetaPlantillas.mkdirs();
            }

            File plantilla3 = new File(carpetaPlantillas, "evaluacion.pdf");
            pdfInfo = new File(folder, "PruebaEva.pdf");

            if (!plantilla3.exists()) {
                CopyRawToSDCard(R.raw.evaluacion, plantilla3.getAbsolutePath());
            }

            reader = new PdfReader(plantilla3.getAbsolutePath());
            info = new PdfReader(pdfInfo.getAbsolutePath());

            //Estampando portada
            File pdf7 = new File(folder, "FinalEva.pdf");
            estampador = new PdfStamper(reader, new FileOutputStream(pdf7));

            //Estampando informacion
            for (int i = 1; i < reader.getNumberOfPages() + 1; i++) {
                PdfImportedPage page = estampador.getImportedPage(info, i);
                estampador.getOverContent(i).addTemplate(page, 0, 0);
            }

            estampador.close();
            info.close();
            reader.close();

            archivo = new File("PruebaEva.pdf");
            archivo.delete();

            //Control de Visitas -------------------------------------------------------------------
            Document documento4 = new Document(PageSize.LETTER, 50, 50, 50, 50);
            File pdf8 = new File(folder, "PruebaVis.pdf");
            PdfWriter escritorpdf3 = PdfWriter.getInstance(documento4, new FileOutputStream(pdf8));

            documento4.open();

            //Posicionamiento del Texto
            //Correlativo Ficha
            PdfContentByte cb17 = escritorpdf3.getDirectContent();
            cb17.beginText();
            float posy3 = documento4.getPageSize().getHeight() - 101;
            cb17.setFontAndSize(baseFont, 12);
            cb17.setTextMatrix(60, posy3);
            cb17.showText(preferencias.getString("idficha", ""));
            cb17.endText();

            //Nombre del Paciente
            PdfContentByte cb18 = escritorpdf3.getDirectContent();
            cb18.beginText();
            posy3 = documento4.getPageSize().getHeight() - 101;
            cb18.setFontAndSize(baseFont, 12);
            cb18.setTextMatrix(180, posy3);
            cb18.showText(preferencias.getString("nombre", ""));
            cb18.endText();

            //Edad del paciente
            /*PdfContentByte cb19 = escritorpdf3.getDirectContent();
            cb19.beginText();
            posy3 = documento4.getPageSize().getHeight() - 101;
            cb19.setFontAndSize(baseFont, 12);
            cb19.setTextMatrix(518, posy3);
            cb19.showText(edad);
            cb19.endText();*/

            esp = new Paragraph(" ", fuentecolumna);
            documento4.add(esp);
            documento4.add(esp);
            documento4.add(esp);
            documento4.add(esp);
            documento4.add(esp);
            documento4.add(esp);
            documento4.add(esp);

            double totV = 0, totP = 0, dif;

            if (tablaDinamica.getCount() > 0) {
                esp = new Paragraph("Tabla de Visitas", fuentetitulo);
                esp.setAlignment(Element.ALIGN_CENTER);
                documento4.add(esp);

                //Tabla Visitas
                PdfPTable TablaV = new PdfPTable(3);
                PdfPCell columnasV, filasV;
                String registros2[] = {"Fecha", "Descripsion", "Monto"};

                //Columnas
                for (int i = 0; i < 3; i++) {
                    columnasV = new PdfPCell(new Phrase(registros2[i], fuentecolumna));
                    columnasV.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnasV.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnasV.setBackgroundColor(fondo);
                    TablaV.addCell(columnasV);
                }
                TablaV.setHeaderRows(tablaDinamica.getCount());

                //Relleno de las filas
                for (int row = 1; row < tablaDinamica.getCount() + 1; row++) {
                    for (int column = 0; column < 3; column++) {
                        filasV = new PdfPCell(new Phrase(tablaDinamica.getCellData(row, column), fuentedatos));
                        filasV.setHorizontalAlignment(Element.ALIGN_CENTER);
                        TablaV.addCell(filasV);
                    }
                }
                TablaV.setSpacingBefore(15);
                documento4.add(TablaV);

                for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                    totV += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                }
            }

            if (tablaDinamica2.getCount() > 0){

                esp = new Paragraph("Tabla de Pagos", fuentetitulo);
                esp.setAlignment(Element.ALIGN_CENTER);
                documento4.add(esp);

                PdfPTable TablaP = new PdfPTable(3);
                PdfPCell columnasP, filasP;
                String registros[] = {"Fecha", "Descripsion", "Monto"};

                //Columnas
                for (int i = 0; i < 3; i++) {
                    columnasP = new PdfPCell(new Phrase(registros[i], fuentecolumna));
                    columnasP.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnasP.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnasP.setBackgroundColor(fondo);
                    TablaP.addCell(columnasP);
                }
                TablaP.setHeaderRows(tablaDinamica2.getCount());

                //Relleno de las filas
                for (int row = 1; row < tablaDinamica2.getCount() + 1; row++) {
                    for (int column = 0; column < 3; column++) {
                        filasP = new PdfPCell(new Phrase(tablaDinamica2.getCellData(row, column), fuentedatos));
                        filasP.setHorizontalAlignment(Element.ALIGN_CENTER);
                        TablaP.addCell(filasP);
                    }
                }
                TablaP.setSpacingBefore(15);
                documento4.add(TablaP);

                for (int i = 1; i < tablaDinamica2.getCount() + 1; i++){
                    totP += Double.parseDouble(tablaDinamica2.getCellData(i, 2));
                }
            }


            dif =  totV - totP;

            //Edad del paciente
            PdfContentByte cb20 = escritorpdf3.getDirectContent();

            cb20.beginText();
            posy3 = documento4.getPageSize().getHeight() - 159;
            cb20.setFontAndSize(baseFont, 14);
            cb20.setTextMatrix(140, posy3);
            cb20.showText(String.format("%.2f", totV));
            cb20.endText();

            cb20.beginText();
            posy3 = documento4.getPageSize().getHeight() - 159;
            cb20.setFontAndSize(baseFont, 14);
            cb20.setTextMatrix(258, posy3);
            cb20.showText(String.format("%.2f", totP));
            cb20.endText();

            cb20.beginText();
            posy3 = documento4.getPageSize().getHeight() - 159;
            cb20.setFontAndSize(baseFont, 14);
            cb20.setTextMatrix(380, posy3);
            cb20.showText(String.format("%.2f", dif));
            cb20.endText();

            documento4.close();

            //Estampando en las plantillas
            //Plantillas Contrato Compromiso

            //Contrato Compromiso ----------------------------------------------------------------------
            carpetaPlantillas = new File(Environment.getExternalStorageDirectory().toString(), "Plantillas");

            if (!carpetaPlantillas.exists()) {
                carpetaPlantillas.mkdirs();
            }

            File plantilla4 = new File(carpetaPlantillas, "hoja_control.pdf");
            pdfInfo = new File(folder, "PruebaVis.pdf");

            if (!plantilla4.exists()) {
                CopyRawToSDCard(R.raw.hoja_control, plantilla4.getAbsolutePath());
            }

            reader = new PdfReader(plantilla4.getAbsolutePath());
            info = new PdfReader(pdfInfo.getAbsolutePath());

            //Estampando portada
            File pdf9 = new File(folder, "FinalVis.pdf");
            estampador = new PdfStamper(reader, new FileOutputStream(pdf9));

            //Estampando informacion
            for (int i = 1; i < reader.getNumberOfPages() + 1; i++) {
                PdfImportedPage page = estampador.getImportedPage(info, i);
                estampador.getOverContent(i).addTemplate(page, 0, 0);
            }

            estampador.close();
            info.close();
            reader.close();

            archivo = new File("PruebaVis.pdf");
            archivo.delete();


            //Archivo Final ------------------------------------------------------------------------
            File fichero1 = new File(folder,"FinalP.pdf");
            File fichero2 = new File(folder,"FinalCon.pdf");
            File fichero3 = new File(folder,"FinalEva.pdf");
            File fichero4 = new File(folder,"FinalVis.pdf");
            PdfReader doc1 = new PdfReader(fichero1.getAbsolutePath());
            PdfReader doc2 = new PdfReader(fichero2.getAbsolutePath());
            PdfReader doc3 = new PdfReader(fichero3.getAbsolutePath());
            PdfReader doc4 = new PdfReader(fichero4.getAbsolutePath());

            File pdfFinal = new File(folder,"EvaluacionEsp.pdf");
            estampador = new PdfStamper(doc1, new FileOutputStream(pdfFinal));

            PdfImportedPage page = estampador.getImportedPage(doc2, 1);
            estampador.insertPage(2, doc2.getPageSize(1));
            estampador.getOverContent(2).addTemplate(page, 0, 0);

            for (int i = 1; i < doc3.getNumberOfPages() + 1; i++) {
                page = estampador.getImportedPage(doc3, i);
                estampador.insertPage(i + 2, doc3.getPageSize(i));
                estampador.getOverContent(i + 2).addTemplate(page, 0, 0);
                contador++;
            }

            for (int i = 1; i < doc4.getNumberOfPages() + 1; i++) {
                page = estampador.getImportedPage(doc4, i);
                estampador.insertPage(i + 4, doc4.getPageSize(i));
                estampador.getOverContent(i + 4).addTemplate(page, 0, 0);
            }

            estampador.close();
            doc1.close();
            doc2.close();
            doc3.close();
            doc4.close();

            fichero1.delete();
            fichero2.delete();
            fichero3.delete();
            fichero4.delete();

            if (pdfFinal.exists()) {
                LectorPDF lectorPDF = new LectorPDF(pdfFinal);
                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setCustomAnimations(R.anim.slide_up,R.anim.slide_down);
                transaction.add(R.id.contenedor, lectorPDF);
                transaction.commit();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkearPermiso() {
        //Array de permisos
        String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permisos) {
            int res = getActivity().checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void solicitarPermiso() {
        String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {         //Verificamos si la version de android del dispositivo es mayor
            requestPermissions(permisos, CODIGO_SOLICITUD_PERMISO);  //o igual a MarshMallow
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean autorizado = true;   //Si el permiso fue autorizado
        switch (requestCode) {
            case CODIGO_SOLICITUD_PERMISO:
                for (int res : grantResults) {
                    //si el usuario concedió todos los permisos
                    autorizado = autorizado && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;

            default:
                //Si el usuario autorizó los permisos
                autorizado = false;
                break;
        }

        if (autorizado) {
            //Si el usuario autorizó todos los permisos podemos ejecutar nuestra tarea
            //abrirCamara();
            generarPDF();
        } else {
            //Se debe alertar al usuario que los permisos no han sido concedidos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "Los permisos de almacenamiento externo fueron denegados", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void CopyRawToSDCard(int id, String path) {
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();
            Log.i(TAG, "copyFile, success!");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile FileNotFoundException " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "copyFile IOException " + e.getMessage());
        }
    }
}
