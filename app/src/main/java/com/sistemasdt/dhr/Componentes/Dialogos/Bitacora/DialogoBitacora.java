package com.sistemasdt.dhr.Componentes.Dialogos.Bitacora;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.ServiciosAPI.QuerysBitacoras;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DialogoBitacora extends DialogFragment {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private BitacoraAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ItemBitacora> listaBitacora;

    public DialogoBitacora() {

    }

    public static DialogoBitacora display(FragmentManager fragmentManager) {
        DialogoBitacora dialogoBitacora = new DialogoBitacora();
        dialogoBitacora.show(fragmentManager, "Dialogo DialogoBitacora");
        return dialogoBitacora;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_bitacora, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Bitacora del Sistema");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));

        toolbar.inflateMenu(R.menu.opciones_toolbarcitas);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_aceptar:
                    dismiss();
                    return true;

                default:
                    return false;
            }
        });

        listaBitacora = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listaBitacora);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        obtenerBitacora();
        return view;
    }

    public void obtenerBitacora() {
        listaBitacora.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        QuerysBitacoras querysBitacoras = new QuerysBitacoras(getContext());
        querysBitacoras.obtenerBitacora(preferenciasUsuario.getInt("ID_USUARIO", 0), new QuerysBitacoras.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listaBitacora.add(new ItemBitacora(
                                jsonArray.getJSONObject(i).getString("ACCION"),
                                jsonArray.getJSONObject(i).getString("EVENTO"),
                                jsonArray.getJSONObject(i).getString("SECCION"),
                                jsonArray.getJSONObject(i).getString("FECHA"),
                                jsonArray.getJSONObject(i).getString("USUARIO")
                        ));
                    }
                    mAdapter = new BitacoraAdapter(listaBitacora);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                obtenerBitacora();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
