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

/**
 * Este Adapter gerencia a exibição de objetos LancamentoVariavel
 * dentro do RecyclerView da tela de Lançamentos.
 */
public class LancamentosAdapter extends RecyclerView.Adapter<LancamentosAdapter.LancamentoViewHolder> {

    private List<LancamentoVariavel> lancamentos = new ArrayList<>();
    private Context context;

    /**
     * Passo 1: O ViewHolder (O "Molde" do Item)
     * Esta classe interna segura as referências para os componentes
     * de layout do seu 'list_item_lancamento.xml'.
     */
    public static class LancamentoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewDescricao, textViewData, textViewValor;

        public LancamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encontra os componentes do layout do item
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewData = itemView.findViewById(R.id.textViewData);
            textViewValor = itemView.findViewById(R.id.textViewValor);
        }
    }

    /**
     * Passo 2: onCreateViewHolder
     * Chamado quando o RecyclerView precisa de um novo "molde" (ViewHolder)
     * para exibir um item.
     */
    @NonNull
    @Override
    public LancamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Salva o contexto (para usar em cores e ícones)
        context = parent.getContext();
        // Infla (cria) a view do item a partir do XML
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_lancamento, parent, false);

        return new LancamentoViewHolder(view);
    }

    /**
     * Passo 3: onBindViewHolder
     * Chamado para "popular" um item com dados.
     * Ele pega o objeto da lista (na 'position') e o insere no layout.
     */
    @Override
    public void onBindViewHolder(@NonNull LancamentoViewHolder holder, int position) {
        // Pega o objeto de dados da posição atual
        LancamentoVariavel lancamento = lancamentos.get(position);

        // Popula os campos do layout
        holder.textViewDescricao.setText(lancamento.getDescricao());
        holder.textViewData.setText(lancamento.getData()); // Já está em dd/mm/aaaa

        // Lógica para formatar Receita vs Despesa
        if ("receita".equals(lancamento.getTipo())) {
            // Formato de Receita (Verde)
            holder.textViewValor.setText(String.format("R$ %.2f", lancamento.getValor()));
            holder.textViewValor.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.imageViewIcon.setImageResource(android.R.drawable.ic_input_add); // Ícone de "+"
        } else {
            // Formato de Despesa (Vermelho)
            holder.textViewValor.setText(String.format("-R$ %.2f", lancamento.getValor()));
            holder.textViewValor.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.imageViewIcon.setImageResource(android.R.drawable.ic_menu_delete); // Ícone de "delete"
        }
    }

    /**
     * Passo 4: getItemCount
     * Informa ao RecyclerView quantos itens existem na lista.
     */
    @Override
    public int getItemCount() {
        return lancamentos.size();
    }

    /**
     * Passo 5: submitList (Método Auxiliar)
     * Usado pelo Fragmento para atualizar a lista de dados do adapter.
     */
    public void submitList(List<LancamentoVariavel> novaLista) {
        this.lancamentos = novaLista;
        // Notifica o RecyclerView que a lista inteira mudou
        notifyDataSetChanged();
    }
}