package com.example.financeiro.ui.lancamentos;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.Locale;

public class AddLancamentoDialogFragment extends DialogFragment {

    private EditText editTextDescricao, editTextValor, editTextData, editTextObs; // Adicionado editTextObs
    private RadioButton radioReceita, radioDespesa;
    private Button buttonCancelar, buttonSalvar;

    private FinanceDatabase db;
    private FinanceDAO financeDAO;
    private LancamentoVariavel lancamentoEditar;

    public void setLancamentoEditar(LancamentoVariavel lancamento) {
        this.lancamentoEditar = lancamento;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_lancamento, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        editTextDescricao = view.findViewById(R.id.editTextDescricaoDialog);
        editTextValor = view.findViewById(R.id.editTextValorDialog);
        editTextData = view.findViewById(R.id.editTextDataDialog);
        editTextObs = view.findViewById(R.id.editTextObsLancamento); // Vincular ID

        radioReceita = view.findViewById(R.id.radioReceitaDialog);
        radioDespesa = view.findViewById(R.id.radioDespesaDialog);
        buttonCancelar = view.findViewById(R.id.buttonCancelar);
        buttonSalvar = view.findViewById(R.id.buttonSalvar);

        editTextData.setFocusable(false);
        editTextData.setClickable(true);
        editTextData.setOnClickListener(v -> abrirCalendario());

        if (lancamentoEditar != null) {
            editTextDescricao.setText(lancamentoEditar.getDescricao());
            editTextValor.setText(String.valueOf(lancamentoEditar.getValor()));
            editTextData.setText(lancamentoEditar.getData());
            editTextObs.setText(lancamentoEditar.getObservacao()); // Preencher se for edição

            if ("receita".equals(lancamentoEditar.getTipo())) {
                radioReceita.setChecked(true);
            } else {
                radioDespesa.setChecked(true);
            }
            buttonSalvar.setText("Atualizar");
        }

        buttonCancelar.setOnClickListener(v -> dismiss());
        buttonSalvar.setOnClickListener(v -> salvarOuAtualizar());

        return view;
    }

    private void abrirCalendario() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String dataF = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month+1, year);
            editTextData.setText(dataF);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void salvarOuAtualizar() {
        String descricao = editTextDescricao.getText().toString();
        String valorStr = editTextValor.getText().toString();
        String data = editTextData.getText().toString();
        String obs = editTextObs.getText().toString(); // Pegar texto

        if (descricao.isEmpty() || valorStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(valorStr.replace(",", "."));
        String tipo = radioReceita.isChecked() ? "receita" : "despesa";

        new Thread(() -> {
            if (lancamentoEditar == null) {
                LancamentoVariavel novo = new LancamentoVariavel();
                novo.setDescricao(descricao);
                novo.setValor(valor);
                novo.setData(data);
                novo.setTipo(tipo);
                novo.setObservacao(obs); // Salvar novo
                financeDAO.insertLancamentoVariavel(novo);
            } else {
                lancamentoEditar.setDescricao(descricao);
                lancamentoEditar.setValor(valor);
                lancamentoEditar.setData(data);
                lancamentoEditar.setTipo(tipo);
                lancamentoEditar.setObservacao(obs); // Atualizar existente
                financeDAO.updateLancamentoVariavel(lancamentoEditar);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Salvo!", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            }
        }).start();
    }
}