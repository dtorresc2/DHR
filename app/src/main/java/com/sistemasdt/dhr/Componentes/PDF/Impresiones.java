package com.sistemasdt.dhr.Componentes.PDF;


import android.app.ProgressDialog;
import android.content.Context;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import com.sistemasdt.dhr.R;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.HistorialOdonto.ItemTratamiento;
import com.sistemasdt.dhr.Rutas.Fichas.FichaNormal.PagosFicha.ItemPago;
import com.sistemasdt.dhr.ServiciosAPI.QuerysFichas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Impresiones {
    private Context CONTEXT;
    private FragmentManager FRAGMENT_MANAGER;
    private ArrayList<String> listaPacientes = null;

    private static final String TAG = "PDF_VIEW";


    private String NOMBRE_PACIENTE;
    private String EDAD_PACIENTE;
    private String CODIGO_FICHA;
    private double TRATAMIENTOS = 0;
    private double PAGOS = 0;
    private double SALDO = 0;

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

    private int ID_HISTORIAL_ODONTO = 0;
    private ArrayList<String[]> listadoTratamientos;

    private ArrayList<String> listadoPagos;

    public Impresiones(Context context, FragmentManager fragmentManager) {
        CONTEXT = context;
        FRAGMENT_MANAGER = fragmentManager;

        listadoPagos = new ArrayList<>();
        listadoTratamientos = new ArrayList<>();
    }

    public void generarFichaNormal(int ID_FICHA) {
        final ProgressDialog progressDialog = new ProgressDialog(CONTEXT, R.style.progressDialog);
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        File carpetaPlantillas = new File(CONTEXT.getExternalFilesDir(null).toString(), "Plantillas");

        File plantilla1 = new File(carpetaPlantillas, "portada_ficha.pdf");
        File plantilla2 = new File(carpetaPlantillas, "cuerpo_ficha.pdf");

        new Thread(() -> {
            obtenerPlantillas(CONTEXT.getString(R.string.S3) + "Plantillas/FichaNormal/portada_ficha.pdf", plantilla1);
            obtenerPlantillas(CONTEXT.getString(R.string.S3) + "Plantillas/FichaNormal/cuerpo_ficha.pdf", plantilla2);
        }).start();

        obtenerFicha(ID_FICHA);
        obtenerHistorialMedico(ID_FICHA);
        obtenerTratamientos(ID_FICHA);
        obtenerPagos(ID_FICHA);

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

                //Historial Odontodologico =========================================================
                Paragraph titulo9 = new Paragraph("Historial Odontodologico", fuentecorrelativo);
                titulo9.setSpacingBefore(15);
                documento.add(titulo9);

                Paragraph paragraph = new Paragraph("Balance de Ficha", fuentedatos);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingBefore(10);
                documento.add(paragraph);

                //TABLA BALANCE DE FICHA
                PdfPTable tabla_balance = new PdfPTable(3);
                PdfPCell columnasbal, filasbal;
                String registros4[] = {"Tratamientos", "Pagos", "Balance"};

                String celdasreg[] = {
                        String.format("%.2f", TRATAMIENTOS),
                        String.format("%.2f", PAGOS),
                        String.format("%.2f", SALDO)
                };

                //Columnas
                for (int i = 0; i < 3; i++) {
                    columnasbal = new PdfPCell(new Phrase(registros4[i], fuentecolumna));
                    columnasbal.setHorizontalAlignment(Element.ALIGN_CENTER);
                    columnasbal.setVerticalAlignment(Element.ALIGN_CENTER);
                    columnasbal.setBackgroundColor(fondo);
                    tabla_balance.addCell(columnasbal);
                }
                tabla_balance.setHeaderRows(1);

                //Relleno de las filas
                for (int row = 0; row < 1; row++) {
                    for (int column = 0; column < 3; column++) {
                        filasbal = new PdfPCell(new Phrase(celdasreg[column], fuentedatos));
                        filasbal.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tabla_balance.addCell(filasbal);
                    }
                }
                tabla_balance.setSpacingBefore(15);
                documento.add(tabla_balance);

                paragraph = new Paragraph("Tratamientos", fuentedatos);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingBefore(10);
                documento.add(paragraph);

                // TABLA DE TRATAMIENTOS
                PdfPTable TABLA_TRATAMIENTOS = new PdfPTable(3);
                PdfPCell COLUMNA_TRATAMIENTO;
                String registros2[] = {"Pieza", "Tratamiento", "Monto"};

                //Columnas
                for (int i = 0; i < registros2.length; i++) {
                    COLUMNA_TRATAMIENTO = new PdfPCell(new Phrase(registros2[i], fuentecolumna));
                    COLUMNA_TRATAMIENTO.setHorizontalAlignment(Element.ALIGN_CENTER);
                    COLUMNA_TRATAMIENTO.setVerticalAlignment(Element.ALIGN_CENTER);
                    COLUMNA_TRATAMIENTO.setBackgroundColor(fondo);
                    TABLA_TRATAMIENTOS.addCell(COLUMNA_TRATAMIENTO);
                }

                TABLA_TRATAMIENTOS.setSpacingBefore(15);
                documento.add(TABLA_TRATAMIENTOS);

                PdfPTable TABLA_TRATAMIENTOS_RELLENO = new PdfPTable(3);
                PdfPCell FILA_TRATAMIENTO;

                //Relleno de las filas
                for (int row = 0; row < listadoTratamientos.size(); row++) {
                    for (int column = 0; column < registros2.length; column++) {
                        FILA_TRATAMIENTO = new PdfPCell(new Phrase(listadoTratamientos.get(row)[column], fuentedatos));
                        FILA_TRATAMIENTO.setHorizontalAlignment(Element.ALIGN_CENTER);
                        TABLA_TRATAMIENTOS_RELLENO.addCell(FILA_TRATAMIENTO);
                    }
                }

                documento.add(TABLA_TRATAMIENTOS_RELLENO);

                paragraph = new Paragraph("Pagos", fuentedatos);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingBefore(10);
                documento.add(paragraph);

                // TABLA DE PAGOS DE FICHA
                PdfPTable TABLA_PAGOS = new PdfPTable(2);
                PdfPCell COLUMNA_PAGO;
                String columnasAux[] = {"Descripcion", "Monto"};

                //Columnas
                for (int i = 0; i < columnasAux.length; i++) {
                    COLUMNA_PAGO = new PdfPCell(new Phrase(columnasAux[i], fuentecolumna));
                    COLUMNA_PAGO.setHorizontalAlignment(Element.ALIGN_CENTER);
                    COLUMNA_PAGO.setVerticalAlignment(Element.ALIGN_CENTER);
                    COLUMNA_PAGO.setBackgroundColor(fondo);
                    TABLA_PAGOS.addCell(COLUMNA_PAGO);
                }

                TABLA_PAGOS.setSpacingBefore(15);
                documento.add(TABLA_PAGOS);

                PdfPTable TABLA_PAGOS_RELLENO = new PdfPTable(2);
                PdfPCell FILA_PAGOS;

                //Relleno de las filas
                for (int row = 0; row < listadoPagos.size(); row++) {
                    String[] cadena = listadoPagos.get(row).split(";");

                    for (int column = 0; column < columnasAux.length; column++) {
                        FILA_PAGOS = new PdfPCell(new Phrase(cadena[column], fuentedatos));
                        FILA_PAGOS.setHorizontalAlignment(Element.ALIGN_CENTER);
                        TABLA_PAGOS_RELLENO.addCell(FILA_PAGOS);
                    }
                }
                documento.add(TABLA_PAGOS_RELLENO);

                //GENERACION DEL DOCUMENTO =========================================================
                documento.close();

                File pdfInfo = new File(folder, "Prueba.pdf");

                PdfReader reader = new PdfReader(plantilla1.getAbsolutePath());
                PdfReader reader2 = new PdfReader(plantilla2.getAbsolutePath());
                PdfReader info = new PdfReader(pdfInfo.getAbsolutePath());

                //Estampando portada
                File pdf2 = new File(folder, "Final.pdf");
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pdf2));

                if (info.getNumberOfPages() > 1) {
                    //Estampando el resto de hojas
                    for (int i = 1; i < info.getNumberOfPages() + 1; i++) {
                        PdfImportedPage page = stamper.getImportedPage(reader2, 1);
                        stamper.insertPage(i + 1, reader2.getPageSize(1));
                        stamper.getUnderContent(i + 1).addTemplate(page, 0, 0);
                    }
                }

                stamper.close();
                reader.close();
                reader2.close();

                File pdf3 = new File(folder, "Final.pdf");
                PdfReader reader3 = new PdfReader(pdf3.getAbsolutePath());

                File pdfFinal = new File(folder, "FichaNormal.pdf");
                PdfStamper estampador_info = new PdfStamper(reader3, new FileOutputStream(pdfFinal));

                //Estampando informacion
                for (int i = 1; i < info.getNumberOfPages() + 1; i++) {
                    PdfImportedPage page = estampador_info.getImportedPage(info, i);
                    estampador_info.getOverContent(i).addTemplate(page, 0, 0);
                }

                estampador_info.close();
                reader3.close();
                info.close();

                pdf.delete();
                pdf2.delete();

                progressDialog.dismiss();

                if (pdfFinal.exists()) {
                    LectorPDF lectorPDF = new LectorPDF(pdfFinal);
                    lectorPDF.display(FRAGMENT_MANAGER);
                }

            } catch (DocumentException | IOException e) {
                Toast.makeText(CONTEXT, e.toString(), Toast.LENGTH_LONG).show();
            }
        }, 1500);

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

                    TRATAMIENTOS = jsonObject.getDouble("DEBE");
                    PAGOS = jsonObject.getDouble("HABER");
                    SALDO = jsonObject.getDouble("SALDO");
                } catch (JSONException e) {
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

    private void obtenerTratamientos(int ID_FICHA) {
        QuerysFichas querysFichas = new QuerysFichas(CONTEXT);
        querysFichas.obtenerHistorialOdontodologico(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    ID_HISTORIAL_ODONTO = jsonObject.getInt("ID_HISTORIAL_ODONTO");

                    QuerysFichas querysFichas1 = new QuerysFichas(CONTEXT);
                    querysFichas1.obtenerTratamientos(ID_HISTORIAL_ODONTO, new QuerysFichas.VolleyOnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            try {
                                JSONArray jsonArray = new JSONArray(object.toString());
                                listadoTratamientos.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    listadoTratamientos.add(new String[]{
                                            jsonArray.getJSONObject(i).getString("NOMBRE_PIEZA"),
                                            jsonArray.getJSONObject(i).getString("PLAN"),
                                            String.format("%.2f", jsonArray.getJSONObject(i).getDouble("COSTO"))
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            obtenerTratamientos(ID_HISTORIAL_ODONTO);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                obtenerTratamientos(ID_FICHA);
            }
        });
    }

    private void obtenerPagos(int ID_FICHA) {
        QuerysFichas querysFichas = new QuerysFichas(CONTEXT);
        querysFichas.obtenerPagos(ID_FICHA, new QuerysFichas.VolleyOnEventListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONArray jsonArray = new JSONArray(object.toString());
                    listadoPagos.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listadoPagos.add(
                                jsonArray.getJSONObject(i).getString("DESCRIPCION") + ";" +
                                        String.format("%.2f", jsonArray.getJSONObject(i).getDouble("PAGO")
                                        ) + ";"
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                obtenerPagos(ID_FICHA);
            }
        });
    }

    private void CopyRawToSDCard(int id, String path) {
        InputStream in = CONTEXT.getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();
            Log.i(TAG, "copyFile, success!");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile FileNotFoundException " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "copyFile IOException " + e.getMessage());
        }
    }

    private void obtenerPlantillas(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Toast.makeText(CONTEXT, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
