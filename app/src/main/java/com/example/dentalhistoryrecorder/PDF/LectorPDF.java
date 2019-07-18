package com.example.dentalhistoryrecorder.PDF;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dentalhistoryrecorder.R;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;

@SuppressLint("ValidFragment")
public class LectorPDF extends DialogFragment {
    private File archivo;
    private PDFView pdfView;
    private Toolbar toolbar;

    @SuppressLint("ValidFragment")
    public LectorPDF(File file) {
        archivo = file;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Slide);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_pdf, container, false);
        toolbar = view.findViewById(R.id.toolbar3);
        toolbar.setTitle("Ficha Electronica");
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        pdfView = view.findViewById(R.id.pdfVisor);
        pdfView.fromFile(archivo)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();

        return view;
    }
}
