package com.example.dentalhistoryrecorder.OpcionInicio;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.example.dentalhistoryrecorder.R;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.security.PublicKey;

public class Configuracion extends DialogFragment {
    private Toolbar toolbar;
    private TextView titulo1, titulo2, titulo3, subtitulo1, subtitulo2, subtitulo3, subtitulo4, subtitulo5;
    private TextView etiquetaEspacioFoto, etiquetaEspacioFichas, etiquetaEspacioFichasE, etiquetaVersion;
    private Switch recordarCuenta;
    private ImageButton limpiar1, limpiar2, limpiar3;
    private TextView compartir, abrirManual;

    //Ruta Carpeta de Fotos
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/"; //Directorio Principal
    private static final String CARPETA_IMAGEN = "DHR"; //Carpeta donde se guardan las fotos en la galeria
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN; //Ruta del directorio
    private static final int CODIGO_SOLICITUD_PERMISO = 123;

    public Configuracion() {

    }

    public static Configuracion display(FragmentManager fragmentManager) {
        Configuracion configuracion = new Configuracion();
        configuracion.show(fragmentManager, "My Activity");
        return configuracion;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_configuracion, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Configuracion");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));
        //toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/

        toolbar.inflateMenu(R.menu.opciones_toolbarcitas);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_aceptar:
                        dismiss();
                        return true;

                    default:
                        return false;
                }
            }
        });

        titulo1 = view.findViewById(R.id.tituloGeneral);
        titulo1.setTypeface(face);

        titulo2 = view.findViewById(R.id.tituloEspacio);
        titulo2.setTypeface(face);

        titulo3 = view.findViewById(R.id.tituloAyuda);
        titulo3.setTypeface(face);

        recordarCuenta = view.findViewById(R.id.recordarCuenta);
        recordarCuenta.setTypeface(face);

        subtitulo1 = view.findViewById(R.id.tituloAlmacen1);
        subtitulo1.setTypeface(face);

        subtitulo2 = view.findViewById(R.id.tituloAlmacen2);
        subtitulo2.setTypeface(face);

        subtitulo3 = view.findViewById(R.id.tituloAlmacen3);
        subtitulo3.setTypeface(face);

        subtitulo4 = view.findViewById(R.id.tituloAcerca1);
        subtitulo4.setTypeface(face);

        etiquetaEspacioFoto = view.findViewById(R.id.tituloEspacio1);
        etiquetaEspacioFoto.setTypeface(face);

        etiquetaEspacioFichas = view.findViewById(R.id.tituloEspacio2);
        etiquetaEspacioFichas.setTypeface(face);

        etiquetaEspacioFichasE = view.findViewById(R.id.tituloEspacio3);
        etiquetaEspacioFichasE.setTypeface(face);

        etiquetaVersion = view.findViewById(R.id.tituloAcerca2);
        etiquetaVersion.setTypeface(face);

        subtitulo5 = view.findViewById(R.id.tituloAcerca3);
        subtitulo5.setTypeface(face);

//        compartir = view.findViewById(R.id.compartirLink);
//        compartir.setTypeface(face);

        abrirManual = view.findViewById(R.id.abrirPDF);
        abrirManual.setTypeface(face);

        //Calculo de Espacio Consumido
        if (checkearPermiso()) {
            calculaEspacioFoto();
            calculaEspacioFichas();
            calculaEspacioFichasE();
        } else {
            solicitarPermiso();
        }

        limpiar1 = view.findViewById(R.id.limpiar1);
        limpiar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaciarFotos();
            }
        });

        limpiar2 = view.findViewById(R.id.limpiar2);
        limpiar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaciarFichas();
            }
        });

        limpiar3 = view.findViewById(R.id.limpiar3);
        limpiar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaciarFichasE();
            }
        });

        final SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        recordarCuenta.setChecked(preferencias.getBoolean("recordar", false));

        recordarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordarCuenta.isChecked()){
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("recordar", true);
                    editor.commit();
                }
                else {
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("recordar", false);
                    editor.commit();
                }
            }
        });

        return view;
    }

    public void calculaEspacioFoto() {
        File carpetaFotos = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        if (carpetaFotos.exists()) {
            File[] archivos = carpetaFotos.listFiles();
            if (archivos.length > 0) {
                double longitud = 0;
                double tamañoReal = 0;
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    longitud += file.length();
                }
                if (longitud > 1024000000) {
                    tamañoReal = longitud / 1024000000;
                    etiquetaEspacioFoto.setText(String.format("%.2f", tamañoReal) + " Gb");
                } else if (longitud > 1024000) {
                    tamañoReal = longitud / 1024000;
                    etiquetaEspacioFoto.setText(String.format("%.2f", tamañoReal) + " Mb");
                } else if (longitud > 1024) {
                    tamañoReal = longitud / 1024;
                    etiquetaEspacioFoto.setText(String.format("%.2f", tamañoReal) + " Kb");
                } else {
                    etiquetaEspacioFoto.setText(String.format("%.2f", longitud) + " bytes");
                }

            } else {
                etiquetaEspacioFoto.setText("0.00 Kb");
            }
        } else {
            etiquetaEspacioFoto.setText("No Disponible");
        }
    }

    public void calculaEspacioFichas() {
        File carpetaFichas = new File(Environment.getExternalStorageDirectory().toString(), "FichasGenerales");
        if (carpetaFichas.exists()) {
            File[] archivos = carpetaFichas.listFiles();
            if (archivos.length > 0) {
                double longitud = 0;
                double tamañoReal = 0;
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    longitud += file.length();

                }
                if (longitud > 1024000000) {
                    tamañoReal = longitud / 1024000000;
                    etiquetaEspacioFichas.setText(String.format("%.2f", tamañoReal) + " Gb");
                } else if (longitud > 1024000) {
                    tamañoReal = longitud / 1024000;
                    etiquetaEspacioFichas.setText(String.format("%.2f", tamañoReal) + " Mb");
                } else if (longitud > 1024) {
                    tamañoReal = longitud / 1024;
                    etiquetaEspacioFichas.setText(String.format("%.2f", tamañoReal) + " Kb");
                } else {
                    etiquetaEspacioFichas.setText(String.format("%.2f", longitud) + " bytes");
                }

            } else {
                etiquetaEspacioFichas.setText("0.00 Kb");
            }
        } else {
            etiquetaEspacioFichas.setText("No Disponible");
        }
    }

    public void calculaEspacioFichasE() {
        File carpetaFichas = new File(Environment.getExternalStorageDirectory().toString(), "FichasEspeciales");
        if (carpetaFichas.exists()) {
            File[] archivos = carpetaFichas.listFiles();
            if (archivos.length > 0) {
                double longitud = 0;
                double tamañoReal = 0;
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    longitud += file.length();

                }
                if (longitud > 1024000000) {
                    tamañoReal = longitud / 1024000000;
                    etiquetaEspacioFichasE.setText(String.format("%.2f", tamañoReal) + " Gb");
                } else if (longitud > 1024000) {
                    tamañoReal = longitud / 1024000;
                    etiquetaEspacioFichasE.setText(String.format("%.2f", tamañoReal) + " Mb");
                } else if (longitud > 1024) {
                    tamañoReal = longitud / 1024;
                    etiquetaEspacioFichasE.setText(String.format("%.2f", tamañoReal) + " Kb");
                } else {
                    etiquetaEspacioFichasE.setText(String.format("%.2f", longitud) + " bytes");
                }

            } else {
                etiquetaEspacioFichasE.setText("0.00 Kb");
            }
        } else {
            etiquetaEspacioFichasE.setText("No Disponible");
        }
    }

    public void vaciarFotos() {
        File carpetaFotos = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        if (carpetaFotos.exists()) {
            File[] archivos = carpetaFotos.listFiles();
            if (archivos.length > 0) {
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    file.delete();
                }

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

                etiquetaEspacioFoto.setText("0.00 Kb");
            } else {
                /*Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("La carpeta esta vacia")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face2)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();*/
            }
        }
    }

    public void vaciarFichas() {
        File carpetaFichas = new File(Environment.getExternalStorageDirectory().toString(), "FichasGenerales");
        final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        if (carpetaFichas.exists()) {
            File[] archivos = carpetaFichas.listFiles();
            if (archivos.length > 0) {
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    file.delete();
                }

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

                etiquetaEspacioFichas.setText("0.00 Kb");
            } else {
                /*Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("La carpeta esta vacia")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face2)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();*/
            }
        }
    }

    public void vaciarFichasE() {
        File carpetaFichas = new File(Environment.getExternalStorageDirectory().toString(), "FichasEspeciales");
        final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        if (carpetaFichas.exists()) {
            File[] archivos = carpetaFichas.listFiles();
            if (archivos.length > 0) {
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    file.delete();
                }

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

                etiquetaEspacioFichasE.setText("0.00 Kb");
            } else {
                /*Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("La carpeta esta vacia")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face2)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();*/
            }
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
            calculaEspacioFoto();
            calculaEspacioFichas();
            calculaEspacioFichasE();
        } else {
            //Se debe alertar al usuario que los permisos no han sido concedidos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "Los permisos de almacenamiento externo fueron denegados", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
