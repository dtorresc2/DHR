package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sistemasdt.dhr.Componentes.PDF.LectorPDF;
import com.sistemasdt.dhr.Componentes.Tabla.TablaDinamica;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Catalogos.Pacientes.ListadoPacientes;
import com.tapadoo.alerter.Alerter;

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

public class Historiales extends Fragment {
    private Toolbar toolbar;
    private TextView titulo_hm, titulo_pad, titulo_ho, titulo_trat, tituloPagos, titulofotos;
    private EditText desc_hospi, desc_alergia, desc_med, desc_otro, desc_dolor, desc_otroHO;
    private CheckBox hospitalizado, alergia, medicamento, tratamiento, hemorragia;
    private CheckBox corazon, artritis, tuberculosis, fiebre, presionA, presionB, diabetes, anemia, epilepsia;
    private CheckBox dolor, gingivitis;
    private TableLayout tableLayout, tableLayout2;
    private TablaDinamica tablaDinamica, tablaDinamica2;
    private String[] header = {"Pieza", "Descripsion", "Costo"};
    private String[] header2 = {"Descripsion", "Pago"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private ArrayList<String[]> rows2 = new ArrayList<>();
    private ArrayList<Bitmap> lista_fotos = new ArrayList<Bitmap>();
    private ImageButton atras, adelante;
    private TextView actual, total_fotos, separador;
    private ImageView galeria;
    private SharedPreferences preferencias;
    private RequestQueue requestQueue;
    private int seleccionado;
    private static final String TAG = "MyActivity";
    private TextView titulo_costo, costo_total, titulo_pagos, pagos_total;
    private double total, totalPagos;
    private static final int CODIGO_SOLICITUD_PERMISO = 123;

    public Historiales() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_historiales, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ficha Normal");
        toolbar.inflateMenu(R.menu.opciones_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListadoPacientes listadoPacientes = new ListadoPacientes();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, listadoPacientes);
                transaction.commit();
            }
        });

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

        //Declarando Historial Medico - Ficha
        titulo_hm = view.findViewById(R.id.titulo_hm_cons);
        titulo_hm.setTypeface(face);

        hospitalizado = view.findViewById(R.id.hospitalizado_cons);
        hospitalizado.setTypeface(face);

        desc_hospi = view.findViewById(R.id.desc_hospi_cons);
        desc_hospi.setTypeface(face);

        alergia = view.findViewById(R.id.alergia_cons);
        alergia.setTypeface(face);

        desc_alergia = view.findViewById(R.id.desc_alergia_cons);
        desc_alergia.setTypeface(face);

        medicamento = view.findViewById(R.id.alergia_cons);
        medicamento.setTypeface(face);

        desc_med = view.findViewById(R.id.desc_alergia_cons);
        desc_med.setTypeface(face);

        tratamiento = view.findViewById(R.id.tratamiento_cons);
        tratamiento.setTypeface(face);

        hemorragia = view.findViewById(R.id.hemorragia_cons);
        hemorragia.setTypeface(face);

        //Declarando Historial Medico - Padecimientos
        titulo_pad = view.findViewById(R.id.pad_cons);
        titulo_pad.setTypeface(face);

        corazon = view.findViewById(R.id.corazon_cons);
        corazon.setTypeface(face);

        artritis = view.findViewById(R.id.artritris_cons);
        artritis.setTypeface(face);

        tuberculosis = view.findViewById(R.id.tuberculosis_cons);
        tuberculosis.setTypeface(face);

        fiebre = view.findViewById(R.id.fiebre_cons);
        fiebre.setTypeface(face);

        presionA = view.findViewById(R.id.presion_alta_cons);
        presionA.setTypeface(face);

        presionB = view.findViewById(R.id.presion_baja_cons);
        presionB.setTypeface(face);

        diabetes = view.findViewById(R.id.diabetes_cons);
        diabetes.setTypeface(face);

        anemia = view.findViewById(R.id.anemia_cons);
        anemia.setTypeface(face);

        epilepsia = view.findViewById(R.id.epilepsia_cons);
        epilepsia.setTypeface(face);

        desc_otro = view.findViewById(R.id.otro_hm_cons);
        desc_otro.setTypeface(face);

        //Declarando Historial Odontodologico - General
        titulo_ho = view.findViewById(R.id.titulo_ho_cons);
        titulo_ho.setTypeface(face);

        dolor = view.findViewById(R.id.dolor_cons);
        dolor.setTypeface(face);

        desc_dolor = view.findViewById(R.id.desc_dolor_cons);
        desc_dolor.setTypeface(face);

        gingivitis = view.findViewById(R.id.gingivitis_cons);
        gingivitis.setTypeface(face);

        desc_otroHO = view.findViewById(R.id.otro_ho_cons);
        desc_otroHO.setTypeface(face);

        //Declarando Historial Odontodologico - Tratamiento

        titulo_trat = view.findViewById(R.id.titulo_tratamiento_cons);
        titulo_trat.setTypeface(face);

        titulo_costo = view.findViewById(R.id.titulo_costo_cons);
        titulo_costo.setTypeface(face);

        costo_total = view.findViewById(R.id.total_costo_cons);
        costo_total.setTypeface(face);

        tableLayout = view.findViewById(R.id.tabla_consulta);

        tablaDinamica = new TablaDinamica(tableLayout, getContext());
        tablaDinamica.addHeader(header);
        tablaDinamica.addData(getClients());
        tablaDinamica.fondoHeader(R.color.AzulOscuro);

        //Declarando Historial Odontodologico - Pagos

        tituloPagos = view.findViewById(R.id.titulo_pagosCons);
        tituloPagos.setTypeface(face);

        titulo_pagos = view.findViewById(R.id.titulo_totalPago);
        titulo_pagos.setTypeface(face);

        pagos_total = view.findViewById(R.id.total_pagoConsulta);
        pagos_total.setTypeface(face);

        tableLayout2 = view.findViewById(R.id.tabla_pagosConsulta);

        tablaDinamica2 = new TablaDinamica(tableLayout2, getContext());
        tablaDinamica2.addHeader(header2);
        tablaDinamica2.addData(getClients2());
        tablaDinamica2.fondoHeader(R.color.AzulOscuro);


        //Declarando Historial Fotografico
        titulofotos = view.findViewById(R.id.titulo_historialfotografico);
        titulofotos.setTypeface(face);

        actual = view.findViewById(R.id.actual_hf_cons);
        actual.setTypeface(face);

        total_fotos = view.findViewById(R.id.total_hf_cons);
        total_fotos.setTypeface(face);

        separador = view.findViewById(R.id.separador_cons);
        separador.setTypeface(face);

        galeria = view.findViewById(R.id.galeria_cons);

        atras = view.findViewById(R.id.atras_hf_cons);
        adelante = view.findViewById(R.id.adelante_hf_cons);

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lista_fotos.size() > 0) {
                    if (seleccionado < 1) {
                        seleccionado = lista_fotos.size() - 1;
                    } else {
                        seleccionado--;
                    }
                    galeria.setImageBitmap(lista_fotos.get(seleccionado));
                }
                actual.setText(String.valueOf(seleccionado + 1));
            }
        });

        adelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lista_fotos.size() > 0) {
                    if (seleccionado >= lista_fotos.size() - 1) {
                        seleccionado = 0;
                    } else {
                        seleccionado++;
                    }
                    galeria.setImageBitmap(lista_fotos.get(seleccionado));
                }
                actual.setText(String.valueOf(seleccionado + 1));
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            consultarHM("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=3");
            consultarHO("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=5");
            consultarHF("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=7");
            consultarPagos("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=9");
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

        return view;
    }

    private ArrayList<String[]> getClients() {
        return rows;
    }

    private ArrayList<String[]> getClients2() {
        return rows2;
    }

    //ListadoPacientes Historial Medico
    public void consultarHM(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("idHistorialMed");
                            hospitalizado.setChecked((jsonArray.getJSONObject(i).getInt("hospitalizado") > 0) ? true : false);
                            desc_hospi.setText(jsonArray.getJSONObject(i).getString("descriphos"));
                            tratamiento.setChecked((jsonArray.getJSONObject(i).getInt("tratmed") > 0) ? true : false);
                            alergia.setChecked((jsonArray.getJSONObject(i).getInt("alergia") > 0) ? true : false);
                            desc_alergia.setText(jsonArray.getJSONObject(i).getString("desc_al"));
                            hemorragia.setChecked((jsonArray.getJSONObject(i).getInt("hemorragia") > 0) ? true : false);
                            medicamento.setChecked((jsonArray.getJSONObject(i).getInt("medicamento") > 0) ? true : false);
                            desc_med.setText(jsonArray.getJSONObject(i).getString("desc_med"));

                            if (Integer.parseInt(id) > 0) {
                                consultarPadecimientos("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=4", id);
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

    //ListadoPacientes Historial Medico - Padecimientos
    public void consultarPadecimientos(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            artritis.setChecked((jsonArray.getJSONObject(i).getInt("artritis") > 0) ? true : false);
                            tuberculosis.setChecked((jsonArray.getJSONObject(i).getInt("tuberculosis") > 0) ? true : false);
                            presionA.setChecked((jsonArray.getJSONObject(i).getInt("presionalta") > 0) ? true : false);
                            presionB.setChecked((jsonArray.getJSONObject(i).getInt("presionbaja") > 0) ? true : false);
                            fiebre.setChecked((jsonArray.getJSONObject(i).getInt("fiebrereu") > 0) ? true : false);
                            anemia.setChecked((jsonArray.getJSONObject(i).getInt("anemia") > 0) ? true : false);
                            epilepsia.setChecked((jsonArray.getJSONObject(i).getInt("epilepsia") > 0) ? true : false);
                            diabetes.setChecked((jsonArray.getJSONObject(i).getInt("diabetes") > 0) ? true : false);
                            desc_otro.setText(jsonArray.getJSONObject(i).getString("otros"));
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

    //ListadoPacientes Historial Odontodologico
    public void consultarHO(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("idHistorialOd");
                            dolor.setChecked((jsonArray.getJSONObject(i).getInt("dolor") > 0) ? true : false);
                            desc_dolor.setText(jsonArray.getJSONObject(i).getString("descdolor"));
                            gingivitis.setChecked((jsonArray.getJSONObject(i).getInt("gingivitis") > 0) ? true : false);
                            desc_otroHO.setText(jsonArray.getJSONObject(i).getString("otros"));

                            if (Integer.parseInt(id) > 0) {
                                consultarTratamiento("http://dhr.sistemasdt.xyz/Normal/consultaficha.php?estado=6", id);
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

    //ListadoPacientes Historial Odontodologico - Tratamiento
    public void consultarTratamiento(String URL, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String[] item = new String[]{
                                    jsonArray.getJSONObject(i).getString("idPiezas"),
                                    jsonArray.getJSONObject(i).getString("plan"),
                                    String.format("%.2f", jsonArray.getJSONObject(i).getDouble("costo"))
                            };
                            tablaDinamica.addItem(item);
                        }

                        if (tablaDinamica.getCount() > 0) {
                            for (int i = 1; i < tablaDinamica.getCount() + 1; i++) {
                                total += Double.parseDouble(tablaDinamica.getCellData(i, 2));
                            }
                            costo_total.setText(String.format("%.2f", total));
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

    //ListadoPacientes Historial Fotografico
    public void consultarHF(String URL) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String codigo_foto = jsonArray.getJSONObject(i).getString("foto");
                            obtenerFotos(codigo_foto);

                            /*if (!codigo_foto.equalsIgnoreCase("")) {
                                byte[] b = Base64.decode(codigo_foto, Base64.DEFAULT);
                                Bitmap imagen_codificada = BitmapFactory.decodeByteArray(b, 0, b.length);
                                lista_fotos.add(imagen_codificada);
                            }*/
                        }
                        /*galeria.setImageBitmap(lista_fotos.get(0));
                        seleccionado = 0;
                        actual.setText(String.valueOf(seleccionado + 1));
                        total_fotos.setText(String.valueOf(lista_fotos.size()));*/
                    }

                } catch (JSONException e) {
                    Log.i(TAG, "" + e);
                    e.printStackTrace();
                }
                progressDialog.dismiss();
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);

    }

    public void obtenerFotos(String path){
        String URL = "http://dhr.sistemasdt.xyz/Normal/" + path ;

        ImageRequest imageRequest = new ImageRequest(URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if (response != null){
                            lista_fotos.add(response);

                            galeria.setImageBitmap(lista_fotos.get(0));
                            seleccionado = 0;
                            actual.setText(String.valueOf(seleccionado + 1));
                            total_fotos.setText(String.valueOf(lista_fotos.size()));
                        }
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(imageRequest);
    }


    public void consultarPagos(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String[] item = new String[]{
                                    jsonArray.getJSONObject(i).getString("descripsion"),
                                    String.format("%.2f", jsonArray.getJSONObject(i).getDouble("pago"))
                            };
                            tablaDinamica2.addItem(item);
                        }

                        if (tablaDinamica2.getCount() > 0) {
                            for (int i = 1; i < tablaDinamica2.getCount() + 1; i++) {
                                totalPagos += Double.parseDouble(tablaDinamica2.getCellData(i, 1));
                            }
                            pagos_total.setText(String.format("%.2f", totalPagos));
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

    private void generarPDF() {
        //InputStream fraw = getResources().openRawResource(R.raw.Compromiso);
        //Nuestra app tiene permiso

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
        }, 2000);

        try {
            BaseFont baseFont = BaseFont.createFont("assets/fonts/bahnschrift.ttf", "UTF-8", BaseFont.EMBEDDED);

            Font fuentecolumna = new Font(baseFont, 12, Font.NORMAL);
            fuentecolumna.setColor(255, 255, 255);
            BaseColor fondo = new BaseColor(49, 63, 76);

            Font fuentedatos = new Font(baseFont, 11, Font.NORMAL);
            fuentedatos.setColor(49, 63, 76);

            Font fuentecorrelativo = new Font(baseFont, 14, Font.NORMAL);
            fuentecorrelativo.setColor(49, 63, 76);

            //Creacion de la Carpeta - Fichas Normales
            File folder = new File(Environment.getExternalStorageDirectory().toString(), "FichasGenerales");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File pdf = new File(folder, "Prueba.pdf");

            //Creacion del PDF
            Document documento = new Document(PageSize.LETTER, 50, 50, 80, 30);
            PdfWriter.getInstance(documento, new FileOutputStream(pdf));
            documento.open();

            //Titulo
            Paragraph titulo = new Paragraph("Datos Personales", fuentecorrelativo);
            documento.add(titulo);

            //Tabla Datos Personales -------------------------------------------
            PdfPTable tabla_datospersonales = new PdfPTable(3);
            PdfPCell columnasdatos, filasdatos;
            String registros[] = {"Correlativo", "Nombre","Edad"};
            String columnasreg[] = {
                    preferencias.getString("idficha", ""),
                    preferencias.getString("nombre", ""),
                    preferencias.getString("edad", "")
            };
            tabla_datospersonales.setWidths(new float[]{2, 4,2});

            //Columnas
            for (int i = 0; i < registros.length; i++) {
                columnasdatos = new PdfPCell(new Phrase(registros[i], fuentecolumna));
                columnasdatos.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnasdatos.setVerticalAlignment(Element.ALIGN_CENTER);
                columnasdatos.setBackgroundColor(fondo);
                tabla_datospersonales.addCell(columnasdatos);
            }
            tabla_datospersonales.setHeaderRows(1);

            //Relleno de las filas
            for (int row = 0; row < 1; row++) {
                for (int column = 0; column < 3; column++) {
                    filasdatos = new PdfPCell(new Phrase(columnasreg[column], fuentedatos));
                    filasdatos.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla_datospersonales.addCell(filasdatos);
                }
            }
            tabla_datospersonales.setSpacingBefore(15);
            documento.add(tabla_datospersonales);

            Paragraph titulo2 = new Paragraph("Historial Medico", fuentecorrelativo);
            titulo2.setSpacingBefore(15);
            documento.add(titulo2);

            //Historial Medico
            Paragraph titulo3 = new Paragraph("Detalle", fuentedatos);
            titulo3.setAlignment(Element.ALIGN_CENTER);
            titulo3.setSpacingBefore(10);
            documento.add(titulo3);

            //Tabla Historial Medico -------------------------------------------
            PdfPTable tabla_historialdetalle = new PdfPTable(5);
            PdfPCell columnasmedd, filasmedd;
            String registros5[] = {"Hospitalizado", "Alergia", "Tratamiento", "Hemorragia", "Medicamento"};
            String celdash[] = new String[6];

            //Columnas
            for (int i = 0; i < 5; i++) {
                columnasmedd = new PdfPCell(new Phrase(registros5[i], fuentecolumna));
                columnasmedd.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnasmedd.setVerticalAlignment(Element.ALIGN_CENTER);
                columnasmedd.setBackgroundColor(fondo);
                tabla_historialdetalle.addCell(columnasmedd);
            }
            tabla_historialdetalle.setHeaderRows(1);

            if (hospitalizado.isChecked()) {
                celdash[0] = "Si";
            } else {
                celdash[0] = "No";
            }

            if (alergia.isChecked()) {
                celdash[1] = "Si";
            } else {
                celdash[1] = "No";
            }

            if (tratamiento.isChecked()) {
                celdash[2] = "Si";
            } else {
                celdash[2] = "No";
            }

            if (hemorragia.isChecked()) {
                celdash[3] = "Si";
            } else {
                celdash[3] = "No";
            }

            if (medicamento.isChecked()) {
                celdash[4] = "Si";
            } else {
                celdash[4] = "No";
            }

            //Relleno de las filas
            for (int row = 0; row < 1; row++) {
                for (int column = 0; column < 5; column++) {
                    filasmedd = new PdfPCell(new Phrase(celdash[column], fuentedatos));
                    filasmedd.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla_historialdetalle.addCell(filasmedd);
                }
            }
            tabla_historialdetalle.setSpacingBefore(15);
            documento.add(tabla_historialdetalle);

            Paragraph titulo4 = new Paragraph("Padecimientos", fuentedatos);
            titulo4.setAlignment(Element.ALIGN_CENTER);
            titulo4.setSpacingBefore(10);
            documento.add(titulo4);

            //Tabla Historial Medico Padecimientos #1
            PdfPTable tabla_padecimientos1 = new PdfPTable(4);
            PdfPCell columnaspade, filaspade;
            String registros6[] = {"Corazon", "Artritis", "Tuberculosis", "F.Reumatoide"};
            String celdaspa[] = new String[6];

            //Columnas
            for (int i = 0; i < 4; i++) {
                columnaspade = new PdfPCell(new Phrase(registros6[i], fuentecolumna));
                columnaspade.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnaspade.setVerticalAlignment(Element.ALIGN_CENTER);
                columnaspade.setBackgroundColor(fondo);
                tabla_padecimientos1.addCell(columnaspade);
            }
            tabla_padecimientos1.setHeaderRows(1);

            if (corazon.isChecked()) {
                celdaspa[0] = "Si";
            } else {
                celdaspa[0] = "No";
            }

            if (artritis.isChecked()) {
                celdaspa[1] = "Si";
            } else {
                celdaspa[1] = "No";
            }

            if (tuberculosis.isChecked()) {
                celdaspa[2] = "Si";
            } else {
                celdaspa[2] = "No";
            }

            if (fiebre.isChecked()) {
                celdaspa[3] = "Si";
            } else {
                celdaspa[3] = "No";
            }

            //Relleno de las filas
            for (int row = 0; row < 1; row++) {
                for (int column = 0; column < 4; column++) {
                    filaspade = new PdfPCell(new Phrase(celdaspa[column], fuentedatos));
                    filaspade.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla_padecimientos1.addCell(filaspade);
                }
            }
            tabla_padecimientos1.setSpacingBefore(15);
            documento.add(tabla_padecimientos1);

            //Tabla Historial Medico Padecimientos #2
            PdfPTable tabla_padecimientos2 = new PdfPTable(5);
            PdfPCell columnaspade2, filaspade2;
            String registros7[] = {"P.Alta", "P.Baja", "Diabetes", "Anemia", "Epilepsia"};
            String celdaspa2[] = new String[6];

            //Columnas
            for (int i = 0; i < 5; i++) {
                columnaspade2 = new PdfPCell(new Phrase(registros7[i], fuentecolumna));
                columnaspade2.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnaspade2.setVerticalAlignment(Element.ALIGN_CENTER);
                columnaspade2.setBackgroundColor(fondo);
                tabla_padecimientos2.addCell(columnaspade2);
            }
            tabla_padecimientos2.setHeaderRows(1);

            if (presionA.isChecked()) {
                celdaspa2[0] = "Si";
            } else {
                celdaspa2[0] = "No";
            }

            if (presionB.isChecked()) {
                celdaspa2[1] = "Si";
            } else {
                celdaspa2[1] = "No";
            }

            if (diabetes.isChecked()) {
                celdaspa2[2] = "Si";
            } else {
                celdaspa2[2] = "No";
            }

            if (anemia.isChecked()) {
                celdaspa2[3] = "Si";
            } else {
                celdaspa2[3] = "No";
            }

            if (epilepsia.isChecked()) {
                celdaspa2[4] = "Si";
            } else {
                celdaspa2[4] = "No";
            }

            //Relleno de las filas
            for (int row = 0; row < 1; row++) {
                for (int column = 0; column < 5; column++) {
                    filaspade2 = new PdfPCell(new Phrase(celdaspa2[column], fuentedatos));
                    filaspade2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla_padecimientos2.addCell(filaspade2);
                }
            }
            tabla_padecimientos2.setSpacingBefore(15);
            documento.add(tabla_padecimientos2);

            if (!desc_otro.getText().equals("")) {
                Paragraph titulo5 = new Paragraph("Otros: " + desc_otro.getText(), fuentedatos);
                tabla_padecimientos2.setSpacingBefore(5);
                documento.add(titulo5);
            } else {
                Paragraph titulo5 = new Paragraph("             Otros: -", fuentedatos);
                tabla_padecimientos2.setSpacingBefore(5);
                documento.add(titulo5);
            }

            //Historial Odontodologico -----------------------------------------
            Paragraph titulo9 = new Paragraph("Historial Odontodologico", fuentecorrelativo);
            titulo9.setSpacingBefore(15);
            documento.add(titulo9);


            //Tabla Abonos
            PdfPTable tablaPagos = new PdfPTable(3);
            PdfPCell columnasPago, filasPago;
            String registros2[] = {"Pieza", "Tratamiento", "Pago"};

            //Columnas
            for (int i = 0; i < 3; i++) {
                columnasPago = new PdfPCell(new Phrase(registros2[i], fuentecolumna));
                columnasPago.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnasPago.setVerticalAlignment(Element.ALIGN_CENTER);
                columnasPago.setBackgroundColor(fondo);
                tablaPagos.addCell(columnasPago);
            }
            tablaPagos.setHeaderRows(tablaDinamica.getCount());

            //Relleno de las filas
            for (int row = 1; row < tablaDinamica.getCount() + 1; row++) {
                for (int column = 0; column < 3; column++) {
                    filasPago = new PdfPCell(new Phrase(tablaDinamica.getCellData(row, column), fuentedatos));
                    filasPago.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaPagos.addCell(filasPago);
                }
            }
            tablaPagos.setSpacingBefore(15);
            documento.add(tablaPagos);


            //Tabla Abonos
            PdfPTable tabla_abonos = new PdfPTable(2);
            PdfPCell columnasabo, filasabo;
            String registros3[] = {"Descripsion", "Pago"};


            //Columnas
            for (int i = 0; i < 2; i++) {
                columnasabo = new PdfPCell(new Phrase(registros3[i], fuentecolumna));
                columnasabo.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnasabo.setVerticalAlignment(Element.ALIGN_CENTER);
                columnasabo.setBackgroundColor(fondo);
                tabla_abonos.addCell(columnasabo);
            }
            tabla_abonos.setHeaderRows(tablaDinamica2.getCount());

            //Relleno de las filas
            for (int row = 1; row < tablaDinamica2.getCount() + 1; row++) {
                for (int column = 0; column < 2; column++) {
                    filasabo = new PdfPCell(new Phrase(tablaDinamica2.getCellData(row, column), fuentedatos));
                    filasabo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla_abonos.addCell(filasabo);
                }
            }
            tabla_abonos.setSpacingBefore(15);
            documento.add(tabla_abonos);

            //Tabla Balance
            PdfPTable tabla_balance = new PdfPTable(3);
            PdfPCell columnasbal, filasbal;
            String registros4[] = {"Costos", "Pagos", "Balance"};
            //String celdasreg[] = {costos.getText(), abonos.getText(), balance.getText()};
            String aux = null, aux2 = null, auxtot = null;
            double balance = 0;

            if (tablaDinamica.getCount() + 1 > 1) {
                double tot = 0;
                for (int i = 1; i < tablaDinamica.getCount() + 1; i++){
                    tot += Double.parseDouble(tablaDinamica.getCellData(i,2));
                }
                aux = String.format("%.2f", tot);
            }

            if (tablaDinamica2.getCount() + 1 > 1){
                double tot = 0;
                for (int i = 1; i < tablaDinamica2.getCount() + 1; i++){
                    tot += Double.parseDouble(tablaDinamica2.getCellData(i,1));
                }
                aux2 = String.format("%.2f", tot);
            }

            balance = Double.parseDouble(aux) - Double.parseDouble(aux2);
            auxtot = String.format("%.2f", balance);

            String celdasreg[] = {aux, aux2, auxtot};

            //Columnas
            for (int i = 0; i < 3; i++) {
                columnasbal = new PdfPCell(new Phrase(registros4[i], fuentecolumna));
                columnasbal.setHorizontalAlignment(Element.ALIGN_CENTER);
                columnasbal.setVerticalAlignment(Element.ALIGN_CENTER);
                columnasbal.setBackgroundColor(fondo);
                tabla_balance.addCell(columnasbal);
            }
            tabla_balance.setHeaderRows(1);

            //Relleno de las filas
            for (int row = 0; row < 1; row++) {
                for (int column = 0; column < 3; column++) {
                    filasbal = new PdfPCell(new Phrase(celdasreg[column], fuentedatos));
                    filasbal.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla_balance.addCell(filasbal);
                }
            }
            tabla_balance.setSpacingBefore(15);
            documento.add(tabla_balance);

            documento.close();

            //APLICANDO A LA PLANTILLA -----------------------------------------
            //Leyendo dos archivos plantillas
            //InputStream fraw = getResources().openRawResource(R.raw.Compromiso);
            //PdfReader reader = new PdfReader("PortadaFicha.pdf");
            //PdfReader reader2 = new PdfReader("CuerpoFicha.pdf");

            File carpetaPlantillas = new File(Environment.getExternalStorageDirectory().toString(), "Plantillas");

            if (!carpetaPlantillas.exists()) {
                carpetaPlantillas.mkdirs();
            }

            File plantilla1 = new File(carpetaPlantillas, "portada_ficha.pdf");
            File plantilla2 = new File(carpetaPlantillas, "cuerpo_ficha.pdf");
            File pdfInfo = new File(folder, "Prueba.pdf");

            if (!plantilla1.exists()) {
                CopyRawToSDCard(R.raw.portada_ficha, plantilla1.getAbsolutePath());
            }

            if (!plantilla2.exists()) {
                CopyRawToSDCard(R.raw.cuerpo_ficha, plantilla2.getAbsolutePath());
            }

            PdfReader reader = new PdfReader(plantilla1.getAbsolutePath());
            PdfReader reader2 = new PdfReader(plantilla2.getAbsolutePath());
            PdfReader info = new PdfReader(pdfInfo.getAbsolutePath());

            //Estampando portada
            File pdf2 = new File(folder, "Final.pdf");
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pdf2));

            if (info.getNumberOfPages() > 1) {
                //Estampando el resto de hojas
                for (int i = 1; i < info.getNumberOfPages() + 1; i++) {
                    PdfImportedPage page = stamper.getImportedPage(reader2, 1);
                    stamper.insertPage(i + 1, reader2.getPageSize(1));
                    stamper.getUnderContent(i + 1).addTemplate(page, 0, 0);
                }
            }

            stamper.close();
            reader.close();
            reader2.close();

            File pdf3 = new File(folder, "Final.pdf");
            PdfReader reader3 = new PdfReader(pdf3.getAbsolutePath());

            Long consecutivo = System.currentTimeMillis() / 1000;
            File pdfFinal = new File(folder, consecutivo.toString() + ".pdf");
            PdfStamper estampador_info = new PdfStamper(reader3, new FileOutputStream(pdfFinal));

            //Estampando informacion
            for (int i = 1; i < info.getNumberOfPages() + 1; i++) {
                PdfImportedPage page = estampador_info.getImportedPage(info, i);
                estampador_info.getOverContent(i).addTemplate(page, 0, 0);
            }

            estampador_info.close();
            reader3.close();
            info.close();

            pdf.delete();
            pdf2.delete();

            if (pdfFinal.exists()) {
                /*final AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                View viewCuadro2 = getLayoutInflater().inflate(R.layout.dialogo_pdf, null);

                Toolbar toolbar3 = viewCuadro2.findViewById(R.id.toolbar3);
                toolbar3.setTitle("Ficha Electronica");
                toolbar3.setNavigationIcon(R.drawable.ic_cerrar);

                FileInputStream fileInputStream = new FileInputStream(pdfFinal);

                PDFView pdfView = viewCuadro2.findViewById(R.id.pdfVisor);


                builder2.setView(viewCuadro2);
                final AlertDialog dialog2 = builder2.create();
                dialog2.setCancelable(false);

                pdfView.fromStream(fileInputStream)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .enableAntialiasing(true)
                        .load();

                toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });

                dialog2.show();

                dialog2.getWindow().getAttributes().windowAnimations = R.style.AppTheme_Slide;*/

                //FragmentManager fragmentManager = getSupportFragmentManager();
                //FullScreenDialog newFragment = new FullScreenDialog();

                /*FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment, "FullScreenFragment")
                        .commit();*/

                /*LectorPDF lectorPDF = new LectorPDF(pdfFinal);
                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setCustomAnimations(R.anim.slide_up,R.anim.slide_down);
                transaction.add(R.id.contenedor, lectorPDF);
                transaction.commit();*/

                LectorPDF lectorPDF = new LectorPDF(pdfFinal);
                lectorPDF.display(getFragmentManager());
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