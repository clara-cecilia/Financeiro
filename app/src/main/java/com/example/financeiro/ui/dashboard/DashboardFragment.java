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
    private TextView textViewSaldo, textViewTotalDividas, textViewTotalFaturas, textViewTituloResumo, textViewInvestimento;

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
        textViewInvestimento = view.findViewById(R.id.textViewInvestimento); // NOVO

        // Configurar as abas e selecionar o mês atual
        configurarFiltrosDeTempo();

        // Listener para quando o usuário clicar em uma aba
        tabLayoutDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filtro = (String) tab.getTag();
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
        int mesAtualIndex = cal.get(Calendar.MONTH);

        // 1. CRIAR ABA "TOTAL ANO"
        TabLayout.Tab tabAno = tabLayoutDashboard.newTab();
        tabAno.setText("TOTAL " + anoAtual);
        tabAno.setTag("%/" + anoAtual);
        tabLayoutDashboard.addTab(tabAno);

        // 2. CRIAR ABAS DOS MESES
        String[] meses = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};

        for (int i = 0; i < 12; i++) {
            TabLayout.Tab tabMes = tabLayoutDashboard.newTab();
            tabMes.setText(meses[i]);

            String tag = String.format(Locale.getDefault(), "%%/%02d/%d", (i + 1), anoAtual);
            tabMes.setTag(tag);

            tabLayoutDashboard.addTab(tabMes);

            if (i == mesAtualIndex) {
                tabMes.select();
            }
        }
    }

    private void calcularResumo(String filtroDataLike) {
        new Thread(() -> {
            // 1. Calcular Saldo
            Double receitas = financeDAO.getReceitasPorMes(filtroDataLike);
            Double despesas = financeDAO.getDespesasPorMes(filtroDataLike);
            Double faturasPagas = financeDAO.getFaturasPagasPorMes(filtroDataLike);

            if (receitas == null) receitas = 0.0;
            if (despesas == null) despesas = 0.0;
            if (faturasPagas == null) faturasPagas = 0.0;

            double saldo = receitas - despesas - faturasPagas;

            // 2. Faturas em Aberto
            Double faturasAbertas = financeDAO.getFaturasAbertasPorMes(filtroDataLike);
            if (faturasAbertas == null) faturasAbertas = 0.0;

            // 3. Dívidas Pendentes
            List<DividaParcelada> dividas = financeDAO.selectDividasPendentes();
            double totalDividasPendentes = 0.0;
            for (DividaParcelada d : dividas) {
                double valorParcela = d.getValorTotal() / d.getNumeroParcelasTotal();
                int parcelasRestantes = d.getNumeroParcelasTotal() - d.getParcelasPagas();
                totalDividasPendentes += (valorParcela * parcelasRestantes);
            }

            // 4. Calcular Sugestão de Investimento (5% das sobras)
            // Se o saldo for negativo ou zero, sugerimos 0.00
            double sugestaoInvestimento = (saldo > 0) ? saldo * 0.05 : 0.0;

            // Atualizar a Tela
            double finalFaturasAbertas = faturasAbertas;
            double finalDividas = totalDividasPendentes;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    textViewSaldo.setText(String.format("R$ %.2f", saldo));

                    if (saldo >= 0) {
                        textViewSaldo.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        textViewSaldo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }

                    textViewTotalFaturas.setText(String.format("R$ %.2f", finalFaturasAbertas));
                    textViewTotalDividas.setText(String.format("R$ %.2f", finalDividas));

                    // Atualiza o novo campo de investimento
                    textViewInvestimento.setText(String.format("R$ %.2f", sugestaoInvestimento));
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tabLayoutDashboard.getTabCount() > 0) {
            TabLayout.Tab abaSelecionada = tabLayoutDashboard.getTabAt(tabLayoutDashboard.getSelectedTabPosition());
            if (abaSelecionada != null) {
                String tag = (String) abaSelecionada.getTag();
                calcularResumo(tag);
            }
        }
    }
}