# Grain Pay API

REST API de controle de finanças pessoais construída com Spring Boot 3.5 e Java 21. Permite registrar receitas e despesas, organizar por categorias, controlar contas, definir orçamentos com alertas automáticos por e-mail e gerar relatórios.

## Stack

- **Java 21** + **Spring Boot 3.5**
- **PostgreSQL** + **Flyway** (migrations)
- **Spring Security** + **JWT** (jjwt 0.12)
- **MapStruct** (mapeamento de DTOs)
- **SpringDoc OpenAPI** (Swagger UI)
- **TestContainers** (testes de integração)
- **Apache Commons CSV** + **OpenPDF** (exportação)
- **Spring Mail** (notificações por e-mail)

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+

## Variáveis de ambiente

### Banco de dados e autenticação

| Variável | Descrição |
|---|---|
| `POSTGRES_DATASOURCE_URL` | URL JDBC (ex: `jdbc:postgresql://localhost:5432/grainpay`) |
| `POSTGRES_DATASOURCE_USERNAME` | Usuário do banco |
| `POSTGRES_DATASOURCE_PASSWORD` | Senha do banco |
| `PORT` | Porta da aplicação (ex: `8080`) |
| `JWT_SECRET` | Chave secreta Base64 (mínimo 32 bytes) |
| `JWT_EXPIRATION` | Expiração do token em ms (padrão: `86400000` = 24h) |

### Notificações por e-mail

| Variável | Descrição |
|---|---|
| `MAIL_HOST` | Servidor SMTP (ex: `smtp.gmail.com`) |
| `MAIL_PORT` | Porta SMTP (padrão: `587`) |
| `MAIL_USERNAME` | Usuário SMTP |
| `MAIL_PASSWORD` | Senha SMTP (ou senha de app) |
| `MAIL_FROM` | Endereço de remetente (ex: `noreply@grainpay.dev`) |

### Gerar JWT_SECRET

```bash
openssl rand -base64 32
```

## Instalação e execução

```bash
# Clonar o repositório
git clone https://github.com/deyvedev/grain-pay-api.git
cd grain-pay-api

# Build
mvn clean install

# Executar (com perfil dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Ou via JAR:

```bash
java -jar target/grain-pay-api-0.0.1.jar --spring.profiles.active=dev
```

## Endpoints

### Autenticação (`/auth`)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/register` | Cadastro de usuário |
| `POST` | `/auth/login` | Login — retorna JWT |

Todos os demais endpoints exigem o header:
```
Authorization: Bearer <token>
```

### Transações (`/api/transactions`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/transactions` | Listar com filtros e paginação |
| `POST` | `/api/transactions` | Criar transação (suporta parcelamento) |
| `GET` | `/api/transactions/{id}` | Buscar por ID |
| `PUT` | `/api/transactions/{id}` | Atualizar |
| `DELETE` | `/api/transactions/{id}` | Excluir |

**Filtros disponíveis (query params):**

```
type=INCOME|EXPENSE
startDate=2026-01-01
endDate=2026-03-31
categoryId=1
paymentType=PIX|CREDIT_CARD|DEBIT_CARD|MONEY|VR|VA|BANK_TRANSFER|BOLETO
minAmount=100.00
maxAmount=500.00
page=0&size=10&sort=date
```

**Parcelamento:** envie `installments > 1` no corpo para gerar N registros automaticamente.

### Categorias (`/api/categories`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/categories` | Listar categorias do usuário |
| `POST` | `/api/categories` | Criar categoria |
| `GET` | `/api/categories/{id}` | Buscar por ID |
| `PUT` | `/api/categories/{id}` | Atualizar |
| `DELETE` | `/api/categories/{id}` | Excluir |

### Contas (`/api/accounts`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/accounts` | Listar contas (paginado) |
| `POST` | `/api/accounts` | Criar conta |
| `GET` | `/api/accounts/{id}` | Buscar por ID |
| `PUT` | `/api/accounts/{id}` | Atualizar |
| `DELETE` | `/api/accounts/{id}` | Excluir |

### Transações recorrentes (`/api/recurring-transactions`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/recurring-transactions` | Listar |
| `POST` | `/api/recurring-transactions` | Criar (DAILY, WEEKLY, MONTHLY, YEARLY) |
| `GET` | `/api/recurring-transactions/{id}` | Buscar por ID |
| `PUT` | `/api/recurring-transactions/{id}` | Atualizar |
| `DELETE` | `/api/recurring-transactions/{id}` | Excluir |

Transações recorrentes são materializadas automaticamente todo dia às 00:05.

### Dashboard (`/api/dashboard`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/dashboard/summary?month=2026-04` | Resumo mensal: total receitas, despesas, saldo e breakdown por categoria |

### Orçamentos (`/api/budgets`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/budgets?month=2026-04` | Listar orçamentos do mês |
| `POST` | `/api/budgets` | Criar orçamento |
| `PUT` | `/api/budgets/{id}` | Atualizar |
| `DELETE` | `/api/budgets/{id}` | Excluir |

Cada orçamento retorna `spent`, `percentage` e `alert` (true quando >= 80%). Quando o limite é atingido, um e-mail de alerta é enviado automaticamente.

### Relatórios (`/api/reports`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/reports/monthly?year=2026` | Totais por mês (12 meses) |
| `GET` | `/api/reports/yearly` | Totais por ano |
| `GET` | `/api/reports/by-category?month=2026-04` | Receita e despesa por categoria |

### Exportação (`/api/export`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/export/csv?month=2026-04` | Download CSV (`text/csv`) |
| `GET` | `/api/export/pdf?month=2026-04` | Download PDF A4 landscape (`application/pdf`) |

## Exemplos

### Cadastro e login

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João","email":"joao@email.com","password":"senha123"}'

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","password":"senha123"}'
```

### Criar transação parcelada

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EXPENSE",
    "amount": 1200.00,
    "date": "2026-04-01",
    "description": "Notebook",
    "paymentType": "CREDIT_CARD",
    "categoryId": 1,
    "installments": 12
  }'
```

### Criar orçamento

```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"categoryId": 1, "limitAmount": 500.00, "month": 4, "year": 2026}'
```

### Exportar CSV do mês

```bash
curl "http://localhost:8080/api/export/csv?month=2026-04" \
  -H "Authorization: Bearer <token>" -o transacoes.csv
```

## Documentação interativa (Swagger)

Disponível em `http://localhost:{PORT}/swagger-ui/index.html` após iniciar a aplicação.

## Banco de dados — Migrations (Flyway)

| Versão | Descrição |
|---|---|
| `V1_0_0` | Criação da tabela `expense` |
| `V1_0_1` | Criação da tabela `income` |
| `V1_0_2` | Correção da tabela `expense` |
| `V1_0_3` | Criação da tabela `users` |
| `V1_0_4` | Criação da tabela `categories` |
| `V1_0_5` | Criação das tabelas `transactions` e `transaction_tags` |
| `V1_0_6` | Remoção das tabelas legadas `income` e `expense` |
| `V1_0_7` | Criação da tabela `accounts` + coluna `account_id` em `transactions` |
| `V1_0_8` | Criação da tabela `recurring_transactions` |
| `V1_0_9` | Criação da tabela `budgets` |
| `V1_1_0` | Coluna `notified` em `budgets` (controle de alerta por e-mail) |

## Notificações por e-mail

O sistema verifica diariamente às 09:00 todos os orçamentos do mês corrente. Quando o gasto atingir >= 80% do limite e ainda não tiver sido notificado, um e-mail HTML é enviado ao usuário com o resumo do orçamento. Cada orçamento só recebe uma notificação por mês.

Para desenvolvimento, recomenda-se o [Mailtrap](https://mailtrap.io) como servidor SMTP de sandbox.

## TODO

- [x] CRUD de transações (Income/Expense unificados)
- [x] Autenticação JWT
- [x] Categorias de transação
- [x] Filtros dinâmicos com Specification
- [x] Swagger / OpenAPI
- [x] Global exception handler
- [x] Auditoria por usuário autenticado
- [x] Contas/Carteiras
- [x] Transações recorrentes (`@Scheduled`)
- [x] Parcelas de cartão de crédito
- [x] Dashboard / resumo financeiro
- [x] Orçamentos por categoria com alerta >= 80%
- [x] Relatórios mensais e anuais
- [x] Exportação CSV/PDF
- [x] Notificações por e-mail (orçamento >= 80%)
- [ ] Metas financeiras (`/api/goals`)
- [ ] Importação de extrato CSV/OFX
- [ ] CI/CD
- [ ] Monitoramento com Grafana/Prometheus

## Contato

[deyvedev@gmail.com](mailto:deyvedev@gmail.com)
