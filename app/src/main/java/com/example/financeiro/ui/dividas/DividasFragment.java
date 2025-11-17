package com.example.financeiro.ui.dividas;

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
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Este Fragmento controla a tela R.layout.fragment_dividas
 */
public class DividasFragment extends Fragment {

    // Referências do Banco de Dados
    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Referências da UI
    private RecyclerView recyclerViewDividas;
    private FloatingActionButton fabAddDivida;

    // private DividaParceladaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dividas, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db")
                .build();
        financeDAO = db.financeDAO();

        recyclerViewDividas = view.findViewById(R.id.recyclerViewDividas);
        fabAddDivida = view.findViewById(R.id.fabAddDivida);

        // Configurar o RecyclerView (LayoutManager, Adapter)
        // ...

        fabAddDivida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir Dialog/Activity para criar nova DividaParcelada
                Toast.makeText(getContext(), "Clicou em Adicionar Dívida", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void buscarDividas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DividaParcelada> listaDividas = financeDAO.selectDividasPendentes();

                // ... Lógica para atualizar o adapter do RecyclerView ...

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // adapter.submitList(listaDividas);
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
        buscarDividas();
    }
}
