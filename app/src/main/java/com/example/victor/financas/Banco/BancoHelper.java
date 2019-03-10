package com.example.victor.financas.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.victor.financas.Helpers.ConversorCaracteres;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BancoHelper {
    private SQLiteDatabase database;
    private Banco banco;

    //Quando instancia manipula já cria a estrutura do BD
    public BancoHelper(Context context){
        banco = new Banco(context);
    }

    public String inserirDado( double valor, String descricao, String tipo, String data){

        ContentValues valores;
        long resultado;
        //avisa o bd que será escrito
        database = banco.getWritableDatabase();
        //recebendo os valores dentro das colunas da tabela
        valores = new ContentValues();
        valores.put(Banco.VALOR, valor);
        valores.put(Banco.DESCRICAO, descricao);
        valores.put(Banco.TIPO, tipo);
        valores.put(Banco.DATA, data);

        //recebe -1 caso não tenha inserido
        resultado = database.insert(Banco.TABELA, null, valores);

        if(resultado != -1) {
            return "Inserido com sucesso!";
        }
        else {
            return "Erro ao inserir";
        }
    }

    public Cursor buscarDados(String where){
        //responsavel por receber os dados do bd
        Cursor cursor;
        //as colunas que serão apresentadas pelo usuário
        //podem ser inseridas todas as colunas
        String[] campos = {
                Banco.ID,
                Banco.VALOR,
                Banco.DESCRICAO,
                Banco.TIPO,
                Banco.DATA
        };

        where = where == "" ? null : where;
        //avisa o bd que será lido
        database = banco.getReadableDatabase();
        //recebendo as info gerais da busca
        cursor = database.query(Banco.TABELA,
                campos,
                where,
                null,
                null,
                null,
                Banco.DATA
        );
        //se cursor recebeu algum dado
        //move primeiro dado para primeira linha da tabela
        if(cursor!=null){
            cursor.moveToFirst();
        }
        database.close();

        return cursor;
    }


    public void alterarDado(int id, double valor, String descricao, String tipo, String data){

        ContentValues valores;
        String where = Banco.ID+"="+id;

        //avisa o bd que será escrito
        database = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(Banco.VALOR, valor);
        valores.put(Banco.DESCRICAO, descricao);
        valores.put(Banco.TIPO, tipo);
        valores.put(Banco.DATA, data);

        database.update(Banco.TABELA, valores ,where,null);
        database.close();
    }

    public void deletarDado(int id) {
        String where = Banco.ID+"="+id;

        Cursor cursor = buscarDados(where);
        int indexValor = cursor.getColumnIndexOrThrow(Banco.VALOR);
        int indexTipo  = cursor.getColumnIndexOrThrow(Banco.TIPO);
        Double valor = Double.parseDouble(cursor.getString(indexValor));
        String tipo = cursor.getString(indexTipo);

//        se o tipo for pago será recebido e vice versa
        tipo = tipo.equals("P") ? "R" : "P";
        atualizarSaldo(valor, tipo);

        database = banco.getReadableDatabase();
        database.delete(Banco.TABELA, where, null);
        database.close();
    }

    public void atualizarSaldo(double valor, String tipo) {
        double saldo = Double.parseDouble(getSaldo());

        if(tipo.equals("P")) {
            saldo += -(valor);
        }
        else {
            saldo += valor;
        }

        alterarDado(1, saldo,"Saldo","S", getData());
    }

    public void atualizarSaldo(double valor, String tipo, double valorAnterior, String tipoAnterior) {
        double saldo = Double.parseDouble(getSaldo());

        if(tipoAnterior.equals("P")) {
            saldo += valorAnterior;
        }
        else {
            saldo += -(valorAnterior);
        }

        if(tipo.equals("P")) {
            saldo += -(valor);
        }
        else {
            saldo += valor;
        }

        alterarDado(1, saldo,"Saldo","S", getData());
    }


    public String getSaldo() {
        Cursor cursorSaldo = buscarDados("tipo = 'S'");

        if(cursorSaldo.moveToFirst()) {
            ConversorCaracteres conversor = new ConversorCaracteres();
            int indexValor = cursorSaldo.getColumnIndexOrThrow(Banco.VALOR);
            return cursorSaldo.getString(indexValor);
        }
        else{
            inserirDado(0,"Saldo","S", getData());
            return "0";
        }
    }

    public void limparDados(String tabela) {
        database = banco.getReadableDatabase();

        database.execSQL("DROP TABLE "+tabela);

        String sql = "CREATE TABLE "+Banco.TABELA+"("
                +Banco.ID+" INTEGER PRIMARY KEY, "+Banco.VALOR+" DOUBLE, "+
                Banco.DESCRICAO+" VARCHAR(100), "+
                Banco.TIPO+" VARCHAR(1), "+Banco.DATA+" DATE)";

        database.execSQL(sql);
        database.close();
    }

    private String getData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

