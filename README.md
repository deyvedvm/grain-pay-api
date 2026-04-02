# Grain Pay API

REST API de controle de finanças pessoais construída com Spring Boot 3.5 e Java 21. Permite registrar receitas e despesas, organizar por categorias e filtrar transações por múltiplos critérios, com autenticação JWT e isolamento de dados por usuário.

## Stack

- **Java 21** + **Spring Boot 3.5**
- **PostgreSQL** + **Flyway** (migrations)
- **Spring Security** + **JWT** (jjwt 0.12)
- **MapStruct** (mapeamento de DTOs)
- **SpringDoc OpenAPI** (Swagger UI)
- **TestContainers** (testes de integração)

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `POSTGRES_DATASOURCE_URL` | URL JDBC do banco (ex: `jdbc:postgresql://localhost:5432/grainpay`) |
| `POSTGRES_DATASOURCE_USERNAME` | Usuário do banco |
| `POSTGRES_DATASOURCE_PASSWORD` | Senha do banco |
| `PORT` | Porta da aplicação (ex: `8080`) |
| `JWT_SECRET` | Chave secreta Base64 (mínimo 32 bytes) |
| `JWT_EXPIRATION` | Expiração do token em ms (padrão: `86400000` = 24h) |

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
| `POST` | `/api/transactions` | Criar transação |
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

### Categorias (`/api/categories`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/categories` | Listar categorias do usuário |
| `POST` | `/api/categories` | Criar categoria |
| `GET` | `/api/categories/{id}` | Buscar por ID |
| `PUT` | `/api/categories/{id}` | Atualizar |
| `DELETE` | `/api/categories/{id}` | Excluir |

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

### Criar transação

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EXPENSE",
    "amount": 150.00,
    "date": "2026-04-02",
    "description": "Supermercado",
    "paymentType": "PIX",
    "categoryId": 1
  }'
```

### Listar transações com filtros

```bash
curl "http://localhost:8080/api/transactions?type=EXPENSE&startDate=2026-04-01&endDate=2026-04-30&page=0&size=10" \
  -H "Authorization: Bearer <token>"
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

## TODO

- [x] CRUD de transações (Income/Expense unificados)
- [x] Autenticação JWT
- [x] Categorias de transação
- [x] Filtros dinâmicos com Specification
- [x] Swagger / OpenAPI
- [x] Global exception handler
- [x] Auditoria por usuário autenticado
- [ ] Contas/Carteiras
- [ ] Transações recorrentes (`@Scheduled`)
- [ ] Parcelas de cartão de crédito
- [ ] Dashboard / resumo financeiro
- [ ] Orçamentos por categoria
- [ ] Relatórios mensais e anuais
- [ ] Exportação CSV/PDF
- [ ] CI/CD
- [ ] Monitoramento com Grafana/Prometheus

## Contato

[deyvedev@gmail.com](mailto:deyvedev@gmail.com)
