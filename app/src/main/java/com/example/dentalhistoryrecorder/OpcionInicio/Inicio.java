package com.example.dentalhistoryrecorder.OpcionInicio;


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
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.MainActivity;
import com.example.dentalhistoryrecorder.Principal;
import com.example.dentalhistoryrecorder.R;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Inicio extends Fragment {
    private Toolbar toolbar;
    private TextView usuario, fichasEspeciales, fichasNormales, totalPacientes, contadorFE, contadorFN, contadorPac;
    private TextView correo, citasPendientes, cerrarSesion;
    private SharedPreferences preferencias;
    private RequestQueue requestQueue;
    private ImageView fotoPerfil;
    private boolean conFoto;

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

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    int mes = c.get(Calendar.MONTH) + 1;
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    private String fechaCita;

    //Editar Perfil
    private ImageView imagenPerfilAux;


    public Inicio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        requestQueue = Volley.newRequestQueue(getContext());
        preferencias = getActivity().getSharedPreferences("Consultar", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logotool);

        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                /*Alerter.create(getActivity())
                        .setTitle(preferencias.getString("idUsuario", ""))
                        .setText(preferencias.getString("correo", ""))
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();*/

                final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.progressDialog);
                progressDialog.setMessage("Cerrando Sesion");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        getActivity().finish();
                    }
                }, 500);


                SharedPreferences.Editor editor = preferencias.edit();
                editor.clear();
                editor.commit();

            }
        });

        toolbar.inflateMenu(R.menu.opciones_tool_inicio);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_editar:
                        /*SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                        Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle(preferencias2.getString("idUsuario",""))
                                .setText(preferencias2.getString("correo",""))
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face3)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();*/
                        EditarPerfil();
                        return true;

                    case R.id.action_configurar:
                        Configuracion configuracion = new Configuracion();
                        configuracion.display(getFragmentManager());
                        return true;

                    default:
                        return false;
                }
            }
        });


        String mesFormateado = (mes < 10) ? "0" + mes : String.valueOf(mes);

        fechaCita = dia +"/" + mesFormateado + "/" + anio;

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

        fotoPerfil = view.findViewById(R.id.imagenPerfil);

        usuario = view.findViewById(R.id.usuarioPerfil);
        usuario.setTypeface(face2);

        fichasEspeciales = view.findViewById(R.id.fichasEspeciales);
        fichasEspeciales.setTypeface(face2);

        contadorFE = view.findViewById(R.id.contadorFE);
        contadorFE.setTypeface(face2);

        fichasNormales = view.findViewById(R.id.fichasNormales);
        fichasNormales.setTypeface(face2);

        contadorFN = view.findViewById(R.id.contadorFN);
        contadorFN.setTypeface(face2);

        totalPacientes = view.findViewById(R.id.totalPacientes);
        totalPacientes.setTypeface(face2);

        contadorPac = view.findViewById(R.id.contadorPac);
        contadorPac.setTypeface(face2);

        correo = view.findViewById(R.id.correo);
        correo.setTypeface(face2);

        citasPendientes = view.findViewById(R.id.citasPendientes);
        citasPendientes.setTypeface(face2);

        cerrarSesion = view.findViewById(R.id.cerrar);
        cerrarSesion.setTypeface(face2);

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                Alerter.create(getActivity())
                        .setTitle(preferencias.getString("idUsuario", ""))
                        .setText(preferencias.getString("correo", ""))
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();


                SharedPreferences.Editor editor = preferencias.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                getActivity().finish();*/
                ObtenerTiempo();
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            consultarPerfil("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=1");
            obtenerCitas("https://diegosistemas.xyz/DHR/Citas/consultarCita.php?estado=1");
        }
        else{
            Alerter.create(getActivity())
                    .setTitle("Error")
                    .setText("Fallo en Conexion a Internet")
                    .setIcon(R.drawable.logonuevo)
                    .setTextTypeface(face2)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.AzulOscuro)
                    .show();
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        return view;
    }

    //Consultar Historial Odontodologico - Tratamiento
    public void consultarPerfil(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            correo.setText(jsonArray.getJSONObject(i).getString("correo"));
                            usuario.setText(jsonArray.getJSONObject(i).getString("clinica"));
                            contadorPac.setText(jsonArray.getJSONObject(i).getString("Pacientes"));
                            contadorFN.setText(jsonArray.getJSONObject(i).getString("Fichas"));
                            contadorFE.setText(jsonArray.getJSONObject(i).getString("FichasE"));
                            String codigo_foto = jsonArray.getJSONObject(i).getString("logo");
                            Toast.makeText(getContext(),codigo_foto,Toast.LENGTH_LONG).show();
                            if (!codigo_foto.isEmpty()) {
                                //byte[] b = Base64.decode(codigo_foto, Base64.DEFAULT);
                                //Bitmap imagen_codificada = BitmapFactory.decodeByteArray(b, 0, b.length);
                                //fotoPerfil.setImageBitmap(imagen_codificada);
                                conFoto = true;

                                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {
                                    ObtenerFoto(codigo_foto);
                                }
                                else{
                                    final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                                    Alerter.create(getActivity())
                                            .setTitle("Error")
                                            .setText("Fallo en Conexion a Internet")
                                            .setIcon(R.drawable.logonuevo)
                                            .setTextTypeface(face3)
                                            .enableSwipeToDismiss()
                                            .setBackgroundColorRes(R.color.AzulOscuro)
                                            .show();
                                }
                            }
                            else {
                                conFoto = false;
                                fotoPerfil.setImageResource(R.drawable.error);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.i("MyActivity", "" + e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MyActivity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("id", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void EditarPerfil() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_editarperfil, null);
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        TextView tituloPerfil = viewCuadro.findViewById(R.id.tituloDialogoPerfil);
        tituloPerfil.setTypeface(face2);

        final CheckBox checkFoto = viewCuadro.findViewById(R.id.checkFoto);
        checkFoto.setTypeface(face2);

        final CheckBox checkNombre = viewCuadro.findViewById(R.id.checkNombre);
        checkNombre.setTypeface(face2);

        final EditText nombrePerfilAux = viewCuadro.findViewById(R.id.nombrePerfilAux);
        nombrePerfilAux.setTypeface(face2);

        imagenPerfilAux = viewCuadro.findViewById(R.id.imagenPerfilAux);

        final Button galeria = viewCuadro.findViewById(R.id.botonGaleria);
        galeria.setTypeface(face2);
        galeria.setClickable(false);

        final Button camara = viewCuadro.findViewById(R.id.botonCamara);
        camara.setTypeface(face2);
        camara.setClickable(false);

        Button botonAceptar = viewCuadro.findViewById(R.id.botonAceptar);
        botonAceptar.setTypeface(face2);

        Button botonCancelar = viewCuadro.findViewById(R.id.botonCancelar);
        botonCancelar.setTypeface(face2);

        builder.setCancelable(false);
        builder.setView(viewCuadro);
        final AlertDialog dialog = builder.create();

        checkFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFoto.isChecked()) {
                    galeria.setClickable(true);
                    camara.setClickable(true);
                } else {
                    galeria.setClickable(false);
                    camara.setClickable(false);
                }
            }
        });

        checkNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNombre.isChecked()) {
                    nombrePerfilAux.setEnabled(true);
                } else {
                    nombrePerfilAux.setEnabled(false);
                }
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
            }
        });

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkearPermiso()) {
                    abrirCamara();
                } else {
                    solicitarPermiso();
                }
            }
        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFoto.isChecked() && !checkNombre.isChecked()) {
                    if (bitmap != null) {
                        Bitmap bitmap_aux = bitmap;
                        ByteArrayOutputStream salida = new ByteArrayOutputStream();
                        bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
                        byte[] b = salida.toByteArray();
                        String codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);


                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            agregarFoto1("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=5", codigoFoto);
                            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                            progressDialog.setMessage("Cargando...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        else{
                            final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo en Conexion a Internet")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face3)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }

                        /*if (conFoto == true){
                            agregarFoto("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=2", codigoFoto);
                            Toast.makeText(getActivity(),"Entre1",Toast.LENGTH_LONG).show();

                        }
                        else{
                            agregarFoto1("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=5", codigoFoto);
                            Toast.makeText(getActivity(),"Entre2",Toast.LENGTH_LONG).show();
                        }*/

                    } else {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                    }
                }

                if (!checkFoto.isChecked() && checkNombre.isChecked()) {
                    //Toast.makeText(getActivity(), "Nombre", Toast.LENGTH_LONG).show();
                    if (!nombrePerfilAux.getText().toString().isEmpty()) {

                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            agregarNombre("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=3", nombrePerfilAux.getText().toString());

                            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                            progressDialog.setMessage("Cargando...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        else{
                            final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo en Conexion a Internet")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face3)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }

                    }
                }

                if (checkFoto.isChecked() && checkNombre.isChecked()) {
                    //Toast.makeText(getActivity(), "Ambos", Toast.LENGTH_LONG).show();

                    if (bitmap != null && !nombrePerfilAux.getText().toString().isEmpty()) {
                        Bitmap bitmap_aux = bitmap;
                        ByteArrayOutputStream salida = new ByteArrayOutputStream();
                        bitmap_aux.compress(Bitmap.CompressFormat.PNG, 100, salida);
                        byte[] b = salida.toByteArray();
                        String codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);

                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            agregarPerfil1("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=6", codigoFoto, nombrePerfilAux.getText().toString());
                            Toast.makeText(getActivity(),"Foto2",Toast.LENGTH_LONG).show();

                        /*if (conFoto == true){
                            agregarPerfil("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=4", codigoFoto, nombrePerfilAux.getText().toString());
                            Toast.makeText(getActivity(),"Foto1",Toast.LENGTH_LONG).show();
                        }
                        else{
                            agregarPerfil1("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=6", codigoFoto, nombrePerfilAux.getText().toString());
                            Toast.makeText(getActivity(),"Foto2",Toast.LENGTH_LONG).show();
                        }*/


                            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                            progressDialog.setMessage("Cargando...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                }
                            }, 1000);
                        }
                        else{
                            final Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo en Conexion a Internet")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face3)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    }
                }

                if (!checkFoto.isChecked() && !checkNombre.isChecked()) {
                    Toast.makeText(getActivity(), "Error No ha seleccionado una opcion", Toast.LENGTH_LONG).show();
                }
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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

                        if (widht > 125 || height > 125) {
                            float escalaAncho = ((float) 125) / widht;
                            float escalaAlto = ((float) 125) / height;
                            Matrix matrix = new Matrix();
                            matrix.postScale(escalaAncho, escalaAlto);
                            image = Bitmap.createBitmap(image, 0, 0, widht, height, matrix, false);
                        }

                        bitmap = image;
                        imagenPerfilAux.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //fotoPerfilA.setImageResource(R.drawable.error);
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

                if (widht > 125 || height > 125) {
                    float escalaAncho = ((float) 125) / widht;
                    float escalaAlto = ((float) 125) / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale(escalaAncho, escalaAlto);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, widht, height, matrix, true);
                }

                imagenPerfilAux.setImageBitmap(bitmap);
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
    public void agregarFoto(String URL, final String fotoo) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                consultarPerfil("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MyActivity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("foto", fotoo);
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void agregarFoto1(String URL, final String fotoo) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                consultarPerfil("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MyActivity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("foto", fotoo);
                Long consecutivo = System.currentTimeMillis() / 1000;
                parametros.put("nom", "DHR_" + consecutivo.toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void agregarNombre(String URL, final String nombre) {
        final String[] id = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                consultarPerfil("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MyActivity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", nombre);
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void agregarPerfil(String URL, final String foto, final String nombre) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                consultarPerfil("https://diegosistemas.xyz/DHR/Perfil/perfil.php?estado=1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MyActivity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", nombre);
                parametros.put("foto", foto);
                Long consecutivo = System.currentTimeMillis() / 1000;
                parametros.put("nom", "DHR_" + consecutivo.toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    //Insertar Ficha y Obtener ID Ficha ------------------------------------------------------------
    public void agregarPerfil1(String URL, final String foto, final String nombre) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                consultarPerfil("https://diegosistemas.xyz/DHR/Inicio/perfil.php?estado=1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MyActivity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", nombre);
                parametros.put("foto", foto);
                Long consecutivo = System.currentTimeMillis() / 1000;
                parametros.put("nom", "DHR_" + consecutivo.toString());
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void obtenerCitas(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.parseInt(response) > 0) {
                    if (Integer.parseInt(response) < 2){
                        citasPendientes.setText( "Tiene " + response + " Cita Pendiete" );
                    }
                    else {
                        citasPendientes.setText( "Tiene " + response + " Citas Pendietes" );
                    }
                } else {
                    citasPendientes.setText("No Hay Citas Hoy");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("My Activity", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("fecha", fechaCita);
                SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                parametros.put("user", preferencias2.getString("idUsuario", "1"));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void ObtenerFoto(String ruta){
        String URL = "https://www.diegosistemas.xyz/DHR/Perfil/" + ruta ;

        ImageRequest imageRequest = new ImageRequest(URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if (response != null){
                            fotoPerfil.setImageBitmap(response);
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


    public void ObtenerTiempo() {
        //TextView countDown;
        //CountDownTimer countDownTimer;
        Date eventDate, presentDate;
        Calendar calendar, calendar1;
        //long initialTime;

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 0);

        eventDate = calendar.getTime();
        presentDate = new Date();

        long diff = eventDate.getTime() - presentDate.getTime();

        long seconds = (diff / 1000) % 60;
        long minutes = (diff / (1000 * 60)) % 60;
        long hours = (diff / (1000 * 60 * 60)) % 24;
        //long days = (diff / (1000 * 60 * 60 * 24)) % 365;
        Toast.makeText(getContext(), "Horas: " + hours + " Minutos: " + minutes + " Segundos: " + seconds, Toast.LENGTH_LONG).show();

    }
}
