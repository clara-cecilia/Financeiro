package com.example.financeiro.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.example.financeiro.Entity.DividaParcelada;
import com.example.financeiro.Entity.Emprestimo;
import com.example.financeiro.Entity.GastoCartao;
import com.example.financeiro.Entity.LancamentoFixo;
import com.example.financeiro.Entity.LancamentoVariavel;

@Dao
public interface FinanceDAO {

    // --- Lançamentos Variáveis ---
    @Insert
    void insertLancamentoVariavel(LancamentoVariavel lancamento);

    @Update
    void updateLancamentoVariavel(LancamentoVariavel lancamento);

    @Delete
    void deleteLancamentoVariavel(LancamentoVariavel lancamento);

    @Query("SELECT * FROM lancamentos_variaveis WHERE data LIKE :mesAnoFormatado ORDER BY data ASC")
    List<LancamentoVariavel> selectLancamentosPorMes(String mesAnoFormatado);

    // --- DASHBOARD: Somas Filtradas por Mês ---
    // Soma receitas deste mês específico
    @Query("SELECT SUM(valor) FROM lancamentos_variaveis WHERE tipo = 'receita' AND data LIKE :mesAno")
    Double getReceitasPorMes(String mesAno);

    // Soma despesas deste mês específico
    @Query("SELECT SUM(valor) FROM lancamentos_variaveis WHERE tipo = 'despesa' AND data LIKE :mesAno")
    Double getDespesasPorMes(String mesAno);

    // Soma faturas de cartão que vencem neste mês E já foram pagas
    // (Assume que se venceu em Nov e está paga, saiu do dinheiro de Nov)
    @Query("SELECT SUM(valor) FROM gastos_cartao WHERE status = 'Paga' AND mesAnoFatura LIKE :mesAno")
    Double getFaturasPagasPorMes(String mesAno);

    // Soma faturas abertas (geral ou do mês) - Aqui deixamos todas as abas do mês para saber o compromisso
    @Query("SELECT SUM(valor) FROM gastos_cartao WHERE status != 'Paga' AND mesAnoFatura LIKE :mesAno")
    Double getFaturasAbertasPorMes(String mesAno);


    // --- Lançamentos Fixos ---
    @Insert
    void insertLancamentoFixo(LancamentoFixo lancamento);
    @Update
    void updateLancamentoFixo(LancamentoFixo lancamento);
    @Delete
    void deleteLancamentoFixo(LancamentoFixo lancamento);
    @Query("SELECT * FROM lancamentos_fixos ORDER BY diaVencimento ASC")
    List<LancamentoFixo> selectAllLancamentosFixos();


    // --- Dívidas Parceladas ---
    @Insert
    void insertDividaParcelada(DividaParcelada divida);
    @Update
    void updateDividaParcelada(DividaParcelada divida);
    @Delete
    void deleteDividaParcelada(DividaParcelada divida);
    @Query("SELECT * FROM dividas_parceladas WHERE parcelasPagas < numeroParcelasTotal ORDER BY dataPrimeiraParcela ASC")
    List<DividaParcelada> selectDividasPendentes();


    // --- Gastos de Cartão ---
    @Insert
    void insertGastoCartao(GastoCartao gasto);
    @Update
    void updateGastoCartao(GastoCartao gasto);
    @Delete
    void deleteGastoCartao(GastoCartao gasto);
    @Query("SELECT * FROM gastos_cartao WHERE nomeCartao = :nomeCartao ORDER BY mesAnoFatura DESC")
    List<GastoCartao> selectGastosPorCartao(String nomeCartao);
    @Query("SELECT DISTINCT nomeCartao FROM gastos_cartao ORDER BY nomeCartao ASC")
    List<String> selectAllNomesCartoes();
    @Query("SELECT * FROM gastos_cartao WHERE mesAnoFatura LIKE :mesAnoLike ORDER BY mesAnoFatura ASC")
    List<GastoCartao> selectGastosPorMes(String mesAnoLike);

    // --- EMPRÉSTIMOS (Tela "Devendo") ---
    @Insert
    void insertEmprestimo(Emprestimo emprestimo);

    @Update
    void updateEmprestimo(Emprestimo emprestimo);

    @Delete
    void deleteEmprestimo(Emprestimo emprestimo);

    // Busca tudo o que eu tenho para RECEBER (Me devem)
    @Query("SELECT * FROM emprestimos WHERE tipo = 'receber' ORDER BY id DESC")
    List<Emprestimo> selectA_Receber();

    // Busca tudo o que eu tenho para PAGAR (Eu devo)
    @Query("SELECT * FROM emprestimos WHERE tipo = 'pagar' ORDER BY id DESC")
    List<Emprestimo> selectA_Pagar();

    // --- SOMAS PARA O DASHBOARD (Módulo Pessoal) ---
    @Query("SELECT SUM(valor) FROM emprestimos WHERE tipo = 'receber'")
    Double getTotalAReceber();

    @Query("SELECT SUM(valor) FROM emprestimos WHERE tipo = 'pagar'")
    Double getTotalAPagar();
}