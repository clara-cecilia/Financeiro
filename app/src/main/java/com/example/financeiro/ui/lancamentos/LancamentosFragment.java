package com.example.financeiro.ui.lancamentos;

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
import com.example.financeiro.Entity.LancamentoVariavel;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;
public class LancamentosFragment extends Fragment {

    // Referências do Banco de Dados
    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Referências da UI
    private TabLayout tabLayoutMeses;
    private RecyclerView recyclerViewLancamentos;
    private FloatingActionButton fabAddLancamento;

    // Lista e Adapter (Vamos usar um RecyclerView, mas o conceito do Adapter é o mesmo)
    // private List<LancamentoVariavel> listaLancamentos;
    // private MeuAdapter adapter; // Você precisará criar um RecyclerView.Adapter

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1. Infla (carrega) o layout XML deste fragmento
        View view = inflater.inflate(R.layout.fragment_lancamentos, container, false);

        // 2. Inicializa o Banco de Dados
        // (getContext() é o equivalente de 'this' ou 'getApplicationContext()' em um Fragment)
        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db")
                .build();
        financeDAO = db.financeDAO();

        // 3. Encontra os componentes da UI (usando 'view.findViewById')
        tabLayoutMeses = view.findViewById(R.id.tabLayoutMeses);
        recyclerViewLancamentos = view.findViewById(R.id.recyclerViewLancamentos);
        fabAddLancamento = view.findViewById(R.id.fabAddLancamento);

        // 4. Configurar a UI
        configurarTabsMeses();

        // 5. Configurar Listeners
        fabAddLancamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui você abriria um Dialog ou nova Activity para adicionar
                // um novo LancamentoVariavel
                salvarLancamentoVariavelTeste(); // Apenas um exemplo
            }
        });

        tabLayoutMeses.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Quando o usuário clica em um mês (ex: "NOV"),
                // nós buscamos os dados daquele mês.
                String mesAno = (String) tab.getTag(); // Ex: "/11/2025"
                buscarLancamentosDoMes(mesAno);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        // 6. Carregar dados iniciais
        String mesAnoInicial = (String) tabLayoutMeses.getTabAt(tabLayoutMeses.getSelectedTabPosition()).getTag();
        buscarLancamentosDoMes(mesAnoInicial);

        return view;
    }

    private void configurarTabsMeses() {
        // Lógica para criar as abas dos meses (NOV, DEZ, JAN...)
        Calendar cal = Calendar.getInstance();
        String[] meses = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};

        int mesAtualIndex = cal.get(Calendar.MONTH); // Ex: 10 (Novembro)

        for (int i = -2; i <= 2; i++) { // 2 meses antes, 2 depois
            int mesIndex = (mesAtualIndex + i + 12) % 12;
            int ano = cal.get(Calendar.YEAR);
            if (mesAtualIndex + i < 0) ano--;
            if (mesAtualIndex + i > 11) ano++;

            String tituloAba = meses[mesIndex];
            String tagMesAno = String.format("/%02d/%d", mesIndex + 1, ano); // Ex: "/11/2025"

            TabLayout.Tab tab = tabLayoutMeses.newTab().setText(tituloAba).setTag(tagMesAno);
            tabLayoutMeses.addTab(tab);
        }
        // Seleciona a aba do meio (mês atual)
        tabLayoutMeses.selectTab(tabLayoutMeses.getTabAt(2));
    }


    /**
     * Esta é a sua antiga função "buscarTransacoesNoBD",
     * agora adaptada para este Fragmento.
     */
    private void buscarLancamentosDoMes(String mesAnoFormatado) {
        // Limpa a lista
        // arrayDisplay.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Busca os dados do banco
                List<LancamentoVariavel> lista = financeDAO.selectLancamentosPorMes("%" + mesAnoFormatado);

                // Prepara os dados para o adapter
                // ... (lógica para formatar os dados para o RecyclerView) ...

                // Atualiza o ListView na Thread principal
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Encontrados: " + lista.size(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Esta é a sua antiga função "salvarTransacaoNoBD",
     * adaptada para este Fragmento.
     */
    private void salvarLancamentoVariavelTeste() {
        // Cria um objeto de teste
        LancamentoVariavel novoLancamento = new LancamentoVariavel();
        novoLancamento.setDescricao("Teste FAB");
        novoLancamento.setValor(50.0);
        novoLancamento.setTipo("despesa");
        novoLancamento.setData("16/11/2025"); // Padrão dd/mm/aaaa

        new Thread(new Runnable() {
            @Override
            public void run() {
                financeDAO.insertLancamentoVariavel(novoLancamento);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Salvo!", Toast.LENGTH_SHORT).show();
                            // Recarrega a lista do mês atual
                            String tag = (String) tabLayoutMeses.getTabAt(tabLayoutMeses.getSelectedTabPosition()).getTag();
                            buscarLancamentosDoMes(tag);
                        }
                    });
                }
            }
        }).start();
    }
}
