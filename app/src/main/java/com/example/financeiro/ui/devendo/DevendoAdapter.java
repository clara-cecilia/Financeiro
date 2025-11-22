package com.example.financeiro.ui.devendo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financeiro.Entity.Emprestimo;
import com.example.financeiro.R;
import java.util.ArrayList;
import java.util.List;

public class DevendoAdapter extends RecyclerView.Adapter<DevendoAdapter.ViewHolder> {
    private List<Emprestimo> lista = new ArrayList<>();
    private OnItemLongClickListener listener;

    public interface OnItemLongClickListener { void onItemLongClick(Emprestimo emp); }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) { this.listener = listener; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, valor, obs;
        ImageView icon;
        public ViewHolder(View v) { super(v);
            nome = v.findViewById(R.id.textNomePessoa);
            valor = v.findViewById(R.id.textValorEmprestimo);
            obs = v.findViewById(R.id.textObs);
            icon = v.findViewById(R.id.iconEmprestimo);
        }
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.list_item_emprestimo, p, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Emprestimo item = lista.get(pos);
        h.nome.setText(item.getNomePessoa());
        h.obs.setText(item.getObservacao());
        h.valor.setText(String.format("R$ %.2f", item.getValor()));

        if ("receber".equals(item.getTipo())) {
            h.valor.setTextColor(Color.parseColor("#388E3C")); // Verde
            h.icon.setImageResource(android.R.drawable.ic_input_add);
        } else {
            h.valor.setTextColor(Color.parseColor("#D32F2F")); // Vermelho
            h.icon.setImageResource(android.R.drawable.ic_delete);
        }

        h.itemView.setOnLongClickListener(v -> {
            if(listener != null) listener.onItemLongClick(item);
            return true;
        });
    }

    @Override public int getItemCount() { return lista.size(); }
    public void submitList(List<Emprestimo> l) { this.lista = l; notifyDataSetChanged(); }
}