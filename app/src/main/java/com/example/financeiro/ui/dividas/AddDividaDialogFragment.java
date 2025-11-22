package com.example.financeiro.ui.dividas;

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
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;

import java.util.Calendar;
import java.util.Locale;

public class AddDividaDialogFragment extends DialogFragment {

    private EditText editDescricao, editValorTotal, editQtdParcelas, editData;
    private Button btnCancelar, btnSalvar;

    private FinanceDatabase db;
    private FinanceDAO financeDAO;

    // Controle de Edição
    private DividaParcelada dividaEditar;

    public void setDividaEditar(DividaParcelada divida) {
        this.dividaEditar = divida;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_divida, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        editDescricao = view.findViewById(R.id.editTextDescricaoDivida);
        editValorTotal = view.findViewById(R.id.editTextValorTotalDivida);
        editQtdParcelas = view.findViewById(R.id.editTextQtdParcelas);
        editData = view.findViewById(R.id.editTextDataPrimeiraParcela);
        btnCancelar = view.findViewById(R.id.buttonCancelarDivida);
        btnSalvar = view.findViewById(R.id.buttonSalvarDivida);

        // Configurar Calendário
        editData.setFocusable(false);
        editData.setClickable(true);
        editData.setOnClickListener(v -> abrirCalendario());

        // Preencher dados se for edição
        if (dividaEditar != null) {
            editDescricao.setText(dividaEditar.getDescricao());
            editValorTotal.setText(String.valueOf(dividaEditar.getValorTotal()));
            editQtdParcelas.setText(String.valueOf(dividaEditar.getNumeroParcelasTotal()));
            editData.setText(dividaEditar.getDataPrimeiraParcela());
            btnSalvar.setText("Atualizar");
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnSalvar.setOnClickListener(v -> salvarOuAtualizar());

        return view;
    }

    private void abrirCalendario() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Formato aaaa-mm-dd para facilitar ordenação ou dd/mm/aaaa (você escolheu dd/mm/aaaa para o projeto)
                    // Vamos manter o padrão visual dd/mm/aaaa
                    String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    editData.setText(dataFormatada);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void salvarOuAtualizar() {
        String descricao = editDescricao.getText().toString();
        String valorStr = editValorTotal.getText().toString();
        String parcelasStr = editQtdParcelas.getText().toString();
        String data = editData.getText().toString();

        if (descricao.isEmpty() || valorStr.isEmpty() || parcelasStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double valorTotal = Double.parseDouble(valorStr.replace(",", "."));
        int numParcelas = Integer.parseInt(parcelasStr);

        new Thread(() -> {
            if (dividaEditar == null) {
                // NOVA DÍVIDA
                DividaParcelada nova = new DividaParcelada();
                nova.setDescricao(descricao);
                nova.setValorTotal(valorTotal);
                nova.setNumeroParcelasTotal(numParcelas);
                nova.setParcelasPagas(0); // Começa zerada
                nova.setDataPrimeiraParcela(data);
                financeDAO.insertDividaParcelada(nova);
            } else {
                // ATUALIZAR
                dividaEditar.setDescricao(descricao);
                dividaEditar.setValorTotal(valorTotal);
                dividaEditar.setNumeroParcelasTotal(numParcelas);
                dividaEditar.setDataPrimeiraParcela(data);
                // Não mexemos nas parcelas pagas aqui (só via menu de contexto)
                financeDAO.updateDividaParcelada(dividaEditar);
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