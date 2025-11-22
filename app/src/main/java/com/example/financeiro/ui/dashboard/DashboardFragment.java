package com.example.financeiro.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.financeiro.DAO.FinanceDAO;
import com.example.financeiro.Database.FinanceDatabase;
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    private TabLayout tabLayoutDashboard;
    private TextView textViewSaldo, textViewTotalDividas, textViewTotalFaturas, textViewTituloResumo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        // Referências da UI
        tabLayoutDashboard = view.findViewById(R.id.tabLayoutDashboard);
        textViewSaldo = view.findViewById(R.id.textViewSaldoAtual);
        textViewTotalDividas = view.findViewById(R.id.textViewTotalDividas);
        textViewTotalFaturas = view.findViewById(R.id.textViewTotalFaturas);
        textViewTituloResumo = view.findViewById(R.id.textViewTituloResumo);

        // Configurar as abas e selecionar o mês atual
        configurarFiltrosDeTempo();

        // Listener para quando o usuário clicar em uma aba
        tabLayoutDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filtro = (String) tab.getTag(); // Ex: "%/11/2025" ou "%/2025"
                String texto = tab.getText().toString();

                textViewTituloResumo.setText("Resumo: " + texto);
                calcularResumo(filtro);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }

    private void configurarFiltrosDeTempo() {
        tabLayoutDashboard.removeAllTabs();

        Calendar cal = Calendar.getInstance();
        int anoAtual = cal.get(Calendar.YEAR);
        int mesAtualIndex = cal.get(Calendar.MONTH); // Jan = 0, Fev = 1... Nov = 10

        // 1. CRIAR ABA "TOTAL ANO" (Primeira posição)
        TabLayout.Tab tabAno = tabLayoutDashboard.newTab();
        tabAno.setText("TOTAL " + anoAtual);
        tabAno.setTag("%/" + anoAtual); // Busca tudo que tenha o ano (ex: %/2025)
        tabLayoutDashboard.addTab(tabAno);

        // 2. CRIAR ABAS DOS MESES (Jan a Dez)
        String[] meses = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};

        for (int i = 0; i < 12; i++) {
            TabLayout.Tab tabMes = tabLayoutDashboard.newTab();
            tabMes.setText(meses[i]);

            // Cria a Tag de busca: "%/01/2025", "%/02/2025"...
            // O 'i + 1' converte o índice 0 em mês 1
            String tag = String.format(Locale.getDefault(), "%%/%02d/%d", (i + 1), anoAtual);
            tabMes.setTag(tag);

            tabLayoutDashboard.addTab(tabMes);

            // LÓGICA DO DEFAULT:
            // Se o índice do loop (i) for igual ao mês atual do sistema, seleciona essa aba.
            if (i == mesAtualIndex) {
                tabMes.select();
            }
        }
    }

    private void calcularResumo(String filtroDataLike) {
        new Thread(() -> {
            // 1. Calcular Saldo (Receitas - Despesas - Faturas Pagas) usando o FILTRO da aba
            Double receitas = financeDAO.getReceitasPorMes(filtroDataLike);
            Double despesas = financeDAO.getDespesasPorMes(filtroDataLike);
            Double faturasPagas = financeDAO.getFaturasPagasPorMes(filtroDataLike);

            // Tratar valores nulos
            if (receitas == null) receitas = 0.0;
            if (despesas == null) despesas = 0.0;
            if (faturasPagas == null) faturasPagas = 0.0;

            double saldo = receitas - despesas - faturasPagas;

            // 2. Faturas em Aberto (Compromissos do período selecionado)
            Double faturasAbertas = financeDAO.getFaturasAbertasPorMes(filtroDataLike);
            if (faturasAbertas == null) faturasAbertas = 0.0;

            // 3. Dívidas Pendentes (Sempre mostra o total geral que falta pagar na vida)
            List<DividaParcelada> dividas = financeDAO.selectDividasPendentes();
            double totalDividasPendentes = 0.0;
            for (DividaParcelada d : dividas) {
                double valorParcela = d.getValorTotal() / d.getNumeroParcelasTotal();
                int parcelasRestantes = d.getNumeroParcelasTotal() - d.getParcelasPagas();
                totalDividasPendentes += (valorParcela * parcelasRestantes);
            }

            // Atualizar a Tela na Thread Principal
            double finalFaturasAbertas = faturasAbertas;
            double finalDividas = totalDividasPendentes;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    textViewSaldo.setText(String.format("R$ %.2f", saldo));

                    // Cor do saldo: Verde (positivo) ou Vermelho (negativo)
                    if (saldo >= 0) {
                        textViewSaldo.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        textViewSaldo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }

                    textViewTotalFaturas.setText(String.format("R$ %.2f", finalFaturasAbertas));
                    textViewTotalDividas.setText(String.format("R$ %.2f", finalDividas));
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarrega os dados da aba que estiver selecionada no momento
        if (tabLayoutDashboard.getTabCount() > 0) {
            TabLayout.Tab abaSelecionada = tabLayoutDashboard.getTabAt(tabLayoutDashboard.getSelectedTabPosition());
            if (abaSelecionada != null) {
                String tag = (String) abaSelecionada.getTag();
                calcularResumo(tag);
            }
        }
    }
}