package com.example.financeiro.Database;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.financeiro.DAO.FinanceDAO;
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.Entity.LancamentoFixo;
import com.example.financeiro.Entity.LancamentoVariavel;

// Este é como seu "Database.java"
// Note que listamos todas as @Entities aqui
@Database(entities = {
        GastoCartao.class,
        DividaParcelada.class,
        LancamentoFixo.class,
        LancamentoVariavel.class
}, version = 1)
public abstract class FinanceDatabase extends RoomDatabase {

    // Ele precisa ter um método abstrato para cada DAO
    public abstract FinanceDAO financeDAO();
}
