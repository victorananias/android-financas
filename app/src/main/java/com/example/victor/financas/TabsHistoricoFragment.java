package com.example.victor.financas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.victor.financas.Banco.Banco;
import com.example.victor.financas.Banco.BancoHelper;
import com.example.victor.financas.Helpers.ConversorCaracteres;

public class TabsHistoricoFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private final static String BUNDLE_ID="id";
    private int nTab;
    ListView listViewDados;
    View view;
    TextView textSoma;

    public static TabsHistoricoFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabsHistoricoFragment fragment = new TabsHistoricoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nTab = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tab, container, false);
        carregarDados(nTab);
        return view;
    }

    void carregarDados(int tab) {
        Cursor cursor = tab == 0 ? getCursorRecebido() : getCursorPago();
        carregarListViewDados(cursor);
        carregarTextSoma(cursor);
    }

    Cursor getCursorPago() {
        BancoHelper bancoHelper = new BancoHelper(view.getContext());
        return bancoHelper.buscarDados("tipo='P'");
    }

    Cursor getCursorRecebido() {
        BancoHelper bancoHelper = new BancoHelper(view.getContext());
        return bancoHelper.buscarDados("tipo='R'");
    }

    public void carregarListViewDados(Cursor cursor) {

        listViewDados = view.findViewById(R.id.listview_dados);

        String[] nomeCampos = new String[]{
                Banco.VALOR,
                Banco.DESCRICAO,
                Banco.DATA
        };

        int[] idView = new int[]{
                R.id.text_valor,
                R.id.text_descricao,
                R.id.text_data
        };

        CustomAdapter adapter = new CustomAdapter(
                view.getContext(),
                cursor,
                nomeCampos,
                idView,
                0);

        listViewDados.setAdapter(adapter);

        registerForContextMenu(listViewDados);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.itens, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();

        Cursor cursor = MainActivity.TAB == 0 ? getCursorRecebido() : getCursorPago();
        cursor.moveToPosition(menuInfo.position);
        String idItem = cursor.getString(cursor.getColumnIndexOrThrow(Banco.ID));
        Intent intent;

        switch (item.getItemId()){
            case R.id.menu_editar:
                intent = new Intent(view.getContext(), FormularioActivity.class);
                intent.putExtra(BUNDLE_ID, idItem);
                startActivity(intent);
                return true;

            case R.id.menu_excluir:
                BancoHelper bancoHelper = new BancoHelper(view.getContext());
                bancoHelper.deletarDado(Integer.parseInt(idItem));

                intent = new Intent(view.getContext(), MainActivity.class);
                MainActivity.ACTIVITY.finish();
                startActivity(intent);

                return true;
        }
        return false;
    }

    private void carregarTextSoma(Cursor cursor) {
        textSoma = view.findViewById(R.id.text_soma);

        String soma = ConversorCaracteres.addMascaraMonetaria(""+getSomaCursor(cursor));

        if(nTab == 0) {
            textSoma.setText("Recebido: " + soma);
        }
        else {
            textSoma.setText("Pago: " + soma);
        }
    }

    double getSomaCursor(Cursor cursor) {
        double soma = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int indexValor = cursor.getColumnIndexOrThrow(Banco.VALOR);
            soma += Double.parseDouble(cursor.getString(indexValor));
        }
        return soma;
    }
}