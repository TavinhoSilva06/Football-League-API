   # Football League API

Uma **plataforma completa de gerenciamento de competições de futebol** com API REST em Java/Spring Boot + Frontend React profissional.

## 📋 Sobre o Projeto

O **Football League API** é um projeto acadêmico desenvolvido como parte do curso de Engenharia de Software na **FATEC-PG**, com foco em aplicar padrões de arquitetura, design patterns e boas práticas de desenvolvimento full-stack.

### Funcionalidades

#### 🎮 Frontend (React + Vite + Tailwind CSS)
- ✅ Interface moderna e responsiva com tema vermelho/branco
- ✅ Navegação intuitiva: Campeonatos → Temporadas → Partidas
- ✅ Gerenciamento completo de times e elencos
- ✅ Animações suaves e efeitos visuais profissionais
- ✅ Validação de formulários com feedback de erro por campo
- ✅ Cache inteligente com React Query
- ✅ Proxy Vite resolvendo CORS automaticamente

#### 🔧 API REST (Spring Boot)
- ✅ Cadastro e gerenciamento de campeonatos
- ✅ Organização de temporadas com rodadas configuráveis
- ✅ Gestão de times e elencos de jogadores
- ✅ Registro de partidas com placares e resultados
- ✅ **Eventos de partida** (gols, assistências, cartões) com atualização automática de estatísticas
- ✅ **Cálculo automático de classificações** com 5 critérios de desempate
- ✅ **Rankings de artilharia e assistências** em tempo real
- ✅ 50+ endpoints RESTful totalmente implementados
- ✅ Validação robusta com mensagens em português

## 🏗️ Arquitetura

```
Football-League-API/
├── backend/                          # API Spring Boot
│   ├── src/main/java/.../
│   │   ├── controller/              # 8 Controllers (37 endpoints)
│   │   ├── service/                 # Lógica de negócio
│   │   ├── repository/              # Acesso a dados JPA
│   │   ├── entity/                  # 7 Entidades JPA
│   │   ├── dto/                     # Request/Response DTOs
│   │   ├── mapper/                  # Conversão entity ↔ DTO
│   │   ├── exception/               # GlobalExceptionHandler
│   │   └── enum/                    # Enums de domínio
│   ├── pom.xml
│   └── compose.yaml                 # Docker Compose (PostgreSQL local)
│
├── frontend/                         # React + Vite
│   ├── src/
│   │   ├── pages/                   # 7 páginas (Campeonatos, Temporadas, Times, etc)
│   │   ├── components/              # UI reutilizáveis + Layout
│   │   ├── api/                     # Cliente axios + hooks React Query
│   │   ├── lib/                     # Utilitários (enums, formatação)
│   │   ├── App.jsx                  # Roteador principal
│   │   └── index.css                # Tailwind + animações
│   ├── docs/                         # Documentação de cada tela
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
| **Backend - Banco** | PostgreSQL | 16-alpine (docker) |
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
- **Maven 3.9+** (usar `./mvnw` do repositório)
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

```powershell
# Terminal 1 - Backend
cd backend

# Inicie o PostgreSQL em container
docker-compose up -d

# O backend vai usar o banco local automaticamente
# Abra o arquivo: backend\src\main\resources\application.properties
# Verifique que NÃO há variável ${DB_PASSWORD} (use o compose.yaml)

# Compile e rode
./mvnw spring-boot:run
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

1. **Crie uma conta em [Supabase](https://supabase.com)**
2. **Crie um projeto** e copie:
   - Host: `db.xxxxx.supabase.co`
   - Database: `postgres`
   - User: `postgres`
   - Password: sua senha

### 1️⃣ Backend (com Supabase)

```powershell
# Terminal 1 - Backend
cd backend

# IMPORTANTE: Defina a senha do Supabase
$env:DB_PASSWORD = "sua_senha_supabase_aqui"

# Verifique
echo $env:DB_PASSWORD

# Compile e rode
./mvnw spring-boot:run
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
| **Setup** | Automático | Criar conta |
| **Dados Persistem** | Até `docker-compose down` | Sempre |
| **Acesso SQL** | Via `psql` | Via Supabase UI |
| **Para Desenvolvimento** | ✅ Recomendado | ⚠️ Cuidado com dados públicos |
| **Para Produção** | ❌ Não use | ✅ Ideal |
| **CORS** | Proxy Vite | Proxy Vite |

---

## 🎯 Fluxo de Uso (Frontend)

1. **Navegação**: `Campeonatos` → selecione um campeonato
2. **Temporadas**: Clique em "📅 Gerenciar Temporadas"
3. **Criar Temporada**: Nome, datas, número de rodadas, status
4. **Inscrever Times**: Aba "Times Inscritos" → "Inscrever Time"
5. **Criar Partidas**: Aba "Partidas" → "Nova Partida"
   - Mandante, Visitante, Rodada, Data/Hora
   - Validação automática: mandante ≠ visitante
6. **Registrar Eventos** (via Postman/API):
   - Registre gols, assistências, cartões dos jogadores
   - Sistema atualiza automaticamente as estatísticas ✨
7. **Finalizar Partida**: Sistema valida placar com eventos registrados
   - Status muda para FINALIZADA
   - Número de jogos é incrementado
8. **Ver Rankings**: 
   - Aba "Artilharia" → Top goleadores 🥅
   - Aba "Assistências" → Top assistentes 🎯
9. **Ver Classificação**: Aba "Classificação" → tabela automática com desempate!
10. **Times**: Gerenciar elencos, ver jogadores por posição

---

## 📡 50+ Endpoints Implementados

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

### Jogadores (4)
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

### Estatísticas (5)
- `GET /estatisticas/temporadas/{id}/artilharia?limit=10` — Top goleadores
- `GET /estatisticas/temporadas/{id}/assistencias?limit=10` — Top assistentes
- `GET /estatisticas/temporadas/{id}` — Todas as estatísticas
- `GET /estatisticas/jogadores/{id}?temporadaId=` — Estatísticas de um jogador
- `PUT /estatisticas/jogadores/{id}?temporadaId=` — Criar/atualizar estatísticas

---

## 🎨 Frontend: Página por Página

| Página | Rota | Funcionalidades |
|--------|------|-----------------|
| **Campeonatos** | `/campeonatos` | Lista, criar, editar, excluir |
| **Detalhe Campeonato** | `/campeonatos/:id` | Ver times, botão para gerenciar temporadas |
| **Temporadas** | `/campeonatos/:id/temporadas` | Lista, criar, editar, excluir temporadas |
| **Hub Temporada** | `/temporadas/:id` | 5 abas: Classificação, Partidas, Artilharia, Assistências, Times Inscritos |
| **Partidas** | Aba em Temporada | Criar, editar, registrar resultado, filtros |
| **Artilharia** | Aba em Temporada | Top goleadores com gols, jogos, média |
| **Assistências** | Aba em Temporada | Top assistentes com assistências, jogos, média |
| **Classificação** | Aba em Temporada | Tabela automática, desempate 5 critérios |
| **Times** | `/times` | Lista, criar, editar, excluir |
| **Detalhe Time** | `/times/:id` | 2 abas: Elenco (jogadores por posição), Jogos |

**Toda interface construída com Tailwind CSS:**
- Tema: vermelho `#b91c1c` + branco
- Animações: fade-in, hover suave, escala em clique
- Responsivo: funciona em mobile, tablet, desktop
- Acessível: teclado, screen reader friendly

---

## 🐛 Troubleshooting

### "Conexão recusada" na API

**Se rodar com Docker:**
```bash
docker-compose down
docker-compose up -d
# Aguarde ~5s o Postgres iniciar
./mvnw spring-boot:run
```

**Se rodar com Supabase:**
```powershell
# Verifique a senha
echo $env:DB_PASSWORD

# Se vazio, redefina
$env:DB_PASSWORD = "sua_senha"

# Reinicie o backend
Ctrl+C
./mvnw spring-boot:run
```

### "Porta 8080 já em uso"

```bash
# Encontre quem está usando
lsof -i :8080

# Mate o processo ou use outra porta
./mvnw spring-boot:run -Dserver.port=8081
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
- `frontend/docs/classificacao.md` — Desempate, MATA_MATA inválido
- `frontend/docs/times.md` — CRUD, escudos
- `frontend/docs/jogadores.md` — Por time, criar/editar

---

## ✨ Design & Animações

- **Cores**: Gradientes vermelho (red-600 → red-700), sombras dinâmicas
- **Botões**: Efeito `active:scale-95`, shadow-lg no hover, transição 200ms
- **Cards**: Backdrop blur, hover:shadow-lg, border-red-300 ao passar o mouse
- **Modais**: Fade-in, backdrop blur, header com gradiente
- **Spinners**: Dupla camada de animação
- **Inputs**: Focus ring, transição suave, disabled states

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

MIT License — Veja [LICENSE](LICENSE) para detalhes.

---

## 🔗 Links Úteis

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)
- [Vite Docs](https://vite.dev)
- [Tailwind CSS](https://tailwindcss.com)
- [Supabase](https://supabase.com)
- [Docker Compose](https://docs.docker.com/compose/)

---

**Última atualização**: 2026-10-06  
**Status**: ✅ Nível 3 Completo (Backend + Frontend + Eventos de Partida + Rankings)
