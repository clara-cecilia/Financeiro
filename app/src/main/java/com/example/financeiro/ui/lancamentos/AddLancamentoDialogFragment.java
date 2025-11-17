package com.example.financeiro.ui.lancamentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;
import com.example.financeiro.DAO.FinanceDAO;
import com.example.financeiro.Database.FinanceDatabase;
import com.example.financeiro.Entity.LancamentoVariavel;
import com.example.financeiro.R;

public class AddLancamentoDialogFragment extends DialogFragment {

    private EditText editTextDescricao, editTextValor, editTextData;
    private RadioButton radioReceita;
    private Button buttonCancelar, buttonSalvar;

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout do dialog
        View view = inflater.inflate(R.layout.dialog_add_lancamento, container, false);

        // Inicializa o DB (assim como no Fragment)
        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db")
                .build();
        financeDAO = db.financeDAO();

        // Encontra os componentes do layout
        editTextDescricao = view.findViewById(R.id.editTextDescricaoDialog);
        editTextValor = view.findViewById(R.id.editTextValorDialog);
        editTextData = view.findViewById(R.id.editTextDataDialog);
        radioReceita = view.findViewById(R.id.radioReceitaDialog);
        buttonCancelar = view.findViewById(R.id.buttonCancelar);
        buttonSalvar = view.findViewById(R.id.buttonSalvar);

        // Define as ações dos botões
        buttonCancelar.setOnClickListener(v -> dismiss()); // Fecha o dialog

        buttonSalvar.setOnClickListener(v -> salvarNovoLancamento());

        return view;
    }

    private void salvarNovoLancamento() {
        String descricao = editTextDescricao.getText().toString();
        String valorStr = editTextValor.getText().toString();
        String data = editTextData.getText().toString();

        // Validação simples
        if (descricao.isEmpty() || valorStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(valorStr);
        String tipo = radioReceita.isChecked() ? "receita" : "despesa";

        // Cria o novo objeto
        LancamentoVariavel novoLancamento = new LancamentoVariavel();
        novoLancamento.setDescricao(descricao);
        novoLancamento.setValor(valor);
        novoLancamento.setData(data); // "dd/mm/aaaa"
        novoLancamento.setTipo(tipo);

        // Salva no banco em uma thread separada
        new Thread(new Runnable() {
            @Override
            public void run() {
                financeDAO.insertLancamentoVariavel(novoLancamento);

                // Volta para a UI Thread para mostrar o Toast e fechar
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            dismiss(); // Fecha o dialog
                        }
                    });
                }
            }
        }).start();
    }
}