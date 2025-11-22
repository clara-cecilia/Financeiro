package com.example.financeiro.ui.lancamentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeiro.Entity.LancamentoVariavel;
import com.example.financeiro.R;

import java.util.ArrayList;
import java.util.List;

public class LancamentosAdapter extends RecyclerView.Adapter<LancamentosAdapter.LancamentoViewHolder> {

    private List<LancamentoVariavel> lancamentos = new ArrayList<>();
    private Context context;

    // Interface para o clique longo
    private OnItemLongClickListener listener;

    public interface OnItemLongClickListener {
        void onItemLongClick(LancamentoVariavel lancamento);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public static class LancamentoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewDescricao, textViewData, textViewValor;

        public LancamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewData = itemView.findViewById(R.id.textViewData);
            textViewValor = itemView.findViewById(R.id.textViewValor);
        }
    }

    @NonNull
    @Override
    public LancamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_lancamento, parent, false);
        return new LancamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LancamentoViewHolder holder, int position) {
        LancamentoVariavel lancamento = lancamentos.get(position);

        holder.textViewDescricao.setText(lancamento.getDescricao());
        holder.textViewData.setText(lancamento.getData());

        if ("receita".equals(lancamento.getTipo())) {
            holder.textViewValor.setText(String.format("R$ %.2f", lancamento.getValor()));
            holder.textViewValor.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.imageViewIcon.setImageResource(android.R.drawable.ic_input_add);
        } else {
            holder.textViewValor.setText(String.format("-R$ %.2f", lancamento.getValor()));
            holder.textViewValor.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.imageViewIcon.setImageResource(android.R.drawable.ic_menu_delete);
        }

        // Configura o clique longo
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(lancamento);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lancamentos.size();
    }

    public void submitList(List<LancamentoVariavel> novaLista) {
        this.lancamentos = novaLista;
        notifyDataSetChanged();
    }
}