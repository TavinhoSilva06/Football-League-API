# ANÁLISE TÉCNICA DO PROJETO
## Chute Certo · Palpite Manager

**Autor:** Otávio Augusto Amaral Silva

**Versão:** 2.0

**Data:** Novembro/2026

**Escopo:** Visão Completa do Sistema (Full Project)

**Status:** Documento atualizado para o estado implementado

**Github:** https://github.com/TavinhoSilva06/Football-League-API

---

## SUMÁRIO

- [1. Visão Geral do Projeto](#1-visão-geral-do-projeto)
- [2. Matriz de Requisitos Funcionais](#2-matriz-de-requisitos-funcionais)
- [3. Matriz de Requisitos Não-Funcionais](#3-matriz-de-requisitos-não-funcionais)
- [4. Modelo de Entidade-Relacionamento](#4-modelo-de-entidade-relacionamento-mer)
- [5. Casos de Uso](#5-casos-de-uso)
- [6. Arquitetura do Sistema](#6-arquitetura-do-sistema)
- [7. Stack Tecnológico](#7-stack-tecnológico)
- [8. Estado Atual e Roadmap](#8-estado-atual-e-roadmap)

## 1. VISÃO GERAL DO PROJETO

### 1.1 Descrição

O **Chute Certo** é uma plataforma full-stack para gerenciamento de competições de futebol e palpites por rodada. O nome comercial do projeto é Chute Certo; o diretório e o repositório mantêm `Football-League-API` por compatibilidade.

A API REST em Java/Spring Boot gerencia campeonatos, temporadas, times, jogadores, partidas, eventos, estatísticas, classificações, usuários e palpites. O frontend em React consome essa API, oferece autenticação JWT, painel de palpites com contagem regressiva e um dashboard de aproveitamento por temporada.

Tagline adotada: **Acerte o placar. Suba de patente.**

### 1.2 Escopo Implementado

- CRUD de campeonatos, temporadas, times, jogadores, participações e partidas.
- Geração automática de calendário de turno e returno.
- Registro de eventos de partida: gols, assistências e cartões.
- Cálculo automático de classificação com critérios de desempate.
- Estatísticas de jogadores por temporada: jogos, gols, assistências e cartões.
- Autenticação JWT com perfis `USER` e `ADMIN` e seed de administrador.
- Palpites de placar exato com prazo configurável de **4 minutos** por rodada.
- Salvamento de todos os placares da rodada em uma única requisição (`POST /palpites/lote`).
- Simulação automática da rodada no instante exato do expiramento do prazo, com fallback periódico.
- Premiação automática por acerto exato e evolução de patente.
- Histórico de resultados por rodada, incluindo mensagem quando o usuário não fez nenhum palpite.
- Dashboard de aproveitamento geral e por temporada.
- Sincronização manual com API-Football; sincronização automática desativada por padrão.

### 1.3 Objetivos Principais

- Centralizar a administração de competições e temporadas em uma única aplicação.
- Permitir que usuários palpitem placares dentro de uma janela temporal controlada.
- Automatizar a simulação de rodadas, a premiação e a abertura da rodada seguinte.
- Oferecer ao usuário feedback claro de resultados, pontos, patente e aproveitamento.
- Manter a API preparada para execução local com Docker Compose ou PostgreSQL remoto via Supabase.

## 2. MATRIZ DE REQUISITOS FUNCIONAIS

| ID | Requisito | Descrição | Status |
|---|---|---|---|
| RF001 | Gerenciar Campeonatos | Criar, listar, editar e excluir campeonatos | Implementado |
| RF002 | Gerenciar Temporadas | Criar, listar, editar e excluir temporadas por campeonato | Implementado |
| RF003 | Gerenciar Times | CRUD de times com nome, cidade, estádio e escudo | Implementado |
| RF004 | Gerenciar Jogadores | CRUD de jogadores vinculados a times, com posição e número | Implementado |
| RF005 | Inscrever Times | Associar times a uma temporada | Implementado |
| RF006 | Gerar Calendário | Criar partidas de turno e returno para os times inscritos com elenco | Implementado |
| RF007 | Gerenciar Partidas | Criar, editar, excluir e consultar partidas por temporada/time/rodada | Implementado |
| RF008 | Registrar Resultados | Atualizar placar e status da partida | Implementado |
| RF009 | Registrar Eventos | Registrar gols, assistências e cartões com minuto e jogador | Implementado |
| RF010 | Calcular Classificação | Gerar classificação com desempate automático | Implementado |
| RF011 | Consultar Estatísticas | Consultar artilharia, assistências, cartões e estatísticas por jogador | Implementado |
| RF012 | Autenticação JWT | Registro, login e perfil autenticado | Implementado |
| RF013 | Controle por Perfil | `ADMIN` administra dados; `USER` palpita; consultas GET são públicas | Implementado |
| RF014 | Palpites por Rodada | Palpitar placares da rodada atual com bloqueio após o prazo | Implementado |
| RF015 | Prazo Configurável | Janela de 4 minutos por rodada, persistida em cada partida | Implementado |
| RF016 | Salvamento em Lote | Enviar todos os palpites da rodada em uma única requisição | Implementado |
| RF017 | Simulação Automática | Simular rodada expirada e abrir o prazo da próxima após concluir a avaliação | Implementado |
| RF018 | Premiação e Patentes | Premiar acerto exato segundo a patente no início da avaliação | Implementado |
| RF019 | Meus Resultados | Histórico por rodada com acertos, erros, pontos e aviso de rodada sem palpite | Implementado |
| RF020 | Dashboard de Aproveitamento | Aproveitamento geral e por temporada | Implementado |
| RF021 | Sincronização externa | Sincronizar times/elencos com API-Football sob demanda | Implementado |

### 2.1 Fora do Escopo Atual

Os itens abaixo constavam de versões anteriores deste documento como recomendações, mas **não estão implementados** no estado atual:

- Favoritos de campeonatos, times ou jogadores.
- Refresh tokens e revogação de tokens.
- Auditoria geral de criação/edição/exclusão.
- Cache backend com Redis ou Caffeine.
- Documentação OpenAPI/Swagger gerada automaticamente.
- Paginação global em todos os endpoints.
- Exportação de relatórios.
- Testes automatizados com cobertura mínima formal.
- Deploy em Kubernetes e pipeline de CI/CD.

## 3. MATRIZ DE REQUISITOS NÃO-FUNCIONAIS

| Categoria | Requisito | Estado Atual |
|---|---|---|
| Segurança | Transporte | HTTPS/TLS depende do ambiente de deploy; desenvolvimento usa HTTP local |
| Segurança | Autenticação | JWT com expiração configurável via `JWT_EXPIRATION_MS` |
| Segurança | Autorização | `USER`, `ADMIN`, regras por rota no `SecurityConfig` |
| Segurança | Senhas | Hash com BCrypt |
| Segurança | Validação | Jakarta Validation com mensagens em português |
| Integridade | Transações | Operações de escrita anotadas com `@Transactional` |
| Integridade | Concorrência | Simulação de rodada com método `synchronized` |
| Confiabilidade | Prazo | Data limite persistida por partida e bloqueio no backend |
| Confiabilidade | Simulação | Agendamento exato mais fallback por cron para prazos perdidos |
| Performance | Simulação | Elencos, estatísticas e usuários carregados em lote para reduzir consultas N+1 |
| Usabilidade | Frontend | Interface responsiva com Tailwind, feedback de carregamento e erro |
| Manutenibilidade | Organização | Camadas controller, service, repository, entity, dto, mapper, scheduler, event e external |
| Observabilidade | Logs | Logs SQL habilitados no perfil dev; tabela de log para sincronização |
| Portabilidade | Banco | Supabase por padrão; Docker Compose disponível para PostgreSQL local |
| Testes | Verificação | Compilação Maven, `npm run lint` e `npm run build` validados |

### 3.1 Configurações Relevantes

| Propriedade | Valor atual | Observação |
|---|---|---|
| `spring.application.name` | `chute-certo-api` | Identidade da API |
| `spring.profiles.active` | `dev` | Perfil padrão |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema ajustado automaticamente |
| `spring.datasource.url` | Supabase PostgreSQL | Pode ser sobrescrita por `SPRING_DATASOURCE_URL` |
| `spring.datasource.password` | `${DB_PASSWORD}` | Obrigatória para conexão remota |
| `football.api.sync.on-startup` | `false` | Sync não executa na inicialização |
| `football.api.sync.scheduled` | `false` | Sync não é periódico |
| `football.api.sync.min-season` / `max-season` | `2022` / `2022` | Faixa liberada pelo plano da API-Football |
| `palpite.prazo-minutos` | `4` | Janela de palpites por rodada |
| `palpite.simulacao.habilitada` | `true` | Ativa o agendador de simulação |
| `palpite.simulacao.cron` | `*/5 * * * * *` | Fallback a cada 5 segundos |

### 3.2 Validações Realizadas

- `.\mvnw.cmd -q -DskipTests compile` no backend.
- `npm run lint` no frontend, com dois avisos antigos em `AuthContext.jsx`.
- `npm run build` no frontend.
- `git diff --check` nos arquivos alterados.

## 4. MODELO DE ENTIDADE-RELACIONAMENTO (MER)

### 4.1 Entidades Implementadas

| Entidade | Chave Primária | Atributos Principais |
|---|---|---|
| Campeonato | `id` | nome, api_id, país, tipo, formato, descrição |
| Temporada | `id` | campeonato_id, nome, data_início, data_fim, num_rodadas, status, data_sync_elenco |
| Time | `id` | nome, sigla, país, cidade, estádio, escudo_url |
| Participação | `id` | temporada_id, time_id |
| Partida | `id` | temporada_id, rodada, data_hora, mandante_id, visitante_id, gols, status, local, data_limite_palpite |
| Jogador | `id` | time_id, nome, nacionalidade, posição, número_camisa, data_nascimento |
| EstatísticaJogador | `id` | jogador_id, temporada_id, jogos, gols, assistências, cartões |
| EventoPartida | `id` | partida_id, jogador_id, temporada_id, tipo, minuto, descrição |
| Usuário | `id` | nome, email único, senha, role, pontos |
| Palpite | `id` | usuário_id, partida_id, gols_mandante, gols_visitante, acerto, pontos_ganhos, criado_em |
| SincronizacaoLog | `id` | tipo, data_sync, quantidade_sincronizada, status, mensagem_erro |

### 4.2 Relacionamentos Principais

- Um Campeonato possui muitas Temporadas.
- Uma Temporada possui muitas Participações, Partidas, Eventos e Estatísticas.
- Times e Temporadas formam um relacionamento N:M por meio de Participação.
- Uma Partida possui dois Times: mandante e visitante.
- Uma Partida possui muitos Eventos e muitos Palpites.
- Um Usuário possui muitos Palpites.
- Um Jogador possui muitos Eventos e uma Estatística por temporada.
- A Patente é derivada dos pontos do usuário; não é coluna persistida.
- `SincronizacaoLog` registra execuções manuais de sincronização externa.

### 4.3 Regras de Domínio

- A patente é calculada por faixa de pontos: Bronze `0`, Prata `1000`, Ouro `2000`, Platina `3000`, Diamante `4000`, Perito `5000+`.
- O acerto de palpite exige placar exato.
- A pontuação por acerto é: Bronze `20`, Prata `18`, Ouro `16`, Platina `14`, Diamante `12`, Perito `10`.
- A patente usada no cálculo é capturada no início da avaliação da rodada.
- Um usuário pode ter no máximo um palpite por partida.
- Após o prazo, nem `USER` nem `ADMIN` podem criar ou alterar palpites da rodada.
- A rodada atual é a menor rodada com partida `AGENDADA`.
- O prazo da primeira rodada começa na geração do calendário; os seguintes começam após a conclusão da simulação anterior.

## 5. CASOS DE USO

### 5.1 Atores do Sistema

| Ator | Descrição | Permissões |
|---|---|---|
| Administrador | Gerencia campeonatos, temporadas, times, jogadores, participações e partidas | CRUD completo e endpoints de simulação/sincronização |
| Usuário Autenticado | Consulta dados e registra palpites | Leitura pública + criação/edição de palpites |
| Visitante | Consulta dados públicos via GET | Apenas leitura |

### 5.2 Casos de Uso Implementados

| ID | Ator | Caso de Uso | Descrição | Status |
|---|---|---|---|---|
| UC001 | Admin | Criar Campeonato | Registra campeonato com formato e regulamento | Implementado |
| UC002 | Admin | Definir Temporada | Cria temporada dentro de um campeonato | Implementado |
| UC003 | Admin | Inscrever Times | Associa times com elenco à temporada | Implementado |
| UC004 | Admin | Gerar Calendário | Gera partidas de turno e returno | Implementado |
| UC005 | Admin | Registrar Resultado | Atualiza placar/status e dispara recálculo de classificação | Implementado |
| UC006 | Admin | Registrar Eventos | Registra gols, assistências e cartões | Implementado |
| UC007 | Usuário | Autenticar-se | Faz login e recebe token JWT | Implementado |
| UC008 | Usuário | Palpitar Rodada | Preenche placares e envia com `Salvar todos` | Implementado |
| UC009 | Sistema | Simular Rodada | Encerra prazo, simula placares e premia palpites | Implementado |
| UC010 | Usuário | Consultar Resultados | Vê acertos/erros por rodada e aviso quando não palpitou | Implementado |
| UC011 | Usuário | Consultar Dashboard | Vê aproveitamento geral e por temporada | Implementado |
| UC012 | Visitante | Listar Campeonatos | Consulta campeonatos, temporadas, times e classificações | Implementado |
| UC013 | Admin | Sincronizar API-Football | Executa sincronização manual de times/elencos | Implementado |

## 6. ARQUITETURA DO SISTEMA

### 6.1 Arquitetura em Camadas

```text
┌─────────────────────────────────────────────────────────┐
│          Presentation Layer (Controllers)              │
│  13 controllers / 56 endpoints REST                    │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────┐
│       Application Layer (Services & Business Logic)    │
│  Regras de palpites, prazos, simulação e premiação      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────┐
│    Data Access Layer (Repositories & JPA/Hibernate)    │
│  11 entidades persistidas em PostgreSQL                │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────┐
│      Database Layer (PostgreSQL / Supabase)            │
└─────────────────────────────────────────────────────────┘
```

### 6.2 Componentes Transversais

| Componente | Tecnologia | Responsabilidade |
|---|---|---|
| Segurança | Spring Security + JWT | Autenticação, autorização e sessão stateless |
| Validação | Jakarta Validation | Validação de DTOs e mensagens amigáveis |
| Tratamento de Erros | GlobalExceptionHandler | Respostas padronizadas de erro |
| Transações | Spring `@Transactional` | Consistência das operações de escrita |
| Eventos | `ApplicationEventPublisher` | Notificação de prazo definido após commit |
| Agendamento | `TaskScheduler` + cron | Simulação exata e fallback de recuperação |
| Integração externa | RestTemplate | Cliente API-Football e log de sincronização |
| Logging | SLF4J + Logback | Logs de aplicação e SQL no perfil dev |
| Cache de consultas | React Query | Cache no frontend; não há cache backend |

### 6.3 Fluxo de Prazo e Simulação

1. O calendário da temporada é gerado e a rodada 1 recebe `data_limite_palpite`.
2. Um evento `PrazoPalpiteDefinidoEvent` dispara um agendamento único para o instante do prazo.
3. O frontend exibe a contagem regressiva e bloqueia os campos quando o prazo expira.
4. O agendador chama a simulação; um cron de 5 segundos atua como fallback após reinício do backend.
5. A simulação é sincronizada, carrega elencos/estatísticas em lote, sorteia placares, eventos e cartões.
6. A rodada é finalizada, os palpites são avaliados e os usuários recebem pontos.
7. Somente após essa conclusão a próxima rodada recebe um novo prazo de 4 minutos.
8. Rodadas simuladas sem palpite aparecem em Meus Resultados com aviso específico.

### 6.4 Padrões de Design Utilizados

- Repository Pattern para abstração de acesso a dados.
- DTO + Mapper para separar contratos de API e entidades.
- Service Layer para regras de negócio.
- Builder Pattern via Lombok para construção de entidades e DTOs.
- Dependency Injection do Spring.
- Event-driven design com listener `AFTER_COMMIT`.
- Transactional scheduling para evitar agendamento de transações que não foram confirmadas.

## 7. STACK TECNOLÓGICO

### 7.1 Backend

| Categoria | Componente | Versão |
|---|---|---|
| Linguagem | Java | 21 |
| Framework | Spring Boot | 3.2.0 |
| Framework Web | Spring Web MVC | 3.2.0 |
| Persistência | Spring Data JPA | 3.2.0 |
| ORM | Hibernate | 6.x gerenciado pelo Spring Boot |
| Banco de Dados | PostgreSQL Driver | Gerenciado pelo Spring Boot |
| Segurança | Spring Security | 6.x |
| JWT | JJWT | 0.12.6 |
| Validação | Jakarta Validation | Gerenciado pelo Spring Boot |
| Redução de boilerplate | Lombok | Gerenciado pelo Spring Boot |
| Build | Maven Wrapper | 3.9+ |

### 7.2 Frontend

| Categoria | Componente | Versão |
|---|---|---|
| Linguagem | JavaScript (ESM) | - |
| Framework | React | 19.2.8 |
| Build | Vite | 8.3.0 |
| Estilos | Tailwind CSS | 4.3.3 |
| HTTP | Axios | 1.20.0 |
| Cache/estado assíncrono | TanStack React Query | 5.104.0 |
| Roteamento | React Router DOM | 7.18.4 |
| Lint | Oxlint | 1.81.0 |

### 7.3 Infraestrutura e Ambiente

| Componente | Descrição |
|---|---|
| Banco padrão | Supabase PostgreSQL configurado em `application.properties` |
| Banco local | PostgreSQL 16-alpine via `backend/compose.yaml`, com overrides de datasource |
| Servidor | Apache Tomcat embarcado no Spring Boot |
| Proxy de desenvolvimento | Vite redirecionando chamadas da API para `127.0.0.1:8080` |
| Versionamento | Git/GitHub |
| Deploy | Execução local por Maven Wrapper e Vite; sem Kubernetes configurado |

## 8. ESTADO ATUAL E ROADMAP

### 8.1 Indicadores-Chave

| Métrica | Valor | Descrição |
|---|---|---|
| Entidades | 11 | Inclui Palpite, EventoPartida e SincronizacaoLog |
| Controllers | 13 | Camadas REST da API |
| Endpoints REST | 56 | Inclui lote, calendário, simulação e sincronização |
| Requisitos Funcionais | 21 | Todos implementados no estado atual |
| Casos de Uso | 13 | Cobrindo administrador, usuário e visitante |
| Prazo por rodada | 4 minutos | Configurável via `palpite.prazo-minutos` |
| Fallback de simulação | 5 segundos | Configurável via `palpite.simulacao.cron` |
| Cobertura de testes | Não medida | Há validação por compile/lint/build, sem suíte formal |
| SLA/Disponibilidade | Não formalizado | Projeto acadêmico sem SLA contratual |

### 8.2 Fases

#### Fase 1: MVP
- Concluído: CRUD de entidades, relacionamentos, PostgreSQL, classificação e estatísticas.

#### Fase 2: Autenticação, Palpites e Gamificação
- Concluído: JWT, RBAC, seed de administrador, palpites, prazo por rodada, lote, simulação automática, premiação, patentes e dashboard.

#### Fase 3: Operação Atual
- Concluído: Supabase, Docker Compose para desenvolvimento, logs, proxy Vite e sync manual com API-Football.
- Não implementado: CI/CD, cache backend, OpenAPI, auditoria geral e testes automatizados formais.

#### Fase 4: Roadmap
- Testes unitários e de integração com cobertura formal.
- Documentação OpenAPI/Swagger.
- CI/CD com GitHub Actions.
- Cache backend e índices otimizados para consultas frequentes.
- Auditoria geral de alterações.
- Refresh tokens e revogação de sessão.
- Favoritos e preferências de usuário.
- Formatos de competição por grupos/mata-mata.
- WebSockets para atualizações em tempo real.
- Monitoramento com métricas e dashboards.

## CONCLUSÃO

O Chute Certo está implementado como uma plataforma full-stack funcional para gestão de competições e palpites esportivos. O estado atual prioriza regras de negócio claras, prazo controlado por rodada, simulação automática eficiente e feedback visual ao usuário.

Itens como favoritos, refresh tokens, auditoria geral, cache backend, OpenAPI, CI/CD e Kubernetes permanecem como evoluções futuras e não devem ser considerados requisitos entregues.

**Documento atualizado — Versão 2.0 — novembro de 2026**
