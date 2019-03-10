package com.example.victor.financas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.victor.financas.Banco.Banco;
import com.example.victor.financas.Banco.BancoHelper;
import com.example.victor.financas.Helpers.ConversorCaracteres;

public class MainActivity extends AppCompatActivity {
    public static Activity ACTIVITY;
    public static int TAB;

    final static String BUNDLE_ID="id";
    Toolbar toolbar;
    TextView textSaldo;
    FloatingActionButton botaoAdd;


    public void carregarToolbar() {
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
    }


    public void carregarSaldo() {
        BancoHelper bancoHelper = new BancoHelper(this);

        textSaldo = findViewById(R.id.text_saldo);

        String saldo = ConversorCaracteres.addMascaraMonetaria(bancoHelper.getSaldo());
        if(saldo.substring(0,1).equals("-")) {
            textSaldo.setTextColor(getResources().getColor(R.color.colorWarn));
        }
        else {
            textSaldo.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        textSaldo.setText("Saldo: " + saldo);
    }


    public void carregarBotaoAdd() {
        botaoAdd = (FloatingActionButton) findViewById(R.id.botao_add);
        botaoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                intent.putExtra(BUNDLE_ID, "-1");
                startActivity(intent);
            }
        });
    }


    private void carregarTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        final TabsFragmentPagerAdapter tabsAdapter = new TabsFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TAB = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAB = 0;
        ACTIVITY = this;
        
        carregarToolbar();
        carregarTabs();
        carregarBotaoAdd();
        carregarSaldo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_limpar:
                carregarAlerta();
                return true;
        }
        return false;
    }

    public void carregarAlerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Limpar Dados?").setPositiveButton(
                "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BancoHelper bancoHelper = new BancoHelper(MainActivity.this);
                        bancoHelper.limparDados(Banco.TABELA);
                        carregarTabs();
                        carregarSaldo();
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        builder.create().show();

    }

}
