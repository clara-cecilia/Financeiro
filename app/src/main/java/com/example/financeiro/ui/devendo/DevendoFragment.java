package com.example.financeiro.ui.devendo;

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
import com.example.financeiro.Entity.Emprestimo;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class DevendoFragment extends Fragment {

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private DevendoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devendo, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        tabLayout = view.findViewById(R.id.tabLayoutTipo);
        recyclerView = view.findViewById(R.id.recyclerDevendo);
        fabAdd = view.findViewById(R.id.fabAddDevendo);

        // Configurar Lista
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DevendoAdapter();

        // Configura o Clique Longo (Editar/Excluir)
        adapter.setOnItemLongClickListener(item -> mostrarOpcoes(item));

        recyclerView.setAdapter(adapter);

        // Listener do Botão Adicionar
        fabAdd.setOnClickListener(v -> {
            AddEmprestimoDialogFragment dialog = new AddEmprestimoDialogFragment();
            dialog.show(getParentFragmentManager(), "AddEmprestimoDialog");
        });

        // Listener das Abas (Me Devem / Eu Devo)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                carregarDados();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }

    private void carregarDados() {
        int posicao = tabLayout.getSelectedTabPosition();
        String tipo = (posicao == 0) ? "receber" : "pagar"; // 0 = Me Devem, 1 = Eu Devo

        new Thread(() -> {
            List<Emprestimo> lista;
            if ("receber".equals(tipo)) {
                lista = financeDAO.selectA_Receber();
            } else {
                lista = financeDAO.selectA_Pagar();
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.submitList(lista));
            }
        }).start();
    }

    private void mostrarOpcoes(Emprestimo item) {
        String[] opcoes = {"Editar", "Excluir"};
        new AlertDialog.Builder(getContext())
                .setTitle(item.getNomePessoa())
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) { // Editar
                        AddEmprestimoDialogFragment dialogFrag = new AddEmprestimoDialogFragment();
                        dialogFrag.setEmprestimoEditar(item);
                        dialogFrag.show(getParentFragmentManager(), "EditEmpDialog");
                    } else { // Excluir
                        excluir(item);
                    }
                }).show();
    }

    private void excluir(Emprestimo item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Excluir")
                .setMessage("Tem certeza?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    new Thread(() -> {
                        financeDAO.deleteEmprestimo(item);
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Excluído", Toast.LENGTH_SHORT).show();
                                carregarDados();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Não", null).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarDados();
    }
}