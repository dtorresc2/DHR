package com.example.dentalhistoryrecorder.OpcionInicio;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dentalhistoryrecorder.MainActivity;
import com.example.dentalhistoryrecorder.R;
import com.tapadoo.alerter.Alerter;

public class Inicio extends Fragment {
    private Toolbar toolbar;
    private TextView usuario;

    public Inicio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        final Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logotool);
        toolbar.inflateMenu(R.menu.opciones_tool_inicio);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_editar:
                        SharedPreferences preferencias2 = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                        Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle(preferencias2.getString("idUsuario",""))
                                .setText(preferencias2.getString("correo",""))
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face3)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();
                        return true;

                    case R.id.action_logout:
                        SharedPreferences preferencias = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bahnschrift.ttf");
                        Alerter.create(getActivity())
                                .setTitle(preferencias.getString("idUsuario",""))
                                .setText(preferencias.getString("correo",""))
                                .setIcon(R.drawable.logonuevo)
                                .setTextTypeface(face)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.AzulOscuro)
                                .show();


                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        getActivity().finish();
                        return true;

                    default:
                        return false;
                }
            }
        });

        usuario = view.findViewById(R.id.usuarioPerfil);
        usuario.setTypeface(face2);

        return view;
    }

}
