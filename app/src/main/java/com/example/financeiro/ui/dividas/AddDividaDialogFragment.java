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

    private EditText editDescricao, editValorTotal, editQtdParcelas, editData, editObs; // Adicionado editObs
    private Button btnCancelar, btnSalvar;
    private FinanceDatabase db;
    private FinanceDAO financeDAO;
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
        editObs = view.findViewById(R.id.editTextObsDivida); // Vincular ID

        btnCancelar = view.findViewById(R.id.buttonCancelarDivida);
        btnSalvar = view.findViewById(R.id.buttonSalvarDivida);

        editData.setFocusable(false);
        editData.setClickable(true);
        editData.setOnClickListener(v -> abrirCalendario());

        if (dividaEditar != null) {
            editDescricao.setText(dividaEditar.getDescricao());
            editValorTotal.setText(String.valueOf(dividaEditar.getValorTotal()));
            editQtdParcelas.setText(String.valueOf(dividaEditar.getNumeroParcelasTotal()));
            editData.setText(dividaEditar.getDataPrimeiraParcela());
            editObs.setText(dividaEditar.getObservacao()); // Preencher
            btnSalvar.setText("Atualizar");
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnSalvar.setOnClickListener(v -> salvarOuAtualizar());

        return view;
    }

    private void abrirCalendario() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String dataF = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month+1, year);
            editData.setText(dataF);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void salvarOuAtualizar() {
        String descricao = editDescricao.getText().toString();
        String valorStr = editValorTotal.getText().toString();
        String parcelasStr = editQtdParcelas.getText().toString();
        String data = editData.getText().toString();
        String obs = editObs.getText().toString(); // Pegar texto

        if (descricao.isEmpty() || valorStr.isEmpty() || parcelasStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double valorTotal = Double.parseDouble(valorStr.replace(",", "."));
        int numParcelas = Integer.parseInt(parcelasStr);

        new Thread(() -> {
            if (dividaEditar == null) {
                DividaParcelada nova = new DividaParcelada();
                nova.setDescricao(descricao);
                nova.setValorTotal(valorTotal);
                nova.setNumeroParcelasTotal(numParcelas);
                nova.setParcelasPagas(0);
                nova.setDataPrimeiraParcela(data);
                nova.setObservacao(obs); // Salvar novo
                financeDAO.insertDividaParcelada(nova);
            } else {
                dividaEditar.setDescricao(descricao);
                dividaEditar.setValorTotal(valorTotal);
                dividaEditar.setNumeroParcelasTotal(numParcelas);
                dividaEditar.setDataPrimeiraParcela(data);
                dividaEditar.setObservacao(obs); // Atualizar existente
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