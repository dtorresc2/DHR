package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialMedico.HistorialMedDos;
import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

public class HistorialOdon extends Fragment {
    private Toolbar toolbar;
    private FloatingActionButton guardador;
    CheckBox dolor, gingivitis;
    private EditText desc_dolor;
    private TextInputEditText otros;
    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;
    private int ID_HISTORIAL_MEDICO = 0;

    public HistorialOdon() {
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
        View view = inflater.inflate(R.layout.fragment_historial_odon, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);

        if (!MODO_EDICION) {
            toolbar.setTitle("Historial Odontodologico (1/2)");
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setTitle("Historial Odontodologico");
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODO_EDICION) {
                    HistorialMedDos historialMedDos = new HistorialMedDos();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.right_in, R.anim.right_out);
                    transaction.replace(R.id.contenedor, historialMedDos);
                    transaction.commit();
                } else {
                    MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                    transaction.replace(R.id.contenedor, menuFichaNormal);
                    transaction.commit();
                }
            }
        });

        dolor = view.findViewById(R.id.dolor);
        dolor.setTypeface(face);
        gingivitis = view.findViewById(R.id.gingivitis);
        gingivitis.setTypeface(face);
        otros = view.findViewById(R.id.otros_ho);
        otros.setTypeface(face);
        desc_dolor = view.findViewById(R.id.desc_dolor);
        desc_dolor.setTypeface(face);

        guardador = view.findViewById(R.id.guardador_hd);

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODO_EDICION) {
                    final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HOD1", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor = sharedPreferences.edit();
                    escritor.putBoolean("DOLOR", dolor.isChecked());
                    escritor.putString("DESC_DOLOR", desc_dolor.getText().toString());
                    escritor.putBoolean("GINGIVITIS", gingivitis.isChecked());
                    escritor.putString("OTROS", otros.getText().toString());
                    escritor.commit();

                    HistorialOdonDos historialOdonDos = new HistorialOdonDos();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, historialOdonDos);
                    transaction.commit();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("DOLOR", (dolor.isChecked()) ? 1 : 0);
                        jsonObject.put("GINGIVITIS", (gingivitis.isChecked()) ? 1 : 0);
                        jsonObject.put("DESCRIPCION_DOLOR", desc_dolor.getText().toString());
                        jsonObject.put("OTROS", otros.getText().toString());

                        QuerysFichas querysFichas = new QuerysFichas(getContext());
                        querysFichas.actualizarHistorialOdontodologico(ID_FICHA, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Historial Odontodologico")
                                        .setText("Actualizado correctamente")
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
                                        .setText("Fallo al actualizar el Historial Odontodologico")
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

        obtenerDatos();

        return view;
    }

    public void obtenerDatos() {
        if (!MODO_EDICION) {
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HOD1", Context.MODE_PRIVATE);
            dolor.setChecked(sharedPreferences.getBoolean("DOLOR", false));
            gingivitis.setChecked(sharedPreferences.getBoolean("GINGIVITIS", false));
            desc_dolor.setText(sharedPreferences.getString("DESC_DOLOR", ""));
            otros.setText(sharedPreferences.getString("OTROS", ""));
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
            progressDialog.setMessage("Cargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            QuerysFichas querysFichas = new QuerysFichas(getContext());
            querysFichas.obtenerHistorialOdontodologico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject(object.toString());
                        dolor.setChecked((jsonObject.getInt("DOLOR") == 1 ? true : false));
                        gingivitis.setChecked((jsonObject.getInt("GINGIVITIS") == 1 ? true : false));
                        desc_dolor.setText(jsonObject.getString("DESCRIPCION_DOLOR"));
                        otros.setText(jsonObject.getString("OTROS"));
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
                    obtenerDatos();
                }
            });
        }
    }
}
