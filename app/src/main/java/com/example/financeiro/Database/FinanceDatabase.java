package com.example.financeiro.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.financeiro.DAO.FinanceDAO;
// Importe todas as entidades
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.Entity.LancamentoFixo;
import com.example.financeiro.Entity.LancamentoVariavel;
import com.example.financeiro.Entity.Emprestimo; // NOVO

@Database(entities = {
        GastoCartao.class,
        DividaParcelada.class,
        LancamentoFixo.class,
        LancamentoVariavel.class,
        Emprestimo.class // NOVO REGISTRO
}, version = 2) // Mudei versão para 2 (mas vamos desinstalar o app, então tanto faz)
public abstract class FinanceDatabase extends RoomDatabase {
    public abstract FinanceDAO financeDAO();
}