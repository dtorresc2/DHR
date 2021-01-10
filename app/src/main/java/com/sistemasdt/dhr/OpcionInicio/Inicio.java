package com.sistemasdt.dhr.OpcionInicio;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.DialogoBitacora;
import com.sistemasdt.dhr.MainActivity;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.ServiciosAPI.QuerysCuentas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;

public class Inicio extends Fragment {
    private TextView usuario, configuracion, editar_perfil, totalPacientes, contadorFE, contadorFN, contadorPac;
    private TextView correo, citasPendientes, cerrarSesion, empresa, editar_nombre, bitacora;
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
    private NetworkImageView networkImageView;

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
        preferencias = getActivity().getSharedPreferences("ListadoPacientes", Context.MODE_PRIVATE);

        String mesFormateado = (mes < 10) ? "0" + mes : String.valueOf(mes);

        fechaCita = dia + "/" + mesFormateado + "/" + anio;
        fotoPerfil = view.findViewById(R.id.imagenPerfil);
        requestQueue = Volley.newRequestQueue(getContext());

//        Picasso.with(getContext())
//                .load(getContext().getResources().getString(R.string.S3) + "not.jpg")
//                .into(fotoPerfil);


        usuario = view.findViewById(R.id.inicio_texto_usuario);
        usuario.setTypeface(face2);

        configuracion = view.findViewById(R.id.inicio_opc_config_texto);
        configuracion.setTypeface(face2);

        editar_perfil = view.findViewById(R.id.inicio_opc_imagen_texto);
        editar_perfil.setTypeface(face2);

        empresa = view.findViewById(R.id.usuarioPerfil);
        empresa.setTypeface(face2);

        bitacora = view.findViewById(R.id.inicio_opc_log_texto);
        bitacora.setTypeface(face2);

        bitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoBitacora dialogoBitacora = new DialogoBitacora();
                dialogoBitacora.display(getFragmentManager());
            }
        });

        cerrarSesion = view.findViewById(R.id.inicio_opc_salir_texto);
        cerrarSesion.setTypeface(face2);

        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configuracion configuracion = new Configuracion();
                configuracion.display(getFragmentManager());
            }
        });

        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditarPerfil();
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

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
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        getActivity().finish();
                    }
                }, 500);

                SharedPreferences.Editor editor = preferencias.edit();
                editor.clear();
                editor.commit();
            }
        });

        obtenerPerfi();

        return view;
    }

    public void obtenerPerfi() {
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

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PERFIL", Context.MODE_PRIVATE);

        QuerysCuentas querysCuentas = new QuerysCuentas(getContext());
        querysCuentas.obtenerCuenta(
                preferenciasUsuario.getInt("ID_CUENTA", 0),
                preferenciasUsuario.getInt("ID_USUARIO", 0),
                new QuerysCuentas.VolleyOnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        try {
                            JSONObject jsonObject = new JSONObject(object.toString());
                            usuario.setText(preferenciasUsuario.getString("codigo", "1") + " - " + jsonObject.getString("USUARIO"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });

        QuerysCuentas querysCuentas2 = new QuerysCuentas(getContext());
        querysCuentas2.serviciosHabilitados(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysCuentas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    empresa.setText(jsonObject.getString("NOMBRE"));

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("EMPRESA", empresa.getText().toString());
                    editor.putString("URL", getContext().getResources().getString(R.string.S3) + jsonObject.getString("URL") + ".jpg");
                    editor.commit();

                    ImageRequest imageRequest = new ImageRequest(getContext().getResources().getString(R.string.S3) + jsonObject.getString("URL") + ".jpg",
                            new BitmapListener(fotoPerfil), 0, 0, null, null,
                            new MyErrorListener(fotoPerfil));
                    requestQueue.add(imageRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void EditarPerfil() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View viewCuadro = getLayoutInflater().inflate(R.layout.dialogo_editarperfil, null);
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PERFIL", Context.MODE_PRIVATE);

        TextView tituloPerfil = viewCuadro.findViewById(R.id.tituloDialogoPerfil);
        tituloPerfil.setTypeface(face2);

        final EditText nombrePerfilAux = viewCuadro.findViewById(R.id.nombrePerfilAux);
        nombrePerfilAux.setTypeface(face2);
        nombrePerfilAux.setText(sharedPreferences.getString("EMPRESA", "-"));

        imagenPerfilAux = viewCuadro.findViewById(R.id.imagenPerfilAux);
        ImageRequest imageRequest = new ImageRequest(  sharedPreferences.getString("URL", getContext().getResources().getString(R.string.S3) + "not.jpg"),
                new BitmapListener(imagenPerfilAux), 0, 0, null, null,
                new MyErrorListener(imagenPerfilAux));
        requestQueue.add(imageRequest);

        final Button galeria = viewCuadro.findViewById(R.id.botonGaleria);
        galeria.setTypeface(face2);

        final Button camara = viewCuadro.findViewById(R.id.botonCamara);
        camara.setTypeface(face2);

        Button botonAceptar = viewCuadro.findViewById(R.id.botonAceptar);
        botonAceptar.setTypeface(face2);

        Button botonCancelar = viewCuadro.findViewById(R.id.botonCancelar);
        botonCancelar.setTypeface(face2);

        builder.setCancelable(false);
        builder.setView(viewCuadro);
        final AlertDialog dialog = builder.create();

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
            public void onClick(View view) {
                if (!nombrePerfilAux.getText().toString().isEmpty()) {
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
                            obtenerPerfi();
                        }
                    }, 1000);

                    final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                    String codigoFoto = null;

                    if (bitmap != null) {
                        Bitmap bitmap_aux = bitmap;
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap_aux.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] b = byteArrayOutputStream.toByteArray();
                        codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);
                    } else {
                        codigoFoto = "0";
                    }

                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("NOMBRE", nombrePerfilAux.getText().toString());
                        jsonBody.put("URL", "url");
                        jsonBody.put("buffer", codigoFoto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    QuerysCuentas querysCuentas = new QuerysCuentas(getContext());
                    querysCuentas.actualizarPerfil(sharedPreferences.getInt("ID_USUARIO", 0), jsonBody, new QuerysCuentas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Hay campos obligatorios", Toast.LENGTH_LONG).show();
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

    public void ObtenerFoto(String ruta) {
        String URL = "http://dhr.sistemasdt.xyz/Perfil/" + ruta;

        ImageRequest imageRequest = new ImageRequest(URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if (response != null) {
                            fotoPerfil.setImageBitmap(response);
                        }
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
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

class BitmapListener implements Response.Listener<Bitmap> {
    ImageView imageViewAux;

    public BitmapListener(ImageView imageView) {
        imageViewAux = imageView;
    }

    @Override
    public void onResponse(Bitmap response) {
        imageViewAux.setImageBitmap(response);

    }
}

class MyErrorListener implements Response.ErrorListener {
    ImageView imageViewAux;

    public MyErrorListener(ImageView imageView) {
        imageViewAux = imageView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        imageViewAux.setImageResource(R.drawable.logotool);
    }
}