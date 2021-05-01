package com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto;


import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;


import com.android.volley.RequestQueue;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialOdonto.HistorialOdonDos;

import com.sistemasdt.dhr.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class HistorialFotografico extends Fragment {
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int COD_SELECCIONA_MULTIPLE = 30;

    private static final int CODIGO_SOLICITUD_PERMISO = 123;
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/"; //Directorio Principal
    private static final String CARPETA_IMAGEN = "DHR"; //Carpeta donde se guardan las fotos en la galeria
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN; //Ruta del directorio
    private String path;
    File fileImagen;
    Bitmap bitmap;

    private Toolbar toolbar;

    private FloatingActionButton camara, fototeca, eliminador, agregador;
    private FloatingActionsMenu menuOpciones;
    private ArrayList<ItemFoto> lista_fotos = new ArrayList<ItemFoto>();
    private static final String TAG = "MyActivity";
    RequestQueue requestQueue;
    private int mOpcion = 0;
    private SharedPreferences preferencias;

    private RecyclerView rv;
    private FotoAdapter fotoAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    public HistorialFotografico() {
        // Required empty public constructor
    }

    //    https://dhr-sanjose.s3.amazonaws.com/imagen-1.jpg
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historialfoto, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Fotografico");
        toolbar.setNavigationIcon(R.drawable.ic_atras);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistorialOdonDos historialOdonDos = new HistorialOdonDos();
                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                transaction.replace(R.id.contenedor, historialOdonDos);
                transaction.commit();
            }
        });

        toolbar.inflateMenu(R.menu.opciones_toolbar_fotos);
        toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_eliminar:
                        return true;

                    default:
                        return false;
                }
            }
        });

        menuOpciones = view.findViewById(R.id.menuDP);
        camara = view.findViewById(R.id.tomar_hf);
        fototeca = view.findViewById(R.id.seleccionar_hf);
//        eliminador = view.findViewById(R.id.borrar_hf);
        agregador = view.findViewById(R.id.registrar_hf);

        rv = view.findViewById(R.id.rv);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
                menuOpciones.collapse();
            }
        });

        //Abrir galeria
        fototeca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/");
//                startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Seleccione"), COD_SELECCIONA_MULTIPLE);
                menuOpciones.collapse();
            }
        });


        //Eliminar fotografia
//        eliminador.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        agregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
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

                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        rv.setLayoutManager(staggeredGridLayoutManager);
//                        lista_fotos.add(image);
                        lista_fotos.add(new ItemFoto(image, false));
                        fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                        rv.setAdapter(fotoAdapter);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
//                    galeria.setImageResource(R.drawable.error);
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

                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                rv.setLayoutManager(staggeredGridLayoutManager);
                lista_fotos.add(new ItemFoto(bitmap, false));
                fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                rv.setAdapter(fotoAdapter);
                fotoAdapter.setOnItemClickListener(new FotoAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position > 0) {
                            menuOpciones.setVisibility(View.GONE);
                            toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                        } else {
                            menuOpciones.setVisibility(View.VISIBLE);
                            toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                        }
                    }
                });
                break;

            case COD_SELECCIONA_MULTIPLE:
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        // your code for multiple image selection
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                            bitmap = testImagen(bitmap, imageUri, getContext());
                            lista_fotos.add(new ItemFoto(bitmap, false));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Uri uri = data.getData();
                    // your codefor single image selection
                    try {
                        Bitmap bitmapAx = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        bitmapAx = testImagen(bitmapAx, uri, getContext());
                        lista_fotos.add(new ItemFoto(bitmapAx, false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                rv.setLayoutManager(staggeredGridLayoutManager);
                fotoAdapter = new FotoAdapter(getActivity(), lista_fotos);
                rv.setAdapter(fotoAdapter);
                fotoAdapter.setOnItemClickListener(new FotoAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position > 0) {
                            menuOpciones.setVisibility(View.GONE);
                            toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(true);
                        } else {
                            menuOpciones.setVisibility(View.VISIBLE);
                            toolbar.getMenu().findItem(R.id.action_eliminar).setVisible(false);
                        }
                    }
                });
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
}