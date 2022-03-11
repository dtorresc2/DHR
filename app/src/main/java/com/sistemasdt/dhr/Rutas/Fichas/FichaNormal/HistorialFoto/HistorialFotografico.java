package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialFoto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.sistemasdt.dhr.Componentes.Dialogos.Bitacora.FuncionesBitacora;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.GuardadorFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdonDos;

import com.sistemasdt.dhr.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha.Pagos;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistorialFotografico extends Fragment {
    private Bitmap bitmap;

    private Toolbar toolbar;

    private FloatingActionButton camara, fototeca, agregador;
    private FloatingActionsMenu menuOpciones;
    private ArrayList<ItemFoto> lista_fotos = new ArrayList<>();
    private ArrayList<ItemFoto> lista_eliminada = new ArrayList<>();
    private int mOpcion = 0;

    private RecyclerView rv;
    private FotoAdapter fotoAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    ActivityResultLauncher<Intent> lanzadorCamara;
    ActivityResultLauncher<String> lanzadorPermisos;
    ActivityResultLauncher<Intent> lanzadorGaleria;

    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;

    public HistorialFotografico() {
        MODO_EDICION = false;

        // INICIALIZADOR DE ACTIVIDAD PARA PERMISOS
        lanzadorPermisos = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        abrirCamara();
                    }
                });

        // INICIALIZADOR DE ACTIVIDAD PARA CAMARA
        lanzadorCamara = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        bitmap = (Bitmap) bundle.get("data");

                        // Almacenar en el Telefono las Imagenes
                        try {
//                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_PICTURES), "DHR");
//                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                            File archivoImagen = new File(mediaStorageDir.getPath() + File.separator +
//                                    "IMG_" + timeStamp + ".jpg");

                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            File archivoImagen = new File(getContext().getCacheDir(),
                                    "IMG_" + timeStamp + ".jpg");

                            //Convert bitmap to byte array
                            Bitmap bitmapAUX = bitmap;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmapAUX.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            archivoImagen.createNewFile();

                            FileOutputStream fos = new FileOutputStream(archivoImagen);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        rv.setLayoutManager(staggeredGridLayoutManager);
                        lista_fotos.add(new ItemFoto(bitmap, "", false));
                        fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                        rv.setAdapter(fotoAdapter);
                        fotoAdapter.setOnItemClickListener(position -> {
                            if (position > 0) {
                                menuOpciones.setVisibility(View.GONE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                            } else {
                                menuOpciones.setVisibility(View.VISIBLE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                            }
                        });
                    }
                });

        // INICIALIZADOR DE ACTIVIDAD PARA GALERIA
//        https://stackoverflow.com/questions/67156608/how-get-image-from-gallery-in-fragmentjetpack-navigation-component
//        https://www.youtube.com/watch?v=qO3FFuBrT2E&ab_channel=CodingDemos
//        https://stackoverflow.com/questions/64431993/how-to-get-specific-number-of-images-with-activity-results-api
        lanzadorGaleria = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    if (result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        Bitmap bitmapAux = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(selectedImageUri));
//                        bitmap = testImagen(bitmapAux, selectedImageUri, getContext());
                        lista_fotos.add(new ItemFoto(bitmapAux, "", false));

                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        rv.setLayoutManager(staggeredGridLayoutManager);
                        fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                        rv.setAdapter(fotoAdapter);
                        fotoAdapter.setOnItemClickListener(position -> {
                            if (position > 0) {
                                menuOpciones.setVisibility(View.GONE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                            } else {
                                menuOpciones.setVisibility(View.VISIBLE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                            }
                        });
                    }
                } catch (Exception exception) {
                    Log.d("TAG", "" + exception.getLocalizedMessage());
                }
            }
        });
    }

    public void activarModoEdicion(int id) {
        MODO_EDICION = true;
        ID_FICHA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historialfoto, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Fotografico");

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!MODO_EDICION) {
                Pagos pagos = new Pagos();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, pagos);
                transaction.commit();
            } else {
                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                transaction.replace(R.id.contenedor, menuFichaNormal);
                transaction.commit();
            }
        });

        toolbar.inflateMenu(R.menu.opciones_toolbar_fotos);
        toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);

        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_eliminar:
                    for (ItemFoto aux : lista_fotos) {
                        if (aux.isSelected()) {
                            lista_eliminada.add(aux);
                        }
                    }

                    // Eliminar lista
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                    builder.setIcon(R.drawable.logonuevo);
                    builder.setTitle("Historial Fotografico");
                    builder.setMessage("¿Desea eliminar los elementos seleccionados?");
                    builder.setPositiveButton("ACEPTAR", (dialog, id) -> {
                        for (ItemFoto aux : lista_eliminada) {
                            lista_fotos.remove(aux);
                        }

                        lista_eliminada.clear();

                        // Actualizar recyleview
                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        rv.setLayoutManager(staggeredGridLayoutManager);
                        fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                        rv.setAdapter(fotoAdapter);
                        fotoAdapter.setOnItemClickListener(position -> {
                            if (position > 0) {
                                menuOpciones.setVisibility(View.GONE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                            } else {
                                menuOpciones.setVisibility(View.VISIBLE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                            }
                        });

                        // Reestablecer menu
                        menuOpciones.setVisibility(View.VISIBLE);
                        toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                    });

                    builder.setNegativeButton("CANCELAR", (dialog, id) -> {
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;

                default:
                    return false;
            }
        });

        menuOpciones = view.findViewById(R.id.menuDP);
        camara = view.findViewById(R.id.tomar_hf);
        fototeca = view.findViewById(R.id.seleccionar_hf);
        agregador = view.findViewById(R.id.registrar_hf);

        rv = view.findViewById(R.id.rv);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Abir camara
        camara.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                lanzadorPermisos.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            menuOpciones.collapse();
        });

        //Abrir galeria
        fototeca.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            lanzadorGaleria.launch(intent);
            menuOpciones.collapse();
        });

        agregador.setOnClickListener(v -> {
            if (lista_fotos.size() > 0) {
                if (!MODO_EDICION) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("HFOTO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Set<String> set = new HashSet<>();
                    String codigoFoto = "";

                    for (int i = 0; i < lista_fotos.size(); i++) {
                        Bitmap bitmap_aux = lista_fotos.get(i).getFoto();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap_aux.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] b = byteArrayOutputStream.toByteArray();
                        codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);

                        String cadena = codigoFoto;
                        set.add(cadena);
                    }

                    editor.putStringSet("listaFotos", set);
                    editor.apply();

                    final SharedPreferences preferenciasFicha2 = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor2 = preferenciasFicha2.edit();
                    escritor2.putString("NO_FOTOS", String.valueOf(lista_fotos.size()));
                    escritor2.commit();

                    GuardadorFichaNormal guardadorFichaNormal = new GuardadorFichaNormal();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, guardadorFichaNormal);
                    transaction.commit();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    JSONArray jsonArrayFotos = new JSONArray();
                    JSONObject jsonObject = new JSONObject();

                    try {
                        for (int i = 0; i < lista_fotos.size(); i++) {
                            Bitmap bitmap_aux = lista_fotos.get(i).getFoto();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap_aux.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] b = byteArrayOutputStream.toByteArray();
                            String codigoFoto = Base64.encodeToString(b, Base64.DEFAULT);

                            JSONObject rowJSON = new JSONObject();
                            rowJSON.put("FOTO", codigoFoto);
                            rowJSON.put("URL", " ");
                            rowJSON.put("DESCRIPCION", " ");
                            rowJSON.put("NOMBRE", " ");
                            rowJSON.put("ID_FICHA", ID_FICHA);

                            jsonArrayFotos.put(rowJSON);
                        }

                        jsonObject.put("HISTORIAL_FOTOS", jsonArrayFotos);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);

                        e.printStackTrace();
                    }

                    QuerysFichas querysFichas = new QuerysFichas(getContext());
                    querysFichas.actualizarHistorialFotografico(ID_FICHA, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            progressDialog.dismiss();

                            Alerter.create(getActivity())
                                    .setTitle("Historial Fotografico")
                                    .setText("Actualizado correctamente")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.FondoSecundario)
                                    .show();

                            FuncionesBitacora funcionesBitacora = new FuncionesBitacora(getContext());
                            funcionesBitacora.registrarBitacora("ACTUALIZACION", "FOTOS - FICHA NORMAL", "Se actualizo la ficha #" + ID_FICHA);

                            MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                            transaction.replace(R.id.contenedor, menuFichaNormal);
                            transaction.commit();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressDialog.dismiss();

                            Alerter.create(getActivity())
                                    .setTitle("Error")
                                    .setText("Fallo al actualizar el Historial Fotografico")
                                    .setIcon(R.drawable.logonuevo)
                                    .setTextTypeface(face)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.AzulOscuro)
                                    .show();
                        }
                    });
                }
            } else {
                Alerter.create(getActivity())
                        .setTitle("Error")
                        .setText("No ha agregado fotos")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(face)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.AzulOscuro)
                        .show();
            }
        });

        obtenerFotos();

        return view;
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            lanzadorCamara.launch(intent);
        }
    }

    public void ObtenerOpcion(int opcion) {
        mOpcion = opcion;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null
                , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
//        https://gist.github.com/akexorcist/4a01eec6e5210a779ec2
        return path;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Bitmap testImagen(Bitmap mBitmap, Uri uri, Context context) {
        Bitmap bitmapAux = null;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(getRealPathFromURI(context, uri));
            exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmapAux = rotateImage(mBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmapAux = rotateImage(mBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:

                case ExifInterface.ORIENTATION_UNDEFINED:
                    bitmapAux = rotateImage(mBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    bitmapAux = mBitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmapAux;
    }

    public void obtenerFotos() {
        lista_fotos.clear();

        if (!MODO_EDICION) {
            SharedPreferences preferences = getActivity().getSharedPreferences("HFOTO", Context.MODE_PRIVATE);
            Set<String> set = preferences.getStringSet("listaFotos", null);

            if (set != null) {
                ArrayList<String> listaAuxiliar = new ArrayList<>(set);

                for (int i = 0; i < listaAuxiliar.size(); i++) {
                    byte[] decodedString = Base64.decode(listaAuxiliar.get(i), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    lista_fotos.add(new ItemFoto(decodedByte, "", false));
                }

                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                rv.setLayoutManager(staggeredGridLayoutManager);
                fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                rv.setAdapter(fotoAdapter);
                fotoAdapter.setOnItemClickListener(position -> {
                    if (position > 0) {
                        menuOpciones.setVisibility(View.GONE);
                        toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                    } else {
                        menuOpciones.setVisibility(View.VISIBLE);
                        toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                    }
                });
            }
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.obtenerHistorialFotografico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONArray jsonArray = new JSONArray(object.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String URL_FOTO = getContext().getResources().getString(R.string.S3) +
                                    jsonArray.getJSONObject(i).getString("URL") + "/" +
                                    jsonArray.getJSONObject(i).getString("NOMBRE");

                            lista_fotos.add(new ItemFoto(null, URL_FOTO, false));
                        }

                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        rv.setLayoutManager(staggeredGridLayoutManager);
                        fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                        rv.setAdapter(fotoAdapter);
                        fotoAdapter.setOnItemClickListener(position -> {
                            if (position > 0) {
                                menuOpciones.setVisibility(View.GONE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                            } else {
                                menuOpciones.setVisibility(View.VISIBLE);
                                toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                            }
                        });

                        for (int i = 0; i < jsonArray.length(); i++) {
                            int index = i;
                            String URL_FOTO = getContext().getResources().getString(R.string.S3) +
                                    jsonArray.getJSONObject(i).getString("URL") + "/" +
                                    jsonArray.getJSONObject(i).getString("NOMBRE");

                            Picasso.with(getContext()).load(URL_FOTO).resize(250, 250).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    lista_fotos.set(index, new ItemFoto(bitmap, URL_FOTO, false));
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    obtenerFotos();
                }
            });
        }
    }
}