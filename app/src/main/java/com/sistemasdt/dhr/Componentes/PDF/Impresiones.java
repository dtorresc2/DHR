package com.sistemasdt.dhr.Componentes.PDF;


import android.content.Context;

import android.os.Handler;

import androidx.fragment.app.FragmentManager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Impresiones {
    private Context CONTEXT;
    private FragmentManager FRAGMENT_MANAGER;
    private ArrayList<String> listaPacientes = null;

    private String NOMBRE_PACIENTE;
    private String EDAD_PACIENTE;
    private String CODIGO_FICHA;

    private int HOSPITALIZADO = 0;
    private int ALERGIA = 0;
    private int TRATAMIENTO = 0;
    private int HEMORRAGIA = 0;
    private int MEDICAMENTO = 0;
    private int ID_HISTORIAL_MEDICO = 0;

    private int CORAZON = 0;
    private int ARTRITIS = 0;
    private int TUBERCULOSIS = 0;
    private int PRESION_ALTA = 0;
    private int PRESION_BAJA = 0;
    private int FIEBREREU = 0;
    private int ANEMIA = 0;
    private int EPILEPSIA = 0;
    private int DIABETES = 0;
    private String DESCRIPCION_OTROS;

    public Impresiones(Context context, FragmentManager fragmentManager) {
        CONTEXT = context;
        FRAGMENT_MANAGER = fragmentManager;
    }

    public void generarFichaNormal(int ID_FICHA) {
        obtenerFicha(ID_FICHA);
        obtenerHistorialMedico(ID_FICHA);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                BaseFont baseFont = BaseFont.createFont("assets/fonts/bahnschrift.ttf", "UTF-8", BaseFont.EMBEDDED);

                Font fuentecolumna = new Font(baseFont, 12, Font.NORMAL);
                fuentecolumna.setColor(255, 255, 255);
                BaseColor fondo = new BaseColor(49, 63, 76);

                Font fuentedatos = new Font(baseFont, 11, Font.NORMAL);
                fuentedatos.setColor(49, 63, 76);

                Font fuentecorrelativo = new Font(baseFont, 14, Font.NORMAL);
                fuentecorrelativo.setColor(49, 63, 76);

                //Creacion de la Carpeta - Fichas Normales
                File folder = new File(CONTEXT.getExternalFilesDir(null).toString());

                File pdf = new File(folder, "Prueba.pdf");
                pdf.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(pdf, false);

                //Creacion del PDF
                Document documento = new Document(PageSize.LETTER, 50, 50, 80, 30);
                PdfWriter.getInstance(documento, fileOutputStream);
                documento.open();

                //Titulo
                Paragraph titulo = new Paragraph("Datos Personales", fuentecorrelativo);
                documento.add(titulo);

                //Tabla Datos Personales -------------------------------------------
                PdfPTable tabla_datospersonales = new PdfPTable(3);
                PdfPCell columnasdatos, filasdatos;
                String registros[] = {"Correlativo", "Nombre", "Edad"};
                String columnasreg[] = {
                        CODIGO_FICHA,
                        NOMBRE_PACIENTE,
                        EDAD_PACIENTE
                };
                tabla_datospersonales.setWidths(new float[]{2, 4, 2});

                //Columnas
                for (int i = 0; i < registros.length; i++) {
                    columnasdatos = new PdfPCell(new Phrase(registros[i], fuentecolumna));
                    columnasdatos.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnasdatos.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnasdatos.setBackgroundColor(fondo);
                    tabla_datospersonales.addCell(columnasdatos);
                }
                tabla_datospersonales.setHeaderRows(1);

                //Relleno de las filas
                for (int row = 0; row < 1; row++) {
                    for (int column = 0; column < 3; column++) {
                        filasdatos = new PdfPCell(new Phrase(columnasreg[column], fuentedatos));
                        filasdatos.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla_datospersonales.addCell(filasdatos);
                    }
                }
                tabla_datospersonales.setSpacingBefore(15);
                documento.add(tabla_datospersonales);

                Paragraph titulo2 = new Paragraph("Historial Medico", fuentecorrelativo);
                titulo2.setSpacingBefore(15);
                documento.add(titulo2);

                //Historial Medico
                Paragraph titulo3 = new Paragraph("Detalle", fuentedatos);
                titulo3.setAlignment(Element.ALIGN_CENTER);
                titulo3.setSpacingBefore(10);
                documento.add(titulo3);

                //Tabla Historial Medico -------------------------------------------
                PdfPTable tabla_historialdetalle = new PdfPTable(5);
                PdfPCell columnasmedd, filasmedd;
                String registros5[] = {"Hospitalizado", "Alergia", "Tratamiento", "Hemorragia", "Medicamento"};
                String celdash[] = new String[6];

                //Columnas
                for (int i = 0; i < 5; i++) {
                    columnasmedd = new PdfPCell(new Phrase(registros5[i], fuentecolumna));
                    columnasmedd.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnasmedd.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnasmedd.setBackgroundColor(fondo);
                    tabla_historialdetalle.addCell(columnasmedd);
                }

                tabla_historialdetalle.setHeaderRows(1);

                celdash[0] = HOSPITALIZADO == 0 ? "NO" : "SI";
                celdash[1] = ALERGIA == 0 ? "NO" : "SI";
                celdash[2] = TRATAMIENTO == 0 ? "NO" : "SI";
                celdash[3] = HEMORRAGIA == 0 ? "NO" : "SI";
                celdash[4] = MEDICAMENTO == 0 ? "NO" : "SI";

                //Relleno de las filas
                for (int row = 0; row < 1; row++) {
                    for (int column = 0; column < 5; column++) {
                        filasmedd = new PdfPCell(new Phrase(celdash[column], fuentedatos));
                        filasmedd.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla_historialdetalle.addCell(filasmedd);
                    }
                }
                tabla_historialdetalle.setSpacingBefore(15);
                documento.add(tabla_historialdetalle);

                Paragraph titulo4 = new Paragraph("Padecimientos", fuentedatos);
                titulo4.setAlignment(Element.ALIGN_CENTER);
                titulo4.setSpacingBefore(10);
                documento.add(titulo4);

                //Tabla Historial Medico Padecimientos #1 ==========================================
                PdfPTable tabla_padecimientos1 = new PdfPTable(4);
                PdfPCell columnaspade, filaspade;
                String registros6[] = {"Corazon", "Artritis", "Tuberculosis", "F.Reumatoide"};
                String celdaspa[] = new String[6];

                //Columnas
                for (int i = 0; i < 4; i++) {
                    columnaspade = new PdfPCell(new Phrase(registros6[i], fuentecolumna));
                    columnaspade.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnaspade.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnaspade.setBackgroundColor(fondo);
                    tabla_padecimientos1.addCell(columnaspade);
                }
                tabla_padecimientos1.setHeaderRows(1);

                celdaspa[0] = CORAZON == 0 ? "NO" : "SI";
                celdaspa[1] = ARTRITIS == 0 ? "NO" : "SI";
                celdaspa[2] = TUBERCULOSIS == 0 ? "NO" : "SI";
                celdaspa[3] = FIEBREREU == 0 ? "NO" : "SI";

                //Relleno de las filas
                for (int row = 0; row < 1; row++) {
                    for (int column = 0; column < 4; column++) {
                        filaspade = new PdfPCell(new Phrase(celdaspa[column], fuentedatos));
                        filaspade.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla_padecimientos1.addCell(filaspade);
                    }
                }
                tabla_padecimientos1.setSpacingBefore(15);
                documento.add(tabla_padecimientos1);

                //Tabla Historial Medico Padecimientos #2 ==========================================
                PdfPTable tabla_padecimientos2 = new PdfPTable(5);
                PdfPCell columnaspade2, filaspade2;
                String registros7[] = {"P.Alta", "P.Baja", "Diabetes", "Anemia", "Epilepsia"};
                String celdaspa2[] = new String[6];

                //Columnas
                for (int i = 0; i < 5; i++) {
                    columnaspade2 = new PdfPCell(new Phrase(registros7[i], fuentecolumna));
                    columnaspade2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnaspade2.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnaspade2.setBackgroundColor(fondo);
                    tabla_padecimientos2.addCell(columnaspade2);
                }
                tabla_padecimientos2.setHeaderRows(1);

                celdaspa2[0] = PRESION_ALTA == 0 ? "NO" : "SI";
                celdaspa2[1] = PRESION_BAJA == 0 ? "NO" : "SI";
                celdaspa2[2] = DIABETES == 0 ? "NO" : "SI";
                celdaspa2[3] = ANEMIA == 0 ? "NO" : "SI";
                celdaspa2[4] = EPILEPSIA == 0 ? "NO" : "SI";

                //Relleno de las filas
                for (int row = 0; row < 1; row++) {
                    for (int column = 0; column < 5; column++) {
                        filaspade2 = new PdfPCell(new Phrase(celdaspa2[column], fuentedatos));
                        filaspade2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla_padecimientos2.addCell(filaspade2);
                    }
                }
                tabla_padecimientos2.setSpacingBefore(15);
                documento.add(tabla_padecimientos2);

                Paragraph titulo5;

                if (!DESCRIPCION_OTROS.equals("")) {
                    titulo5 = new Paragraph("Otros: " + DESCRIPCION_OTROS, fuentedatos);
                } else {
                    titulo5 = new Paragraph("             Otros: -", fuentedatos);
                }
                tabla_padecimientos2.setSpacingBefore(5);
                documento.add(titulo5);


                //GENERACION DEL DOCUMENTO =========================================================
                documento.close();

                LectorPDF lectorPDF = new LectorPDF(pdf);
                lectorPDF.display(FRAGMENT_MANAGER);


            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }, 1000);

    }

    private void obtenerFicha(int ID_FICHA) {
        QuerysFichas querysFichas = new QuerysFichas(CONTEXT);
        querysFichas.obtenerFichaEspecifica(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    NOMBRE_PACIENTE = jsonObject.getString("NOMBRE");
                    EDAD_PACIENTE = jsonObject.getString("EDAD_PACIENTE");
                    CODIGO_FICHA = jsonObject.getString("CODIGO_INTERNO");
                } catch (JSONException e) {
//                    Toast.makeText(CONTEXT, e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                obtenerFicha(ID_FICHA);
            }
        });
    }

    private void obtenerHistorialMedico(int ID_FICHA) {
        QuerysFichas querysFichas = new QuerysFichas(CONTEXT);
        querysFichas.obtenerHistorialMedico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    HOSPITALIZADO = jsonObject.getInt("HOSPITALIZADO");
                    ALERGIA = jsonObject.getInt("ALERGIA");
                    TRATAMIENTO = jsonObject.getInt("TRATAMIENTO_MEDICO");
                    HEMORRAGIA = jsonObject.getInt("HEMORRAGIA");
                    MEDICAMENTO = jsonObject.getInt("MEDICAMENTO");
                    ID_HISTORIAL_MEDICO = jsonObject.getInt("ID_HISTORIAL_MEDICO");

                    obtenerPadecimientos(ID_HISTORIAL_MEDICO);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                obtenerHistorialMedico(ID_FICHA);
            }
        });
    }

    private void obtenerPadecimientos(int ID_HISTORIALM) {
        QuerysFichas querysFichas = new QuerysFichas(CONTEXT);
        querysFichas.obtenerPadecimientosHM(ID_HISTORIALM, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    CORAZON = jsonObject.getInt("CORAZON");
                    ARTRITIS = jsonObject.getInt("ARTRITIS");
                    TUBERCULOSIS = jsonObject.getInt("TUBERCULOSIS");
                    PRESION_ALTA = jsonObject.getInt("PRESION_ALTA");
                    PRESION_BAJA = jsonObject.getInt("PRESION_BAJA");
                    FIEBREREU = jsonObject.getInt("FIEBREREU");
                    ANEMIA = jsonObject.getInt("ANEMIA");
                    EPILEPSIA = jsonObject.getInt("EPILEPSIA");
                    DIABETES = jsonObject.getInt("DIABETES");
                    DESCRIPCION_OTROS = jsonObject.getString("OTROS");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                obtenerPadecimientos(ID_HISTORIAL_MEDICO);
            }
        });
    }

}
