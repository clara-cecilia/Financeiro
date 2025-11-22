package com.example.financeiro.ui.cartoes;

import android.app.DatePickerDialog;
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
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.R;
import java.util.Calendar;
import java.util.Locale;

public class AddFaturaDialogFragment extends DialogFragment {

    private EditText editNomeCartao, editValor, editData;
    private Button btnCancelar, btnSalvar;
    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Variável para saber se estamos editando
    private GastoCartao faturaParaEditar = null;

    // Método para passar a fatura se for edição
    public void setFaturaParaEditar(GastoCartao fatura) {
        this.faturaParaEditar = fatura;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_fatura, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        editNomeCartao = view.findViewById(R.id.editTextNomeCartao);
        editValor = view.findViewById(R.id.editTextValorFatura);
        editData = view.findViewById(R.id.editTextDataVencimento);
        btnCancelar = view.findViewById(R.id.buttonCancelarFatura);
        btnSalvar = view.findViewById(R.id.buttonSalvarFatura);

        // CONFIGURAÇÃO DO CALENDÁRIO (DatePicker)
        // Bloqueia digitação manual para obrigar uso do calendário
        editData.setFocusable(false);
        editData.setClickable(true);
        editData.setOnClickListener(v -> abrirCalendario());

        // Se for edição, preenche os campos
        if (faturaParaEditar != null) {
            editNomeCartao.setText(faturaParaEditar.getNomeCartao());
            editValor.setText(String.valueOf(faturaParaEditar.getValor()));
            editData.setText(faturaParaEditar.getMesAnoFatura());
            btnSalvar.setText("Atualizar");
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnSalvar.setOnClickListener(v -> salvarOuAtualizar());

        return view;
    }

    private void abrirCalendario() {
        Calendar cal = Calendar.getInstance();

        // Tenta parsear a data atual do campo se houver
        // (Lógica simplificada, usa a data de hoje se falhar)

        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Formata para dd/mm/aaaa
                    String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    editData.setText(dataFormatada);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void salvarOuAtualizar() {
        String nome = editNomeCartao.getText().toString();
        String valorStr = editValor.getText().toString();
        String data = editData.getText().toString();

        if (nome.isEmpty() || valorStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(valorStr.replace(",", "."));

        new Thread(() -> {
            if (faturaParaEditar == null) {
                // NOVA FATURA
                GastoCartao nova = new GastoCartao();
                nova.setNomeCartao(nome);
                nova.setValor(valor);
                nova.setMesAnoFatura(data);
                nova.setStatus("Aberto");
                financeDAO.insertGastoCartao(nova);
            } else {
                // ATUALIZAR EXISTENTE (Edição)
                faturaParaEditar.setNomeCartao(nome);
                faturaParaEditar.setValor(valor);
                faturaParaEditar.setMesAnoFatura(data);
                // Mantém o status que já estava
                financeDAO.updateGastoCartao(faturaParaEditar);
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