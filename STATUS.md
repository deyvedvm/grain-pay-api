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

## Fase 2 — CONCLUÍDA (2026-04-07)

### ✅ Account (Contas/Carteiras)
- CRUD completo — `GET/POST/PUT/DELETE /api/accounts` (paginado)
- Migration `V1_0_7`: tabela `accounts` + coluna `account_id` em `transactions`

### ✅ RecurringTransaction (Transações Recorrentes)
- Entidade com `RecurrenceType` (DAILY, WEEKLY, MONTHLY, YEARLY)
- Job `@Scheduled` às 00:05 materializa transações recorrentes do dia
- `GET/POST/PUT/DELETE /api/recurring-transactions`
- Migration `V1_0_8`

### ✅ Parcelas de cartão
- `installments > 1` no `POST /api/transactions` gera N registros automaticamente
- Cada parcela: `currentInstallment = i`, `date += (i-1) meses`, `amount = total / N`, `description = "desc (i/N)"`
- `TransactionService.save()` anotado com `@Transactional`, retorna `List<TransactionResponse>`

### ✅ Dashboard / Resumo mensal
- `GET /api/dashboard/summary?month=2026-04`
- Retorna: `totalIncome`, `totalExpenses`, `balance`, `expensesByCategory`, `incomeBySource`
- Queries JPQL com `GROUP BY` + `COALESCE` para categoria nula

---

## Fase 3 — CONCLUÍDA (2026-04-07)

### ✅ Orçamentos por categoria
- `GET/POST/PUT/DELETE /api/budgets?month=2026-04`
- Cada orçamento retorna: `limitAmount`, `spent`, `percentage`, `alert` (≥ 80%)
- Constraint `UNIQUE (user_id, category_id, month, year)` no banco
- Migration `V1_0_9`

### ✅ Relatórios
- `GET /api/reports/monthly?year=2026` — totais por mês (12 meses, zeros onde sem dados)
- `GET /api/reports/yearly` — totais por ano (todos os anos com dados)
- `GET /api/reports/by-category?month=2026-04` — receita e despesa por categoria

### ✅ Exportação CSV/PDF
- `GET /api/export/csv?month=2026-04` — download `text/csv`, colunas: ID, Date, Type, Description, Amount, Category, Payment Type, Account, Installments, Notes
- `GET /api/export/pdf?month=2026-04` — download `application/pdf`, A4 landscape, tabela com 6 colunas
- Libs: Apache Commons CSV 1.12.0 + OpenPDF 2.0.3

---

## Fase 4 — EM ANDAMENTO

### ✅ Notificações por e-mail
- E-mail HTML disparado quando orçamento atinge >= 80% do limite
- Job `@Scheduled` às 09:00 (`BudgetNotificationScheduler`)
- Campo `notified` na tabela `budgets` evita reenvio no mesmo mês
- Spring Mail + `JavaMailSender` + template HTML inline
- Migration `V1_1_0`
- Vars: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`

| Item | Descrição |
|---|---|
| Metas financeiras | Entidade `Goal` (nome, valor alvo, prazo, progresso); `GET/POST/PUT/DELETE /api/goals` |
| Importação de extrato | Upload CSV/OFX para criação automática de transações; `POST /api/import` |

---

## Versão 2.0 — PLANEJADA (futuro)

| Item | Descrição |
|---|---|
| Multi-moeda | Campo `currency` nas transações + taxa de conversão |
| Autenticação social | Login com Google/GitHub via OAuth2 |

---

## Deploy

### Atual — Fly.io

- Plataforma escolhida: **Fly.io**
- Docker-native, sempre ligado, HTTPS automático, ~$5/mês (512 MB RAM)
- Deploy separado do frontend (Vercel/Netlify) — comunicação via CORS
- Configuração: `fly.toml` na raiz do projeto
- JVM tunada para ambiente com pouca memória: `-Xmx300m -Xss512k`

### Versão 2.0 — AWS ECS Fargate (Terraform salvo)

- Infraestrutura já esboçada em `terraform/`
- Recursos provisionados: ECR, ECS Cluster, Task Definition (Fargate 512 CPU / 1024 MB), ECS Service
- VPC completa: 2 subnets públicas (us-east-1a/b), IGW, route table, Security Group (porta 8080)
- PostgreSQL externo (Neon DB) via env vars na task definition
- Pendente para ativação: rodar `terraform init && terraform apply` com as credenciais via `TF_VAR_*`
- Custo estimado com ALB: **$40-60/mês**

### Frontend (futuro)

- Deploy independente via **Vercel** ou **Netlify**
- Consome a API do backend via URL pública (Fly.io ou AWS)
- CORS já deve ser configurado no backend para aceitar a origem do frontend
