package com.example.dentalhistoryrecorder.Rutas.Catalogos.Cuentas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Servicios.ListadoServicios;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysCuentas;
import com.example.dentalhistoryrecorder.ServiciosAPI.QuerysServicios;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Cuentas extends Fragment {
    private Toolbar toolbar;
    private TextInputEditText usuarioCuenta, password, passwordConfirm;
    private TextInputLayout usuarioCuentaLayout, passwordLayout, passwordConfirmLayout;
    private FloatingActionButton guardadorCuenta;
    private ArrayList<ItemCuenta> mListadoCuentas;

    private boolean modoEdicion;
    private Typeface typeface;
    private int ID_CUENTA;

    public Cuentas() {
        modoEdicion = false;
    }

    public void enviarCuentas(ArrayList<ItemCuenta> listadoCuentas) {
        mListadoCuentas = listadoCuentas;
    }

    public void editarCuenta(int id) {
        modoEdicion = true;
        ID_CUENTA = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuentas, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        if (!modoEdicion)
            toolbar.setTitle("Cuenta Nueva");
        else
            toolbar.setTitle("Cuenta #" + ID_CUENTA);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListadoCuentas listadoCuentas = new ListadoCuentas();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.contenedor, listadoCuentas);
                transaction.commit();
            }
        });

        usuarioCuentaLayout = view.findViewById(R.id.usuarioCuentaLayout);
        usuarioCuentaLayout.setTypeface(typeface);

        passwordLayout = view.findViewById(R.id.passwordLayout);
        passwordLayout.setTypeface(typeface);

        passwordConfirmLayout = view.findViewById(R.id.passwordConfirmLayout);
        passwordConfirmLayout.setTypeface(typeface);

        usuarioCuenta = view.findViewById(R.id.usuarioCuenta);
        usuarioCuenta.setTypeface(typeface);
        usuarioCuenta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usuarioRequerido()) {
                    if (validarUsuario()) {
                        validarUsuarioRepetido();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password = view.findViewById(R.id.password);
        password.setTypeface(typeface);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordRequerido();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordConfirm = view.findViewById(R.id.passwordConfirm);
        passwordConfirm.setTypeface(typeface);
        passwordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordConfirmRequerido()) {
                    validarPassword();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        guardadorCuenta = view.findViewById(R.id.grabarCuenta);
        guardadorCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usuarioRequerido() || !passwordRequerido() || !passwordConfirmRequerido() ||
                        !validarUsuario() ||
                        !validarPassword() ||
                        !validarUsuarioRepetido())
                    return;

                if (!modoEdicion)
                    registrarCuenta();
            }
        });

        return view;
    }

    private void registrarCuenta() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysCuentas querysCuentas = new QuerysCuentas(getContext());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("USUARIO", usuarioCuenta.getText().toString().trim());
            jsonBody.put("PASSWORD", password.getText().toString().trim());
            jsonBody.put("ID_USUARIO", preferenciasUsuario.getInt("ID_USUARIO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        querysCuentas.registrarCuenta(jsonBody, new QuerysCuentas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Alerter.create(getActivity())
                        .setTitle("Cuenta Registrada")
                        .setIcon(R.drawable.logonuevo)
                        .setTextTypeface(typeface)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.FondoSecundario)
                        .show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ListadoCuentas listadoCuentas = new ListadoCuentas();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.contenedor, listadoCuentas);
                        transaction.commit();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    VALIDACIONES
    private boolean usuarioRequerido() {
        String textoCodigo = usuarioCuenta.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            usuarioCuentaLayout.setError("Campo requerido");
            return false;
        } else {
            usuarioCuentaLayout.setError(null);
            return true;
        }
    }

    private boolean passwordRequerido() {
        String textoCodigo = password.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            passwordLayout.setError("Campo requerido");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }
    }

    private boolean passwordConfirmRequerido() {
        String textoCodigo = passwordConfirm.getText().toString().trim();
        if (textoCodigo.isEmpty()) {
            passwordConfirmLayout.setError("Campo requerido");
            return false;
        } else {
            passwordConfirmLayout.setError(null);
            return true;
        }
    }

    private boolean validarUsuario() {
        String textoDescripcion = usuarioCuenta.getText().toString().trim();
        Pattern patron = Pattern.compile("^[a-z0-9]+$");
        if (patron.matcher(textoDescripcion).matches()) {
            usuarioCuentaLayout.setError(null);
            return true;
        } else {
            usuarioCuentaLayout.setError("Usuario invalido");
            return false;
        }
    }

    private boolean validarPassword() {
        String pass = password.getText().toString().trim();
        String passConfirm = passwordConfirm.getText().toString().trim();

        if (pass.equals(passConfirm)) {
            passwordConfirmLayout.setError(null);
            return true;
        } else {
            passwordConfirmLayout.setError("Las contraseñas no coinciden");
            return false;
        }
    }

    private boolean validarUsuarioRepetido() {
        String usuario = usuarioCuenta.getText().toString().trim();
        ArrayList<ItemCuenta> listaConsultada = new ArrayList<>();

        for (ItemCuenta item : mListadoCuentas) {
            if (item.getUsuarioCuenta().toLowerCase().equals(usuario)) {
                listaConsultada.add(item);
            }
        }

        if (listaConsultada.size() == 0) {
            usuarioCuentaLayout.setError(null);
            return true;
        } else {
            usuarioCuentaLayout.setError("Usuario duplicado");
            return false;
        }
    }
}