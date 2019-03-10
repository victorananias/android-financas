package com.example.victor.financas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.victor.financas.Banco.Banco;
import com.example.victor.financas.Banco.BancoHelper;
import com.example.victor.financas.Helpers.ConversorCaracteres;
import com.example.victor.financas.Helpers.MascaraMonetaria;

import java.util.Calendar;
import java.util.Locale;

public class FormularioActivity extends AppCompatActivity {

    EditText editTextValor;
    EditText editTextDescricao;
    TextView textDatePicker;
    RadioGroup grupoRadio;
    RadioButton radioPago;
    RadioButton radioRecebido;
    ImageButton botaoSalvar;
    DatePickerDialog.OnDateSetListener datePickerListener;
    Toolbar toolbar;

    Cursor cursor;
    BancoHelper bancoHelper;

    String idItem = "-1";
    String dataConta = "";
    String tipoConta;

    private Double valorAnterior;
    private String tipoAnterior;

//    Pegando a data atual
    Calendar calendario = Calendar.getInstance();
    private int dia = calendario.get(Calendar.DAY_OF_MONTH);
    private int mes = calendario.get(Calendar.MONTH);
    private int ano = calendario.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        iniciarComponentes();
        carregarGrupoRadio();
        radioRecebido.setChecked(true);

        carregarDadosItem();
        carregarbotaoSalvar();
        carregarDatePicker();
        carregarToolbar();

        editTextValor.addTextChangedListener(new MascaraMonetaria(editTextValor));
    }

    public void iniciarComponentes(){
        editTextValor       = (EditText)    findViewById(R.id.edit_text_valor);
        editTextDescricao   = (EditText)    findViewById(R.id.edit_text_descricao);
        textDatePicker      = (TextView)    findViewById(R.id.text_datepicker);
        grupoRadio          = (RadioGroup)  findViewById(R.id.radio_group);
        radioRecebido       = (RadioButton) findViewById(R.id.radio_recebido);
        radioPago           = (RadioButton) findViewById(R.id.radio_pago);
        botaoSalvar         = (ImageButton) findViewById(R.id.botao_salvar);
        toolbar             = (Toolbar)     findViewById(R.id.toolbar);
    }

    public void carregarGrupoRadio() {
        grupoRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton botaoEscolhido = (RadioButton) group.findViewById(checkedId);
                if(botaoEscolhido == radioRecebido) {
                    tipoConta = "R";
                }
                else{
                    tipoConta = "P";
                }
            }
        });
    }

    private void carregarDatePicker() {
        textDatePicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {


                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        FormularioActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePickerListener,
                        ano,mes,dia
                );
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                Faz com que o datepicker pegue o idioma automaticamente
                Locale locale = getResources().getConfiguration().locale;
                Locale.setDefault(locale);

                datePickerDialog.show();
            }
        });

        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                mes += 1; // A contagem de meses inicia em 0
                dataConta = ano+"-"+mes+"-"+dia;
                textDatePicker.setText(dia+"/"+mes+"/"+ano);
                textDatePicker.setTextColor(Color.BLACK);
            }
        };
    }


    public void carregarbotaoSalvar(){
        botaoSalvar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String editValor = editTextValor.getText().toString();

            if(!editValor.equals("")) {
                double valor = ConversorCaracteres.rmMascaraMonetaria(editValor);
                String descricao = editTextDescricao.getText().toString();
                String tipo = tipoConta;
                String data = dataConta;

                bancoHelper = new BancoHelper(getBaseContext());

                String resultado = "Erro";

                if (idItem.equals("-1")) {
                    resultado = bancoHelper.inserirDado(valor, descricao, tipo, data);
                    bancoHelper.atualizarSaldo(valor, tipo);
                } else {


                    bancoHelper.alterarDado(
                            Integer.parseInt(idItem),
                            valor,
                            editTextDescricao.getText().toString(),
                            tipoConta,
                            data
                    );

                    bancoHelper.atualizarSaldo(valor, tipoConta, valorAnterior, tipoAnterior);
                    resultado = "Salvo!";
                }


                Toast.makeText(FormularioActivity.this, resultado, Toast.LENGTH_SHORT).show();

                //            Voltando para a MainActivity
                Intent intent = new Intent(FormularioActivity.this, MainActivity.class);
                MainActivity.ACTIVITY.finish();
                startActivity(intent);
                finish();
            }
            }
        });
    }

    public void carregarToolbar() {
        if(idItem.equals("-1")) {
            toolbar.setTitle("Adicionar");
        }
        else{
            toolbar.setTitle("Editar");
        }
        setSupportActionBar(toolbar);
    }


    private void carregarDadosItem() {
        idItem = getIntent().getStringExtra((MainActivity.BUNDLE_ID));
        if(!idItem.equals("-1")) {
            bancoHelper = new BancoHelper(getBaseContext());
            cursor = bancoHelper.buscarDados(Banco.ID+"="+Integer.parseInt(idItem));


            int indexValor = cursor.getColumnIndexOrThrow(Banco.VALOR);
            int indexDescricao = cursor.getColumnIndexOrThrow(Banco.DESCRICAO);
            int indexData = cursor.getColumnIndexOrThrow(Banco.DATA);
            int indexTipo = cursor.getColumnIndexOrThrow(Banco.TIPO);


            //        Salvando dados anteriores
            valorAnterior = Double.parseDouble(cursor.getString(indexValor));
            tipoAnterior  = cursor.getString(indexTipo);
            dataConta     = cursor.getString(indexData);


            String valor = ConversorCaracteres.addMascaraMonetaria(cursor.getString(indexValor));
            String descricao = cursor.getString(indexDescricao);
            String data = ConversorCaracteres.formatarData(dataConta);

//            Se houver uma data
            if(!dataConta.equals("")) {

                dia  = Integer.parseInt(data.substring(0, 2));
                mes  = Integer.parseInt(data.substring(3, 5))-1; // a contagem do mês começa em 0
                ano  = Integer.parseInt(data.substring(6, 10));

                textDatePicker.setText(data);
                textDatePicker.setTextColor(Color.BLACK);
            }


            editTextDescricao.setText(descricao);
            editTextValor.setText(valor);
            textDatePicker.setText(data);

//          Recebido está marcado por padrão
            if(tipoAnterior.equals("P")) {
                radioPago.setChecked(true);
            }

        }
    }
}
