# Visão geral do frontend

React (Vite + JavaScript) consumindo a Football League API (Spring Boot, porta 8080).

## Stack
react, react-router-dom, axios, @tanstack/react-query, Tailwind CSS (via `@tailwindcss/vite`).

## Estrutura
```
frontend/
  src/api/client.js   axios com baseURL "/api" + parseApiError()
  src/pages/          uma página por tela (ver docs desta pasta)
  src/components/     componentes reutilizáveis (tabelas, formulários, modais)
  docs/               um .md por seção
```

## Como rodar
1. Backend: `cd backend`, defina a variável `DB_PASSWORD` (o banco é Supabase) e rode `./mvnw spring-boot:run`.
2. Frontend: `cd frontend && npm install && npm run dev` (abre em http://localhost:5173).

O backend não tem CORS. O Vite redireciona `/api/*` para `http://localhost:8080/*` removendo o prefixo `/api` (`vite.config.js`). Sempre chame a API via `api` de `src/api/client.js`, nunca com a URL completa.

## Rotas planejadas
| Rota | Documento |
|---|---|
| `/campeonatos`, `/campeonatos/:id` | campeonatos.md |
| `/campeonatos/:id/temporadas`, `/temporadas/:id` | temporadas.md |
| `/temporadas/:id/times` | participacoes.md |
| `/temporadas/:id/partidas` | partidas.md |
| `/temporadas/:id/classificacao` | classificacao.md |
| `/times`, `/times/:id` | times.md |
| `/times/:id/jogadores` | jogadores.md |

## Formato de erro da API
Todo erro vem como `{ timestamp, status, message, path }`.

| Status | Quando | O que a UI faz |
|---|---|---|
| 400 | Validação (`errors: { campo: mensagem }`) ou regra de negócio | Mostrar erro por campo; senão, `message` em destaque |
| 404 | Entidade inexistente | Tela "não encontrado" |
| 409 | Violação de constraint no banco (nome duplicado, FK) | Mensagem própria da UI, pois o texto do backend é genérico |
| 500 | Erro inesperado | Mensagem genérica |

`parseApiError(error)` devolve `{ status, message, fieldErrors }`.

## Enums (valor da API -> rótulo)
- TipoCampeonato: `LIGA_NACIONAL` Liga nacional, `CONTINENTAL` Continental, `COPA` Copa
- FormatoCampeonato: `PONTOS_CORRIDOS` Pontos corridos, `MATA_MATA` Mata-mata
- StatusTemporada: `PLANEJADA` Planejada, `EM_ANDAMENTO` Em andamento, `ENCERRADA` Encerrada
- StatusPartida: `AGENDADA`, `EM_ANDAMENTO`, `FINALIZADA`, `ADIADA`, `CANCELADA`
- PosicaoJogador: `GOLEIRO`, `DEFESA`, `MEIO_CAMPO`, `ATACANTE`

Datas: `LocalDate` = `yyyy-MM-dd`; `LocalDateTime` = `yyyy-MM-ddTHH:mm:ss`.

## Limitações conhecidas do backend (não corrigidas)
- Enum inválido em query/body e JSON malformado retornam 500 em vez de 400. Use `<select>` para todos os enums.
- Mensagens 409 são genéricas.
- Não há `GET /jogadores` (lista geral) nem endpoint de estatísticas de jogador.
- Não há autenticação.
