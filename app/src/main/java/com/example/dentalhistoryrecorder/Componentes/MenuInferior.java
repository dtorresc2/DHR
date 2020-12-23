package com.example.dentalhistoryrecorder.Componentes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dentalhistoryrecorder.R;

public class MenuInferior extends BottomSheetDialogFragment {
    private MenuInferiorListener mMenuInferiorListener;
    private Context mContext;

    public void eventoClick(MenuInferiorListener menuInferiorListener){
        mMenuInferiorListener = menuInferiorListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_inferior, container, false);

        Button button1 = view.findViewById(R.id.boton1);
        Button button2 = view.findViewById(R.id.boton2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuInferiorListener.onButtonClicked("Editando");
                dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuInferiorListener.onButtonClicked("Eliminado");
                dismiss();
            }
        });

        return view;
    }

    public interface MenuInferiorListener {
        void onButtonClicked(String text);
    }

}
