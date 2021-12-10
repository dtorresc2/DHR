package com.sistemasdt.dhr.Componentes.PDF;

import android.app.ProgressDialog;
import android.os.Handler;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.sistemasdt.dhr.R;

import java.io.IOException;

public class Impresiones {

    public Impresiones() {

    }

    public void generarFichaNormal(int ID_FICHA) {
        try {
            BaseFont baseFont = BaseFont.createFont("assets/fonts/bahnschrift.ttf", "UTF-8", BaseFont.EMBEDDED);

            Font fuentecolumna = new Font(baseFont, 12, Font.NORMAL);
            fuentecolumna.setColor(255, 255, 255);
            BaseColor fondo = new BaseColor(49, 63, 76);

            Font fuentedatos = new Font(baseFont, 11, Font.NORMAL);
            fuentedatos.setColor(49, 63, 76);

            Font fuentecorrelativo = new Font(baseFont, 14, Font.NORMAL);



        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

}
