package com.example.financeiro.ui.dividas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Este Adapter gerencia a exibição de objetos DividaParcelada
 * dentro do RecyclerView da tela de Dívidas.
 */
public class DividasAdapter extends RecyclerView.Adapter<DividasAdapter.DividaViewHolder> {

    private List<DividaParcelada> dividas = new ArrayList<>();
    private Context context;

    /**
     * Passo 1: O ViewHolder (O "Molde" do Item)
     * Esta classe interna segura as referências para os componentes
     * de layout do seu 'list_item_divida_parcelada.xml'.
     */
    public static class DividaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDescricaoDivida, textViewValorParcela, textViewStatusParcela, textViewValorTotal;
        ProgressBar progressBarDivida;

        public DividaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encontra os componentes do layout do item
            textViewDescricaoDivida = itemView.findViewById(R.id.textViewDescricaoDivida);
            textViewValorParcela = itemView.findViewById(R.id.textViewValorParcela);
            textViewStatusParcela = itemView.findViewById(R.id.textViewStatusParcela);
            textViewValorTotal = itemView.findViewById(R.id.textViewValorTotal);
            progressBarDivida = itemView.findViewById(R.id.progressBarDivida);
        }
    }

    /**
     * Passo 2: onCreateViewHolder
     */
    @NonNull
    @Override
    public DividaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_divida_parcelada, parent, false);
        return new DividaViewHolder(view);
    }

    /**
     * Passo 3: onBindViewHolder
     * Pega o objeto DividaParcelada e o insere no layout.
     */
    @Override
    public void onBindViewHolder(@NonNull DividaViewHolder holder, int position) {
        DividaParcelada divida = dividas.get(position);

        double valorParcela = divida.getValorDaParcela();

        holder.textViewDescricaoDivida.setText(divida.getDescricao());
        holder.textViewValorParcela.setText(String.format("R$ %.2f", valorParcela));
        holder.textViewValorTotal.setText(String.format("Total: R$ %.2f", divida.getValorTotal()));
        holder.textViewStatusParcela.setText(String.format("Parcela %d de %d", divida.getParcelasPagas(), divida.getNumeroParcelasTotal()));

        // Configura a ProgressBar
        holder.progressBarDivida.setMax(divida.getNumeroParcelasTotal());
        holder.progressBarDivida.setProgress(divida.getParcelasPagas());
    }

    /**
     * Passo 4: getItemCount
     */
    @Override
    public int getItemCount() {
        return dividas.size();
    }

    /**
     * Passo 5: submitList (Método Auxiliar)
     */
    public void submitList(List<DividaParcelada> novaLista) {
        this.dividas = novaLista;
        notifyDataSetChanged();
    }
}