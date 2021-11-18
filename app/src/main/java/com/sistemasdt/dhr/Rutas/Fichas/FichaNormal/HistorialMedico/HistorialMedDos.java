package com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialMedico;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.Ficha;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.HistorialOdon;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.MenuFichaNormal;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

public class HistorialMedDos extends Fragment {
    private Toolbar toolbar;
    private CheckBox corazon, artritris, tuberculosis, f_reuma, pres_alta, pres_baja, diabetes, anemia, epilepsia;
    private TextInputEditText otro;
    private FloatingActionButton guardador;
    private TextInputLayout otrosLayout;
    private boolean MODO_EDICION = false;
    private int ID_FICHA = 0;
    private int ID_HISTORIAL_MEDICO = 0;

    public HistorialMedDos() {
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
        View view = inflater.inflate(R.layout.fragment_historialmed_dos, container, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);

        if (!MODO_EDICION) {
            toolbar.setTitle("Historial Medico (2/2)");
            toolbar.setNavigationIcon(R.drawable.ic_atras);
        } else {
            toolbar.setTitle("Padecimientos");
            toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODO_EDICION) {
                    HistorialMed historialMed = new HistorialMed();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                    transaction.replace(R.id.contenedor, historialMed);
                    transaction.commit();
                } else {
                    MenuFichaNormal menuFichaNormal = new MenuFichaNormal();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_in, R.anim.right_out);
                    transaction.replace(R.id.contenedor, menuFichaNormal);
                    transaction.commit();
                }
            }
        });

        guardador = view.findViewById(R.id.guardador_hm2);

        corazon = view.findViewById(R.id.corazon);
        corazon.setTypeface(face);
        artritris = view.findViewById(R.id.artritris);
        artritris.setTypeface(face);
        tuberculosis = view.findViewById(R.id.tuberculosis);
        tuberculosis.setTypeface(face);
        f_reuma = view.findViewById(R.id.fiebre);
        f_reuma.setTypeface(face);
        pres_alta = view.findViewById(R.id.presion_alta);
        pres_alta.setTypeface(face);
        pres_baja = view.findViewById(R.id.presion_baja);
        pres_baja.setTypeface(face);
        diabetes = view.findViewById(R.id.diabetes);
        diabetes.setTypeface(face);
        anemia = view.findViewById(R.id.anemia);
        anemia.setTypeface(face);
        epilepsia = view.findViewById(R.id.epilepsia);
        epilepsia.setTypeface(face);
        otro = view.findViewById(R.id.otro_hm);
        otro.setTypeface(face);

        otrosLayout = view.findViewById(R.id.otroLayout);
        otrosLayout.setTypeface(face);

        guardador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODO_EDICION) {
                    final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED2", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor escritor = sharedPreferences.edit();
                    escritor.putBoolean("CORAZON", corazon.isChecked());
                    escritor.putBoolean("ARTRITIS", artritris.isChecked());
                    escritor.putBoolean("TUBERCULOSIS", tuberculosis.isChecked());
                    escritor.putBoolean("FIEBREREU", f_reuma.isChecked());
                    escritor.putBoolean("PRESION_ALTA", pres_alta.isChecked());
                    escritor.putBoolean("PRESION_BAJA", pres_baja.isChecked());
                    escritor.putBoolean("DIABETES", diabetes.isChecked());
                    escritor.putBoolean("ANEMIA", anemia.isChecked());
                    escritor.putBoolean("EPILEPSIA", epilepsia.isChecked());
                    escritor.putString("OTROS", otro.getText().toString());
                    escritor.commit();

                    HistorialOdon historialOdon = new HistorialOdon();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out);
                    transaction.replace(R.id.contenedor, historialOdon);
                    transaction.commit();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("CORAZON", (corazon.isChecked()) ? 1 : 0);
                        jsonObject.put("ARTRITIS", (artritris.isChecked()) ? 1 : 0);
                        jsonObject.put("TUBERCULOSIS", (tuberculosis.isChecked()) ? 1 : 0);
                        jsonObject.put("PRESION_ALTA", (pres_alta.isChecked()) ? 1 : 0);
                        jsonObject.put("PRESION_BAJA", (pres_baja.isChecked()) ? 1 : 0);
                        jsonObject.put("FIEBREREU", (f_reuma.isChecked()) ? 1 : 0);
                        jsonObject.put("ANEMIA", (anemia.isChecked()) ? 1 : 0);
                        jsonObject.put("EPILEPSIA", (epilepsia.isChecked()) ? 1 : 0);
                        jsonObject.put("DIABETES", (diabetes.isChecked()) ? 1 : 0);
                        jsonObject.put("OTROS", otro.getText().toString());

                        QuerysFichas querysFichas = new QuerysFichas(getContext());
                        querysFichas.actualizarPadecimientosHM(ID_HISTORIAL_MEDICO, jsonObject, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Padecimientos")
                                        .setText("Actualizados correctamente")
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
                            public void onFailure(Exception e) {
                                progressDialog.dismiss();

                                Alerter.create(getActivity())
                                        .setTitle("Error")
                                        .setText("Fallo al actualizar los padecimientos")
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
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HMED2", Context.MODE_PRIVATE);
            corazon.setChecked(sharedPreferences.getBoolean("CORAZON", false));
            artritris.setChecked(sharedPreferences.getBoolean("ARTRITIS", false));
            tuberculosis.setChecked(sharedPreferences.getBoolean("TUBERCULOSIS", false));
            f_reuma.setChecked(sharedPreferences.getBoolean("FIEBREREU", false));
            pres_alta.setChecked(sharedPreferences.getBoolean("PRESION_ALTA", false));
            pres_baja.setChecked(sharedPreferences.getBoolean("PRESION_BAJA", false));
            diabetes.setChecked(sharedPreferences.getBoolean("DIABETES", false));
            anemia.setChecked(sharedPreferences.getBoolean("ANEMIA", false));
            epilepsia.setChecked(sharedPreferences.getBoolean("EPILEPSIA", false));
            otro.setText(sharedPreferences.getString("OTROS", ""));
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

                        QuerysFichas querysFichasAux = new QuerysFichas(getContext());
                        querysFichasAux.obtenerPadecimientosHM(ID_HISTORIAL_MEDICO, new QuerysFichas.VolleyOnEventListener() {
                            @Override
                            public void onSuccess(Object object2) {
                                try {
                                    JSONObject aux = new JSONObject(object2.toString());
                                    corazon.setChecked((aux.getInt("CORAZON") == 1 ? true : false));
                                    artritris.setChecked((aux.getInt("ARTRITIS") == 1 ? true : false));
                                    tuberculosis.setChecked((aux.getInt("TUBERCULOSIS") == 1 ? true : false));
                                    pres_alta.setChecked((aux.getInt("PRESION_ALTA") == 1 ? true : false));
                                    pres_baja.setChecked((aux.getInt("PRESION_BAJA") == 1 ? true : false));
                                    f_reuma.setChecked((aux.getInt("FIEBREREU") == 1 ? true : false));
                                    anemia.setChecked((aux.getInt("ANEMIA") == 1 ? true : false));
                                    epilepsia.setChecked((aux.getInt("EPILEPSIA") == 1 ? true : false));
                                    diabetes.setChecked((aux.getInt("DIABETES") == 1 ? true : false));
                                    otro.setText(aux.getString("OTROS"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressDialog.dismiss();
                                obtenerDatos();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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