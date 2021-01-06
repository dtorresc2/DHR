package com.example.dentalhistoryrecorder.Componentes.PDF;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.dentalhistoryrecorder.R;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;

@SuppressLint("ValidFragment")
public class LectorPDF extends DialogFragment {
    private static File archivo;
    private PDFView pdfView;
    private Toolbar toolbar;
    public static final String TAG = "example_dialog";

    /*@SuppressLint("ValidFragment")
    public LectorPDF(File file) {
        archivo = file;
    }*/

    public LectorPDF(File file) {
        archivo = file;
    }

    public static LectorPDF display(FragmentManager fragmentManager) {
        LectorPDF exampleDialog = new LectorPDF(archivo);
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_pdf, container, false);
        toolbar = view.findViewById(R.id.toolbar3);
        toolbar.setTitle("Ficha Electronica");
        toolbar.setTitleTextColor(getResources().getColor(R.color.Blanco));
        toolbar.setNavigationIcon(R.drawable.ic_cerrar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.inflateMenu(R.menu.opciones_pdf);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_compartir:
                        Uri uri = Uri.fromFile(archivo);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("*/*");
                        startActivity(Intent.createChooser(shareIntent, "Compartir Ficha"));
                        return true;

                    default:
                        return false;
                }
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
