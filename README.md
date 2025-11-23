# 💰 App Financeiro - Manual e Documentação

> **Controle financeiro inteligente, móvel e automatizado.**

---

## 🎯 1. Objetivo do Aplicativo

O **App Financeiro** foi desenvolvido com o propósito de substituir o controle financeiro manual realizado em planilhas de Excel. O objetivo principal é oferecer uma plataforma móvel, intuitiva e automatizada para o registro de receitas, despesas, dívidas parceladas, faturas de cartão de crédito e empréstimos pessoais.

Diferente de uma planilha, onde o usuário precisa criar fórmulas e gerenciar colunas manualmente, o aplicativo realiza todos os cálculos de saldo, projeções e organização temporal de forma **automática**, garantindo a integridade dos dados e facilitando a tomada de decisão financeira.

---

## 📱 2. Estrutura e Navegação

O aplicativo é organizado em **5 seções principais**, acessíveis através de uma barra de navegação inferior e interconectadas por um banco de dados unificado.

### 📊 2.1. Dashboard (Tela Inicial)
A central de inteligência do aplicativo. Processa as informações para apresentar a saúde financeira atual.

* **Resumo Financeiro:** Exibe o saldo líquido (**Sobras**) resultante da equação:
    > `Receitas - Despesas Variáveis - Faturas de Cartão Pagas`
* **Filtro Temporal (Ano/Mês):** Menu suspenso para Ano e abas para Meses (JAN, FEV...). Permite visualizar o desempenho de qualquer período, passado ou futuro.
* **Indicadores de Compromisso:**
    * *Dívidas Totais:* Montante global a pagar.
    * *Faturas/Gastos:* Compromissos de curto prazo em aberto.
* **💡 Sugestão de Investimento:** Um cartão inteligente que, ao detectar saldo positivo, calcula automaticamente **5%** desse valor e sugere como aporte para poupança/investimento.

### 💸 2.2. Lançamentos (Receitas e Despesas)
Módulo para o fluxo de caixa diário (Salário, luz, transporte, etc).

* **Navegação:** Histórico de 1 ano para trás e 1 ano para o futuro.
* **Registro:** Botão `(+)` para inserir Descrição, Valor, Data e Observação.
* **Edição/Exclusão:** Clique longo (pressionar) no item abre o menu de opções.

### 🏦 2.3. Dívidas (Parcelamentos)
Controle de compras parceladas ou longo prazo (Empréstimos, eletrodomésticos).

* **Cadastro:** Valor Total, Número de Parcelas e Data da 1ª parcela.
* **Acompanhamento:** Barra de progresso visual (ex: "3 de 12").
* **Pagamento Inteligente:** Ao manter pressionado, opção **"Pagar Próxima Parcela"**. O sistema abate uma parcela e atualiza o progresso. Ao finalizar, a dívida é arquivada.

### 💳 2.4. Cartões (Faturas de Crédito)
Simula a fatura do cartão de crédito, com controle por mês de vencimento.

* **Status de Pagamento:**
    * 🔴 **Vermelho (Aberto):** Conta como "Fatura Aberta" no Dashboard.
    * 🟢 **Verde (Pago):** Valor descontado do saldo no Dashboard.
* **Gestão:** Clique longo para alterar status (Pagar/Reabrir), editar ou excluir.

### 🤝 2.5. Pessoal (Empréstimos P2P)
Controle de finanças informais (dinheiro emprestado a terceiros ou tomado emprestado).

* **Abas:** "Me Devem (Receber)" e "Eu Devo (Pagar)".
* **Controle:** Mantém esses valores separados do fluxo de caixa mensal até que sejam efetivamente pagos.

---

## ⚙️ 3. Lógica Técnica e Adaptação

A transição da planilha para o aplicativo baseou-se na estruturação de um **Banco de Dados Relacional** para eliminar a manutenção manual.

### 3.1. Automatização Temporal
Substituição das colunas manuais do Excel por **Queries SQL**.
* **Como funciona:** Ao clicar na aba "NOV/2025", o sistema consulta o banco: *"Mostre-me todos os registros onde a data contenha '/11/2025'"*.
* Isso permite um histórico infinito sem poluição visual.

### 3.2. Inteligência de Cálculo
O cálculo de "Sobras" é dinâmico e reflete a realidade do caixa:
1.  Soma todas as **Receitas** do mês.
2.  Subtrai todas as **Despesas Variáveis**.
3.  Subtrai apenas as **Faturas de Cartão marcadas como "Pagas"**.
    * *Nota:* Se uma conta não foi paga, ela não é deduzida, mostrando que o dinheiro ainda existe (embora comprometido).

### 3.3. Integridade dos Dados
* **Persistência:** Utilização de banco de dados local (**Room Database**). Dados salvos mesmo ao reiniciar o aparelho.
* **Validação:** Formulários com seletores de calendário para impedir erros de digitação e quebras de cálculo.

---

## ✅ Conclusão

O aplicativo cumpre seu papel de automatizar o registro financeiro, retirando a carga cognitiva de realizar cálculos e gerenciar layouts. O usuário foca na entrada de dados (**o quê e quanto**), enquanto o sistema cuida do processamento e análise (**como e quando**).
