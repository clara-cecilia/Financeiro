package com.example.financeiro.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gastos_cartao")
public class GastoCartao {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nomeCartao; // Ex: "PicPay", "Banco do Brasil"

    private double valor;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String mesAnoFatura;

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCartao() {
        return nomeCartao;
    }

    public void setNomeCartao(String nomeCartao) {
        this.nomeCartao = nomeCartao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getMesAnoFatura() {
        return mesAnoFatura;
    }

    public void setMesAnoFatura(String mesAnoFatura) {
        this.mesAnoFatura = mesAnoFatura;
    }

}
