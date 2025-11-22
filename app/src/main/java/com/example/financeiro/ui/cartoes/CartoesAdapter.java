package com.example.financeiro.ui.cartoes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.R;
import java.util.ArrayList;
import java.util.List;

public class CartoesAdapter extends RecyclerView.Adapter<CartoesAdapter.CartaoViewHolder> {

    private List<GastoCartao> listaFaturas = new ArrayList<>();
    private OnItemLongClickListener listener;

    // Interface para comunicar o clique longo ao Fragment
    public interface OnItemLongClickListener {
        void onItemLongClick(GastoCartao fatura);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public static class CartaoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtValor, txtVencimento, txtStatus;

        public CartaoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.textViewNomeCartao);
            txtValor = itemView.findViewById(R.id.textViewValorFatura);
            txtVencimento = itemView.findViewById(R.id.textViewVencimento);
            // txtStatus não existe no layout XML ainda, mas vamos usar a cor do valor
        }
    }

    @NonNull
    @Override
    public CartaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cartao_fatura, parent, false);
        return new CartaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartaoViewHolder holder, int position) {
        GastoCartao fatura = listaFaturas.get(position);

        holder.txtNome.setText(fatura.getNomeCartao());
        holder.txtVencimento.setText("Vence: " + fatura.getMesAnoFatura());

        // Lógica Visual: Se Paga = Verde, Se Aberto = Vermelho
        if ("Paga".equals(fatura.getStatus())) {
            holder.txtValor.setText("PAGO R$ " + String.format("%.2f", fatura.getValor()));
            holder.txtValor.setTextColor(Color.parseColor("#4CAF50")); // Verde
            holder.txtNome.setAlpha(0.5f); // Deixa o nome meio apagadinho
        } else {
            holder.txtValor.setText("R$ " + String.format("%.2f", fatura.getValor()));
            holder.txtValor.setTextColor(Color.RED);
            holder.txtNome.setAlpha(1.0f);
        }

        // Configura o Clique Longo (Segurar)
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(fatura);
            }
            return true; // Indica que consumimos o evento
        });
    }

    @Override
    public int getItemCount() {
        return listaFaturas.size();
    }

    public void submitList(List<GastoCartao> novaLista) {
        this.listaFaturas = novaLista;
        notifyDataSetChanged();
    }
}