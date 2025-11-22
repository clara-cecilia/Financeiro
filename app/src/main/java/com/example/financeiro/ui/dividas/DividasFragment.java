package com.example.financeiro.ui.dividas;

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
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DividasFragment extends Fragment {

    private FinanceDatabase db;
    private FinanceDAO financeDAO;
    private RecyclerView recyclerViewDividas;
    private FloatingActionButton fabAddDivida;
    private DividasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dividas, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        recyclerViewDividas = view.findViewById(R.id.recyclerViewDividas);
        fabAddDivida = view.findViewById(R.id.fabAddDivida);

        recyclerViewDividas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DividasAdapter();

        // CLIQUE LONGO
        adapter.setOnItemLongClickListener(new DividasAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DividaParcelada divida) {
                mostrarOpcoesDivida(divida);
            }
        });

        recyclerViewDividas.setAdapter(adapter);

        fabAddDivida.setOnClickListener(v -> {
            AddDividaDialogFragment dialog = new AddDividaDialogFragment();
            dialog.show(getParentFragmentManager(), "AddDividaDialog");
        });

        return view;
    }

    private void mostrarOpcoesDivida(DividaParcelada divida) {
        // Opções dinâmicas
        String opcaoPagar = "Pagar Próxima Parcela (" + (divida.getParcelasPagas() + 1) + "/" + divida.getNumeroParcelasTotal() + ")";

        String[] opcoes = {opcaoPagar, "Editar", "Excluir"};

        new AlertDialog.Builder(getContext())
                .setTitle(divida.getDescricao())
                .setItems(opcoes, (dialog, which) -> {
                    switch (which) {
                        case 0: // Pagar Parcela
                            pagarParcela(divida);
                            break;
                        case 1: // Editar
                            editarDivida(divida);
                            break;
                        case 2: // Excluir
                            excluirDivida(divida);
                            break;
                    }
                })
                .show();
    }

    private void pagarParcela(DividaParcelada divida) {
        if (divida.getParcelasPagas() >= divida.getNumeroParcelasTotal()) {
            Toast.makeText(getContext(), "Esta dívida já está paga!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Incrementa parcelas pagas
        divida.setParcelasPagas(divida.getParcelasPagas() + 1);

        new Thread(() -> {
            financeDAO.updateDividaParcelada(divida);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Parcela Paga!", Toast.LENGTH_SHORT).show();
                    buscarDividas(); // Atualiza a lista
                });
            }
        }).start();
    }

    private void editarDivida(DividaParcelada divida) {
        AddDividaDialogFragment dialog = new AddDividaDialogFragment();
        dialog.setDividaEditar(divida);
        dialog.show(getParentFragmentManager(), "EditDividaDialog");
    }

    private void excluirDivida(DividaParcelada divida) {
        new AlertDialog.Builder(getContext())
                .setTitle("Excluir Dívida")
                .setMessage("Tem certeza? Isso apagará todo o histórico desta dívida.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    new Thread(() -> {
                        financeDAO.deleteDividaParcelada(divida);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Dívida excluída", Toast.LENGTH_SHORT).show();
                                buscarDividas();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void buscarDividas() {
        new Thread(() -> {
            // Busca apenas as pendentes. Se você pagar a última parcela, ela sumirá da lista.
            List<DividaParcelada> listaDividas = financeDAO.selectDividasPendentes();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.submitList(listaDividas);
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        buscarDividas();
    }
}