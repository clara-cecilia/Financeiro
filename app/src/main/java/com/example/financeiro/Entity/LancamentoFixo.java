package com.example.financeiro.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lancamentos_fixos")
public class LancamentoFixo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String descricao;
    private double valor;
    // "receita" ou "despesa"
    private String tipo;

    // Dia do mês que o lançamento ocorre (ex: 5, 10, 20)
    private int diaVencimento;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(int diaVencimento) { this.diaVencimento = diaVencimento;}

}
