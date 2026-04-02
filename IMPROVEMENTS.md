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
VT              → Vale Transporte
BANK_TRANSFER   → TED / DOC
BOLETO          → Boleto Bancário
```

---

### 2. Categorias de Transação

Hoje não existe nenhuma categorização. Crie uma entidade `Category`:

```
id, name, type (EXPENSE/INCOME), icon, color, userId
```

Exemplos de categorias:
- **Receitas:** Salário, Freelance/Serviços Prestados, Rendimentos, Reembolso
- **Despesas:** Alimentação, Transporte, Moradia, Saúde, Lazer, Educação, Assinaturas, Impostos

---

### 3. Transações Recorrentes

Suporte a gastos mensais recorrentes (mensalidades, planos, assinaturas):

```java
// Entidade RecurringTransaction
id, description, amount, paymentType, category,
recurrenceType (DAILY/WEEKLY/MONTHLY/YEARLY),
startDate, endDate (nullable),
dayOfMonth, isActive, userId
```

Com um **job agendado** (`@Scheduled`) que materializa as transações automaticamente a cada período.

---

### 4. Contas / Carteiras

Separe o dinheiro por origem:

```java
// Entidade Account
id, name, type (CHECKING/SAVINGS/CREDIT_CARD/WALLET/VR/VA),
bankName, balance, userId
```

Isso permite rastrear saldo por conta, não só o total global.

---

### 5. Enriquecimento do modelo de Expense/Income

**Expense** precisa de:
- `category` (FK)
- `account` (FK — qual conta foi debitada)
- `installments` (parcelas no cartão de crédito)
- `currentInstallment`
- `notes` (observações)
- `tags` (Set\<String\>)
- `isRecurring` (boolean)
- `recurringTransactionId` (FK opcional)

**Income** precisa de:
- `category` (FK)
- `account` (FK — onde foi creditado)
- `source` (enum: SALARY, FREELANCE, INVESTMENT, REFUND, OTHER)
- `notes`

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

### 7. Filtros e Busca nos endpoints existentes

Os repositórios hoje não têm queries customizadas. Adicione:

```
GET /api/expenses?startDate=&endDate=&categoryId=&paymentType=&minAmount=&maxAmount=
GET /api/incomes?startDate=&endDate=&source=
```

Use `Specification<T>` do Spring Data JPA para filtros dinâmicos.

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
| **Queries** | Adicionar `@Query` / `Specification` nos repositórios |
| **Paginação** | Adicionar filtros nos endpoints paginados |
| **Auditoria** | Resolver o auditor hardcoded (`test-user`) |
| **Migrations** | Padronizar tipos das colunas (`income.id` é `SERIAL`, `expense.id` é `BIGSERIAL`) |
| **DTOs** | Separar DTOs de criação (`CreateDTO`) dos de resposta (`ResponseDTO`) |
| **Testes** | Aumentar cobertura com testes de integração por feature |
| **Actuator** | Configurar endpoints de health/metrics para produção |

---

## Priorização sugerida

```
Fase 1 (Core) ──────────────────────────────────────────
  ✦ Autenticação JWT + User
  ✦ PaymentType completo (PIX, Débito, VA, VT)
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
