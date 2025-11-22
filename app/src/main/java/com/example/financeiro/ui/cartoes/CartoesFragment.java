package com.example.financeiro.ui.cartoes;

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
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CartoesFragment extends Fragment {

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    private TabLayout tabLayoutCartoes;
    private RecyclerView recyclerViewFaturas;
    private FloatingActionButton fabAddFatura;
    private CartoesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartoes, container, false);

        // Banco
        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        // UI
        tabLayoutCartoes = view.findViewById(R.id.tabLayoutCartoes);
        recyclerViewFaturas = view.findViewById(R.id.recyclerViewFaturas);
        fabAddFatura = view.findViewById(R.id.fabAddFatura);

        // Configurar Lista
        recyclerViewFaturas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartoesAdapter();
        adapter.setOnItemLongClickListener(new CartoesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(GastoCartao fatura) {
                mostrarOpcoesFatura(fatura);
            }
        });
        recyclerViewFaturas.setAdapter(adapter);

        // Configurar Abas de Data
        configurarTabsMeses();

        // Listeners
        tabLayoutCartoes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filtroData = (String) tab.getTag(); // Ex: "%/12/2025"
                buscarFaturas(filtroData);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Clique no Botão Adicionar
        fabAddFatura.setOnClickListener(v -> {
            AddFaturaDialogFragment dialog = new AddFaturaDialogFragment();
            dialog.show(getParentFragmentManager(), "AddFaturaDialog");
        });

        return view;
    }

    private void configurarTabsMeses() {
        tabLayoutCartoes.removeAllTabs();

        Calendar cal = Calendar.getInstance();
        // Vamos gerar abas de 2 meses atrás até 12 meses para frente
        int mesAtual = cal.get(Calendar.MONTH);
        int anoAtual = cal.get(Calendar.YEAR);

        // Ajusta calendario para 2 meses atrás
        cal.add(Calendar.MONTH, -2);

        SimpleDateFormat formatoAba = new SimpleDateFormat("MMM/yy", new Locale("pt", "BR"));

        for (int i = 0; i < 15; i++) { // Total de 15 abas
            String titulo = formatoAba.format(cal.getTime()).toUpperCase(); // "DEZ/25"

            // Tag para busca no banco: "%/MM/yyyy"
            // Nota: Mês no Calendar começa em 0, então somamos 1
            int mes = cal.get(Calendar.MONTH) + 1;
            int ano = cal.get(Calendar.YEAR);
            String tagBusca = String.format(Locale.getDefault(), "%%/%02d/%d", mes, ano);

            TabLayout.Tab tab = tabLayoutCartoes.newTab().setText(titulo).setTag(tagBusca);
            tabLayoutCartoes.addTab(tab);

            // Se for o mês atual (original), seleciona
            if (mes == mesAtual + 1 && ano == anoAtual) {
                tab.select();
            }

            cal.add(Calendar.MONTH, 1); // Avança 1 mês
        }
    }

    private void buscarFaturas(String filtroData) {
        new Thread(() -> {
            // Busca no banco usando LIKE '%/mm/aaaa'
            List<GastoCartao> lista = financeDAO.selectGastosPorMes(filtroData);

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
        if (tabLayoutCartoes.getTabCount() > 0) {
            String tag = (String) tabLayoutCartoes.getTabAt(tabLayoutCartoes.getSelectedTabPosition()).getTag();
            buscarFaturas(tag);
        }
    }

    // Mostra o "Alert Build" com as opções
    private void mostrarOpcoesFatura(GastoCartao fatura) {
        String textoStatus = "Paga".equals(fatura.getStatus()) ? "Marcar como Aberto" : "Marcar como Paga";

        String[] opcoes = {"Editar", textoStatus, "Excluir"};

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Opções da Fatura: " + fatura.getNomeCartao())
                .setItems(opcoes, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            editarFatura(fatura);
                            break;
                        case 1: // Mudar Status
                            alternarStatus(fatura);
                            break;
                        case 2: // Excluir
                            excluirFatura(fatura);
                            break;
                    }
                })
                .show();
    }

    private void editarFatura(GastoCartao fatura) {
        AddFaturaDialogFragment dialog = new AddFaturaDialogFragment();
        dialog.setFaturaParaEditar(fatura); // Passamos o objeto para o diálogo
        dialog.show(getParentFragmentManager(), "EditFaturaDialog");

        // Dica: Para atualizar a lista após editar, precisamos garantir que o onResume ou um callback seja chamado.
        // Como o Dialog é assíncrono, a atualização pode não ser imediata na tela sem um listener extra.
        // O jeito mais simples agora é trocar de aba e voltar, ou implementar um listener de fechamento.
        // Para simplificar, adicione isso no onDismiss do dialog no futuro.
    }

    private void alternarStatus(GastoCartao fatura) {
        if ("Paga".equals(fatura.getStatus())) {
            fatura.setStatus("Aberto");
        } else {
            fatura.setStatus("Paga");
        }

        new Thread(() -> {
            financeDAO.updateGastoCartao(fatura);
            atualizarListaNaThreadPrincipal();
        }).start();
    }

    private void excluirFatura(GastoCartao fatura) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir esta fatura?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    new Thread(() -> {
                        financeDAO.deleteGastoCartao(fatura);
                        atualizarListaNaThreadPrincipal();
                    }).start();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void atualizarListaNaThreadPrincipal() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Recarrega a aba atual
                String tag = (String) tabLayoutCartoes.getTabAt(tabLayoutCartoes.getSelectedTabPosition()).getTag();
                buscarFaturas(tag);
                Toast.makeText(getContext(), "Atualizado!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}