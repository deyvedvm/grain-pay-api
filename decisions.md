# Decisões arquiteturais — Grain Pay API

Registro das principais decisões técnicas, com contexto e trade-offs.

---

## 1. Spring Boot 3.5 + Java 21

**Decisão:** usar a versão mais recente do Spring Boot com Java 21 (LTS).

**Motivo:** Virtual threads (Project Loom) disponíveis via `spring.threads.virtual.enabled=true`, records do Java 16+, pattern matching e suporte a GraalVM Native Image no futuro. Stack bem suportada em produção.

**Trade-off:** requer Java 21 no ambiente de build e runtime — sem suporte a versões anteriores.

---

## 2. JWT stateless (sem sessão)

**Decisão:** autenticação via JWT com sessão `STATELESS`, sem `HttpSession` ou cookies.

**Motivo:** compatível com deploy em múltiplas instâncias sem sticky session ou armazenamento compartilhado de sessão.

**Trade-off:** não há revogação de token antes do vencimento. Expiração curta (24h) mitiga o risco.

---

## 3. Transações de Income e Expense unificadas

**Decisão:** entidade única `Transaction` com campo `type` (INCOME/EXPENSE) em vez de tabelas separadas.

**Motivo:** simplifica queries de dashboard, relatórios e filtros. As tabelas legadas `income` e `expense` foram removidas na migration `V1_0_6`.

**Trade-off:** campos específicos (`source` para income, `installments`/`isRecurring` para expense) ficam nullable na mesma tabela.

---

## 4. Flyway para migrations

**Decisão:** todas as mudanças de schema via Flyway com versionamento sequencial.

**Motivo:** garante rastreabilidade e reprodutibilidade do schema entre ambientes (local, CI, prod). Incompatível com `ddl-auto=create` do Hibernate em produção.

**Trade-off:** toda mudança de schema exige uma nova migration — sem atalhos via `ddl-auto=update`.

---

## 5. MapStruct para mapeamento de DTOs

**Decisão:** MapStruct em vez de mapeamento manual ou ModelMapper.

**Motivo:** geração de código em tempo de compilação — sem reflexão em runtime, mais rápido e com erros detectados no build.

**Trade-off:** campos calculados (ex: `progress`, `spent`, `percentage`) não são mapeados automaticamente e precisam de método manual no service.

---

## 6. Cálculo de `spent` e `progress` no service, não no banco

**Decisão:** `BudgetService` e `GoalService` calculam percentuais em Java, não em SQL.

**Motivo:** lógica de negócio centralizada e testável unitariamente sem banco.

**Trade-off:** para listas grandes, pode gerar N+1 queries. Aceitável no volume esperado para um app pessoal.

---

## 7. Importação de extrato: CSV only (MVP)

**Decisão:** suporte apenas a CSV no MVP. OFX descartado.

**Motivo:** Commons CSV já estava no projeto (usado na exportação). OFX exige parser específico e os principais bancos brasileiros permitem exportar em CSV.

**Trade-off:** usuários de bancos que só exportam OFX precisarão converter manualmente.

---

## 8. Vínculo automático de categoria/conta por substring

**Decisão:** ao importar CSV, categoria e conta são vinculadas por `containsIgnoreCase` do `description`.

**Motivo:** zero configuração necessária — funciona imediatamente se o nome da categoria/conta aparecer na descrição da transação.

**Trade-off:** match pode ser impreciso (ex: categoria "Inter" pode casar com "Internacional"). Primeiro match vence; sem precedência ou pesos.

---

## 9. Detecção de duplicatas por (user_id, date, amount, description)

**Decisão:** duplicata = mesma combinação de usuário, data, valor e descrição.

**Motivo:** simples, sem configuração, cobre o caso mais comum de re-importação do mesmo extrato.

**Trade-off:** não detecta duplicatas com descrição levemente diferente (ex: com/sem acentos, espaços extras). Falsos positivos possíveis se o usuário tiver duas transações idênticas no mesmo dia.

---

## 10. Deploy no Fly.io (MVP) → AWS ECS Fargate (v2.0)

**Decisão:** Fly.io para o MVP por simplicidade e custo zero no tier free. AWS ECS Fargate planejado para v2.0.

**Motivo:** Fly.io não requer configuração de VPC, load balancer ou IAM para começar. Terraform para AWS já está preparado no repositório.

**Trade-off:** migração entre provedores exige ajuste nas variáveis de ambiente e possivelmente no Dockerfile.
