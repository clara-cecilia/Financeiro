package com.example.financeiro.ui.devendo;

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
import com.example.financeiro.Entity.Emprestimo;
import com.example.financeiro.R;

import java.util.Calendar;
import java.util.Locale;

public class AddEmprestimoDialogFragment extends DialogFragment {

    private EditText editNome, editValor, editData, editObs;
    private RadioButton radioReceber, radioPagar;
    private Button btnCancelar, btnSalvar;

    private FinanceDatabase db;
    private FinanceDAO financeDAO;
    private Emprestimo emprestimoEditar; // Para edição

    public void setEmprestimoEditar(Emprestimo emprestimo) {
        this.emprestimoEditar = emprestimo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_emprestimo, container, false);

        db = Room.databaseBuilder(getContext(), FinanceDatabase.class, "financeiro.db").build();
        financeDAO = db.financeDAO();

        // Vincular componentes
        editNome = view.findViewById(R.id.editTextNomePessoa);
        editValor = view.findViewById(R.id.editTextValorEmprestimo);
        editData = view.findViewById(R.id.editTextDataEmprestimo);
        editObs = view.findViewById(R.id.editTextObsEmprestimo);
        radioReceber = view.findViewById(R.id.radioReceber);
        radioPagar = view.findViewById(R.id.radioPagar);
        btnCancelar = view.findViewById(R.id.buttonCancelarEmp);
        btnSalvar = view.findViewById(R.id.buttonSalvarEmp);

        // Calendário
        editData.setFocusable(false);
        editData.setOnClickListener(v -> abrirCalendario());

        // Preencher se for edição
        if (emprestimoEditar != null) {
            editNome.setText(emprestimoEditar.getNomePessoa());
            editValor.setText(String.valueOf(emprestimoEditar.getValor()));
            editData.setText(emprestimoEditar.getData());
            editObs.setText(emprestimoEditar.getObservacao());

            if ("receber".equals(emprestimoEditar.getTipo())) {
                radioReceber.setChecked(true);
            } else {
                radioPagar.setChecked(true);
            }
            btnSalvar.setText("Atualizar");
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnSalvar.setOnClickListener(v -> salvar());

        return view;
    }

    private void abrirCalendario() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String dataF = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month+1, year);
            editData.setText(dataF);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void salvar() {
        String nome = editNome.getText().toString();
        String valorStr = editValor.getText().toString();
        String data = editData.getText().toString();
        String obs = editObs.getText().toString();

        if (nome.isEmpty() || valorStr.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(valorStr.replace(",", "."));
        String tipo = radioReceber.isChecked() ? "receber" : "pagar";

        new Thread(() -> {
            if (emprestimoEditar == null) {
                Emprestimo novo = new Emprestimo();
                novo.setNomePessoa(nome);
                novo.setValor(valor);
                novo.setData(data);
                novo.setObservacao(obs);
                novo.setTipo(tipo);
                financeDAO.insertEmprestimo(novo);
            } else {
                emprestimoEditar.setNomePessoa(nome);
                emprestimoEditar.setValor(valor);
                emprestimoEditar.setData(data);
                emprestimoEditar.setObservacao(obs);
                emprestimoEditar.setTipo(tipo);
                financeDAO.updateEmprestimo(emprestimoEditar);
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