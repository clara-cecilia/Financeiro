package com.example.financeiro.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    private TabLayout tabLayoutDashboard;
    private TextView textViewSaldo, textViewTotalDividas, textViewTotalFaturas, textViewTituloResumo, textViewInvestimento;
    private TextView textViewReceber, textViewPagar; // Novos campos do Módulo Pessoal
    private Spinner spinnerAno;

    private int anoSelecionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        // Referências UI
        tabLayoutDashboard = view.findViewById(R.id.tabLayoutDashboard);
        textViewSaldo = view.findViewById(R.id.textViewSaldoAtual);
        textViewTotalDividas = view.findViewById(R.id.textViewTotalDividas);
        textViewTotalFaturas = view.findViewById(R.id.textViewTotalFaturas);
        textViewTituloResumo = view.findViewById(R.id.textViewTituloResumo);
        textViewInvestimento = view.findViewById(R.id.textViewInvestimento);

        // Novos Vínculos Pessoal
        textViewReceber = view.findViewById(R.id.textViewTotalReceber);
        textViewPagar = view.findViewById(R.id.textViewTotalPagar);

        spinnerAno = view.findViewById(R.id.spinnerAno);

        // Configura o Spinner de Anos (5 anos para trás e 5 para frente)
        configurarSpinnerAno();

        // Listener das Abas
        tabLayoutDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                atualizarFiltroEBuscar();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }

    private void configurarSpinnerAno() {
        Calendar cal = Calendar.getInstance();
        int anoAtual = cal.get(Calendar.YEAR);
        anoSelecionado = anoAtual;

        // Lista de anos (ex: 2022 a 2030)
        List<String> anos = new ArrayList<>();
        for (int i = anoAtual - 3; i <= anoAtual + 5; i++) {
            anos.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, anos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAno.setAdapter(adapter);

        // Seleciona o ano atual na lista
        int posicaoAnoAtual = anos.indexOf(String.valueOf(anoAtual));
        spinnerAno.setSelection(posicaoAnoAtual);

        // Listener para quando trocar o ano
        spinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                anoSelecionado = Integer.parseInt(anos.get(position));
                configurarAbasMeses(); // Recria as abas com o novo ano (ex: JAN/2024)
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarAbasMeses() {
        // Salva a posição da aba atual para tentar manter a seleção
        int abaAnterior = tabLayoutDashboard.getSelectedTabPosition();
        if (abaAnterior < 0) abaAnterior = 0;

        tabLayoutDashboard.removeAllTabs();

        // 1. ABA TOTAL DO ANO
        TabLayout.Tab tabAno = tabLayoutDashboard.newTab();
        tabAno.setText("TOTAL " + anoSelecionado);
        tabAno.setTag("%/" + anoSelecionado);
        tabLayoutDashboard.addTab(tabAno);

        // 2. ABAS DOS MESES
        String[] meses = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};

        Calendar cal = Calendar.getInstance();
        int mesAtualDoSistema = cal.get(Calendar.MONTH);
        int anoAtualDoSistema = cal.get(Calendar.YEAR);

        for (int i = 0; i < 12; i++) {
            TabLayout.Tab tabMes = tabLayoutDashboard.newTab();
            tabMes.setText(meses[i]);

            // Tag ex: "%/01/2024"
            String tag = String.format(Locale.getDefault(), "%%/%02d/%d", (i + 1), anoSelecionado);
            tabMes.setTag(tag);
            tabLayoutDashboard.addTab(tabMes);
        }

        // Se o ano selecionado for o atual, seleciona o mês atual automaticamente
        if (anoSelecionado == anoAtualDoSistema && abaAnterior == 0) {
            TabLayout.Tab tabHoje = tabLayoutDashboard.getTabAt(mesAtualDoSistema + 1); // +1 por causa da aba Total
            if(tabHoje != null) tabHoje.select();
        } else {
            // Tenta manter a aba (ex: estava vendo FEV de 2025, ao mudar ano, mostra FEV de 2024)
            TabLayout.Tab tabParaSelecionar = tabLayoutDashboard.getTabAt(abaAnterior);
            if(tabParaSelecionar != null) tabParaSelecionar.select();
        }
    }

    private void atualizarFiltroEBuscar() {
        TabLayout.Tab aba = tabLayoutDashboard.getTabAt(tabLayoutDashboard.getSelectedTabPosition());
        if (aba != null) {
            String filtro = (String) aba.getTag();
            String texto = aba.getText().toString();
            textViewTituloResumo.setText("Resumo: " + texto + (texto.startsWith("TOTAL") ? "" : "/" + anoSelecionado));
            calcularResumo(filtro);
        }
    }

    private void calcularResumo(String filtroDataLike) {
        new Thread(() -> {
            // 1. Calcular Saldo (Receitas - Despesas - Faturas Pagas)
            Double receitas = financeDAO.getReceitasPorMes(filtroDataLike);
            Double despesas = financeDAO.getDespesasPorMes(filtroDataLike);
            Double faturasPagas = financeDAO.getFaturasPagasPorMes(filtroDataLike);

            if (receitas == null) receitas = 0.0;
            if (despesas == null) despesas = 0.0;
            if (faturasPagas == null) faturasPagas = 0.0;

            double saldo = receitas - despesas - faturasPagas;

            // 2. Faturas em Aberto
            Double faturasAbertasTemp = financeDAO.getFaturasAbertasPorMes(filtroDataLike);
            if (faturasAbertasTemp == null) faturasAbertasTemp = 0.0; // Modificamos a variável temporária

            // 3. Dívidas Pendentes
            List<DividaParcelada> dividas = financeDAO.selectDividasPendentes();
            double totalDividasPendentes = 0.0;
            for (DividaParcelada d : dividas) {
                double valorParcela = d.getValorTotal() / d.getNumeroParcelasTotal();
                int parcelasRestantes = d.getNumeroParcelasTotal() - d.getParcelasPagas();
                totalDividasPendentes += (valorParcela * parcelasRestantes);
            }

            // 4. Valores Pessoais (Global)
            Double totalReceberTemp = financeDAO.getTotalAReceber();
            Double totalPagarTemp = financeDAO.getTotalAPagar();
            if (totalReceberTemp == null) totalReceberTemp = 0.0;
            if (totalPagarTemp == null) totalPagarTemp = 0.0;

            // 5. Investimento (5% das Sobras positivas)
            double sugestaoInvestimento = (saldo > 0) ? saldo * 0.05 : 0.0;

            // --- CORREÇÃO DO ERRO ---
            // Criamos variáveis FINAIS aqui para passar para a tela.
            // O Java exige que variáveis usadas dentro do runOnUiThread não tenham mudado de valor.
            double finalFaturasAbertas = faturasAbertasTemp;
            double finalDividas = totalDividasPendentes;
            double finalReceber = totalReceberTemp;
            double finalPagar = totalPagarTemp;
            double finalSaldo = saldo; // Só por garantia

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    textViewSaldo.setText(String.format("R$ %.2f", finalSaldo));

                    if (finalSaldo >= 0) {
                        textViewSaldo.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        textViewSaldo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }

                    // Aqui usamos as variáveis 'final...' que criamos acima
                    textViewTotalFaturas.setText(String.format("R$ %.2f", finalFaturasAbertas));
                    textViewTotalDividas.setText(String.format("R$ %.2f", finalDividas));
                    textViewInvestimento.setText(String.format("R$ %.2f", sugestaoInvestimento));

                    // Atualiza cards Pessoais
                    textViewReceber.setText(String.format("R$ %.2f", finalReceber));
                    textViewPagar.setText(String.format("R$ %.2f", finalPagar));
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tabLayoutDashboard.getTabCount() > 0) {
            atualizarFiltroEBuscar();
        }
    }
}