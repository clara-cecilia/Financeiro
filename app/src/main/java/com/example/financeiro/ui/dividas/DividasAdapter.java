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

public class DividasAdapter extends RecyclerView.Adapter<DividasAdapter.DividaViewHolder> {

    private List<DividaParcelada> dividas = new ArrayList<>();
    private Context context;
    private OnItemLongClickListener listener;

    public interface OnItemLongClickListener {
        void onItemLongClick(DividaParcelada divida);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public static class DividaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDescricaoDivida, textViewValorParcela, textViewStatusParcela, textViewValorTotal;
        ProgressBar progressBarDivida;

        public DividaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescricaoDivida = itemView.findViewById(R.id.textViewDescricaoDivida);
            textViewValorParcela = itemView.findViewById(R.id.textViewValorParcela);
            textViewStatusParcela = itemView.findViewById(R.id.textViewStatusParcela);
            textViewValorTotal = itemView.findViewById(R.id.textViewValorTotal);
            progressBarDivida = itemView.findViewById(R.id.progressBarDivida);
        }
    }

    @NonNull
    @Override
    public DividaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_divida_parcelada, parent, false);
        return new DividaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DividaViewHolder holder, int position) {
        DividaParcelada divida = dividas.get(position);

        double valorParcela = divida.getValorDaParcela();

        holder.textViewDescricaoDivida.setText(divida.getDescricao());
        holder.textViewValorParcela.setText(String.format("R$ %.2f", valorParcela));
        holder.textViewValorTotal.setText(String.format("Total: R$ %.2f", divida.getValorTotal()));

        // Ex: "Parcela 3 de 12"
        holder.textViewStatusParcela.setText(String.format("Parcela %d de %d", divida.getParcelasPagas(), divida.getNumeroParcelasTotal()));

        // Configura a Barra de Progresso
        holder.progressBarDivida.setMax(divida.getNumeroParcelasTotal());
        holder.progressBarDivida.setProgress(divida.getParcelasPagas());

        // Clique Longo
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(divida);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dividas.size();
    }

    public void submitList(List<DividaParcelada> novaLista) {
        this.dividas = novaLista;
        notifyDataSetChanged();
    }
}