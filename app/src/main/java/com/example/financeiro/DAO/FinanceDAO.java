package com.example.financeiro.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

// 1. Importar TODAS as Novas Entidades
import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.Entity.LancamentoFixo;
import com.example.financeiro.Entity.LancamentoVariavel;

// 2. NÃO importar as entidades antigas (Transacao, Divida)

@Dao
public interface FinanceDAO {

    // --- Lançamentos Variáveis (Tela "Lançamentos") ---
    @Insert
    void insertLancamentoVariavel(LancamentoVariavel lancamento);

    @Update
    void updateLancamentoVariavel(LancamentoVariavel lancamento);

    @Delete
    void deleteLancamentoVariavel(LancamentoVariavel lancamento);

    // Query para buscar lançamentos por mês/ano.
    // Como a data é dd/mm/aaaa, usamos LIKE '%/mm/aaaa'
    @Query("SELECT * FROM lancamentos_variaveis WHERE data LIKE :mesAnoFormatado ORDER BY data ASC")
    List<LancamentoVariavel> selectLancamentosPorMes(String mesAnoFormatado); // Ex: "%/11/2025"


    // --- Lançamentos Fixos (Tela "Lançamentos" - CRUD de Fixos) ---
    @Insert
    void insertLancamentoFixo(LancamentoFixo lancamento);

    @Update
    void updateLancamentoFixo(LancamentoFixo lancamento);

    @Delete
    void deleteLancamentoFixo(LancamentoFixo lancamento);

    @Query("SELECT * FROM lancamentos_fixos ORDER BY diaVencimento ASC")
    List<LancamentoFixo> selectAllLancamentosFixos();


    // --- Dívidas Parceladas (Tela "Dívidas") ---
    @Insert
    void insertDividaParcelada(DividaParcelada divida);

    @Update
    void updateDividaParcelada(DividaParcelada divida); // Para pagar parcela

    @Delete
    void deleteDividaParcelada(DividaParcelada divida);

    @Query("SELECT * FROM dividas_parceladas WHERE parcelasPagas < numeroParcelasTotal ORDER BY dataPrimeiraParcela ASC")
    List<DividaParcelada> selectDividasPendentes();

    @Query("SELECT * FROM gastos_cartao WHERE mesAnoFatura LIKE :mesAnoLike ORDER BY mesAnoFatura ASC")
    List<GastoCartao> selectGastosPorMes(String mesAnoLike); // Ex: "%/12/2025"


    // --- Gastos de Cartão (Tela "Cartões") ---
    @Insert
    void insertGastoCartao(GastoCartao gasto);

    @Update
    void updateGastoCartao(GastoCartao gasto); // Para marcar como "PAGA"

    @Delete
    void deleteGastoCartao(GastoCartao gasto);

    @Query("SELECT * FROM gastos_cartao WHERE nomeCartao = :nomeCartao ORDER BY mesAnoFatura DESC")
    List<GastoCartao> selectGastosPorCartao(String nomeCartao);

    @Query("SELECT DISTINCT nomeCartao FROM gastos_cartao ORDER BY nomeCartao ASC")
    List<String> selectAllNomesCartoes();
}