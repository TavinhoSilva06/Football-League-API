# Football League API — Plano de Implementação (14 dias)

## 📋 Sumário Executivo

Este é o plano detalhado para implementar uma **API REST profissional em Java/Spring Boot** para gerenciar competições de futebol em **14 dias corridos** (2 semanas).

- **Escopo mínimo aceitável**: Nível 3 (CRUD + relacionamentos + regras de negócio com classificação automática)
- **Stretch goals**: Nível 4 (autenticação, favoritos, paginação, Swagger, cache, auditoria)
- **Disponibilidade**: Solo, 4–8h/dia variável
- **Prioridade se time apertar**: Cortar Nível 4 de baixo para cima (#7 auditoria → #6 cache → #4 paginação)

---

## 🎯 Contexto do Projeto

O projeto começou com um esqueleto gerado pelo Spring Initializr:
- Apenas a classe `FootballLeagueApiApplication` (entry point)
- Um teste vazio
- Dependências já declaradas no `pom.xml`: Data JPA, Security, Session-JDBC, Web MVC, Postgres, Lombok
- `compose.yaml` já configurado para somar um container Postgres
- Uma especificação completa em `docs/especificacao-football-league-api.md`

**Nenhuma entidade, repositório, serviço, controller ou DTO existe ainda.**

---

## 🏗️ Decisões de Arquitetura

### Estrutura de Pacotes (Camadas)

Sob `com.example.Football_League_API`:

```
controller/              → REST endpoints
service/                 → Lógica de negócio
repository/              → Acesso a dados (JPA)
entity/                  → Entidades JPA
dto/
  ├── request/           → Dtos de entrada (POST/PUT)
  └── response/          → DTOs de saída (GET)
mapper/                  → Conversão entity ↔ DTO (manual)
exception/               → Exceções customizadas + GlobalExceptionHandler
config/                  → Spring @Configuration classes
security/                → Nível 4: autenticação/autorização
validation/              → Custom validators para validações cross-field
enum/                    → Enums de domínio (tipo campeonato, status, etc)
```

**Justificativa**: Para ~9 entidades / ~30 endpoints em 2 semanas solo, a estrutura em camadas é mais simples e navegável que uma estrutura por feature. Convencional no Spring Boot.

### ID e Chaves

- **ID**: `Long` com `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- **Vantagem**: Mapeia nativamente às sequences do Postgres; IDs sequenciais legíveis em testes manuais (vs UUIDs)

### Enums

Sempre `@Enumerated(EnumType.STRING)` — **nunca** `ORDINAL`:
- `ORDINAL` é frágil (reordenar quebra o banco)
- String é legível no banco ao debugar

**Enums planejados**:
- `TipoCampeonato`: LIGA_NACIONAL, CONTINENTAL, COPA
- `FormatoCampeonato`: PONTOS_CORRIDOS (futuro: GRUPOS, MATA_MATA)
- `StatusTemporada`: PLANEJADA, EM_ANDAMENTO, ENCERRADA
- `StatusPartida`: AGENDADA, EM_ANDAMENTO, FINALIZADA, ADIADA, CANCELADA
- `PosicaoJogador`: GOLEIRO, DEFESA, MEIO_CAMPO, ATACANTE (exemplos)
- `Papel` (Nível 4): ADMIN, USUARIO
- `TipoFavorito` (Nível 4): CAMPEONATO, TIME, JOGADOR

### Classificação (Standings) — Decisão Crítica

**Calculada on-the-fly, sem tabela persistida**:

A cada `GET .../classificacao`, o serviço:
1. Busca todas as `Participacao` da temporada (garante times com 0 jogos aparecerem)
2. Agrega resultados das `Partida` com status `FINALIZADA`
3. Aplica `PontosCorridosTiebreakComparator` isolado
4. Ordena e retorna

**Vantagens**:
- Nenhum estado derivado em cache → nenhum risco de contagem duplicada ao corrigir um resultado
- Simples: um passe único sobre os dados atuais
- Extensível: `TiebreakStrategyResolver.resolve(Campeonato)` é o ponto para formatos futuros (grupos/mata-mata) sem redesenhar nada
- Performático o suficiente (e `@Cacheable` pode ser adicionado depois se necessário, Nível 4)

### EstatisticaJogador

Upsert direto via `PUT /jogadores/{id}/estatisticas`, **não derivado de eventos**:
- A spec marca gols/cartões como "eventos futuros" explicitamente
- Não construir rastreamento de eventos neste ciclo
- Mantém o escopo controlado para 2 semanas

### DTOs em Toda a API

Entidades JPA **nunca** são serializadas diretamente:
- Request DTOs: reusadas para create + update onde os campos coincidem
- Response DTOs: estrutura simples, sem entidades nested
- Exemplo: `PartidaResponse` embute um pequeno `TimeResumoResponse(id, nome)` para mandante/visitante

**Mapeamento manual** (sem MapStruct/ModelMapper):
- Não somar dependências novas sobre um parent SNAPSHOT instável
- Métodos mapper simples o suficiente para 2 semanas

### Schema & Migrations

`spring.jpa.hibernate.ddl-auto=update` — **sem Flyway/Liquibase**:
- Schema muda todos os dias durante Nível 1–3
- Ferramenta de migração seria overhead puro
- Dados de exemplo via `DataSeeder` (`CommandLineRunner`, `@Profile("dev")`), não `data.sql`

### Compose & Postgres

- **Imagem**: `postgres:16-alpine` (pinned, não `latest`)
- **Credenciais**: db=`mydatabase`, user=`myuser`, password=`secret`
- **Porta**: dinâmica (`'5432'`) — o `spring-boot-docker-compose` descobre a porta real sozinho

### Delete Behavior

**Restringir por padrão** (409 se houver filhos), nunca cascade silencioso:
- Dados esportivos não devem sumir em cascata
- Exemplos: não posso deletar um Time que tem Participações/Partidas

### Git & Commits

- **Repo**: Inicializado na pasta **externa** `Football-League-API/` (captura `.idea/` + módulo Maven juntos)
- **Branch padrão**: `main`, trabalhar direto nela para Nível 1–3 (baixo risco de reverter)
- **Commits**: ~1–4 por dia, por fatia vertical coesa (entity+repo+service+controller de um recurso)
- **Mensagens**: `feat(campeonato): ...`, `fix(partida): ...`, `test(classificacao): ...`
- **Branch especial**: `feature/auth-favoritos` só no Dia 12 (autenticação, item mais arriscado)

---

## 📅 Plano Dia a Dia (14 Dias)

> **Nota importante**: Os "dias" abaixo são um ritmo mínimo (~4-6h). Sempre que sobrar tempo/energia
> (até ~8h), adiante o trabalho do próximo dia — a **ordem de dependência** entre itens é o que importa,
> não o rótulo "Dia N". Os únicos pontos que vale a pena respeitar como parada deliberada são
> os **checkpoints** (Dias 4, 7, 10) e o commit de congelamento do Nível 3 no Dia 10.
> Tempo adiantado vira **buffer extra somado ao Dia 13**.

### **Dia 1 — Setup (pesado, ~6-7h)** ✅ 80% CONCLUÍDO

**Checklist**:
- [x] Verificar `./mvnw compile` resolvendo dependências com JDK 21 instalado (bloqueador #1) — ⚠️ Ajuste: Java 26 → Java 21 LTS
- [x] `git init` na pasta externa `Football-League-API/`
- [x] `git remote add origin https://github.com/TavinhoSilva06/Football-League-API.git`
- [x] `git fetch origin` e `git checkout -b main origin/main`
- [x] Ajustar `.gitignore` (adicionar `*.log`, `.env`, `application-local*.properties`)
- [x] Atualizar `pom.xml` para Java 21 LTS (se Java 26 não estiver disponível)
- [x] Fixar `compose.yaml` para `postgres:16-alpine`
- [x] Configurar `application.properties`:
  - [x] `spring.jpa.hibernate.ddl-auto=update`
  - [x] `spring.jpa.show-sql=true`
  - [x] `spring.jpa.properties.hibernate.format_sql=true`
  - [x] Datasource (url, user, password do compose)
  - [x] SQL + Hibernate logging (DEBUG)
- [x] Criar estrutura de pacotes (controller, service, repository, entity, dto, mapper, exception, config, validation, enum)
- [x] Commitar: "chore(setup): initial project structure and configuration"
- [x] Push para origin/main

**Artefatos**:
- Projeto compilável (`./mvnw compile` verde)
- Postgres pinned e documentado
- Estrutura de pacotes criada
- Histórico git inicializado

---

### **Dia 2 — Nível 1, Parte A (~6h)**

**Objetivo**: Primeiras entidades + CRUD básico para Campeonato e Temporada.

**Checklist**:
- [ ] Criar enums: `TipoCampeonato`, `FormatoCampeonato`, `StatusTemporada` (package `enum/`)
- [ ] Entidade `Campeonato`:
  - Fields: `id`, `nome` (unique, not null), `pais`, `tipo` (enum), `descricao`, `formato` (enum)
  - 1:N com `Temporada`
- [ ] Repository `CampeonatoRepository` (extends `JpaRepository`)
- [ ] DTOs: `CampeonatoRequestDto`, `CampeonatoResponseDto`
- [ ] Mapper: `CampeonatoMapper` (mão)
- [ ] Service `CampeonatoService`: 
  - `findAll()`, `findById(id)`, `create(dto)`, `update(id, dto)`, `delete(id)`
  - Validações básicas (not null, unique)
- [ ] Controller `CampeonatoController`:
  - `GET /campeonatos`, `GET /campeonatos/{id}`, `POST /campeonatos`, `PUT /campeonatos/{id}`, `DELETE /campeonatos/{id}`
- [ ] Entidade `Temporada`:
  - Fields: `id`, `campeonato` (M:1), `nome` (e.g. "2026/27"), `dataInicio`, `dataFim`, `status` (enum)
  - Unique constraint: (campeonato_id, nome)
  - 1:N com `Participacao` e `Partida`
- [ ] Repository, DTOs, Mapper, Service, Controller para Temporada (similar a Campeonato)
  - Endpoints: `GET/POST /campeonatos/{campeonatoId}/temporadas`, `GET/PUT/DELETE /temporadas/{id}`
- [ ] GlobalExceptionHandler mínimo (404 NotFound, 400 BadRequest/validation, 409 DataIntegrityViolation)
- [ ] Commitar: "feat(campeonato,temporada): level 1 crud"

---

### **Dia 3 — Nível 1, Parte B (~6h)**

**Objetivo**: CRUD de Time e Jogador, primeiros dados de seed.

**Checklist**:
- [ ] Enum `PosicaoJogador`
- [ ] Entidade `Time`:
  - Fields: `id`, `nome` (unique), `sigla`, `pais`, `cidade`, `estadio`, `escudoUrl`
  - 1:N com `Jogador`
- [ ] Repository, DTOs, Mapper, Service, Controller para Time
  - Endpoints: `GET /times`, `GET /times/{id}`, `POST/PUT/DELETE /times/{id}`
- [ ] Entidade `Jogador`:
  - Fields: `id`, `time` (M:1, nullable), `nome`, `nacionalidade`, `posicao` (enum), `numeroCamisa`, `dataNascimento`
  - 1:N com `EstatisticaJogador`
- [ ] Repository, DTOs, Mapper, Service, Controller para Jogador
  - Endpoints: `GET /jogadores/{id}`, `GET /times/{id}/jogadores`, `POST/PUT/DELETE /jogadores/{id}`
- [ ] `DataSeeder` (`CommandLineRunner`, `@Profile("dev")`):
  - Cria ~3 campeonatos (Premier League, Serie A, Bundesliga)
  - 1-2 temporadas para cada
  - 6-8 times distribuídos
  - 15-20 jogadores com posições variadas
- [ ] Commitar: "feat(time,jogador): level 1 crud + data seeder"

---

### **Dia 4 — CHECKPOINT 1 (leve, ~4-5h)**

**Objetivo**: Validação manual de tudo que foi implementado nos Dias 1-3.

**Checklist**:
- [ ] Testar manualmente todos os Campeonatos endpoints (happy path + validação + 404 + duplicate)
- [ ] Testar todos os Temporadas endpoints
- [ ] Testar todos os Times endpoints
- [ ] Testar todos os Jogadores endpoints (incluindo filtro por time)
- [ ] Validar comportamento de delete com filhos:
  - Tentar deletar um Campeonato que tem Temporadas → 409
  - Tentar deletar um Time que será participante de uma Partida → 409 (quando Partida existir)
- [ ] Verificar que o seeder popula dados sensatos
- [ ] **Nenhuma feature nova**, só correção de bugs

**Saída**:
- Todos os endpoints Nível 1 funcionando
- Comportamento delete definido e consistente
- Confiança que a base está sólida para Nível 2

---

### **Dia 5 — Nível 2, Parte A (~6-7h)**

**Objetivo**: Participação (ligação entre Time e Temporada).

**Checklist**:
- [ ] Entidade `Participacao`:
  - Fields: `id`, `temporada` (M:1), `time` (M:1)
  - Unique constraint: (temporada_id, time_id)
  - Sem campos de pontuação/stats (esses são calculados on-the-fly)
- [ ] Repository, DTOs, Mapper, Service, Controller para Participacao
  - Service: validar que (temporada, time) é único → 409 se duplica
  - Endpoints: `GET /temporadas/{temporadaId}/participacoes`, `POST /temporadas/{temporadaId}/participacoes`, `DELETE /participacoes/{id}`
- [ ] Expandir seeder para adicionar Participacoes (cada time adicionado a cada temporada)
- [ ] Commitar: "feat(participacao): level 2 team-season membership"

---

### **Dia 6 — Nível 2, Parte B (pesado, ~7-8h)**

**Objetivo**: Partidas com validações rigorosas e filtros.

**Checklist**:
- [ ] Enum `StatusPartida`
- [ ] Entidade `Partida`:
  - Fields: `id`, `temporada` (M:1), `rodada` (int), `dataHora`, `timeMandante` (M:1 Time), `timeVisitante` (M:1 Time), `golsMandante` (nullable Integer), `golsVisitante` (nullable Integer), `status` (enum), `local` (nullable)
  - Validações (no construtor ou setter):
    - `timeMandante != timeVisitante`
    - Ambos os times devem ter `Participacao` na mesma temporada
    - Placares nullable na criação, só setáveis para status FINALIZADA/EM_ANDAMENTO
- [ ] Repository, DTOs, Mapper, Service para Partida
- [ ] PartidaService.criar(): validações acima + lógica de criação
- [ ] Controller `PartidaController`:
  - `POST /partidas` (cria partida)
  - `GET /partidas/{id}` (detalhe)
  - `GET /campeonatos/{campeonatoId}/partidas?temporadaId=&rodada=&status=` (filtros)
  - `GET /temporadas/{temporadaId}/partidas?rodada=&status=`
  - `GET /times/{timeId}/partidas?temporadaId=&status=`
  - (Filtros por query params, **não** paginação — isso é Nível 4)
- [ ] Expandir seeder com Partidas (alguns AGENDADA, alguns FINALIZADA com resultados)
- [ ] Commitar: "feat(partida): level 2 match creation with validations"

---

### **Dia 7 — CHECKPOINT 2 (~5h)**

**Objetivo**: Validação de relacionamentos Nível 2.

**Checklist**:
- [ ] Testar participações: adicionar time a temporada, validar unique, tentar duplicar → 409
- [ ] Testar partidas: criar com times válidos, filtrar por temporada/rodada/status
- [ ] Testar validações: mandante==visitante → 400, time não participante → 400, etc
- [ ] Expandir seeder com dados mais realistas (partidas entre times da mesma temporada)
- [ ] **Nenhuma feature nova**, só correção de bugs

**Saída**:
- Nível 2 completamente funcional
- Dados de seed prontos para o Dia 8 (com alguns resultados já finalizados)

---

### **Dia 8 — Nível 3, Parte A: Resultado + Classificação (ALTO RISCO, ~7-8h)**

⚠️ **Este é o dia de maior risco do projeto. Agendar o seu melhor horário.**

**Objetivo**: Registrar resultados em partidas e calcular classificação automaticamente.

**Checklist**:
- [ ] `PartidaService.registrarResultado(id, novosMandante, novosVisitante)`:
  - Idempotente: pode corrigir um resultado já existente
  - Ao mudar um resultado, a classificação anterior não deve ficar duplicada (justamente por não persistir tabela)
  - Muda status para FINALIZADA se necessário
- [ ] Endpoint: `PUT /partidas/{id}/resultado` com body `{ placarMandante, placarVisitante }`
- [ ] `ClassificacaoService` (arquivo de maior risco):
  - `calcularClassificacao(temporadaId)`:
    1. Buscar todas as `Participacao` para a temporada (garante times com 0 jogos)
    2. Para cada `Partida` FINALIZADA da temporada:
       - Atualizar pontos, vitórias, empates, derrotas, gols pró/contra de cada time
    3. Aplicar `PontosCorridosTiebreakComparator`
    4. Atribuir posição
    5. Retornar lista de `ClassificacaoEntrada`
  - `PontosCorridosTiebreakComparator`:
    - Ordena por: (1) pontos DESC, (2) vitórias DESC, (3) saldo de gols DESC, (4) gols pró DESC, (5) nome time ASC
  - `TiebreakStrategyResolver.resolve(Campeonato)`: retorna o comparator (ponto de extensão para formatos futuros)
- [ ] Endpoints:
  - `GET /temporadas/{id}/classificacao`
  - `GET /campeonatos/{id}/classificacao` (resolve para temporada EM_ANDAMENTO/atual, com override `?temporadaId=`)
  - **Documentar a decisão** de qual temporada usar quando um campeonato tem múltiplas
- [ ] Testes unitários para `ClassificacaoService` (ver Seção Testes abaixo):
  - Tabela básica
  - Desempate resolvido em cada um dos 5 níveis
  - Time com 0 jogos aparece
  - Corrigir um resultado já finalizado não duplica contagem
- [ ] Commitar: "feat(classificacao): level 3 standings calculation"

**Riscos mitigados**:
- Um passe único sem persistência de estado derivado
- Testes unitários desde já
- Isolamento da lógica de desempate

---

### **Dia 9 — Nível 3, Parte B: Estatísticas (~6-7h)**

**Objetivo**: Estatísticas de jogadores por temporada e rankings.

**Checklist**:
- [ ] Entidade `EstatisticaJogador`:
  - Fields: `id`, `jogador` (M:1), `temporada` (M:1), `jogos`, `gols`, `assistencias`, `cartoesAmarelos`, `cartoesVermelhos`
  - Unique constraint: (jogador_id, temporada_id)
- [ ] Repository, DTOs, Mapper, Service para EstatisticaJogador
- [ ] Service: upsert (se não existe, cria; se existe, atualiza)
- [ ] Endpoints:
  - `PUT /jogadores/{id}/estatisticas?temporadaId=` (upsert)
  - `GET /jogadores/{id}/estatisticas?temporadaId=`
  - `GET /temporadas/{id}/artilharia?limit=10` (top 10 goleadores)
  - `GET /temporadas/{id}/assistencias?limit=10` (top 10 assistentes)
- [ ] Revisar validação em todos os serviços:
  - Chaves únicas respeitadas
  - Campos obrigatórios presentes
  - Consistência entre entidades relacionadas
- [ ] Expandir seeder com estatísticas
- [ ] Commitar: "feat(estatistica): level 3 player stats and rankings"

---

### **Dia 10 — CHECKPOINT 3 / CONGELAMENTO DO NÍVEL 3 (~6h)**

**Objetivo**: Validação end-to-end e marcação do ponto seguro de parada.

**Checklist**:
- [ ] Regressão manual **completa** ponta a ponta:
  1. Criar campeonato
  2. Criar temporada
  3. Criar times
  4. Adicionar participações
  5. Criar partidas
  6. Registrar resultados
  7. Verificar classificação ordenada corretamente
  8. Verificar rankings de artilharia/assistências
- [ ] Salvar conjunto de requests manual em arquivo `requests.http` (ou Postman collection) para repetibilidade
- [ ] Começar testes automatizados se houver tempo (pode escorregar para Dia 11)
- [ ] **Commit marco**: "Nível 3 complete and stable"
- [ ] Push para origin/main
- [ ] ⚠️ **Este é o ponto de parada seguro**: se o cronograma apertar agora, o Nível 3 já satisfaz a entrega mínima.

**Saída**:
- Nível 3 100% funcional e validado
- Projeto tem valor real (CRUD + negócio funcionando)
- Histórico git limpo e meaningful
- Buffer mental para Nível 4 (stretch goals)

---

### **Dia 11 — Stretch #1+#2: Erros Padronizados + Testes (~6-7h)**

**Objetivo**: Melhorar robustez com tratamento de erro consistente e automação de testes.

**Checklist**:
- [ ] Evoluir `GlobalExceptionHandler`:
  - Formato `ProblemDetail` padronizado: `{ timestamp, status, message, path, errors: [...] }`
  - Erros por campo para validação
  - Aplicar a todas as exceções da API
- [ ] Completar Bean Validation annotations em **todos** os DTOs de request:
  - `@NotNull`, `@NotBlank`, `@Size`, `@Pattern`, etc
  - Custom validators para regras cross-field (ex: dataInicio < dataFim)
- [ ] Suite de testes automatizados:
  - **Priority 1**: `ClassificacaoServiceTest` (5 critérios de desempate + no-double-count)
  - **Priority 2**: `PartidaServiceTest` (validações: mandante!=visitante, time fora temporada, etc)
  - **Priority 3**: `ParticipacaoServiceTest` (unique constraint)
  - **Priority 4**: 2-3 `@WebMvcTest` para `CampeonatoController` e `PartidaController` (happy path + 404/400)
- [ ] Rodar `./mvnw test` verde
- [ ] Commitar: "refactor(errors, tests): standardized error format and unit tests"

**Budget**: ~1.5-2 dias total (iniciado no Dia 10, grosso no Dia 11); não deixar vazar para Dia 12.

---

### **Dia 12 — Stretch #3: Autenticação por Sessão + Favoritos (SEGUNDO MAIOR RISCO, ~7-8h)**

⚠️ **Nível 4 começa aqui. Branch `feature/auth-favoritos` a partir daqui.**

**Checklist**:
- [ ] **Entidade `Usuario`**:
  - Fields: `id`, `nome`, `email` (unique, not null), `senhaHash`, `papel` (enum ADMIN/USUARIO), `criadoEm`
- [ ] **Spring Security config**:
  - `SecurityConfig` com `BCryptPasswordEncoder`
  - `UserDetailsService` buscando `Usuario` do banco
  - GET endpoints públicos; POST/PUT/DELETE restritos a ADMIN (em `@PreAuthorize`)
  - `/usuarios/me/**` requer autenticação
- [ ] **Endpoints de autenticação**:
  - `POST /usuarios` (sign up, hash de senha)
  - `POST /auth/login` (valida usuário+senha, cria sessão)
- [ ] **Entidade `Favorito`**:
  - Fields: `id`, `usuario` (M:1), `tipo` (enum CAMPEONATO/TIME/JOGADOR), `campeonatoId`/`timeId`/`jogadorId` (nullable, validar que exatamente um é setado)
  - Unique constraint: (usuario_id, tipo, campeonatoId/timeId/jogadorId) conforme aplicável
- [ ] **Endpoints de favoritos**:
  - `GET /usuarios/me/favoritos` (lista favoritos do usuário autenticado)
  - `POST /usuarios/me/favoritos` (adiciona favorito)
  - `DELETE /usuarios/me/favoritos/{favoritoId}` (remove favorito)
- [ ] ⚠️ **ATENÇÃO EXPLÍCITA**: `spring-session-jdbc` cria tabelas `SPRING_SESSION*` que **não** são gerenciadas pelo Hibernate.
  - Configurar `spring.session.jdbc.initialize-schema=always` em `application.properties`
  - Senão: login dá 500 silenciosamente
- [ ] Testar manualmente com cliente que preserve cookies (Postman, curl com `-b/-c`)
- [ ] Só fazer merge para `main` **após validação manual com sucesso**
- [ ] Commitar no merge: "feat(auth,favoritos): level 4 session-based auth and user favorites"

---

### **Dia 13 — Buffer / Recuperação (flexível, 4-8h)**

**Objetivo**: Absorver atrasos e atacar próximos stretch items.

**Checklist**:
1. **Primeiro**: Absorver qualquer atraso dos Dias 1, 8, 12
2. **Se no prazo**, atacar restante do stretch na ordem de prioridade:

   **#4 — Paginação / Ordenação / Filtros** (~2-3h):
   - Converter endpoints de listagem para `Pageable`
   - `GET /campeonatos?page=0&size=20&sort=nome,asc`
   - Aplicar a Campeonatos, Times, Jogadores, Partidas
   - Commitar: "feat(pagination): add page/size/sort to list endpoints"

   **#5 — OpenAPI / Swagger** (~1.5-2h):
   - Adicionar `springdoc-openapi` (verificar compatibilidade com parent 4.2.0-SNAPSHOT; se não existir, cortar em 30min)
   - `@Operation`, `@Parameter`, `@Schema` nas classes + endpoints
   - `/swagger-ui.html` acessível
   - Commitar: "docs(swagger): add openapi documentation"

3. **Se ainda sobrar tempo**, cada item em commit próprio.

---

### **Dia 14 — Polimento Final (~5-6h)**

**Objetivo**: Regressão completa, documentação e cleanup.

**Checklist**:
- [ ] Regressão manual **100%**:
  - Todos os endpoints Nível 3 + qualquer Nível 4 implementado
  - Edge cases e validações
  - Delete cascading behavior
- [ ] Escrever README.md:
  - Como rodar o projeto (`./mvnw spring-boot:run`, compose auto-starts Postgres)
  - Modelo de entidades (diagrama ER ou listing)
  - Lista de endpoints por recurso
  - Checklist explícito: "Níveis 1-3 ✅, Nível 4 → #1+#2 ✅ | #3 ✅ | #4 ❌ | #5 ✅ | #6 ❌ | #7 ❌"
  - Notas de configuração e troubleshooting
- [ ] Cleanup:
  - Remover código morto, TODOs não-relacionados
  - Validar que nenhum segredo (senha, token, chave) está commitado
  - Confirmar seeder é restrito ao perfil `dev` (`@Profile("dev")`)
- [ ] **Se sobrar tempo**, atacar Nível 4 restante (em ordem):

   **#6 — Cache** (~1.5h):
   - `@Cacheable` em `classificacao`, `artilharia`, `assistencias`
   - `@CacheEvict` em `registrarResultado`, `upsertEstatistica`
   - `ConcurrentMapCacheManager` simples (sem Redis)
   - Commitar: "perf(cache): add caching for hot queries"

   **#7 — Auditoria** (❌ **não tente** a menos que tudo esteja pronto com horas sobrando):
   - Mínimo: `@CreatedDate`, `@LastModifiedDate` via Spring Data auditing
   - Não: full change-log table
   - Muito baixa prioridade

- [ ] `./mvnw test` verde (se testes implementados)
- [ ] Final commit: "chore: final cleanup and documentation"
- [ ] Push para origin/main
- [ ] Projeto pronto para entrega

---

## 🧪 Estratégia de Testes

### Automatizar (Maior Risco de Lógica)

**`ClassificacaoServiceTest`** (CRÍTICO — Dia 8+):
- Tabela básica gerada corretamente
- Desempate resolvido em cada um dos 5 níveis:
  1. Pontos DESC
  2. Vitórias DESC (mesmo pontos)
  3. Saldo de gols DESC (mesmo vitórias)
  4. Gols pró DESC (mesmo saldo)
  5. Nome time ASC (tecnicamente estável)
- Time com 0 jogos ainda aparece na tabela
- Corrigir um resultado já finalizado não duplica contagem (nenhum estado persistido)

**`PartidaServiceTest`**:
- Criar partida com mandante==visitante → exceção
- Criar partida com time não participante → exceção
- Placares só aceitos para status FINALIZADA/EM_ANDAMENTO

**`ParticipacaoServiceTest`**:
- Adicionar (temporada, time) duplicado → exceção 409

**2-3 `@WebMvcTest`** para `CampeonatoController`, `PartidaController`:
- Happy path: GET retorna 200
- 404: GET ID inexistente
- 400: POST com validation error
- Usar `@WithMockUser` para testes autenticados (se auth implementado)

### Só Manual (Baixo Risco de Lógica)

CRUD simples de Campeonato/Time/Jogador/Temporada — melhor coberto por `.http` file/Postman collection salva no repo do que por JUnit, dado o orçamento de tempo.

**Budget**: ~1.5-2 dias total (início Dia 10, grosso Dia 11).

---

## ⚠️ Riscos e Mitigações

| Risco | Probabilidade | Impacto | Mitigação | Corte de Emergência |
|-------|---------------|--------|-----------|---------------------|
| **Spring Boot 4.2.0-SNAPSHOT + Java 26 instabilidade** | Alta | Alto | Validar `./mvnw compile` Dia 1, antes de escrever código. Fall back para Java 21 LTS / Boot 3.x GA se quebrar. | Decidir até **fim do Dia 1** |
| **Classificação: casos-limite de desempate** | Média-Alta | Alto | Testes unitários dedicados (Dias 8/10-11); lógica em passe único, sem estado derivado persistido | Testes desde Dia 8 |
| **Ambiguidade `/campeonatos/{id}/classificacao` com múltiplas temporadas** | Média | Médio | Documentar decisão explicitamente Dia 6/8 (temporada EM_ANDAMENTO, override `?temporadaId=`) | Documentar antes de push |
| **`spring-session-jdbc` schema não auto-criado** | Média | Médio | Checklist explícito Dia 12: `initialize-schema=always` em properties | Item checklist Dia 12 |
| **`springdoc-openapi` incompatibilidade com Boot SNAPSHOT** | Média | Baixo | Time-box Dia 13, cortar em 30-45min se não resolver | Cut #5 se tempo apertar |
| **Scope creep em eventos de partida** (gols/cartões) | Baixa | Alto | Spec marca como futuro; manter EstatisticaJogador como upsert manual, não derivado | Discipline: não implementar |
| **Disponibilidade 4-8h/dia variável** | Alta | Médio | Dia 13 é buffer; Dia 10 é parada segura; cortar Nível 4 de baixo (#7, #6, #4) se apertar | Buffer Dia 13; parada segura Dia 10 |
| **Delete cascata / dados órfãos** | Média | Médio | Decidir restringir por padrão (409) Dias 4/6, não deixar JPA default implícito | Definir Dias 4/6 |

---

## ✅ Definição de Pronto

### **Nível 3 (Entrega Mínima — Obrigatório)**

- ✅ 7 entidades centrais (Campeonato, Temporada, Time, Jogador, Participacao, Partida, EstatisticaJogador) com relacionamentos e constraints corretos
- ✅ CRUD completo: Campeonato, Temporada, Time, Jogador
- ✅ Criação + listagem: Participacao
- ✅ Criação + listagem + resultado: Partida
- ✅ Endpoints de classificação (`GET /temporadas/{id}/classificacao`, `GET /campeonatos/{id}/classificacao`) corretos e ordenados por desempate
- ✅ Classificação **apenas de partidas FINALIZADA**, sem duplicação ao corrigir resultado (validado por testes + manual)
- ✅ Upsert de EstatisticaJogador + endpoints de artilharia/assistências
- ✅ **DTOs em toda a API** (sem vazamento de entidade)
- ✅ Erros 404/409/400 consistentes
- ✅ App sobe via `./mvnw spring-boot:run` com Postgres automático e dados de seed
- ✅ Histórico git incremental + commits meaningful
- ✅ README documenta o que foi implementado

**Go/No-go**: Checkout Dia 10, ponto de parada seguro.

### **Nível 4 (Stretch Goals — Tentado Após Nível 3, Prioridade)**

1. **Erros padronizados + validação** (Dia 11) — RECOMENDADO
   - ✅ `ProblemDetail` format em todas exceções
   - ✅ Bean Validation em todos DTOs
   
2. **Testes automatizados** (Dia 11) — RECOMENDADO
   - ✅ `ClassificacaoService`: 5 desempates + no-double-count
   - ✅ Validações de `PartidaService`, `ParticipacaoService`
   - ✅ 2-3 `@WebMvcTest`
   - ✅ `./mvnw test` verde

3. **Autenticação por sessão + favoritos** (Dia 12) — IMPORTANTE
   - ✅ `Usuario`, `SecurityConfig`, `POST /usuarios`, `POST /auth/login`
   - ✅ `Favorito`, endpoints `/usuarios/me/favoritos`
   - ✅ Validado com cliente preservando cookies

4. **Paginação/ordenação/filtros** (Dia 13) — NICE-TO-HAVE
   - ✅ Endpoints de listagem com `Pageable`

5. **OpenAPI/Swagger** (Dia 13) — NICE-TO-HAVE
   - ✅ `springdoc-openapi` integrado, `/swagger-ui.html` acessível

6. **Cache** (Dia 14) — NICE-TO-HAVE
   - ✅ `@Cacheable` em classificacao/artilharia, `@CacheEvict` em updates

7. **Auditoria** (Dia 14 se sobrar) — NICE-TO-HAVE
   - ✅ `@CreatedDate`, `@LastModifiedDate` via Spring Data

---

## 📁 Estrutura Final de Arquivos Críticos

```
Football-League-API/
├── .git/                                  ← repo git
├── .idea/                                 ← IntelliJ config (tracked)
├── Football-League-API/                   ← módulo Maven
│   ├── pom.xml
│   ├── compose.yaml                       ← postgres:16-alpine
│   ├── HELP.md
│   ├── mvnw, mvnw.cmd
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/Football_League_API/
│   │   │   │   ├── FootballLeagueApiApplication.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Campeonato.java
│   │   │   │   │   ├── Temporada.java
│   │   │   │   │   ├── Time.java
│   │   │   │   │   ├── Jogador.java
│   │   │   │   │   ├── Participacao.java
│   │   │   │   │   ├── Partida.java
│   │   │   │   │   ├── EstatisticaJogador.java
│   │   │   │   │   ├── Usuario.java           ← Nível 4
│   │   │   │   │   ├── Favorito.java          ← Nível 4
│   │   │   │   ├── dto/{request,response}/   ← DTOs por entidade
│   │   │   │   ├── mapper/                    ← Mappers manuais
│   │   │   │   ├── repository/                ← JPARepositories
│   │   │   │   ├── service/
│   │   │   │   │   ├── CampeonatoService.java
│   │   │   │   │   ├── ClassificacaoService.java  ← CRÍTICO
│   │   │   │   │   ├── ...
│   │   │   │   ├── controller/                ← REST controllers
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── enum/
│   │   │   │   │   ├── TipoCampeonato.java
│   │   │   │   │   ├── StatusPartida.java
│   │   │   │   │   ├── ...
│   │   │   │   ├── config/
│   │   │   │   ├── security/                  ← Nível 4
│   │   │   │   └── validation/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── static/
│   │   │       └── templates/
│   │   └── test/
│   │       └── java/com/example/Football_League_API/
│   │           ├── FootballLeagueApiApplicationTests.java
│   │           ├── service/
│   │           │   ├── ClassificacaoServiceTest.java  ← CRÍTICO
│   │           │   ├── ...
│   │           └── controller/
│   │               └── ...WebMvcTests.java
│   └── docs/
│       └── especificacao-football-league-api.md       ← Spec (source of truth)
├── README.md                              ← Como rodar, endpoints, níveis
├── PLANO_IMPLEMENTACAO.md                 ← Este arquivo
└── SETUP_NOTES.md                         ← Issues de ambiente (Dia 1)
```

---

## 🚀 Como Executar Este Plano

1. **Dia 1**: Configure o ambiente (git, Java, Maven). Corrija qualquer problema de SSL/compilação. Crie a estrutura de pacotes.
2. **Dias 2-3**: Implemente Nível 1 (Campeonato, Temporada, Time, Jogador).
3. **Dia 4**: Valide manualmente.
4. **Dias 5-6**: Implemente Nível 2 (Participacao, Partida).
5. **Dia 7**: Valide manualmente.
6. **Dias 8-9**: Implemente Nível 3 (Classificação, Estatísticas) — **ALTO RISCO, MELHOR HORÁRIO**.
7. **Dia 10**: Valide ponta a ponta, **marco de Nível 3 completo** (parada segura se cronograma apertar).
8. **Dia 11+**: Stretch goals (testes, erros, auth, paginação, Swagger, cache, auditoria).
9. **Dia 14**: Polimento final, regressão, README, commit de conclusão.

---

## 📞 Notas Finais

- **Não é tudo ou nada**: Se o cronograma apertar, Nível 3 é uma entrega valiosa e completa.
- **Tempo adiantado**: Sempre adiante para o próximo dia — a ordem de dependência é o que importa, não o rótulo "Dia N".
- **Checkpoints são pontos de parada deliberados**: Use-os para validar e ajustar o cronograma.
- **Riscos bem identificados**: Veja a seção Riscos e Mitigações acima. Dia 1 é crítico para resolvê-los.
- **Commit cedo, commit frequente**: Histórico git significativo é parte da entrega.

---

**Versão**: 1.0  
**Data**: 2026-09-21  
**Criado por**: Claude Code + Planejamento Estruturado
