package com.sistemasdt.dhr.Rutas.Fichas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto.FotoAdapter;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialFoto.ItemFoto;
import com.sistemasdt.dhr.Rutas.Fichas.HistorialOdonto.HistorialOdonDos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

public class GuardadorFichaNormal extends Fragment {
    private Toolbar toolbar;
    private TextView tituloResumenPaciente, resumenPaciente;
    private TextView tituloResumen, tituloResumenTratamientos, resumenTratamientos;
    private TextView tituloResumenPagos, resumenPagos, tituloResumenFotos, resumenFotos;
    private FloatingActionButton guardador;

    public GuardadorFichaNormal() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guardador_ficha_normal, container, false);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Resumen de Ficha");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar lista
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Resumen de Ficha");
                builder.setMessage("¿Seguro que desea cancelar?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MenuFichas menuFichas = new MenuFichas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        transaction.replace(R.id.contenedor, menuFichas);
                        transaction.commit();
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        tituloResumenPaciente = view.findViewById(R.id.tituloResumenPaciente);
        tituloResumenPaciente.setTypeface(face);

        resumenPaciente = view.findViewById(R.id.resumenPaciente);
        resumenPaciente.setTypeface(face);

        tituloResumen = view.findViewById(R.id.tituloResumen);
        tituloResumen.setTypeface(face);

        tituloResumenTratamientos = view.findViewById(R.id.tituloResumenTratamientos);
        tituloResumenTratamientos.setTypeface(face);

        resumenTratamientos = view.findViewById(R.id.resumenTratamientos);
        resumenTratamientos.setTypeface(face);

        tituloResumenPagos = view.findViewById(R.id.tituloResumenPagos);
        tituloResumenPagos.setTypeface(face);

        resumenPagos = view.findViewById(R.id.resumenPagos);
        resumenPagos.setTypeface(face);

        tituloResumenFotos = view.findViewById(R.id.tituloResumenFotos);
        tituloResumenFotos.setTypeface(face);

        tituloResumenFotos = view.findViewById(R.id.tituloResumenFotos);
        tituloResumenFotos.setTypeface(face);

        resumenFotos = view.findViewById(R.id.resumenFotos);
        resumenFotos.setTypeface(face);

        guardador = view.findViewById(R.id.guardadorFichaN);
        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progressDialog);
                builder.setIcon(R.drawable.logonuevo);
                builder.setTitle("Resumen de Ficha");
                builder.setMessage("¿Seguro que desea registrar?");
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        guardarFicha();
//                        MenuFichas menuFichas = new MenuFichas();
//                        FragmentTransaction transaction = getFragmentManager().beginTransaction()
//                                .setCustomAnimations(R.anim.left_in, R.anim.left_out);
//                        transaction.replace(R.id.contenedor, menuFichas);
//                        transaction.commit();
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("RESUMEN_FN", Context.MODE_PRIVATE);
        resumenPaciente.setText(sharedPreferences.getString("PACIENTE", "0"));
        resumenTratamientos.setText(sharedPreferences.getString("NO_TRATAMIENTOS", "0"));
        resumenPagos.setText(sharedPreferences.getString("NO_PAGOS", "0"));
        resumenFotos.setText(sharedPreferences.getString("NO_FOTOS", "0"));

        return view;
    }

    public void guardarFicha() {
        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        //FICHA GENERAL
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FICHA", Context.MODE_PRIVATE);
//        sharedPreferences.getString("ID_PACIENTE", "");
//        sharedPreferences.getString("PACIENTE", "");
//        sharedPreferences.getString("FECHA", "");
//        sharedPreferences.getString("MEDICO", "");
//        sharedPreferences.getString("MOTIVO", "");
//        sharedPreferences.getString("REFERENTE", "");

        // HISTORIAL MEDICO 1
        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("HMED1", Context.MODE_PRIVATE);
//        sharedPreferences1.getBoolean("HOSPITALIZADO", false);
//        sharedPreferences1.getString("DESCRIPCION_HOS", " ");
//        sharedPreferences1.getBoolean("TRATAMIENTO_MEDICO", false);
//        sharedPreferences1.getBoolean("ALERGIA", false);
//        sharedPreferences1.getString("DESCRIPCION_ALERGIA", " ");
//        sharedPreferences1.getBoolean("HEMORRAGIA", false);
//        sharedPreferences1.getBoolean("MEDICAMENTO", false);
//        sharedPreferences1.getString("DESCRIPCION_MEDICAMENTO", " ");

        // HISTORIAL MEDICO 2
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("HMED2", Context.MODE_PRIVATE);
//        sharedPreferences2.getBoolean("CORAZON", false);
//        sharedPreferences2.getBoolean("ARTRITIS", false);
//        sharedPreferences2.getBoolean("TUBERCULOSIS", false);
//        sharedPreferences2.getBoolean("FIEBREREU", false);
//        sharedPreferences2.getBoolean("PRESION_ALTA", false);
//        sharedPreferences2.getBoolean("PRESION_BAJA", false);
//        sharedPreferences2.getBoolean("DIABETES", false);
//        sharedPreferences2.getBoolean("ANEMIA", false);
//        sharedPreferences2.getBoolean("EPILEPSIA", false);
//        sharedPreferences2.getString("OTROS", " ");

        // HISTORIAL ODONTO 1
        SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences("HOD1", Context.MODE_PRIVATE);
//        sharedPreferences3.getBoolean("DOLOR", false);
//        sharedPreferences3.getString("DESC_DOLOR", " ");
//        sharedPreferences3.getBoolean("GINGIVITIS", false);
//        sharedPreferences3.getString("OTROS", " ");

        // HISTORIAL ODONTO 2
        SharedPreferences sharedPreferences4 = getActivity().getSharedPreferences("HOD2", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences4.getStringSet("listaTratamientos", null);
        ArrayList<String> listaAuxiliar = new ArrayList<>(set);

        // HISTORIAL FOTOGRAFICO
        SharedPreferences sharedPreferences5 = getActivity().getSharedPreferences("HFOTO", Context.MODE_PRIVATE);
        Set<String> arregloFotos = sharedPreferences5.getStringSet("listaFotos", null);
        ArrayList<String> listadoFotos = new ArrayList<>(arregloFotos);

        // JSON PARA REGISTRAR FICHA
        JSONObject jsonObject = new JSONObject();
        try {
            // FICHA
            JSONObject jsonFicha = new JSONObject();
            jsonFicha.put("CODIGO_INTERNO", 1);
            jsonFicha.put("FECHA", sharedPreferences.getString("FECHA", ""));
            jsonFicha.put("MEDICO", sharedPreferences.getString("MEDICO", ""));
            jsonFicha.put("MOTIVO", sharedPreferences.getString("MOTIVO", ""));
            jsonFicha.put("REFERENTE", sharedPreferences.getString("REFERENTE", ""));
            jsonFicha.put("ID_PACIENTE", Integer.parseInt(sharedPreferences.getString("ID_PACIENTE", "0")));
            jsonFicha.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));

            // HISTORIAL MEDICO
            JSONObject jsonHM = new JSONObject();
            jsonHM.put("HOSPITALIZADO", (sharedPreferences1.getBoolean("HOSPITALIZADO", false)) ? 1 : 0);
            jsonHM.put("DESCRIPCION_HOS", sharedPreferences1.getString("DESCRIPCION_HOS", " "));
            jsonHM.put("TRATAMIENTO_MEDICO", (sharedPreferences1.getBoolean("TRATAMIENTO_MEDICO", false)) ? 1 : 0);
            jsonHM.put("ALERGIA", (sharedPreferences1.getBoolean("ALERGIA", false)) ? 1 : 0);
            jsonHM.put("DESCRIPCION_ALERGIA", sharedPreferences1.getString("DESCRIPCION_ALERGIA", " "));
            jsonHM.put("HEMORRAGIA", (sharedPreferences1.getBoolean("HEMORRAGIA", false)) ? 1 : 0);
            jsonHM.put("MEDICAMENTO", (sharedPreferences1.getBoolean("MEDICAMENTO", false)) ? 1 : 0);
            jsonHM.put("DESCRIPCION_MEDICAMENTO", sharedPreferences1.getString("DESCRIPCION_MEDICAMENTO", " "));
            jsonHM.put("ID_FICHA", 0);

            // HISTORIAL MEDICO - PADECIMIENTOS
            JSONObject jsonHM2 = new JSONObject();
            jsonHM2.put("CORAZON", (sharedPreferences2.getBoolean("CORAZON", false)) ? 1 : 0);
            jsonHM2.put("ARTRITIS", (sharedPreferences2.getBoolean("ARTRITIS", false)) ? 1 : 0);
            jsonHM2.put("TUBERCULOSIS", (sharedPreferences2.getBoolean("TUBERCULOSIS", false)) ? 1 : 0);
            jsonHM2.put("PRESION_ALTA", (sharedPreferences2.getBoolean("PRESION_ALTA", false)) ? 1 : 0);
            jsonHM2.put("PRESION_BAJA", (sharedPreferences2.getBoolean("PRESION_BAJA", false)) ? 1 : 0);
            jsonHM2.put("FIEBREREU", (sharedPreferences2.getBoolean("FIEBREREU", false)) ? 1 : 0);
            jsonHM2.put("ANEMIA", (sharedPreferences2.getBoolean("ANEMIA", false)) ? 1 : 0);
            jsonHM2.put("EPILEPSIA", (sharedPreferences2.getBoolean("EPILEPSIA", false)) ? 1 : 0);
            jsonHM2.put("DIABETES", (sharedPreferences2.getBoolean("DIABETES", false)) ? 1 : 0);
            jsonHM2.put("OTROS", sharedPreferences2.getString("OTROS", " "));
            jsonHM2.put("ID_HISTORIAL_MEDICO", 0);

            jsonHM.put("PADECIMIENTOS", jsonHM2);

            // HISTORIAL ODONTODOLOGICO
            JSONObject jsonHO = new JSONObject();
            jsonHO.put("DOLOR", (sharedPreferences2.getBoolean("DOLOR", false)) ? 1 : 0);
            jsonHO.put("DESC_DOLOR", sharedPreferences3.getString("DESC_DOLOR", " "));
            jsonHO.put("GINGIVITIS", (sharedPreferences2.getBoolean("GINGIVITIS", false)) ? 1 : 0);
            jsonHO.put("OTROS", sharedPreferences3.getString("OTROS", " "));
            jsonHO.put("ID_FICHA", 0);

            // HISTORIAL ODONTODOLOGICO - TRATAMIENTOS
            JSONArray jsonArray = new JSONArray();
            for (String item : listaAuxiliar) {
                try {
                    String cadenaAuxiliar[] = item.split(";");

                    JSONObject rowJSON = new JSONObject();
                    rowJSON.put("PLAN", cadenaAuxiliar[2]);
                    rowJSON.put("COSTO", Double.parseDouble(cadenaAuxiliar[3]));
                    rowJSON.put("FECHA", cadenaAuxiliar[4]);
                    rowJSON.put("ID_PIEZA", Integer.parseInt(cadenaAuxiliar[0]));
                    rowJSON.put("ID_SERVICIO", Integer.parseInt(cadenaAuxiliar[1]));
                    rowJSON.put("ID_HISTORIAL_ODONTO", 0);

                    jsonArray.put(rowJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            jsonHO.put("TRATAMIENTOS", jsonArray);

            // HISTORIAL FOTOGRAFICO
            JSONArray jsonArrayFotos = new JSONArray();
            for (int i = 0; i < listadoFotos.size(); i++) {
                try {
                    byte[] decodedString = Base64.decode(listadoFotos.get(i), Base64.DEFAULT);
                    String codigoFoto = Base64.encodeToString(decodedString, Base64.DEFAULT);

                    JSONObject rowJSON = new JSONObject();
                    rowJSON.put("FOTO", codigoFoto);
                    rowJSON.put("URL", " ");
                    rowJSON.put("DESCRIPCION", " ");
                    rowJSON.put("NOMBRE", " ");
                    rowJSON.put("ID_FICHA", 0);

                    jsonArrayFotos.put(rowJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // PAGOS
            JSONArray jsonArrayPagos = new JSONArray();
            for (int i = 0; i < 2; i++) {
                try {
                    JSONObject rowJSON = new JSONObject();
                    rowJSON.put("PAGO", 55.25);
                    rowJSON.put("DESCRIPCION", "Pago ficha 1");
                    rowJSON.put("FECHA", "2020/12/01");
                    rowJSON.put("ID_FICHA", 0);

                    jsonArrayPagos.put(rowJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // DATOS A REGISTRAR
            jsonObject.put("FICHA", jsonFicha);
            jsonObject.put("HISTORIAL_MEDICO", jsonHM);
            jsonObject.put("HISTORIAL_ODONTO", jsonHO);
            jsonObject.put("HISTORIAL_FOTOS", jsonArrayFotos);
            jsonObject.put("PAGOS", jsonArrayPagos);

            Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}