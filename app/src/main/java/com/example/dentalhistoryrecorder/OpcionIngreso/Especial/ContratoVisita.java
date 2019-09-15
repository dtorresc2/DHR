package com.example.dentalhistoryrecorder.OpcionIngreso.Especial;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.Pizarron.Lienzo;
import com.example.dentalhistoryrecorder.R;
import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;


public class ContratoVisita extends Fragment {
    Toolbar toolbar;
    TextView titulo, parrafo1, parrafo2, parrafo3, parrafo4, parrafo5, parrafo6, parrafo7;
    FloatingActionButton siguiente;
    ImageView firma;
    private static final int CODIGO_SOLICITUD_PERMISO = 123;
    Lienzo lienzo;
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/"; //Directorio Principal
    private static final String CARPETA_IMAGEN = "DHR_Firmas"; //Carpeta donde se guardan las fotos en la galeria
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN; //Ruta del directorio
    RequestQueue requestQueue;
    private static final String TAG = "MyActivity";

    public ContratoVisita() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contrato_visita, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Contrato de Compromiso");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Agregar agregar = new Agregar();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, agregar);
                transaction.commit();
            }
        });

        titulo = view.findViewById(R.id.tituloContrato);
        titulo.setTypeface(face);

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

        siguiente = view.findViewById(R.id.siguiente);

        firma = view.findViewById(R.id.firma);
        firma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Hola",Toast.LENGTH_LONG).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_firma, null);
                ImageButton limpiar = viewCuadro.findViewById(R.id.limpiar);
                ImageButton guardar = viewCuadro.findViewById(R.id.guardar);
                TextView titulo = viewCuadro.findViewById(R.id.TituloFirmaDialogo);
                titulo.setTypeface(face2);
                Button cancelar = viewCuadro.findViewById(R.id.cancelar);
                cancelar.setTypeface(face2);
                final Button aceptar = viewCuadro.findViewById(R.id.aceptar);
                aceptar.setTypeface(face2);
                lienzo = viewCuadro.findViewById(R.id.lienzo);

                builder.setView(viewCuadro);
                final AlertDialog dialog = builder.create();
                dialog.setCancelable(false);

                limpiar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lienzo.limpiarCanvas();
                    }
                });

                guardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkearPermiso()) {
                            //Nuestra app tiene permiso
                            guardarFirma();
                        } else {
                            //Nuestra app no tiene permiso, entonces debo solicitar el mismo
                            solicitarPermiso();
                        }
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            lienzo.setDrawingCacheEnabled(true);
                            Bitmap bitmap = Bitmap.createBitmap(lienzo.getDrawingCache());
                            firma.setImageBitmap(bitmap);
                            //firma.setBackgroundColor(getResources().getColor(R.color.Blanco));
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialog.show();
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


                lienzo.setDrawingCacheEnabled(true);
                Bitmap bitmap_aux = Bitmap.createBitmap(lienzo.getDrawingCache());
                ByteArrayOutputStream salida = new ByteArrayOutputStream();
                bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
                byte[] b = salida.toByteArray();

                String codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);

                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    agregarFirma("https://diegosistemas.xyz/DHR/Especial/ingresoE.php?estado=5", codigoFoto);
                    IngEvaluacion ingEvaluacion = new IngEvaluacion();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, ingEvaluacion);
                    transaction.commit();

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
        });

        return view;
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
            guardarFirma();

        } else {
            //Se debe alertar al usuario que los permisos no han sido concedidos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "Los permisos de almacenamiento externo fueron denegados", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void guardarFirma() {
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();

        if (isCreada == false) {
            isCreada = miFile.mkdirs();
        }

        if (isCreada == true){
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = "DHR_Firma_" + consecutivo.toString() + ".png";
            String path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre; //Ruta de Almacenamiento
            File fileImage = new File(path);

            try {
                lienzo.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(lienzo.getDrawingCache());
                FileOutputStream ostream = new FileOutputStream(fileImage);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //Insertar Datos Personales y Obtener ID Paciente ----------------------------------------------
    public void agregarFirma(String URL, final String foto) {
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
                parametros.put("firma", foto);
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}
