package com.example.victor.financas.Helpers;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversorCaracteres {

    public static String addMascaraMonetaria(String num){
        double numero = Double.parseDouble(num);

//        Locale meuLocal = new Locale( "pt", "BR" );
        NumberFormat formato = NumberFormat.getCurrencyInstance();

        String saida = formato.format(numero);

        if(numero < 0) {
            saida = saida.replace("(","");
            saida = saida.replace(")","");
            saida = "-" + saida;
        }

        return saida;
    }
//
    public static double rmMascaraMonetaria(String num){
        double valor = Double.parseDouble(num.replaceAll("[^\\d]", ""));
        valor = valor/100;
        return valor;
    }

    public static String formatarData(String dat) {
        if(dat.equals("")) {
            return dat;
        }
        else {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date data = new Date();
            try {
                data = formato.parse(dat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formato.applyPattern("dd/MM/yyyy");
            String dataFormatada = formato.format(data);

            return dataFormatada;
        }
    }
}
