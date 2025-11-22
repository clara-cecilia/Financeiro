package com.example.financeiro.ui.dividas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.example.financeiro.DAO.FinanceDAO;
import com.example.financeiro.Database.FinanceDatabase;
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;

public class AddDividaDialogFragment extends DialogFragment {

    private EditText editDescricao, editValorTotal, editQtdParcelas, editData;
    private Button btnCancelar, btnSalvar;

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_divida, container, false);

        // Inicializar Banco de Dados
        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        // Vincular componentes
        editDescricao = view.findViewById(R.id.editTextDescricaoDivida);
        editValorTotal = view.findViewById(R.id.editTextValorTotalDivida);
        editQtdParcelas = view.findViewById(R.id.editTextQtdParcelas);
        editData = view.findViewById(R.id.editTextDataPrimeiraParcela);
        btnCancelar = view.findViewById(R.id.buttonCancelarDivida);
        btnSalvar = view.findViewById(R.id.buttonSalvarDivida);

        // Listeners
        btnCancelar.setOnClickListener(v -> dismiss());

        btnSalvar.setOnClickListener(v -> salvarDivida());

        return view;
    }

    private void salvarDivida() {
        String descricao = editDescricao.getText().toString();
        String valorStr = editValorTotal.getText().toString();
        String parcelasStr = editQtdParcelas.getText().toString();
        String data = editData.getText().toString();

        if (descricao.isEmpty() || valorStr.isEmpty() || parcelasStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar Objeto
        DividaParcelada divida = new DividaParcelada();
        divida.setDescricao(descricao);
        divida.setValorTotal(Double.parseDouble(valorStr));
        divida.setNumeroParcelasTotal(Integer.parseInt(parcelasStr));
        divida.setParcelasPagas(0); // Começa com 0 pagas
        divida.setDataPrimeiraParcela(data);

        // Salvar em Background
        new Thread(() -> {
            financeDAO.insertDividaParcelada(divida);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Dívida adicionada!", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            }
        }).start();
    }
}