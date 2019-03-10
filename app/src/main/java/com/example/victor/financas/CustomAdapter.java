package com.example.victor.financas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.victor.financas.Banco.Banco;
import com.example.victor.financas.Helpers.ConversorCaracteres;


public class CustomAdapter extends SimpleCursorAdapter {

    TextView textValor;
    TextView textDescricao;
    TextView textData;

    private int layout;
    private final LayoutInflater inflater;


    public CustomAdapter(Context context, Cursor c, String[] from, int[] to, int flags) {
        super(context, R.layout.listview_row, c, from, to, flags);
        this.layout   = R.layout.listview_row;
        this.inflater = LayoutInflater.from(context);
    }

    public void iniciarComponentes( View view) {
        textValor       = (TextView) view.findViewById(R.id.text_valor);
        textDescricao   = (TextView) view.findViewById(R.id.text_descricao);
        textData        = (TextView) view.findViewById(R.id.text_data);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        iniciarComponentes(view);

        int indexValor = cursor.getColumnIndexOrThrow(Banco.VALOR);
        int indexDescricao = cursor.getColumnIndexOrThrow(Banco.DESCRICAO);
        int indexData = cursor.getColumnIndexOrThrow(Banco.DATA);

        String valor = ConversorCaracteres.addMascaraMonetaria(cursor.getString(indexValor));
        String descricao = cursor.getString(indexDescricao);
        String data = "";

        data = ConversorCaracteres.formatarData(cursor.getString(indexData));

        textValor.setText(valor);
        textDescricao.setText(descricao);
        textData.setText(data);
    }
}
