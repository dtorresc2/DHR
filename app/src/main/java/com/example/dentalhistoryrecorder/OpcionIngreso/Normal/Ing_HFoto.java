package com.example.dentalhistoryrecorder.OpcionIngreso.Normal;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.OpcionIngreso.Agregar;
import com.example.dentalhistoryrecorder.OpcionSeguimiento.Seguimiento;
import com.example.dentalhistoryrecorder.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.tapadoo.alerter.Alerter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ing_HFoto extends Fragment {
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int CODIGO_SOLICITUD_PERMISO = 123;
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/"; //Directorio Principal
    private static final String CARPETA_IMAGEN = "DHR"; //Carpeta donde se guardan las fotos en la galeria
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN; //Ruta del directorio
    private String path;
    File fileImagen;
    Bitmap bitmap;
    private int widht;
    private int height;
    private int seleccionado;
    private int fotos_guardadas;
    private Toolbar toolbar;
    private ImageView galeria;
    private TextView tactual, tsep, ttotal;
    private FloatingActionButton camara, fototeca, eliminador, agregador;
    private ArrayList<Bitmap> lista_fotos = new ArrayList<Bitmap>();
    private ImageButton atras, adelante;
    private static final String TAG = "MyActivity";
    RequestQueue requestQueue;
    private int mOpcion = 0;
    private SharedPreferences preferencias;

    public Ing_HFoto() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ing__hfoto, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Fotografico");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        requestQueue = Volley.newRequestQueue(getContext());

        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);

        galeria = view.findViewById(R.id.visor);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMenu();
            }
        });

        tactual = view.findViewById(R.id.actual);
        tactual.setTypeface(face);
        ttotal = view.findViewById(R.id.total);
        ttotal.setTypeface(face);
        tsep = view.findViewById(R.id.separador);
        tsep.setTypeface(face);

        camara = view.findViewById(R.id.tomar_hf);
        fototeca = view.findViewById(R.id.seleccionar_hf);
        eliminador = view.findViewById(R.id.borrar_hf);
        agregador = view.findViewById(R.id.registrar_hf);

        atras = view.findViewById(R.id.atras);
        adelante = view.findViewById(R.id.adelante);
        seleccionado = 0;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "ATRAS", Toast.LENGTH_SHORT).show();

                switch (mOpcion){
                    case 1:
                        Agregar agregar = new Agregar();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction.replace(R.id.contenedor, agregar);
                        transaction.commit();
                        break;

                    case 2:
                        Seguimiento seguimiento = new Seguimiento();
                        FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction2.replace(R.id.contenedor, seguimiento);
                        transaction2.commit();
                        break;
                }
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Cargar Datos
        /*final SharedPreferences preferencias = getActivity().getSharedPreferences("datosfotos", Context.MODE_PRIVATE);

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
            galeria.setImageBitmap(lista_fotos.get(0));
            seleccionado = 0;
            tactual.setText(String.valueOf(seleccionado + 1));
            ttotal.setText(String.valueOf(lista_fotos.size()));
        }*/

        //Abir camara
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkearPermiso()) {
                    //Nuestra app tiene permiso
                    abrirCamara();
                } else {
                    //Nuestra app no tiene permiso, entonces debo solicitar el mismo
                    solicitarPermiso();
                }
            }
        });

        //Abrir galeria
        fototeca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
            }
        });

        //Galeria - Flecha izquierda
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
                tactual.setText(String.valueOf(seleccionado + 1));
            }
        });

        //Galeria - Flecha derecha
        adelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Entre " + lista_fotos.size() ,Toast.LENGTH_LONG);
                if (lista_fotos.size() > 0) {
                    if (seleccionado >= lista_fotos.size() - 1) {
                        seleccionado = 0;
                    } else {
                        seleccionado++;
                    }
                    galeria.setImageBitmap(lista_fotos.get(seleccionado));
                }
                tactual.setText(String.valueOf(seleccionado + 1));
            }
        });

        //Eliminar fotografia
        eliminador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lista_fotos.size() > 1) {
                    lista_fotos.remove(seleccionado);

                    if (seleccionado == 0) {
                        seleccionado = 1;
                        tactual.setText(String.valueOf(seleccionado));
                    } else {
                        seleccionado--;
                        tactual.setText(String.valueOf(seleccionado + 1));
                    }

                    if (lista_fotos.size() == 1) {
                        galeria.setImageBitmap(lista_fotos.get(0));
                    } else {
                        galeria.setImageBitmap(lista_fotos.get(seleccionado));
                    }

                    ttotal.setText(String.valueOf(lista_fotos.size()));
                } else if (lista_fotos.size() == 1) {
                    lista_fotos.remove(0);
                    galeria.setImageResource(R.drawable.error);
                    seleccionado--;
                    tactual.setText(String.valueOf(seleccionado));
                    ttotal.setText(String.valueOf(lista_fotos.size()));
                } else {
                    galeria.setImageResource(R.drawable.error);
                    Toast.makeText(getActivity(), "Galeria Vacia", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Agregar para insertar en BD
        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences.Editor escritor = preferencias.edit();
                //escritor.putInt("fotos", lista_fotos.size());

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

                if (lista_fotos.size() > 0) {
                    for (int i = 0; i < lista_fotos.size(); i++) {
                        Bitmap bitmap_aux = lista_fotos.get(i);
                        ByteArrayOutputStream salida = new ByteArrayOutputStream();
                        bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
                        byte[] b = salida.toByteArray();

                        String codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);

                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            switch (mOpcion) {
                                case 1:
                                    insertarHFoto("http://dhr.sistemasdt.xyz/Normal/ficha.php?estado=13", codigoFoto);
                                    break;

                                case 2:
                                    agregarFoto("http://dhr.sistemasdt.xyz/Normal/seguimiento.php?estado=4", codigoFoto);
                                    break;
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
                //escritor.commit();
                lista_fotos.clear();
                galeria.setImageResource(R.drawable.error);
                seleccionado = 0;
                tactual.setText("0");
                ttotal.setText("0");

                switch (mOpcion) {
                    case 1:
                        Agregar agregar = new Agregar();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_in, R.anim.left_out);
                        transaction.replace(R.id.contenedor, agregar);
                        transaction.commit();
                        break;

                    case 2:
                        Seguimiento seguimiento = new Seguimiento();
                        FragmentTransaction transaction2 = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction2.replace(R.id.contenedor, seguimiento);
                        transaction2.commit();
                        break;
                }
            }
        });

        return view;
    }

    public void mostrarMenu() {
        final CharSequence[] opciones = {"Tomar Fotografia", "Elegir de la Galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una Opcion");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("Tomar Fotografia")) {
                    //Metodo para tomar fotografia
                    if (checkearPermiso()) {
                        //Nuestra app tiene permiso
                        abrirCamara();
                    } else {
                        //Nuestra app no tiene permiso, entonces debo solicitar el mismo
                        solicitarPermiso();
                    }
                } else {
                    if (opciones[which].equals("Elegir de la Galeria")) {
                        //Metodo para tomar fotografia
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    ParcelFileDescriptor parcelFileDescriptor = null;
                    try {
                        //Codigo para convertir uri en bitmap
                        parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(miPath, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();

                        widht = image.getWidth();
                        height = image.getHeight();

                        if (widht > 600 || height > 800) {
                            float escalaAncho = ((float) 600) / widht;
                            float escalaAlto = ((float) 800) / height;
                            Matrix matrix = new Matrix();
                            matrix.postScale(escalaAncho, escalaAlto);
                            image = Bitmap.createBitmap(image, 0, 0, widht, height, matrix, false);
                        }

                        //matrix.postRotate(90);
                        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, widht, height, true);
                        //Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                        //galeria.setImageBitmap(rotatedBitmap);
                        //lista_fotos.add(rotatedBitmap);
                        lista_fotos.add(image);
                        galeria.setImageBitmap(lista_fotos.get(lista_fotos.size() - 1));
                        seleccionado = lista_fotos.size() - 1;
                        tactual.setText(String.valueOf(seleccionado + 1));
                        ttotal.setText(String.valueOf(lista_fotos.size()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    galeria.setImageResource(R.drawable.error);
                }
                break;

            case COD_FOTO:
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path", "" + path);
                            }
                        });

                bitmap = BitmapFactory.decodeFile(path);

                widht = bitmap.getWidth();
                height = bitmap.getHeight();

                if (widht > 600 || height > 800) {
                    float escalaAncho = ((float) 600) / widht;
                    float escalaAlto = ((float) 800) / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale(escalaAncho, escalaAlto);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, widht, height, matrix, true);
                }

                lista_fotos.add(bitmap);
                //galeria.setImageBitmap(bitmap);
                galeria.setImageBitmap(lista_fotos.get(lista_fotos.size() - 1));
                seleccionado = lista_fotos.size() - 1;
                tactual.setText(String.valueOf(seleccionado + 1));
                ttotal.setText(String.valueOf(lista_fotos.size()));
                break;
        }
    }

    private void abrirCamara() {
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();

        if (isCreada == false) {
            isCreada = miFile.mkdirs();
        }

        if (isCreada == true) {
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = "DHR_" + consecutivo.toString() + ".png";

            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre; //Ruta de Almacenamiento
            fileImagen = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            startActivityForResult(intent, COD_FOTO);
        } else {
            Toast.makeText(getContext(), "Fallo al crear carpeta", Toast.LENGTH_SHORT).show();
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
            abrirCamara();
        } else {
            //Se debe alertar al usuario que los permisos no han sido concedidos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "Los permisos de almacenamiento externo fueron denegados", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void insertarHFoto(String URL, final String fotoo) {
        final String[] id = new String[1];
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
                parametros.put("foto", fotoo);
                parametros.put("desc", "HistorialF");
                Long consecutivo = System.currentTimeMillis() / 1000;
                parametros.put("nom", "DHR_" + consecutivo.toString());
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void agregarFoto(String URL, final String fotoo) {
        final String[] id = new String[1];
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
                parametros.put("foto", fotoo);
                parametros.put("desc", "HistorialF");
                Long consecutivo = System.currentTimeMillis() / 1000;
                parametros.put("nom", "DHR_" + consecutivo.toString());
                parametros.put("id", preferencias.getString("idficha", ""));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

}

/*
    if (Intent.ACTION_SEND_MULTIPLE.equals(action)) && Intent.hasExtra(Intent.EXTRA_STREAM)) {
        ArrayList<Parcelable> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        for (Parcelable parcel : list) {
        Uri uri = (Uri) parcel;
        /// do things here. }
        }
*/