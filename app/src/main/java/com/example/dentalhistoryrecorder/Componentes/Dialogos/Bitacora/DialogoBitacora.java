package com.example.dentalhistoryrecorder.Componentes.Dialogos.Bitacora;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalhistoryrecorder.R;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas.ItemPieza;
import com.example.dentalhistoryrecorder.Rutas.Catalogos.Piezas.PiezasAdapter;

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
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialogo_bitacora, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Bitacora del Sistema");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));

        toolbar.inflateMenu(R.menu.opciones_toolbarcitas);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_aceptar:
                        dismiss();
                        return true;

                    default:
                        return false;
                }
            }
        });

        listaBitacora = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.listaBitacora);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

//        final SharedPreferences preferenciasUsuario = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
//        listaBitacora.add(new ItemBitacora(1,
//                "Ficha Registrada",
//                "20/12/2021 4:55 PM",
//                String.valueOf(preferenciasUsuario.getInt("ID_CUENTA", 0))
//        ));

        listaBitacora.add(new ItemBitacora(1,
                "Ficha Registrada",
                "20/12/2021 4:55 PM",
                "admin"
        ));

        listaBitacora.add(new ItemBitacora(1,
                "Ficha Registrada",
                "20/12/2021 4:55 PM",
                "admin"
        ));

        listaBitacora.add(new ItemBitacora(1,
                "Ficha Registrada",
                "20/12/2021 4:55 PM",
                "admin"
        ));

        mAdapter = new BitacoraAdapter(listaBitacora);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}
