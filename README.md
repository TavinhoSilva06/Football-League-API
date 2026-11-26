   # Chute Certo · Palpite Manager

 Uma **plataforma completa de gerenciamento de competições de futebol** com API REST em Java/Spring Boot + Frontend React profissional. O nome comercial do projeto é **Chute Certo**; o diretório/repositório mantém `Football-League-API` por compatibilidade.

 ## 📋 Sobre o Projeto

 O **Chute Certo** é um projeto acadêmico desenvolvido como parte do curso de Engenharia de Software na **FATEC-SP**, com foco em aplicar padrões de arquitetura, design patterns e boas práticas de desenvolvimento full-stack.

### Funcionalidades

#### 🎮 Frontend (React + Vite + Tailwind CSS)
- ✅ Interface moderna e responsiva com sidebar fixa, topbar sticky, tema branco/azul e animações
- ✅ Navegação intuitiva: Visão geral, Palpites e Times
- ✅ Gerenciamento completo de times e elencos
- ✅ Animações suaves e efeitos visuais profissionais
- ✅ Validação de formulários com feedback de erro por campo
- ✅ Cache inteligente com React Query
- ✅ Proxy Vite resolvendo CORS automaticamente
- ✅ Login/cadastro com JWT e perfil `USER`/`ADMIN`
- ✅ Aba de palpites com placar exato, pontos e patente
- ✅ Contagem regressiva por rodada, bloqueio após o prazo e botão **Salvar todos**
- ✅ Painel `/palpites` com aproveitamento geral e por temporada
- ✅ Atualização automática da rodada após a simulação, sem botão manual na interface

#### 🔧 API REST (Spring Boot)
- ✅ Cadastro e gerenciamento de campeonatos
- ✅ Organização de temporadas com rodadas configuráveis
- ✅ Gestão de times e elencos de jogadores
- ✅ Registro de partidas com placares e resultados
- ✅ **Eventos de partida** (gols, assistências, cartões) com atualização automática de estatísticas
- ✅ **Cálculo automático de classificações** com 5 critérios de desempate
- ✅ **Rankings de artilharia e assistências** em tempo real
- ✅ 56 endpoints RESTful totalmente implementados
- ✅ Validação robusta com mensagens em português
- ✅ Autenticação JWT com perfis `USER`/`ADMIN` e seed de administrador
- ✅ Prazo por rodada persistido, simulação agendada no instante exato e fallback por polling
- ✅ Premiação automática de palpites e patentes com proteção contra duplicidade
- ✅ Salvamento de palpites em lote em uma única requisição
- ✅ Integração opcional com API-Football (sync manual; automática desativada por padrão)

## 🏗️ Arquitetura

```
Football-League-API/
├── backend/                          # API Spring Boot
│   ├── src/main/java/.../com/example/Football_League_API/
│   │   ├── controller/              # 13 controllers (56 endpoints)
│   │   ├── service/                 # Lógica de negócio
│   │   ├── repository/              # Acesso a dados JPA
│   │   ├── entity/                  # 11 entidades JPA
│   │   ├── dto/                     # Request/Response DTOs
│   │   ├── mapper/                  # Conversão entity ↔ DTO
│   │   ├── scheduler/               # Agendamento exato + fallback do prazo
│   │   ├── event/                   # Evento de prazo definido
│   │   ├── external/                # Cliente e sincronização com API-Football
│   │   ├── config/                  # Seed admin, segurança e RestTemplate
│   │   ├── security/                # JWT filter/service
│   │   ├── exception/               # GlobalExceptionHandler
│   │   └── enu/                     # Enums de domínio
│   ├── src/main/resources/application.properties
│   ├── pom.xml
│   └── compose.yaml                 # Docker Compose (PostgreSQL local)
│
├── frontend/                         # React + Vite
│   ├── src/
│   │   ├── pages/                   # Campeonatos, Temporadas, Partidas, Palpites, Dashboard, Times, Login
│   │   ├── components/              # Layout, ui, icons, PartidaItem, PalpiteResultadoItem
│   │   ├── api/                     # Cliente axios + hooks React Query
│   │   ├── lib/                     # brand, patentes, enums, formatação
│   │   ├── AuthContext.jsx          # Sessão JWT
│   │   ├── App.jsx                  # Roteador principal
│   │   └── index.css                # Tailwind + animações
│   ├── docs/                         # Documentação por tela
│   ├── vite.config.js               # Proxy CORS
│   └── package.json
│
└── README.md                         # Este arquivo
```

## 🛠️ Tecnologias

| Aspecto | Tecnologia | Versão |
|--------|-----------|--------|
| **Backend - Linguagem** | Java | 21 LTS |
| **Backend - Framework** | Spring Boot | 3.2.0 |
| **Backend - Build** | Maven | 3.9.16+ |
| **Backend - ORM** | Hibernate / Spring Data JPA | 6.3.1 |
| **Backend - Banco** | PostgreSQL | Supabase (padrão) ou 16-alpine via Docker |
| **Frontend - Framework** | React | 19.2 |
| **Frontend - Build** | Vite | 8.3 |
| **Frontend - Estilos** | Tailwind CSS | 4.3 |
| **Frontend - HTTP** | Axios | 1.20 |
| **Frontend - Cache** | TanStack React Query | 5.104 |
| **Frontend - Routing** | React Router | 7.18 |

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- **Node.js 18+** (`node -v`)
- **Java 21 LTS** (`java -version`)
- **Maven 3.9+** (usar `.\mvnw.cmd` no Windows ou `./mvnw` no Linux/macOS)
- **Docker + Docker Compose** (para opção local)

---

## 📌 Opção 1: Com Docker Compose (Banco Local)

### Setup

```bash
# Clone o repositório
git clone https://github.com/TavinhoSilva06/Football-League-API.git
cd Football-League-API
```

### 1️⃣ Backend (com banco local)

O `application.properties` aponta para o Supabase por padrão. Para usar o Postgres local do `compose.yaml`, sobrescreva o datasource por variáveis de ambiente:

```powershell
# Terminal 1 - Backend
cd backend

# Inicie o PostgreSQL em container
docker compose up -d

# Descubra a porta publicada no host (o compose.yaml não fixa a porta do host)
docker compose port postgres 5432
# Exemplo de saída: 0.0.0.0:49153

# Ajuste para a porta retornada acima
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:49153/mydatabase"
$env:SPRING_DATASOURCE_USERNAME = "myuser"
$env:SPRING_DATASOURCE_PASSWORD = "secret"

# Compile e rode
.\mvnw.cmd spring-boot:run
```

**✅ Saída esperada:**
```
HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection
Started FootballLeagueApiApplication in X seconds
```

Backend em `http://localhost:8080` com dados de seed automaticamente populados.

### 2️⃣ Frontend

```powershell
# Terminal 2 - Frontend
cd frontend

# Instale dependências (primeira vez apenas)
npm install

# Rode o dev server
npm run dev
```

**✅ Saída esperada:**
```
VITE Local: http://localhost:5173
```

Frontend em `http://localhost:5173` → abre automaticamente no navegador.

---

## 📌 Opção 2: Com Supabase (Banco Remoto)

### Setup

1. Use o projeto Supabase já configurado em `spring.datasource.url`, ou crie o seu em [Supabase](https://supabase.com)
2. Para um projeto próprio, copie:
   - Host: `db.xxxxx.supabase.co`
   - Database: `postgres`
   - User: `postgres`
   - Password: sua senha
3. Se o host for diferente do padrão, defina também `$env:SPRING_DATASOURCE_URL`

### 1️⃣ Backend (com Supabase)

```powershell
# Terminal 1 - Backend
cd backend

# IMPORTANTE: Defina a senha do Supabase
$env:DB_PASSWORD = "sua_senha_supabase_aqui"

# Se usar outro projeto, ajuste também a URL
# $env:SPRING_DATASOURCE_URL = "jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres"

# Compile e rode
.\mvnw.cmd spring-boot:run
```

**O que acontece:**
- O `application.properties` lê `${DB_PASSWORD}` da variável de ambiente
- Conecta ao Supabase automaticamente
- DataSeeder popula com 3 campeonatos, 8 times, 14 jogadores, etc.

**✅ Verifique no Supabase:**
- Abra SQL Editor do projeto
- Veja as tabelas: `campeonatos`, `times`, `jogadores`, etc.

### 2️⃣ Frontend

```powershell
# Terminal 2 - Frontend
cd frontend
npm install
npm run dev
```

**Tudo funciona igual.** O frontend não sabe (nem precisa) se o banco é local ou Supabase!

---

## 📊 Comparação: Docker Compose vs Supabase

| Aspecto | Docker Compose | Supabase |
|---------|---|---|
| **Custos** | Grátis | Grátis (tier) |
| **Setup** | Requer override das variáveis do datasource | É o padrão configurado |
| **Dados Persistem** | Sem volume: até `docker compose down` | Sempre |
| **Acesso SQL** | Via `psql` | Via Supabase UI |
| **Para Desenvolvimento** | ✅ Isolado do banco remoto | ⚠️ Cuidado com dados públicos |
| **Para Produção** | ❌ Não use | ✅ Ideal |
| **CORS** | Proxy Vite | Proxy Vite |

---

## 🔐 Autenticação, Perfis e Palpites

- Cadastro público (`POST /auth/register`) cria usuários com perfil `USER`.
- Login (`POST /auth/login`) retorna token JWT; `GET /auth/me` retorna o perfil atual.
- `ADMIN` faz CRUD de campeonatos, temporadas, times e partidas; `USER` pode palpitar. A simulação é automática após o prazo da rodada.
- Administrador inicial criado por `AdminSeeder` com `app.admin.*` (sobrescrito por `ADMIN_NOME`, `ADMIN_EMAIL` e `ADMIN_PASSWORD`).

### Variáveis de ambiente

```powershell
$env:ADMIN_NOME = "Admin"
$env:ADMIN_EMAIL = "admin@footballleague.com"
$env:ADMIN_PASSWORD = "admin123"
$env:JWT_SECRET = "troque-por-uma-chave-longa-e-segura"
$env:JWT_EXPIRATION_MS = "86400000"
```

Também existe `backend/.env.example` com todas as variáveis.

### Configurações atuais

| Propriedade | Valor | Observação |
|-------------|-------|------------|
| `spring.application.name` | `chute-certo-api` | Identidade da API |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema ajustado automaticamente |
| `football.api.sync.on-startup` | `false` | Sync externo não roda ao iniciar |
| `football.api.sync.scheduled` | `false` | Sync externo não é periódico |
| `football.api.sync.min-season` / `max-season` | `2022` / `2022` | Faixa liberada pelo plano da API-Football |
| `palpite.prazo-minutos` | `4` | Duração da janela de palpites por rodada |
| `palpite.simulacao.habilitada` | `true` | Ativa o agendador de simulação |
| `palpite.simulacao.cron` | `*/5 * * * * *` | Fallback de recuperação a cada 5 segundos |

### Regras de palpites

- A rodada atual é a menor rodada que ainda tem partida `AGENDADA`.
- O acerto exige **placar exato**; cada acerto vale pontos conforme a patente do usuário no início da avaliação:

| Patente | Pontos por acerto | Limite |
|---------|-------------------|--------|
| Bronze | 20 | 0 |
| Prata | 18 | 1.000 |
| Ouro | 16 | 2.000 |
| Platina | 14 | 3.000 |
| Diamante | 12 | 4.000 |
| Perito | 10 | 5.000+ |

- Cada rodada fica aberta por `palpite.prazo-minutos` (padrão: 4), contados a partir da geração do calendário ou da conclusão da simulação da rodada anterior.
- Após o prazo ninguém cria ou altera palpites da rodada, nem `USER` nem `ADMIN`.
- A simulação é agendada para o instante exato em que o prazo expira; um fallback a cada `palpite.simulacao.cron` (padrão: 5s) recupera prazos perdidos após reinício do backend.
- A simulação sorteia os placares, finaliza a rodada, premia os palpites uma única vez e só então abre o prazo da próxima rodada.
- Rodadas simuladas sem nenhum palpite do usuário aparecem em **Meus Resultados** com a mensagem de que não houve palpite naquela rodada.
- `POST /temporadas/{temporadaId}/rodadas/{rodada}/simular` continua disponível na API, mas a interface não dispara simulação manual; o endpoint só funciona depois de expirado o prazo.
- Na interface, os placares são enviados juntos por `POST /palpites/lote`; o botão **Salvar todos** aparece enquanto a rodada está aberta.
- A simulação depende de o backend estar em execução; `palpite.simulacao.habilitada=true` ativa o agendador.
- Depois da simulação os palpites ficam bloqueados e a próxima rodada é aberta com um novo prazo.

---

## 🎯 Fluxo de Uso (Frontend)

1. **Navegação**: `Campeonatos` → selecione um campeonato
2. **Temporadas**: Clique em "📅 Gerenciar Temporadas"
3. **Criar Temporada**: Nome, datas, número de rodadas, status
4. **Inscrever Times**: Aba "Times Inscritos" → "Inscrever Time"
5. **Preparar Partidas**: gere o calendário com `POST /temporadas/{id}/calendario` ou crie partidas manualmente na aba "Partidas"
   - Geração automática: turno e returno, times inscritos com elenco
   - Criação manual: mandante, visitante, rodada, data/hora e validação mandante ≠ visitante
   - O prazo da primeira rodada começa quando o calendário é gerado
6. **Registrar Eventos** (via Postman/API):
   - Registre gols, assistências, cartões dos jogadores
   - Sistema atualiza automaticamente as estatísticas ✨
7. **Finalizar Partida**: Sistema valida placar com eventos registrados
   - Status muda para FINALIZADA
   - Número de jogos é incrementado
8. **Ver Rankings e Estatísticas**: 
   - Aba "Artilharia" → Top goleadores (apenas com gols) 🥅
   - Aba "Assistências" → Top assistentes (apenas com assistências) 🎯
   - Aba "Cartões" → Jogadores com cartões (amarelos + vermelhos) 🟨🟥
9. **Ver Classificação**: Aba "Classificação" → tabela automática com desempate!
10. **Times**: Gerenciar elencos, ver jogadores por posição
11. **Palpites**: Entre na conta, abra a aba "Palpites", preencha os placares e use "Salvar todos" antes do prazo; acompanhe a contagem regressiva e o resultado automático
    - Acerto apenas com placar exato
    - Pontos e patente atualizados automaticamente após a simulação
12. **Painel de Palpites**: acesse `/palpites` para ver aproveitamento geral, acertos, erros, pontos e histórico por temporada

---

## 📡 56 Endpoints Implementados

### Campeonatos (6)
- `GET /campeonatos` — Lista todos
- `GET /campeonatos/{id}` — Detalhe
- `GET /campeonatos/{id}/times` — Times participantes
- `POST /campeonatos` — Criar
- `PUT /campeonatos/{id}` — Editar
- `DELETE /campeonatos/{id}` — Deletar (restrição: sem temporadas)

### Temporadas (7)
- `GET /temporadas` — Lista todas
- `GET /temporadas/{id}` — Detalhe
- `GET /campeonatos/{id}/temporadas` — Por campeonato
- `POST /campeonatos/{id}/temporadas` — Criar
- `PUT /temporadas/{id}` — Editar
- `PUT /temporadas/{id}/encerrar` — Encerrar (se todas rodadas ✓)
- `DELETE /temporadas/{id}` — Deletar

### Times (5)
- `GET /times` — Lista todos
- `GET /times/{id}` — Detalhe
- `POST /times` — Criar
- `PUT /times/{id}` — Editar
- `DELETE /times/{id}` — Deletar

### Jogadores (5)
- `GET /jogadores/{id}` — Detalhe
- `GET /jogadores/time/{id}` — Por time
- `POST /jogadores` — Criar
- `PUT /jogadores/{id}` — Editar
- `DELETE /jogadores/{id}` — Deletar

### Participações (3)
- `GET /temporadas/{id}/participacoes` — Lista
- `POST /temporadas/{id}/participacoes` — Inscrever time
- `DELETE /participacoes/{id}` — Remover

### Partidas (8)
- `GET /partidas` — Todas
- `GET /partidas/{id}` — Detalhe
- `GET /temporadas/{id}/partidas` — Por temporada (filtros: rodada, status)
- `GET /times/{id}/partidas` — Por time (filtro: temporada)
- `POST /temporadas/{id}/partidas` — Criar
- `PUT /partidas/{id}` — Editar
- `PUT /partidas/{id}/resultado` — Registrar/corrigir resultado
- `DELETE /partidas/{id}` — Deletar

### Classificação (2)
- `GET /temporadas/{id}/classificacao` — Tabela com desempate automático
- `GET /campeonatos/{id}/classificacao` — Classificação por campeonato (com override de temporada)

### Eventos de Partida (4)
- `POST /partidas/{id}/eventos` — Registrar evento (gol, assistência, cartão)
- `GET /partidas/{id}/eventos` — Listar eventos de uma partida
- `GET /partidas/{id}/eventos/{eventoId}` — Detalhe de um evento
- `DELETE /partidas/{id}/eventos/{eventoId}` — Remover evento

### Estatísticas (6)
- `GET /estatisticas/temporadas/{id}/artilharia?limit=10` — Top goleadores
- `GET /estatisticas/temporadas/{id}/assistencias?limit=10` — Top assistentes
- `GET /estatisticas/temporadas/{id}/cartoes?limit=10` — Top jogadores com cartões
- `GET /estatisticas/temporadas/{id}` — Todas as estatísticas
- `GET /estatisticas/jogadores/{id}?temporadaId=` — Estatísticas de um jogador
- `PUT /estatisticas/jogadores/{id}?temporadaId=` — Criar/atualizar estatísticas

### Autenticação e Palpites (7)
- `POST /auth/register` — Cadastrar usuário (`USER`)
- `POST /auth/login` — Login com token JWT
- `GET /auth/me` — Perfil autenticado (pontos, patente, role)
- `GET /temporadas/{temporadaId}/palpites/rodada-atual` — Jogos e palpites da rodada atual
- `GET /temporadas/{temporadaId}/palpites/resultados` — Histórico de acertos por rodada
- `POST /palpites` — Criar/atualizar um palpite
- `POST /palpites/lote` — Criar/atualizar vários palpites em uma única requisição

### Simulação e Calendário (2)
- `POST /temporadas/{temporadaId}/calendario` — Gerar turno e returno automáticos
- `POST /temporadas/{temporadaId}/rodadas/{rodada}/simular` — Simular e premiar rodada após o prazo

### Sincronização (1)
- `POST /api/v1/sync/football-api` — Sincronizar times/elencos com API-Football (execução manual)

---

## 🎨 Frontend: Página por Página

| Página | Rota | Funcionalidades |
|--------|------|-----------------|
| **Visão geral / Campeonatos** | `/campeonatos` | Lista, criar, editar, excluir |
| **Detalhe Campeonato** | `/campeonatos/:id` | Ver times, botão para gerenciar temporadas |
| **Temporadas** | `/campeonatos/:id/temporadas` | Lista, criar, editar, excluir temporadas |
| **Hub Temporada** | `/temporadas/:id/*` | 8 abas: Classificação, Partidas, Palpites, Meus Resultados, Artilharia, Assistências, Cartões, Times Inscritos |
| **Painel de Palpites** | `/palpites` | Aproveitamento geral e por temporada, acertos, erros e pontos; link para `/temporadas/:id?aba=palpites` |
| **Partidas** | Aba em Temporada | Criar, editar, registrar resultado, filtros (somente ADMIN), escudos |
| **Palpites** | Aba em Temporada | Preencher placares e salvar todos de uma vez, contagem regressiva, resultado automático, pontos/patente e acertos |
| **Meus Resultados** | Aba em Temporada | Histórico de acertos e erros por rodada simulada |
| **Artilharia** | Aba em Temporada | Top goleadores (apenas com gols > 0): gols, jogos, média |
| **Assistências** | Aba em Temporada | Top assistentes (apenas com asst > 0): assistências, jogos, média |
| **Cartões** | Aba em Temporada | Jogadores com cartões (apenas com cartões > 0): amarelos, vermelhos, total |
| **Classificação** | Aba em Temporada | Tabela automática, desempate 5 critérios |
| **Times** | `/times` | Lista, criar, editar, excluir |
| **Detalhe Time** | `/times/:id` | 2 abas: Elenco (jogadores por posição), Jogos |
| **Login** | `/login` | Entrar com e-mail/senha |
| **Cadastro** | `/register` | Criar conta `USER` |

`/` redireciona para `/campeonatos`; `/temporadas` redireciona para `/campeonatos`.

**Toda interface construída com Tailwind CSS:**
- Tema: branco + azul (`#2563eb` → `#0ea5e9`), vermelho reservado para erros/ações de exclusão
- Animações: fade-in, slide-in, pop-in, float, gradient-shift, hover e escala em clique
- Responsivo: funciona em mobile, tablet, desktop
- Acessível: teclado, screen reader friendly

---

## 🐛 Troubleshooting

### "Conexão recusada" na API

**Se rodar com Docker:**
```powershell
docker compose down
docker compose up -d
# Aguarde ~5s o Postgres iniciar
docker compose port postgres 5432
# Reajuste SPRING_DATASOURCE_URL com a porta retornada
.\mvnw.cmd spring-boot:run
```

**Se rodar com Supabase:**
```powershell
# Verifique a senha
echo $env:DB_PASSWORD

# Se vazio, redefina
$env:DB_PASSWORD = "sua_senha"

# Reinicie o backend
Ctrl+C
.\mvnw.cmd spring-boot:run
```

### "Porta 8080 já em uso"

```powershell
# Encontre quem está usando
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess

# Mate o processo ou use outra porta
Stop-Process -Id <PID>
.\mvnw.cmd spring-boot:run -Dserver.port=8081
```

### Frontend não conecta na API

Verifique se o **backend está rodando** em `http://localhost:8080` e o **proxy do Vite** está ativo (rodando `npm run dev`).

---

## 📝 Docs do Frontend

Cada tela tem documentação detalhada:

- `frontend/docs/visao-geral.md` — Stack, estrutura, como rodar
- `frontend/docs/campeonatos.md` — Endpoints, formulários, regras
- `frontend/docs/temporadas.md` — Ciclo de vida, encerramento
- `frontend/docs/partidas.md` — Criação, filtros, resultado
- `frontend/docs/participacoes.md` — Inscrição de times na temporada
- `frontend/docs/classificacao.md` — Desempate, MATA_MATA inválido
- `frontend/docs/times.md` — CRUD, escudos
- `frontend/docs/jogadores.md` — Por time, criar/editar

As telas de Palpites e o painel de aproveitamento são descritos nas seções **Autenticação, Perfis e Palpites** e **Frontend: Página por Página** deste README.

---

## ✨ Design & Animações

- **Cores**: Fundo branco, gradientes azuis (`#2563eb` → `#0ea5e9`), orbes decorativos animados
- **Botões**: Gradiente animado, `hover:-translate-y-0.5`, shadow colorida, `active:scale-95`
- **Cards**: Bordas suaves, hover com elevação e sombra azul, entrada com fade-in
- **Tabs**: Pílulas com transição e destaque animado
- **Modais**: Fade-in, backdrop blur, header com gradiente e pop-in
- **Spinners**: Camadas duplas com rotação e pulso
- **Inputs**: Focus ring azul, hover suave, disabled states

---

## 📚 Documentação Adicional

- **[PLANO_IMPLEMENTACAO.md](backend/docs/PLANO_IMPLEMENTACAO.md)** — Decisões arquiteturais
- **[especificacao-football-league-api.md](backend/docs/especificacao-football-league-api.md)** — Spec funcional
- **[frontend/docs/](frontend/docs/)** — Documentação por tela

---

## 👨‍💻 Autor

**Tavinho Silva** (TavinhoSilva06)  
**Instituição**: FATEC-SP  
**Disciplina**: Engenharia de Software  
**Data**: 2026-10-01

---

## 📄 Licença

MIT License — licença prevista para o projeto; o arquivo `LICENSE` ainda não foi adicionado ao repositório.

---

## 🔗 Links Úteis

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)
- [Vite Docs](https://vite.dev)
- [Tailwind CSS](https://tailwindcss.com)
- [Supabase](https://supabase.com)
- [Docker Compose](https://docs.docker.com/compose/)

---

**Última atualização**: 2026-11-26<br>
**Status**: ✅ Nível 3 100% Completo + JWT, Perfis, Palpites em lote, Prazo por rodada, Simulação automática, Dashboard de aproveitamento e Patentes
