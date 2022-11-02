package com.sistemasdt.dhr.Rutas.Inicio;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.DialogoBitacora;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.MainActivity;

import com.sistemasdt.dhr.Componentes.Dialogos.Configuracion.DialogoConfiguracion;
import com.sistemasdt.dhr.R;

import com.sistemasdt.dhr.ServiciosAPI.QuerysCuentas;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class Inicio extends Fragment {
    private TextView usuario, configuracion, editar_perfil;
    private TextView cerrarSesion, empresa, bitacora;
    private RoundedImageView fotoPerfil;
    private Bitmap bitmap;

    private TextInputLayout layoutPerfil;
    private TextInputEditText nombrePerfilAux;

    //Editar Perfil
    private ImageView imagenPerfilAux;

    ActivityResultLauncher<Intent> lanzadorCamara;
    ActivityResultLauncher<Intent> lanzadorGaleria;

    private JobScheduler jobScheduler;
    private final int ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS = 335;
    private final int ID_SERVICIO_NOTIFICACIONES_CITAS_DIA = 336;

    public Inicio() {
        // INICIALIZADOR DE ACTIVIDAD PARA CAMARA
        lanzadorCamara = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        bitmap = (Bitmap) bundle.get("data");
                        imagenPerfilAux.setImageBitmap(bitmap);
                    }
                });

        lanzadorGaleria = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    if (result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(selectedImageUri));
                        imagenPerfilAux.setImageBitmap(bitmap);
                    }
                } catch (Exception exception) {
                    Log.d("TAG", "" + exception.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        fotoPerfil = view.findViewById(R.id.imagenPerfil);

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

        bitacora.setOnClickListener(v -> {
            DialogoBitacora dialogoBitacora = new DialogoBitacora();
            dialogoBitacora.display(getActivity().getSupportFragmentManager());
        });

        cerrarSesion = view.findViewById(R.id.inicio_opc_salir_texto);
        cerrarSesion.setTypeface(face2);

        configuracion.setOnClickListener(view1 -> {
            DialogoConfiguracion dialogoConfiguracion = new DialogoConfiguracion();
            dialogoConfiguracion.display(getActivity().getSupportFragmentManager());
        });

        editar_perfil.setOnClickListener(view12 -> EditarPerfil());

        //MANEJADOR DE SERVICIOS
        jobScheduler = (JobScheduler) getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);

        cerrarSesion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
            builder.setIcon(R.drawable.logonuevo);
            builder.setTitle("DHR");
            builder.setMessage("¿Desea cerrar la sesion?");
            builder.setPositiveButton("ACEPTAR", (dialog, id) -> {
                SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

                //FINALIZANDO SERVICIOS
                jobScheduler.cancel(ID_SERVICIO_NOTIFICACIONES_CITAS_DIA);
                jobScheduler.cancel(ID_SERVICIO_NOTIFICACIONES_CITAS_CERCANAS);

                final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.progressDialog);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    getActivity().finish();
                }, 500);

                SharedPreferences.Editor editor = preferencias.edit();
                editor.clear();
                editor.commit();
            });
            builder.setNegativeButton("CANCELAR", (dialog, id) -> {
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        obtenerPerfil();

        return view;
    }

    public void obtenerPerfil() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        obtenerPerfil();
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

                    Picasso.with(getContext())
                            .load(getContext().getResources().getString(R.string.S3) + jsonObject.getString("URL") + ".jpg")
                            .placeholder(R.drawable.logonuevo)
                            .resize(125, 125)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    fotoPerfil.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    fotoPerfil.setImageDrawable(placeHolderDrawable);
                                }
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                obtenerPerfil();
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

        layoutPerfil = viewCuadro.findViewById(R.id.layoutNombrePerfil);
        layoutPerfil.setTypeface(face2);

        nombrePerfilAux = viewCuadro.findViewById(R.id.nombrePerfilAux);
        nombrePerfilAux.setTypeface(face2);
        nombrePerfilAux.setText(sharedPreferences.getString("EMPRESA", "-"));
        nombrePerfilAux.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoRequerido();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imagenPerfilAux = viewCuadro.findViewById(R.id.imagenPerfilAux);

        Picasso.with(getContext())
                .load(sharedPreferences.getString("URL", getContext().getResources().getString(R.string.S3) + "not.jpg"))
                .placeholder(R.drawable.logonuevo)
                .resize(125, 125)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imagenPerfilAux.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imagenPerfilAux.setImageDrawable(placeHolderDrawable);
                    }
                });

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

        galeria.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            lanzadorGaleria.launch(intent);
        });

        camara.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                lanzadorCamara.launch(intent);
            }
        });

        botonAceptar.setOnClickListener(view -> {
            if (!textoRequerido())
                return;

            final SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
            String codigoFoto;

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
            querysCuentas.actualizarPerfil(sharedPreferences1.getInt("ID_USUARIO", 0), jsonBody, new QuerysCuentas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    dialog.dismiss();
                    obtenerPerfil();
                    FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                    funcionesBitacora.registrarBitacora("ACTUALIZACION", "PERFIL", "Se actualizo el perfil");
                }

                @Override
                public void onFailure(Exception e) {
                    Alerter.create(getActivity())
                            .setTitle("Error")
                            .setText("Fallo al actualizar el perfil")
                            .setIcon(R.drawable.logonuevo)
                            .setTextTypeface(face2)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.AzulOscuro)
                            .show();
                }
            });

        });

        botonCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // VALIDACIONES
    private boolean textoRequerido() {
        String texto = nombrePerfilAux.getText().toString().trim();
        if (texto.isEmpty()) {
            layoutPerfil.setError("Campo requerido");
            return false;
        } else {
            layoutPerfil.setError(null);
            return true;
        }
    }
}