package com.example.financeiro.Entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lancamentos_variaveis")
public class LancamentoVariavel {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String descricao;

    private double valor;

    // "receita" ou "despesa"
    private String tipo;

    // "16/11/2025" (Data do lançamento no formato dd/mm/aaaa)
    private String data;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

}
