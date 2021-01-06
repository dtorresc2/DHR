package com.example.dentalhistoryrecorder.Componentes.Tabla;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class TablaDinamica {
    private TableLayout tableLayout;
    private Context context;
    private String[] header;
    private ArrayList<String[]> data;
    private TableRow tableRow;
    private TextView txtCell, aux;
    private int indexR;
    private int indexC;
    private int colorFondo;

    public TablaDinamica(TableLayout tableLayout, Context context) {
        this.tableLayout = tableLayout;
        this.context = context;
    }

    public void addHeader(String[] header) {
        this.header = header;
        createHeader();
    }

    public void addData(ArrayList<String[]> data) {
        this.data = data;
        createDataTable();
    }

    private void newRow() {
        tableRow = new TableRow(context);
    }

    private void newCell() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/bahnschrift.ttf");
        txtCell = new TextView(context);
        txtCell.setGravity(Gravity.CENTER);
        txtCell.setTextSize(14);
        txtCell.setTypeface(face);
    }

    private void createHeader() {
        indexC = 0;
        newRow();
        while (indexC < header.length) {
            newCell();
            txtCell.setText(header[indexC++]);
            tableRow.addView(txtCell, newTableRowParams());
        }
        tableLayout.addView(tableRow);
    }

    private void createDataTable() {
        String info;
        for (indexR = 1; indexR <= data.size(); indexR++) {
            newRow();
            for (indexC = 0; indexC < header.length; indexC++) {
                newCell();
                String[] row = data.get(indexR - 1);
                info = (indexC < row.length) ? row[indexC] : "";
                txtCell.setText(info);
                tableRow.addView(txtCell, newTableRowParams());
            }
            tableLayout.addView(tableRow);
        }
    }

    public void addItem(String[] item) {
        String info;
        data.add(item);
        indexC = 0;
        newRow();
        while (indexC < header.length) {
            newCell();
            info = (indexC < item.length) ? item[indexC++] : "";
            txtCell.setText(info);
            tableRow.addView(txtCell, newTableRowParams());
        }
        tableLayout.addView(tableRow, data.size(), newTableRowParams());
        repintarTabla();
    }

    public void fondoHeader(int color) {
        indexC = 0;
        while (indexC < header.length) {
            txtCell = getCell(0, indexC++);
            txtCell.setBackgroundColor(color);
        }
    }

    public void fondoData(int color) {
        if (data.size() > 0) {
            for (indexR = 1; indexR <= data.size(); indexR++) {
                for (indexC = 0; indexC < header.length; indexC++) {
                    txtCell = getCell(indexR, indexC);
                    txtCell.setBackgroundColor(color);
                }
            }
            this.colorFondo = color;
        }
    }

    public void repintarTabla() {
        indexC = 0;
        while (indexC < header.length) {
            txtCell = getCell(data.size(), indexC++);
            txtCell.setBackgroundColor(colorFondo);
        }
    }

    private TableRow getRow(int index) {
        return (TableRow) tableLayout.getChildAt(index);
    }

    private TextView getCell(int rowIndex, int colIndex) {
        tableRow = getRow(rowIndex);
        return (TextView) tableRow.getChildAt(colIndex);
    }

    private TableRow.LayoutParams newTableRowParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(1, 1, 1, 1);
        params.weight = 1;
        return params;
    }

    public int getCount(){
        return data.size();
    }

    public String getCellData(int rowIndex, int colIndex){
        tableRow = getRow(rowIndex);
        aux = (TextView) tableRow.getChildAt(colIndex);
        return aux.getText().toString();
    }

    public void removeRow(int rowIndex){
        tableLayout.removeViewAt(rowIndex);
        data.remove(rowIndex - 1);
        //repintarTabla();
    }

    public void removeAll(){
        if (data.size() > 0){
            for (int i = 1; i < data.size() + 1; i++){
                tableLayout.removeViewAt(i);
            }
        }
    }
}
