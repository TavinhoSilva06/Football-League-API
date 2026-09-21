# Football League API

Uma **API REST profissional em Java/Spring Boot** para gerenciamento de competições de futebol, temporadas, times, jogadores, partidas e classificações automáticas.

## 📋 Sobre o Projeto

O **Football League API** é um projeto acadêmico desenvolvido como parte do curso de Engenharia de Software na **FATEC-SP**, com foco em aplicar padrões de arquitetura, design patterns e boas práticas de desenvolvimento.

A API permite:
- ✅ Cadastro e gerenciamento de campeonatos (Premier League, Serie A, Bundesliga, etc)
- ✅ Organização de temporadas (edições anuais)
- ✅ Gestão de times e elencos de jogadores
- ✅ Registro de partidas com placares e resultados
- ✅ **Cálculo automático de classificações** com critérios de desempate (pontos → vitórias → saldo de gols → gols pró → nome)
- ✅ Estatísticas de jogadores (gols, assistências, cartões)
- ✅ (Stretch Goal) Favoritos de usuários e autenticação por sessão
- ✅ (Stretch Goal) Paginação, filtros, Swagger/OpenAPI, cache, auditoria

## 🎯 Objetivo

Implementar um sistema de backend robusto, bem estruturado e extensível para suportar múltiplos campeonatos de futebol simultâneos, com regras de negócio complexas (cálculo de classificação) e um design que permita evolução futura (novos formatos de competição, eventos de jogo, análises).

## 🏗️ Arquitetura

### Estrutura de Camadas

```
Football-League-API/
└── Football-League-API/
    └── src/main/java/com/example/Football_League_API/
        ├── controller/          → REST endpoints (@RestController)
        ├── service/             → Lógica de negócio (@Service)
        ├── repository/          → Acesso a dados (JpaRepository)
        ├── entity/              → Entidades JPA (@Entity)
        ├── dto/
        │   ├── request/         → DTOs de entrada (POST/PUT)
        │   └── response/        → DTOs de saída (GET)
        ├── mapper/              → Conversão entity ↔ DTO (manual)
        ├── exception/           → Exceções customizadas + GlobalExceptionHandler
        ├── enum/                → Enums de domínio (TipoCampeonato, StatusPartida, etc)
        ├── config/              → Configurações Spring
        ├── validation/          → Validadores customizados
        └── security/            → Autenticação/Autorização (Nível 4)
```

### Decisões Arquiteturais Principais

1. **Classificação On-the-Fly** (sem tabela persistida)
   - A cada `GET .../classificacao`, calcula resultado atual a partir das partidas finalizadas
   - Elimina risco de contagem duplicada ao corrigir resultados
   - Extensível para formatos futuros (grupos, mata-mata)

2. **DTOs em Toda a API**
   - Entidades JPA nunca são serializadas diretamente
   - Mappers manuais (sem MapStruct) para simplicidade em 2 semanas

3. **Enums com STRING**
   - `@Enumerated(EnumType.STRING)` — legível no BD ao debugar

4. **Delete com Restrição**
   - Não posso deletar uma Campeonato que tem Temporadas (409)
   - Evita cascata silenciosa e perda de dados

## 🛠️ Tecnologias

| Categoria | Tecnologia | Versão |
|-----------|-----------|--------|
| **Linguagem** | Java | 21 LTS |
| **Framework** | Spring Boot | 4.2.0-SNAPSHOT |
| **Build** | Maven | 3.9.16 |
| **ORM** | Spring Data JPA / Hibernate | Automático (Parent) |
| **Segurança** | Spring Security | Automático (Parent) |
| **Sessão** | Spring Session JDBC | Automático (Parent) |
| **Banco de Dados** | PostgreSQL | 16-alpine |
| **Testing** | JUnit 5 | Automático (Parent) |
| **Utilidades** | Lombok | Automático (Parent) |

## 📦 Dependências Principais

```xml
<!-- Data & ORM -->
spring-boot-starter-data-jpa
spring-boot-starter-web
postgresql (driver)

<!-- Segurança & Sessão -->
spring-boot-starter-security
spring-boot-starter-session-jdbc

<!-- Desenvolvimento -->
spring-boot-devtools
spring-boot-docker-compose
lombok

<!-- Testes -->
spring-boot-starter-data-jpa-test
spring-boot-starter-security-test
spring-boot-starter-webmvc-test
```

## 🚀 Como Rodar

### Pré-requisitos

- **Java 21 LTS** (ou superior)
- **Maven 3.9.16+** (ou usar o wrapper `./mvnw`)
- **Docker + Docker Compose** (para PostgreSQL)

### Setup Inicial

1. **Clone o repositório**
   ```bash
   git clone https://github.com/TavinhoSilva06/Football-League-API.git
   cd Football-League-API/Football-League-API
   ```

2. **Inicie o PostgreSQL via Docker Compose**
   ```bash
   docker-compose up -d
   ```
   - Cria um container Postgres `postgres:16-alpine`
   - DB: `mydatabase`, User: `myuser`, Password: `secret`
   - Disponível em `localhost:5432` (porta dinâmica)

3. **Compile e rode a aplicação**
   ```bash
   ./mvnw clean spring-boot:run
   ```
   - Ou, em Windows:
   ```cmd
   mvnw.cmd clean spring-boot:run
   ```

4. **Acesse a API**
   - Base URL: `http://localhost:8080`
   - Exemplo: `GET http://localhost:8080/campeonatos`

5. **Ver logs**
   - SQL queries: Ativadas por padrão (`spring.jpa.show-sql=true`)
   - Hibernate logging: DEBUG habilitado em `application.properties`

## 📚 Endpoints Principais

### Campeonatos
- `GET /campeonatos` — Listar todos
- `GET /campeonatos/{id}` — Detalhe
- `POST /campeonatos` — Criar
- `PUT /campeonatos/{id}` — Atualizar
- `DELETE /campeonatos/{id}` — Deletar

### Temporadas
- `GET /campeonatos/{campeonatoId}/temporadas` — Listar por campeonato
- `GET /temporadas/{id}` — Detalhe
- `POST /campeonatos/{campeonatoId}/temporadas` — Criar
- `PUT /temporadas/{id}` — Atualizar
- `DELETE /temporadas/{id}` — Deletar

### Times
- `GET /times` — Listar todos
- `GET /times/{id}` — Detalhe
- `GET /times/{id}/jogadores` — Jogadores do time
- `POST /times` — Criar
- `PUT /times/{id}` — Atualizar
- `DELETE /times/{id}` — Deletar

### Jogadores
- `GET /jogadores/{id}` — Detalhe
- `POST /jogadores` — Criar
- `PUT /jogadores/{id}` — Atualizar
- `DELETE /jogadores/{id}` — Deletar

### Participações (Time-Temporada)
- `GET /temporadas/{temporadaId}/participacoes` — Listar
- `POST /temporadas/{temporadaId}/participacoes` — Adicionar time à temporada
- `DELETE /participacoes/{id}` — Remover

### Partidas
- `GET /partidas/{id}` — Detalhe
- `POST /partidas` — Criar
- `PUT /partidas/{id}/resultado` — Registrar/corrigir placar
- `GET /campeonatos/{id}/partidas?temporadaId=&rodada=&status=` — Filtros
- `GET /temporadas/{id}/partidas?rodada=&status=` — Por temporada
- `GET /times/{id}/partidas?temporadaId=&status=` — Por time

### Classificação
- `GET /temporadas/{id}/classificacao` — Tabela ordenada por desempate
- `GET /campeonatos/{id}/classificacao` — Tabela da temporada atual/EM_ANDAMENTO

### Estatísticas
- `GET /jogadores/{id}/estatisticas?temporadaId=` — Stats de jogador
- `GET /temporadas/{id}/artilharia?limit=10` — Top 10 goleadores
- `GET /temporadas/{id}/assistencias?limit=10` — Top 10 assistentes

## 🎯 Níveis de Implementação

### ✅ Nível 1 — CRUD Essencial
- [x] Entidades: Campeonato, Temporada, Time, Jogador
- [x] CRUD completo (GET, POST, PUT, DELETE)
- [x] Validações básicas
- [x] DataSeeder com dados iniciais

### ✅ Nível 2 — Relacionamentos
- [x] Participacao (Time-Temporada join)
- [x] Partida (com validações de mandante/visitante)
- [x] Filtros por temporada, time, rodada, status
- [x] Dados de seed expandidos

### ✅ Nível 3 — Regras de Negócio (META MÍNIMA)
- [x] Registro de resultados (PUT /partidas/{id}/resultado)
- [x] **Cálculo automático de classificação** com 5 critérios de desempate
- [x] Estatísticas de jogador (upsert)
- [x] Rankings (artilharia, assistências)
- [x] Validações de integridade

### 🔄 Nível 4 — Recursos Profissionais (STRETCH GOALS)
- [ ] Erro padronizado (ProblemDetail)
- [ ] Testes automatizados (ClassificacaoService, controllers)
- [ ] Autenticação por sessão (POST /usuarios, POST /auth/login)
- [ ] Favoritos de usuários (/usuarios/me/favoritos)
- [ ] Paginação/ordenação/filtros avançados
- [ ] OpenAPI/Swagger (`/swagger-ui.html`)
- [ ] Cache (@Cacheable em classificação/artilharia)
- [ ] Auditoria (@CreatedDate, @LastModifiedDate)

## 📅 Cronograma (14 Dias)

| Dia | Fase | Status | Progresso |
|-----|------|--------|-----------|
| 1 | Setup | ✅ 80% | Git + Config + Packages |
| 2-3 | Nível 1 Parte A | ⏳ | Campeonato + Temporada |
| 3 | Nível 1 Parte B | ⏳ | Time + Jogador + Seeder |
| 4 | CHECKPOINT 1 | ⏳ | Validação manual Nível 1 |
| 5 | Nível 2 Parte A | ⏳ | Participacao |
| 6 | Nível 2 Parte B | ⏳ | Partida |
| 7 | CHECKPOINT 2 | ⏳ | Validação manual Nível 2 |
| 8 | Nível 3 Parte A | ⏳ | Classificação (🔴 ALTO RISCO) |
| 9 | Nível 3 Parte B | ⏳ | Estatísticas |
| 10 | CHECKPOINT 3 | ⏳ | Validação Nível 3 (CONGELAMENTO) |
| 11+ | Nível 4 (Stretch) | ⏳ | Erros, Testes, Auth, etc |

## 📋 Documentação Completa

- **[PLANO_IMPLEMENTACAO.md](Football-League-API/docs/PLANO_IMPLEMENTACAO.md)** — Plano detalhado de 14 dias com decisões arquiteturais e riscos
- **[especificacao-football-league-api.md](Football-League-API/docs/especificacao-football-league-api.md)** — Especificação funcional completa (fonte da verdade)
- **[SETUP_NOTES.md](SETUP_NOTES.md)** — Notas sobre issues de ambiente (SSL Maven, Java 26 → Java 21, etc)

## 🐛 Troubleshooting

### Maven não consegue baixar dependências (SSL Error)
```
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path
```
**Solução**: 
- Verificar certificados JDK (`keytool -list -v -keystore $JAVA_HOME/lib/security/cacerts`)
- Atualizar JDK para versão com certificados atualizados
- Ou usar proxy/VPN que confia nos certificados Maven Central

### Postgres não inicia
```bash
docker-compose down
docker-compose up -d
```

### Porta 5432 já em uso
- Docker Compose usa porta dinâmica por padrão
- Spring Boot descobre automaticamente via `spring-boot-docker-compose`
- Se precisar de porta fixa, editar `compose.yaml`

## 👨‍💻 Desenvolvedor

**Autor**: [Seu Nome]  
**Instituição**: FATEC-SP  
**Disciplina**: Engenharia de Software  
**Data**: 2026-09-21

## 📄 Licença

MIT License — Veja [LICENSE](LICENSE) para detalhes.

## 🔗 Links Úteis

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Docker Compose](https://docs.docker.com/compose/)

---

**Última atualização**: 2026-09-21  
**Status**: Em desenvolvimento (Dia 1 de 14)