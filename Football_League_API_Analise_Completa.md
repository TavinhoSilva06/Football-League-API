# ANÁLISE TÉCNICA DO PROJETO
## Football League API

**Autor:** Otávio Augusto Amaral Silva
**Versão:** 1.0  
**Data:** Outubro/2026  
**Escopo:** Visão Completa do Sistema (Full Project)  
**Status:** Análise Completa
**Github:** https://github.com/TavinhoSilva06/Football-League-API

---

## 📋 SUMÁRIO

- [1. Visão Geral do Projeto](#1-visão-geral-do-projeto)
- [2. Matriz de Requisitos Funcionais](#2-matriz-de-requisitos-funcionais)
- [3. Matriz de Requisitos Não-Funcionais](#3-matriz-de-requisitos-não-funcionais)
- [4. Modelo de Entidade-Relacionamento](#4-modelo-de-entidade-relacionamento-mer)
- [5. Casos de Uso](#5-casos-de-uso)
- [6. Arquitetura do Sistema](#6-arquitetura-do-sistema)
- [7. Stack Tecnológico](#7-stack-tecnológico)

## 1. VISÃO GERAL DO PROJETO


### 1.1 Descrição
A Football League API é uma plataforma REST profissional desenvolvida em Java com Spring Boot, destinada ao gerenciamento 
e consulta de competições de futebol. 
O sistema foi projetado para agregar múltiplos campeonatos nacionais e 
continentais, permitindo navegação por temporadas, times, jogadores, partidas, classificações e estatísticas.


### 1.2 Escopo Expandido (Full Project)
Este documento apresenta a análise como se o projeto fosse implementado em sua completude, incluindo:
CRUD completo de entidades de negócio
Gerenciamento avançado de usuários com autenticação e autorização
Sistema de favoritos com perfis de usuário
Cálculo automático de classificações com regras configuráveis por campeonato
Estatísticas detalhadas de jogadores e times
Paginação, filtros e ordenação em todos os endpoints
Auditoria e rastreabilidade de alterações
Cache estratégico para consultas frequentes
Testes automatizados e documentação OpenAPI/Swagger
Suporte a múltiplos formatos de competição


### 1.3 Objetivos Principais
Disponibilizar uma API REST robusta e escalável para:
Consulta e administração de campeonatos e temporadas
Cadastro e gestão de times e jogadores
Registro e consulta de partidas, calendários e resultados
Cálculo automático de pontuação e classificações
Consulta de estatísticas por jogador, time e competição
Gerenciamento de favoritos e preferências de usuários
Suporte a múltiplos formatos de competição (pontos corridos, mata-mata, grupos)

## 2. MATRIZ DE REQUISITOS FUNCIONAIS

| ID | Requisito | Descrição | Prioridade |
|--------------------|--------------------|--------------------|--------------------|
| RF001 | Gerenciar Campeonatos | CRUD completo de campeonatos com suporte a múltiplos formatos | Alta |
| RF002 | Gerenciar Temporadas | Criar, listar, atualizar e deletar temporadas de campeonatos | Alta |
| RF003 | Gerenciar Times | CRUD de times com dados institucionais (nome, cidade, estádio, escudo) | Alta |
| RF004 | Gerenciar Jogadores | CRUD de jogadores com vinculação a times e histórico de posições | Alta |
| RF005 | Registrar Partidas | Criar, agendar e atualizar partidas com status e calendário | Alta |
| RF006 | Registrar Resultados | Atualizar placar de partidas e calcular automaticamente classificação | Alta |
| RF007 | Calcular Classificação | Gerar tabela de classificação com regras de desempate configuráveis | Alta |
| RF008 | Consultar Estatísticas | Exibir estatísticas de jogadores (gols, assistências, cartões) por temporada | Alta |
| RF009 | Participação de Times | Associar times a temporadas e campeonatos | Alta |
| RF010 | Autenticação e Autorização | Login com JWT, refresh tokens, controle de acesso por perfil | Alta |
| RF011 | Gerenciar Favoritos | Usuários podem marcar e listar favoritos (campeonatos, times, jogadores) | Média |
| RF012 | Paginação e Filtros | Endpoints com paginação, busca, filtros e ordenação | Média |
| RF013 | Busca Avançada | Filtrar por país, tipo de campeonato, status, período | Média |
| RF014 | Gerar Relatórios | Exportar dados de classificação, artilharia e estatísticas | Média |
| RF015 | Auditoria de Mudanças | Registrar quem criou/modificou/deletou cada registro com timestamp | Média |


## 3. MATRIZ DE REQUISITOS NÃO-FUNCIONAIS

| Categoria | Requisito | Descrição |
|--------------------|--------------------|--------------------|
| Performance | Tempo de Resposta | Responder em menos de 500ms para 95% das requisições |
| Escalabilidade | Throughput | Suportar até 10.000 requisições/segundo com horizontal scaling |
| Confiabilidade | Disponibilidade | Disponibilidade 99.5% com SLA garantido |
| Segurança | Transporte | Usar HTTPS/TLS 1.3, certificados válidos |
| Segurança | Autenticação | JWT com refresh tokens, expiração configurável |
| Segurança | Proteção | CSRF protection, rate limiting, validação de entrada |
| Segurança | Criptografia | Criptografia de senhas com bcrypt, auditoria de acessos |
| Usabilidade | Documentação | Documentação OpenAPI completa, exemplos de integração |
| Manutenibilidade | Código | Código testável com cobertura mínima de 80%, padrão clean code |
| Compatibilidade | Versionamento | Suportar múltiplas versões da API com backward compatibility |
| Integridade | Transações | Transações ACID, validações em múltiplas camadas |
| Portabilidade | Deployamento | Executar em Docker, Kubernetes, cloud providers (AWS, GCP, Azure) |


## 4. MODELO DE ENTIDADE-RELACIONAMENTO (MER)
### 4.1 Entidades Principais

| Entidade | Chave Primária | Atributos Principais |
|--------------------|--------------------|--------------------|
| Campeonato | id | nome, país, tipo (ENUM), formato, descrição, criado_em, atualizado_em |
| Temporada | id | campeonato_id (FK), nome, data_inicio, data_fim, status (ENUM) |
| Time | id | nome, sigla, país, cidade, estádio, escudo_url, criado_em |
| Participação | id | temporada_id (FK), time_id (FK), pontos, jogos, vitorias, empates, derrotas |
| Partida | id | temporada_id (FK), rodada, data_hora, time_mandante_id (FK), time_visitante_id (FK), gols_mandante, gols_visitante, status (ENUM), local |
| Jogador | id | time_id (FK), nome, nacionalidade, posição (ENUM), número_camisa, data_nascimento, criado_em |
| EstatísticaJogador | id | jogador_id (FK), temporada_id (FK), jogos, gols, assistências, cartões_amarelos, cartões_vermelhos |
| Usuário | id | nome, email (UNIQUE), senha_hash, papel (ENUM), criado_em, atualizado_em |
| Favorito | id | usuário_id (FK), tipo (ENUM), campeonato_id (FK), time_id (FK), jogador_id (FK), criado_em |

### 4.2 Relacionamentos Principais
1:N - Um Campeonato possui muitas Temporadas
1:N - Uma Temporada pertence a um Campeonato e possui muitas Participações
N:M - Uma Participação associa um Time a uma Temporada (tabela de junção)
1:N - Uma Temporada possui muitas Partidas
2:1 - Uma Partida relaciona dois Times (mandante e visitante)
1:N - Um Time possui muitos Jogadores
1:N - Um Jogador pode ter múltiplas Estatísticas (por temporada/campeonato)
1:N - Um Usuário pode possuir muitos Favoritos
N:1 - Um Favorito referencia Campeonato, Time ou Jogador (polimórfico)

## 5. CASOS DE USO
### 5.1 Atores do Sistema

| Ator | Descrição | Permissões |
|--------------------|--------------------|--------------------|
| Administrador | Gerencia campeonatos, temporadas, times, jogadores e usuários | Total (CRUD completo) |
| Editor de Partidas | Cria, atualiza resultados e calendário de partidas | Criar e editar partidas |
| Usuário Autenticado | Consulta dados, marca favoritos | Leitura + Marcar favoritos |
| Usuário Anônimo | Apenas consulta dados públicos | Apenas leitura |

### 5.2 Casos de Uso Principais

| ID | Ator | Caso de Uso | Descrição | Prioridade |
|--------------------|--------------------|--------------------|--------------------|--------------------|
| UC001 | Admin | Criar Campeonato | Admin registra novo campeonato com formato e regulamento | Alta |
| UC002 | Admin | Definir Temporada | Admin cria temporada dentro de um campeonato | Alta |
| UC003 | Admin | Adicionar Participantes | Admin associa times a uma temporada | Alta |
| UC004 | Admin | Gerenciar Usuários | Admin cria, desativa e altera permissões de usuários | Alta |
| UC005 | Editor | Agendar Partida | Editor cria partida com data, hora e times participantes | Alta |
| UC006 | Editor | Registrar Resultado | Editor atualiza placar e classificação é recalculada automaticamente | Alta |
| UC007 | Usuário | Consultar Classificação | Usuário visualiza tabela de classificação atualizada | Média |
| UC008 | Usuário | Marcar Favorito | Usuário adiciona campeonato/time/jogador aos favoritos | Média |
| UC009 | Usuário | Pesquisar Estatísticas | Usuário busca gols, assistências, cartões de jogadores | Média |
| UC010 | Anônimo | Listar Campeonatos | Visitante vê lista de campeonatos disponíveis | Alta |


## 6. ARQUITETURA DO SISTEMA
### 6.1 Arquitetura em Camadas
┌─────────────────────────────────────────────────────────┐│          Presentation Layer (Controllers)              ││  Recebe requisições HTTP, valida entrada, retorna JSON │└──────────────────────┬──────────────────────────────────┘                       │┌──────────────────────┴──────────────────────────────────┐│       Application Layer (Services & Business Logic)    ││  Implementa regras de negócio, orquestração, validações│└──────────────────────┬──────────────────────────────────┘                       │┌──────────────────────┴──────────────────────────────────┐│    Data Access Layer (Repositories & JPA/Hibernate)    ││              Acesso ao banco de dados                  │└──────────────────────┬──────────────────────────────────┘                       │┌──────────────────────┴──────────────────────────────────┐│      Database Layer (PostgreSQL)                       ││              Persistência de dados                     │└─────────────────────────────────────────────────────────┘
### 6.2 Componentes Transversais (Cross-cutting Concerns)

| Componente | Tecnologia | Responsabilidade |
|--------------------|--------------------|--------------------|
| Segurança | Spring Security + JWT | Autenticação, autorização, proteção de recursos |
| Validação | Spring Validation | Validação de entrada com mensagens customizadas |
| Tratamento de Erros | GlobalExceptionHandler | Centralizar respostas de erro padronizadas |
| Cache | Spring Cache (Redis/Caffeine) | Cachear consultas frequentes (classificação, artilharia) |
| Auditoria | JPA Auditing | Rastreamento de quem criou/modificou cada registro |
| Logging | SLF4J + Logback | Análise de erros, monitoramento de performance |
| Documentação | SpringFox/Springdoc-OpenAPI | Gerar Swagger/OpenAPI automaticamente |

### 6.3 Padrões de Design Utilizados
Repository Pattern: Abstração do acesso a dados
Mapper Pattern: Conversão entre entidades e DTOs
Service Locator: Localização de serviços
Dependency Injection: Spring Framework
Singleton Pattern: Beans do Spring
Builder Pattern: Construção de entidades complexas (Lombok)

## 7. STACK TECNOLÓGICO
### 7.1 Backend

| Categoria | Componente | Versão |
|--------------------|--------------------|--------------------|
| Linguagem | Java | 21 |
| Framework | Spring Boot | 3.2.0 |
| Framework Web | Spring Web MVC | 3.2.0 |
| Persistência | Spring Data JPA | 3.2.0 |
| ORM | Hibernate | 6.4.0 |
| Banco de Dados | PostgreSQL Driver | 42.7.0 |
| Segurança | Spring Security | 6.x |
| Validação | Validation API (Jakarta) | 3.0 |
| Redução de Boilerplate | Lombok | 1.18.30 |
| Documentação | Springdoc-OpenAPI | 2.1.0 |
| Logging | SLF4J + Logback | 1.4.x |
| Testing | JUnit 5 (Jupiter) | 5.10.0 |
| Mocking | Mockito | 5.0.0 |
| Integração | TestContainers | 1.19.0 |
| Build | Maven | 3.9+ |

### 7.2 Infraestrutura

| Componente | Descrição | Versão |
|--------------------|--------------------|--------------------|
| Servidor Web | Apache Tomcat (embarcado) | 10.x |
| Banco de Dados | PostgreSQL | 14+ |
| Cache | Redis (opcional) | 7.0+ |
| Containerização | Docker | 24.0+ |
| Orquestração | Kubernetes | 1.28+ |
| CI/CD | GitHub Actions ou Jenkins | - |
| Versionamento | Git | - |

### 7.3 Desenvolvimento e Testes

| Categoria | Tecnologia | Propósito |
|--------------------|--------------------|--------------------|
| IDE | IntelliJ IDEA / Eclipse | Desenvolvimento |
| Gerenciador de Dependências | Maven | Build e gerenciamento de libs |
| Testes Unitários | JUnit 5 + Mockito | Testar lógica isolada |
| Testes de Integração | TestContainers | Testar com BD real em container |
| Análise de Código | SonarQube | Qualidade de código |
| Documentação | Swagger/OpenAPI 3.0 | API documentation |

### 7.4 Deployment
Código-fonte    ↓ (Git Push)GitHub/GitLab    ↓ (GitHub Actions/Jenkins)Build & Test (Maven)    ↓Docker Image Build    ↓Container Registry (Docker Hub, ECR, GCR)    ↓Kubernetes Cluster    ↓Production Environment

## CONCLUSÃO
A Football League API é projetada como uma plataforma escalável, segura e bem-documentada para gerenciamento de competições de futebol. Com arquitetura em camadas, componentes transversais robustos e stack tecnológico moderno, o sistema está preparado para crescimento e manutenção a longo prazo.
### Próximos Passos Recomendados
Implementar testes automatizados (unitários e integração)
Configurar CI/CD pipeline (GitHub Actions)
Adicionar autenticação JWT completa
Implementar cache estratégico (Redis)
Adicionar auditoria e logging detalhado
Documentar API com OpenAPI/Swagger
Configurar container Docker e Kubernetes
Implementar monitoramento (APM, métricas)

Documento FinalizadoVersão 1.0 - outubro de 2026

---

## 📊 RESUMO EXECUTIVO

### Indicadores-Chave

| Métrica | Valor | Descrição |
|---------|-------|-----------|
| **Entidades** | 9 | Campeonato, Temporada, Time, Participacao, Partida, Jogador, EstatisticaJogador, Usuario, Favorito |
| **Endpoints REST** | 30+ | Todos os recursos cobertos com operações CRUD + custom queries |
| **Requisitos Funcionais** | 15 | Todas as funcionalidades-chave do sistema |
| **Requisitos Não-Funcionais** | 12 | Performance, segurança, escalabilidade, confiabilidade |
| **Casos de Uso** | 10 | Cobrindo todos os atores e fluxos principais |
| **Cobertura de Testes** | 80%+ | Mínimo recomendado para código production-ready |
| **Tempo de Resposta (P95)** | <500ms | Garantido com cache e índices de BD |
| **Disponibilidade SLA** | 99.5% | Com redundância e auto-recovery |

### Fases de Implementação Recomendadas

#### **Fase 1: MVP (Atual)**
- ✅ CRUD básico de entidades
- ✅ Cálculo de classificações
- ✅ Relacionamentos entre entidades
- ✅ Banco de dados PostgreSQL

#### **Fase 2: Autenticação & Features**
- 🔄 JWT Authentication + Refresh Tokens
- 🔄 Role-Based Access Control (RBAC)
- 🔄 Sistema de Favoritos
- 🔄 Paginação & Filtros Avançados
- 🔄 Swagger/OpenAPI Docs
- 🔄 Testes 80% coverage

#### **Fase 3: Produção**
- 🔄 Cache com Redis
- 🔄 Docker & Docker Compose
- 🔄 CI/CD Pipeline (GitHub Actions)
- 🔄 Auditoria Completa
- 🔄 Monitoring (Prometheus + Grafana)

#### **Fase 4: Escalabilidade**
- 🔄 Formatos knockout/grupos
- 🔄 Eventos de partida (gols, cartões)
- 🔄 Transferências de jogadores
- 🔄 Integração com APIs externas
- 🔄 WebSocket para live updates
- 🔄 Analytics avançado

### Tecnologias Recomendadas

```
Backend:
├── Java 21 LTS (JVM)
├── Spring Boot 3.2.0 (Framework)
├── Spring Data JPA (ORM)
├── Spring Security (Autenticação)
└── PostgreSQL (Database)

Cache & Performance:
├── Redis (Distributed Cache)
├── Hibernate L1 Cache
└── Database Indexes

DevOps & Deployment:
├── Docker & Docker Compose
├── Kubernetes (opcional)
├── GitHub Actions (CI/CD)
└── Cloud Providers (AWS/GCP/Azure)

Monitoring & Observability:
├── Prometheus (Metrics)
├── Grafana (Dashboards)
├── ELK Stack (Logs)
└── Jaeger (Tracing)

Testing & Quality:
├── JUnit 5 (Unit Tests)
├── Mockito (Mocking)
├── Testcontainers (Integration Tests)
├── SonarQube (Code Quality)
└── Jacoco (Coverage)
```

---

