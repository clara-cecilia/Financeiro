package com.example.financeiro.ui.dividas;

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
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DividasFragment extends Fragment {

    // Referências do Banco de Dados
    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Referências da UI
    private RecyclerView recyclerViewDividas;
    private FloatingActionButton fabAddDivida;

    // O Adapter que vai organizar a lista
    private DividasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dividas, container, false);

        // 1. Inicializa o Banco de Dados
        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db")
                .build();
        financeDAO = db.financeDAO();

        // 2. Encontra os componentes da tela
        recyclerViewDividas = view.findViewById(R.id.recyclerViewDividas);
        fabAddDivida = view.findViewById(R.id.fabAddDivida);

        // 3. Configura o RecyclerView (A parte que faltava!)
        // Define que será uma lista vertical padrão
        recyclerViewDividas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cria e conecta o adapter
        adapter = new DividasAdapter();
        recyclerViewDividas.setAdapter(adapter);

        // 4. Ação do Botão Flutuante (Adicionar Dívida)
        fabAddDivida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre o diálogo que criamos anteriormente
                AddDividaDialogFragment dialog = new AddDividaDialogFragment();
                dialog.show(getParentFragmentManager(), "AddDividaDialog");
            }
        });

        return view;
    }

    private void buscarDividas() {
        // Busca os dados em segundo plano (background) para não travar a tela
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Busca apenas as dívidas que ainda não foram totalmente pagas
                List<DividaParcelada> listaDividas = financeDAO.selectDividasPendentes();

                // Volta para a tela principal (UI Thread) para mostrar os dados
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Entrega a lista para o Adapter exibir
                            if (listaDividas != null) {
                                adapter.submitList(listaDividas);

                                // Feedback visual se a lista estiver vazia (opcional, para teste)
                                if (listaDividas.isEmpty()) {
                                    // Toast.makeText(getContext(), "Nenhuma dívida pendente.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarrega os dados toda vez que o usuário voltar para esta tela ou fechar o diálogo
        buscarDividas();
    }
}