package com.example.financeiro.ui.cartoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.financeiro.DAO.FinanceDAO;
import com.example.financeiro.Database.FinanceDatabase;
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * Este Fragmento controla a tela R.layout.fragment_cartoes
 */
public class CartoesFragment extends Fragment {

    // Referências do Banco de Dados
    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Referências da UI
    private TabLayout tabLayoutCartoes;
    private RecyclerView recyclerViewFaturas;
    private FloatingActionButton fabAddFatura;

    // private FaturaCartaoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cartoes, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db")
                .build();
        financeDAO = db.financeDAO();

        tabLayoutCartoes = view.findViewById(R.id.tabLayoutCartoes);
        recyclerViewFaturas = view.findViewById(R.id.recyclerViewFaturas);
        fabAddFatura = view.findViewById(R.id.fabAddFatura);

        // Configurar RecyclerView
        // ...

        fabAddFatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir Dialog/Activity para adicionar nova Fatura
                Toast.makeText(getContext(), "Clicou em Adicionar Fatura", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayoutCartoes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String nomeCartao = tab.getText().toString();
                buscarFaturasDoCartao(nomeCartao);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }

    private void configurarTabsCartoes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Busca todos os nomes de cartões únicos do banco
                List<String> nomesCartoes = financeDAO.selectAllNomesCartoes();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tabLayoutCartoes.removeAllTabs();
                            if (nomesCartoes.isEmpty()) {
                                // Adiciona cartões de exemplo se estiver vazio
                                tabLayoutCartoes.addTab(tabLayoutCartoes.newTab().setText("PicPay"));
                                tabLayoutCartoes.addTab(tabLayoutCartoes.newTab().setText("Banco do Brasil"));
                            } else {
                                for (String nome : nomesCartoes) {
                                    tabLayoutCartoes.addTab(tabLayoutCartoes.newTab().setText(nome));
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void buscarFaturasDoCartao(String nomeCartao) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<GastoCartao> listaFaturas = financeDAO.selectGastosPorCartao(nomeCartao);

                // ... Lógica para atualizar o adapter do RecyclerView ...
                // Esta lógica precisa ser mais complexa para adicionar os
                // cabeçalhos de mês (Ex: "Novembro 2025")

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // adapter.submitList(listaFaturasComHeaders);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarrega os dados toda vez que o usuário voltar para esta tela
        configurarTabsCartoes();
    }
}