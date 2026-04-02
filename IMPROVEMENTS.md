# Grain Pay API — Proposta de Melhorias

## Domínio — O que está faltando

### 1. Modelo de Pagamento completo

Atualmente só existem `MONEY`, `VR` e `CREDIT_CARD`. Expanda o enum `PaymentType`:

```
MONEY           → Dinheiro/espécie
PIX             → PIX
CREDIT_CARD     → Cartão de Crédito
DEBIT_CARD      → Cartão de Débito
VR              → Vale Refeição
VA              → Vale Alimentação
BANK_TRANSFER   → TED / DOC
BOLETO          → Boleto Bancário
```

---

### 2. Entidade Transaction (substitui Income e Expense)

Unificar `Income` e `Expense` em uma única entidade `Transaction` com um campo `type`:

```java
// Enum TransactionType
INCOME, EXPENSE

// Enum IncomeSource
SALARY, FREELANCE, INVESTMENT, REFUND, OTHER

// Entidade Transaction
id                      → Long
type                    → TransactionType (INCOME / EXPENSE)
amount                  → BigDecimal
date                    → LocalDate
description             → String
paymentType             → PaymentType
category                → Category (FK)
account                 → Account (FK)
notes                   → String (nullable)
tags                    → Set<String> (nullable)
userId                  → Long (FK)

// Específicos de EXPENSE (nullable):
installments            → Integer (parcelas no cartão)
currentInstallment      → Integer
isRecurring             → boolean
recurringTransactionId  → Long (FK, nullable)

// Específico de INCOME (nullable):
source                  → IncomeSource
```

**Motivação:** uma única tabela `transactions` simplifica queries de dashboard e relatórios, elimina duplicação de campos e facilita extensão futura (ex: transferências entre contas).

Os DTOs na API podem continuar separados (`CreateIncomeRequest`, `CreateExpenseRequest`) mapeando para a mesma entidade — melhor experiência sem comprometer o modelo.

---

### 3. Categorias de Transação

Crie uma entidade `Category`:

```
id, name, type (EXPENSE/INCOME), icon, color, userId
```

Exemplos de categorias:
- **Receitas:** Salário, Freelance/Serviços Prestados, Rendimentos, Reembolso
- **Despesas:** Alimentação, Transporte, Moradia, Saúde, Lazer, Educação, Assinaturas, Impostos

---

### 4. Transações Recorrentes

Suporte a gastos mensais recorrentes (mensalidades, planos, assinaturas):

```java
// Entidade RecurringTransaction
id, description, amount, type (TransactionType), paymentType, category,
recurrenceType (DAILY/WEEKLY/MONTHLY/YEARLY),
startDate, endDate (nullable),
dayOfMonth, isActive, userId
```

Com um **job agendado** (`@Scheduled`) que materializa as transações automaticamente a cada período.

---

### 5. Contas / Carteiras

Separe o dinheiro por origem:

```java
// Entidade Account
id, name, type (CHECKING/SAVINGS/CREDIT_CARD/WALLET/VR/VA),
bankName, balance, userId
```

Isso permite rastrear saldo por conta, não só o total global.

---

## Funcionalidades — Endpoints novos

### 6. Dashboard / Resumo Financeiro

```
GET /api/dashboard/summary?month=2026-03
```
Retorna:
- Total de receitas no período
- Total de despesas no período
- Saldo do período
- Despesas por categoria (para gráfico de pizza)
- Receitas por fonte

---

### 7. Filtros e Busca

```
GET /api/transactions?startDate=&endDate=&type=&categoryId=&paymentType=&minAmount=&maxAmount=
```

Use `Specification<Transaction>` do Spring Data JPA para filtros dinâmicos.

---

### 8. Relatórios Mensais

```
GET /api/reports/monthly?year=2026&month=3
GET /api/reports/yearly?year=2026
GET /api/reports/by-category?startDate=&endDate=
```

---

### 9. Orçamentos (Budget)

Permite ao usuário definir um limite de gasto por categoria por mês:

```java
// Entidade Budget
id, category, amount (limite), month, year, userId
```

```
GET /api/budgets?month=2026-03   → lista orçamentos com % utilizado
```

---

## Segurança — Ausência crítica

### 10. Autenticação e Multi-tenancy

Atualmente o auditor é hardcoded como `"test-user"`. Implemente:

- **Spring Security + JWT** para autenticação
- Entidade `User` com roles
- Todos os dados filtrados por `userId` (cada usuário vê só os seus dados)
- Endpoints de `POST /auth/register` e `POST /auth/login`

---

## Qualidade e Observabilidade

### 11. Melhorias técnicas

| Área | Melhoria |
|---|---|
| **Queries** | Adicionar `Specification<Transaction>` no repositório |
| **Paginação** | Adicionar filtros nos endpoints paginados |
| **Auditoria** | Resolver o auditor hardcoded (`test-user`) |
| **Migrations** | Padronizar tipos das colunas e migrar `income`/`expense` para `transaction` |
| **DTOs** | Separar DTOs de criação (`CreateIncomeRequest`, `CreateExpenseRequest`) dos de resposta (`TransactionResponse`) |
| **Testes** | Aumentar cobertura com testes de integração por feature |
| **Actuator** | Configurar endpoints de health/metrics para produção |

---

## Priorização sugerida

```
Fase 1 (Core) ──────────────────────────────────────────
  ✦ Autenticação JWT + User
  ✦ Migrar Income/Expense → Transaction (com type INCOME/EXPENSE)
  ✦ PaymentType completo (PIX, Débito, VA)
  ✦ Categorias de transação
  ✦ Filtros por data/categoria nos endpoints

Fase 2 (Riqueza de dados) ───────────────────────────────
  ✦ Contas/Carteiras
  ✦ Transações recorrentes + job agendado
  ✦ Parcelas de cartão de crédito
  ✦ Dashboard/resumo mensal

Fase 3 (Inteligência) ───────────────────────────────────
  ✦ Orçamentos por categoria
  ✦ Relatórios anuais
  ✦ Alertas (ex: orçamento 80% atingido)
  ✦ Exportação CSV/PDF
```
