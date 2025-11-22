package com.example.financeiro.ui.lancamentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.financeiro.DAO.FinanceDAO;
import com.example.financeiro.Database.FinanceDatabase;
import com.example.financeiro.Entity.LancamentoVariavel;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LancamentosFragment extends Fragment {

    private FinanceDatabase db;
    private FinanceDAO financeDAO;
    private TabLayout tabLayoutMeses;
    private RecyclerView recyclerViewLancamentos;
    private FloatingActionButton fabAddLancamento;

    private LancamentosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lancamentos, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        tabLayoutMeses = view.findViewById(R.id.tabLayoutMeses);
        recyclerViewLancamentos = view.findViewById(R.id.recyclerViewLancamentos);
        fabAddLancamento = view.findViewById(R.id.fabAddLancamento);

        // Configura RecyclerView
        recyclerViewLancamentos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LancamentosAdapter();

        // Configura o clique longo (Menu de Opções)
        adapter.setOnItemLongClickListener(new LancamentosAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(LancamentoVariavel lancamento) {
                mostrarOpcoes(lancamento);
            }
        });

        recyclerViewLancamentos.setAdapter(adapter);

        // Configura Abas (Agora com o Ano!)
        configurarTabsMeses();

        // Botão Adicionar
        fabAddLancamento.setOnClickListener(v -> {
            AddLancamentoDialogFragment dialog = new AddLancamentoDialogFragment();
            dialog.show(getParentFragmentManager(), "AddLancamentoDialog");
        });

        // Listener de Mudança de Aba
        tabLayoutMeses.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String mesAno = (String) tab.getTag();
                buscarLancamentosDoMes(mesAno);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }

    private void mostrarOpcoes(LancamentoVariavel lancamento) {
        String[] opcoes = {"Editar", "Excluir"};

        new AlertDialog.Builder(getContext())
                .setTitle("Opções: " + lancamento.getDescricao())
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) { // Editar
                        editarLancamento(lancamento);
                    } else { // Excluir
                        excluirLancamento(lancamento);
                    }
                })
                .show();
    }

    private void editarLancamento(LancamentoVariavel lancamento) {
        AddLancamentoDialogFragment dialog = new AddLancamentoDialogFragment();
        dialog.setLancamentoEditar(lancamento);
        dialog.show(getParentFragmentManager(), "EditLancamentoDialog");
    }

    private void excluirLancamento(LancamentoVariavel lancamento) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar")
                .setMessage("Excluir este lançamento?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    new Thread(() -> {
                        financeDAO.deleteLancamentoVariavel(lancamento);
                        atualizarLista();
                    }).start();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void atualizarLista() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Pega a tag da aba selecionada atualmente
                String tag = (String) tabLayoutMeses.getTabAt(tabLayoutMeses.getSelectedTabPosition()).getTag();
                buscarLancamentosDoMes(tag);
            });
        }
    }

    private void configurarTabsMeses() {
        tabLayoutMeses.removeAllTabs();

        Calendar cal = Calendar.getInstance();

        // 1. Volta 3 meses no tempo a partir de hoje
        cal.add(Calendar.MONTH, -3);

        SimpleDateFormat formatoTitulo = new SimpleDateFormat("MMM/yy", new Locale("pt", "BR"));

        // 2. Gera 10 abas no total:
        //    3 passadas + 1 atual + 3 futuras = 7 abas
        for (int i = 0; i < 7; i++) {
            String titulo = formatoTitulo.format(cal.getTime()).toUpperCase(); // Ex: MAI/25

            int mes = cal.get(Calendar.MONTH) + 1;
            int ano = cal.get(Calendar.YEAR);
            // Tag para busca no banco
            String tag = String.format(Locale.getDefault(), "/%02d/%d", mes, ano);

            TabLayout.Tab tab = tabLayoutMeses.newTab().setText(titulo).setTag(tag);
            tabLayoutMeses.addTab(tab);

            cal.add(Calendar.MONTH, 1); // Avança 1 mês
        }

        // 3. Seleciona a aba do Mês Atual
        // Como voltamos 3 meses, o mês atual estará na posição (índice) 4.
        TabLayout.Tab abaAtual = tabLayoutMeses.getTabAt(3);
        if (abaAtual != null) {
            abaAtual.select();
            buscarLancamentosDoMes((String) abaAtual.getTag());
        }
    }

    private void buscarLancamentosDoMes(String mesAnoFormatado) {
        new Thread(() -> {
            // Busca no banco (lembrando que a tag é "/11/2025", então add o %)
            List<LancamentoVariavel> lista = financeDAO.selectLancamentosPorMes("%" + mesAnoFormatado);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.submitList(lista);
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Atualiza sempre que a tela aparecer (fecha diálogo, volta de outra aba, etc)
        if (tabLayoutMeses.getTabCount() > 0) {
            String tag = (String) tabLayoutMeses.getTabAt(tabLayoutMeses.getSelectedTabPosition()).getTag();
            buscarLancamentosDoMes(tag);
        }
    }
}