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

3. **Compile e rode a aplicação com Profile DEV**
   ```bash
   ./mvnw clean spring-boot:run -Dspring.profiles.active=dev
   ```
   - Ou, em Windows PowerShell:
   ```powershell
   ./mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev"
   ```
   - Ou via IntelliJ: Configurar em **Run** → **Edit Configurations** → Maven → `-Dspring.profiles.active=dev`

4. **Aguarde o DataSeeder popular o banco**
   - A aplicação cria automaticamente:
     - 3 campeonatos (Premier League, Série A, Bundesliga)
     - 3 temporadas
     - 8 times
     - 14 jogadores
     - 8 participações

5. **Acesse a API**
   - Base URL: `http://localhost:8080`
   - Exemplo: `GET http://localhost:8080/campeonatos`

6. **Usar Postman para testar (RECOMENDADO)**
   - Abra Postman e importe: `Football-League-API/docs/postman-collection.json`
   - Crie um Environment com `base_url = http://localhost:8080`
   - Todos os 23 endpoints estão organizados e prontos para testar

7. **Ver logs**
   - SQL queries: Ativadas por padrão (`spring.jpa.show-sql=true`)
   - Hibernate logging: DEBUG habilitado em `application.properties`
   - Procure por "DataSeeder" para confirmar a população de dados

## 📚 Endpoints Principais

### Campeonatos ✅ IMPLEMENTADO
- `GET /campeonatos` — Listar todos
- `GET /campeonatos/{id}` — Detalhe
- `GET /campeonatos/{id}/times` — Times participantes ✨ NOVO
- `POST /campeonatos` — Criar
- `PUT /campeonatos/{id}` — Atualizar
- `DELETE /campeonatos/{id}` — Deletar

### Temporadas ✅ IMPLEMENTADO
- `GET /temporadas` — Listar todas (com numRodadas) ✨ NOVO
- `GET /campeonatos/{campeonatoId}/temporadas` — Listar por campeonato
- `GET /temporadas/{id}` — Detalhe
- `POST /campeonatos/{campeonatoId}/temporadas` — Criar (com numRodadas)
- `PUT /temporadas/{id}` — Atualizar
- `DELETE /temporadas/{id}` — Deletar
- `PUT /temporadas/{id}/encerrar` — Encerrar quando todas rodadas forem jogadas ✨ NOVO

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

### Participações (Time-Temporada) ✅ IMPLEMENTADO
- `GET /temporadas/{temporadaId}/participacoes` — Listar participações de uma temporada
- `POST /temporadas/{temporadaId}/participacoes` — Adicionar time à temporada
- `DELETE /participacoes/{id}` — Remover participação

### Partidas ✅ IMPLEMENTADO
- `GET /partidas` — Listar todas as partidas
- `GET /partidas/{id}` — Detalhe
- `POST /temporadas/{temporadaId}/partidas` — Criar (com validação de rodadas)
- `PUT /partidas/{id}` — Atualizar resultado/status
- `GET /temporadas/{temporadaId}/partidas?rodada=&status=` — Por temporada com filtros
- `GET /times/{timeId}/partidas?temporadaId=` — Por time com filtro de temporada
- `DELETE /partidas/{id}` — Deletar partida

### Classificação
- `GET /temporadas/{id}/classificacao` — Tabela ordenada por desempate
- `GET /campeonatos/{id}/classificacao` — Tabela da temporada atual/EM_ANDAMENTO

### Estatísticas
- `GET /jogadores/{id}/estatisticas?temporadaId=` — Stats de jogador
- `GET /temporadas/{id}/artilharia?limit=10` — Top 10 goleadores
- `GET /temporadas/{id}/assistencias?limit=10` — Top 10 assistentes

## 🎯 Níveis de Implementação

### ✅ Nível 1 — CRUD Essencial (COMPLETO)
- [x] Entidades: Campeonato, Temporada, Time, Jogador, EstatisticaJogador
- [x] CRUD completo (GET, POST, PUT, DELETE) para Campeonato, Temporada, Time, Jogador
- [x] Participacao: GET, POST, DELETE
- [x] Validações básicas (@NotBlank, @NotNull)
- [x] DataSeeder com dados realistas (3 campeonatos, 8 times, 14 jogadores)
- [x] GlobalExceptionHandler (404, 400, 409)
- [x] Postman Collection com 23 endpoints e variáveis de ambiente

### ✅ Nível 2 — Relacionamentos e Partidas (100% COMPLETO)
- [x] Partida: Entidade completa com todos os campos
- [x] Enum StatusPartida (AGENDADA, EM_ANDAMENTO, FINALIZADA, ADIADA, CANCELADA)
- [x] PartidaService com validações (mandante ≠ visitante, temporada/times válidos)
- [x] PartidaController com CRUD completo + 6 endpoints de filtro
- [x] Endpoint GET /partidas para listar todas as partidas
- [x] DTOs PartidaRequestDto e PartidaResponseDto com validações
- [x] Mapper PartidaMapper para conversão entity ↔ DTO
- [x] Repository PartidaRepository com múltiplos métodos de busca
- [x] DataSeeder expandido com 7+ Partidas de exemplo
- [x] Postman Collection atualizada com endpoints de Partida
- [x] Validação manual 100% (todos os endpoints testados)

### ⏳ Nível 3 — Regras de Negócio (META MÍNIMA)
- [ ] Registro de resultados (PUT /partidas/{id}/resultado)
- [ ] **Cálculo automático de classificação** com 5 critérios de desempate
- [ ] Endpoints de estatísticas + Rankings (artilharia, assistências)
- [ ] Validações de integridade

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
| 1 | Setup | ✅ 100% | Git + Config + Packages + Java 21 |
| 2 | Nível 1 Parte A | ✅ 100% | Campeonato + Temporada |
| 3 | Nível 1 Parte B | ✅ 100% | Time + Jogador + Participacao + Seeder + Postman |
| 4 | CHECKPOINT 1 | ✅ 100% | Validação manual Nível 1 (todos endpoints OK) |
| 5 | Nível 2 | ✅ 100% | Partida completa (CRUD + 6 filtros + validações) |
| 6 | Nível 2 Polish | ✅ 100% | Endpoint GET /partidas + Postman + validação |
| 7 | CHECKPOINT 2 | ✅ 100% | Validação manual Nível 2 (todos endpoints OK) |
| 8 | Nível 3 Parte A | ⏳ | Classificação (🔴 ALTO RISCO) |
| 9 | Nível 3 Parte B | ⏳ | Endpoints de Estatísticas + Rankings |
| 10 | CHECKPOINT 3 | ⏳ | Validação Nível 3 (CONGELAMENTO) |
| 11 | Nível 4 #1+#2 | ⏳ | Testes + Erros Padronizados |
| 12 | Nível 4 #3 | ⏳ | Autenticação + Favoritos |
| 13 | Buffer / Stretch | ⏳ | Paginação, Swagger, etc |
| 14 | Polimento Final | ⏳ | Regressão, README, Push |

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

## 🎯 Funcionalidades Extras Implementadas (Além do Plano)

Durante o Checkpoint 2, foram adicionadas funcionalidades extras para melhorar a robustez:

### ✨ Novos Endpoints
- `GET /temporadas` — Lista todas as temporadas com numRodadas
- `GET /campeonatos/{id}/times` — Lista times de um campeonato
- `PUT /temporadas/{id}/encerrar` — Encerra temporada quando todas rodadas são jogadas

### ✨ Validações Avançadas
- **Validação de Rodadas**: Não permite criar Partida com rodada > numRodadas
- **Encerramento Automático**: Temporada só encerra quando TODAS rodadas forem finalizadas
- **Tratamento de Erros**: `IllegalArgumentException` retorna HTTP 400 com mensagem customizada

### ✨ Estrutura de Dados Aprimorada
- Campo `numRodadas` em Temporada (38 para Premier League/Série A, 34 para Bundesliga)
- DTOs (Request/Response) atualizados com numRodadas
- DataSeeder com 110+ partidas distribuídas apropriadamente

### ✨ Código Comentado
- GlobalExceptionHandler com explicações detalhadas
- Mappers com documentação dos fluxos de conversão
- Services com comentários em métodos críticos

---

**Última atualização**: 2026-09-24  
**Status**: Em desenvolvimento (Dias 1-7 de 14 completos - 50% do projeto + Melhorias Extras)  
**Próxima Meta**: Iniciar Nível 3 (Classificação Automática - Dia 8) 🎯