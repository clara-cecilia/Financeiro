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
import com.example.financeiro.R;

/**
 * Este Fragmento controla a tela R.layout.fragment_dashboard
 * (Ainda não sabemos o que fazer, então ele só mostra o layout)
 */
public class DashboardFragment extends Fragment {

    // Referências do Banco de Dados
    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Referências da UI
    private TextView textViewSaldoAtual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1. Infla o layout
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 2. Inicializa o Banco
        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db")
                .build();
        financeDAO = db.financeDAO();

        // 3. Encontra os componentes
        textViewSaldoAtual = view.findViewById(R.id.textViewSaldoAtual);

        // 4. Carregar os dados (precisamos criar esta função)
        // buscarDadosDashboard();

        return view;
    }

    private void buscarDadosDashboard() {
        // Esta função será complexa.
        // Ela precisará:
        // 1. Buscar TODAS as Receitas (Fixas e Variáveis)
        // 2. Buscar TODAS as Despesas (Fixas, Variáveis, Dívidas Pagas, Faturas Pagas)
        // 3. Calcular o Saldo (Receitas - Despesas)

        new Thread(new Runnable() {
            @Override
            public void run() {
                // ... Lógica do banco ...
                double saldo = 748.13; // Valor de exemplo

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewSaldoAtual.setText("R$ " + String.format("%.2f", saldo));
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
        buscarDadosDashboard();
    }
}