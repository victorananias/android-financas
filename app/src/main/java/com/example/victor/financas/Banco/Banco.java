package com.example.victor.financas.Banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Banco extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "banco.db";
    private static final int VERSAO = 12;

    public static final String TABELA = "valores";
    public static final String ID = "_id";
    public static final String VALOR = "valor";
    public static final String DESCRICAO = "descricao";
    public static final String TIPO = "tipo";
    public static final String DATA = "data";

    public Banco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        String sql = "CREATE TABLE "+TABELA+"("
                +ID+" INTEGER PRIMARY KEY, "+VALOR+" DOUBLE, "+
                DESCRICAO+" VARCHAR(100), "+
                TIPO+" VARCHAR(1), "+DATA+" DATE)";
        database.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " +TABELA);
        onCreate(database);
    }
}
