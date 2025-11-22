package com.example.financeiro.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dividas_parceladas")
public class DividaParcelada {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String descricao;

    private double valorTotal; // Ex: 1200.00

    private int numeroParcelasTotal; // Ex: 12

    private int parcelasPagas; // Ex: 3

    // "2025-10-10" (Data da primeira parcela, para sabermos os próximos venc.)
    private String dataPrimeiraParcela;
    private String observacao; // NOVO

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public int getNumeroParcelasTotal() { return numeroParcelasTotal; }
    public void setNumeroParcelasTotal(int numeroParcelasTotal) { this.numeroParcelasTotal = numeroParcelasTotal; }
    public int getParcelasPagas() { return parcelasPagas; }
    public void setParcelasPagas(int parcelasPagas) { this.parcelasPagas = parcelasPagas; }
    public String getDataPrimeiraParcela() { return dataPrimeiraParcela; }
    public void setDataPrimeiraParcela(String dataPrimeiraParcela) { this.dataPrimeiraParcela = dataPrimeiraParcela; }

    /**
     * Método auxiliar para calcular o valor de cada parcela
     */
    public double getValorDaParcela() {
        if (numeroParcelasTotal == 0) return 0;
        return valorTotal / numeroParcelasTotal;
    }
}
