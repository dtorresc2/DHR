package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialMedico;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;

import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.Rutas.Fichas.MenuFichas;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

public class HistorialMed extends Fragment {
    private Toolbar toolbar;
    private CheckBox hospitalizado, alergia, medicamento, tratamiento, hemorragia;
    private EditText desc_hos, desc_alergia, desc_medicamento, otro;
    private FloatingActionButton guardador;
    private static final String TAG = "MyActivity";
    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;
    private int ID_HISTORIAL_MEDICO = 0;

    public HistorialMed() {
        // Required empty public constructor
        MODO_EDICION = false;
    }

    public void activarModoEdicion(int id) {
        MODO_EDICION = true;
        ID_FICHA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historialmed, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        //Barra de Titulo
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Historial Medico (1/2)");

        if (!MODO_EDICION) {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODO_EDICION) {
                    Ficha ficha = new Ficha();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                    transaction.replace(R.id.contenedor, ficha);
                    transaction.commit();
                } else {
                    MenuFichas menuFichas = new MenuFichas();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                    transaction.replace(R.id.contenedor, menuFichas);
                    transaction.commit();
                }
            }
        });

        guardador = view.findViewById(R.id.guardador_hm);

        //Componentes del Formulario
        hospitalizado = view.findViewById(R.id.hospitalizado);
        hospitalizado.setTypeface(face);
        alergia = view.findViewById(R.id.alergia);
        alergia.setTypeface(face);
        medicamento = view.findViewById(R.id.medicamento);
        medicamento.setTypeface(face);
        desc_hos = view.findViewById(R.id.desc_hospi);
        desc_hos.setTypeface(face);
        desc_alergia = view.findViewById(R.id.desc_alergia);
        desc_alergia.setTypeface(face);
        desc_medicamento = view.findViewById(R.id.desc_medi);
        desc_medicamento.setTypeface(face);

        tratamiento = view.findViewById(R.id.tratamiento);
        tratamiento.setTypeface(face);
        hemorragia = view.findViewById(R.id.hemorragia);
        hemorragia.setTypeface(face);

        //Detalle
        hospitalizado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (hospitalizado.isChecked()) {
                    desc_hos.setEnabled(true);
                } else {
                    desc_hos.setEnabled(false);
                }
            }
        });

        alergia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (alergia.isChecked()) {
                    desc_alergia.setEnabled(true);
                } else {
                    desc_alergia.setEnabled(false);
                }
            }
        });

        medicamento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (medicamento.isChecked()) {
                    desc_medicamento.setEnabled(true);
                } else {
                    desc_medicamento.setEnabled(false);
                }
            }
        });

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODO_EDICION) {
                    final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED1", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor = sharedPreferences.edit();
                    escritor.putBoolean("HOSPITALIZADO", hospitalizado.isChecked());
                    escritor.putString("DESCRIPCION_HOS", desc_hos.getText().toString());
                    escritor.putBoolean("TRATAMIENTO_MEDICO", tratamiento.isChecked());
                    escritor.putBoolean("ALERGIA", alergia.isChecked());
                    escritor.putString("DESCRIPCION_ALERGIA", desc_alergia.getText().toString());
                    escritor.putBoolean("HEMORRAGIA", hemorragia.isChecked());
                    escritor.putBoolean("MEDICAMENTO", medicamento.isChecked());
                    escritor.putString("DESCRIPCION_MEDICAMENTO", desc_medicamento.getText().toString());
                    escritor.commit();

                    HistorialMedDos historialMedDos = new HistorialMedDos();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, historialMedDos);
                    transaction.commit();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("HOSPITALIZADO", (hospitalizado.isChecked()) ? 1 : 0);
                        jsonObject.put("DESCRIPCION_HOS", desc_hos.getText().toString());
                        jsonObject.put("TRATAMIENTO_MEDICO", (tratamiento.isChecked()) ? 1 : 0);
                        jsonObject.put("ALERGIA", (alergia.isChecked()) ? 1 : 0);
                        jsonObject.put("DESCRIPCION_ALERGIA", desc_alergia.getText().toString());
                        jsonObject.put("HEMORRAGIA", (hemorragia.isChecked()) ? 1 : 0);
                        jsonObject.put("MEDICAMENTO", (medicamento.isChecked()) ? 1 : 0);
                        jsonObject.put("DESCRIPCION_MEDICAMENTO", desc_medicamento.getText().toString());

                        QuerysFichas querysFichas = new QuerysFichas(getContext());
                        querysFichas.actualizarHistorialMedico(ID_HISTORIAL_MEDICO, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Historial Medico")
                                        .setText("Actualizada correctamente")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.FondoSecundario)
                                        .show();


                                MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                                transaction.replace(R.id.contenedor, menuFichaNormal);
                                transaction.commit();
                            }

                            @Override
                            public void onSuccessBitmap(Bitmap object) {

                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Error")
                                        .setText("Fallo al actualizar el historial medico")
                                        .setIcon(R.drawable.logonuevo)
                                        .setTextTypeface(face)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorRes(R.color.AzulOscuro)
                                        .show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cargarDatos();

        return view;
    }

    public void cargarDatos() {
        if (!MODO_EDICION) {
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED1", Context.MODE_PRIVATE);
            hospitalizado.setChecked(sharedPreferences.getBoolean("HOSPITALIZADO", false));
            desc_hos.setText(sharedPreferences.getString("DESCRIPCION_HOS", ""));
            alergia.setChecked(sharedPreferences.getBoolean("ALERGIA", false));
            desc_alergia.setText(sharedPreferences.getString("DESCRIPCION_ALERGIA", ""));
            medicamento.setChecked(sharedPreferences.getBoolean("MEDICAMENTO", false));
            desc_medicamento.setText(sharedPreferences.getString("DESCRIPCION_MEDICAMENTO", ""));
            tratamiento.setChecked(sharedPreferences.getBoolean("TRATAMIENTO_MEDICO", false));
            hemorragia.setChecked(sharedPreferences.getBoolean("HEMORRAGIA", false));
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.obtenerHistorialMedico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject(object.toString());
                        ID_HISTORIAL_MEDICO = jsonObject.getInt("ID_HISTORIAL_MEDICO");
                        hospitalizado.setChecked((jsonObject.getInt("HOSPITALIZADO") == 1 ? true : false));
                        desc_hos.setText(jsonObject.getString("DESCRIPCION_HOS"));
                        alergia.setChecked((jsonObject.getInt("ALERGIA") == 1 ? true : false));
                        desc_alergia.setText(jsonObject.getString("DESCRIPCION_ALERGIA"));
                        medicamento.setChecked((jsonObject.getInt("MEDICAMENTO") == 1 ? true : false));
                        desc_medicamento.setText(jsonObject.getString("DESCRIPCION_MEDICAMENTO"));
                        tratamiento.setChecked((jsonObject.getInt("TRATAMIENTO_MEDICO") == 1 ? true : false));
                        hemorragia.setChecked((jsonObject.getInt("HEMORRAGIA") == 1 ? true : false));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onSuccessBitmap(Bitmap object) {

                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    cargarDatos();
                }
            });
        }
    }
}