# Grain Pay API — Status e Roadmap

## Fase 1 — CONCLUÍDA (2026-04-02)

- Spring Security + JWT (jjwt 0.12.6) — `POST /auth/register` e `POST /auth/login`
- Entidades: `User`, `Category`, `Transaction` (com `@ElementCollection` para tags)
- Migrations Flyway V1_0_3 a V1_0_6
- `TransactionType` (INCOME/EXPENSE), `IncomeSource`, `PaymentType` expandido
- Filtros dinâmicos via `JpaSpecificationExecutor` + `TransactionSpecification`
- `AuditingConfig` usa usuário autenticado via `SecurityContextHolder`
- Código legado (Income, Expense) removido

---

## Fase 2 — EM ANDAMENTO (iniciada 2026-04-04)

### ✅ Account (Contas/Carteiras)

| Arquivo | Descrição |
|---|---|
| `models/AccountType.java` | Enum: CHECKING, SAVINGS, CREDIT_CARD, WALLET, VR, VA |
| `models/Account.java` | Entidade JPA |
| `dtos/CreateAccountRequest.java` | Record com validações |
| `dtos/AccountResponse.java` | Record de resposta |
| `mappers/AccountMapper.java` | MapStruct |
| `repositories/AccountRepository.java` | `findAllByUserId` |
| `exceptions/AccountNotFoundException.java` | Registrada no handler global |
| `services/AccountService.java` | CRUD com isolamento por userId |
| `controllers/AccountController.java` | `GET/POST/PUT/DELETE /api/accounts` |
| `V1_0_7__create_accounts.sql` | Tabela `accounts` + coluna `account_id` em `transactions` |

**Transaction atualizada:** campo `account` (FK nullable) na entidade, `accountId` no request, `AccountResponse` embutido na resposta.

---

### ⬜ RecurringTransaction (Transações Recorrentes)

- Entidade: `id, description, amount, type, paymentType, category, account, recurrenceType, startDate, endDate, dayOfMonth, isActive, user`
- `RecurrenceType` enum: DAILY, WEEKLY, MONTHLY, YEARLY
- Job `@Scheduled` roda às 00:05 e materializa transações recorrentes do dia
- Endpoints: `GET/POST/PUT/DELETE /api/recurring-transactions`
- Migration: `V1_0_8__create_recurring_transactions.sql`

### ⬜ Parcelas de cartão

- `Transaction` já tem `installments` e `currentInstallment`
- Lógica no `TransactionService`: ao criar com `installments > 1`, gerar N registros automaticamente

### ⬜ Dashboard / Resumo mensal

- `GET /api/dashboard/summary?month=2026-04`
- Retorna: total receitas, total despesas, saldo, despesas por categoria, receitas por fonte
- Query com GROUP BY via JPQL ou query nativa

---

## Fase 3 — PLANEJADA

| Item | Descrição |
|---|---|
| Orçamentos | Entidade `Budget` (category, amount, month, year, user) + `GET /api/budgets?month=` com % utilizado |
| Relatórios | `GET /api/reports/monthly`, `/yearly`, `/by-category` |
| Alertas | Notificação quando orçamento atingir 80% |
| Exportação | CSV/PDF das transações |
