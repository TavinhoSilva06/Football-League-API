# Especificação — Football League API

## 1. Visão do projeto

Construir uma API REST profissional em **Java com Spring Boot** para consulta e gerenciamento de competições de futebol. A plataforma deve concentrar vários campeonatos nacionais e continentais, permitindo navegar por suas temporadas, times, jogadores, partidas, classificação e estatísticas.

O projeto não é uma API feita exclusivamente para um único campeonato. Cada campeonato deve ser tratado como um **registro da plataforma**, o que permite incluir, consultar e administrar novas competições sem alterar a arquitetura da aplicação.

Exemplos de competições iniciais:

- Premier League — Inglaterra
- Serie A — Itália
- Bundesliga — Alemanha
- Campeonato Brasileiro Série A — Brasil
- La Liga — Espanha
- Ligue 1 — França
- UEFA Champions League — Europa

Nome sugerido: **Football League API**.

## 2. Objetivo

Disponibilizar uma base REST que possa servir a um site, aplicativo mobile ou painel administrativo para:

- listar e detalhar campeonatos e temporadas;
- consultar os times participantes;
- registrar e consultar partidas, resultados e calendário;
- calcular e exibir classificações automaticamente;
- apresentar jogadores e estatísticas;
- permitir que usuários mantenham campeonatos, times e jogadores favoritos.

## 3. Endpoints de referência

### Campeonatos

`GET /campeonatos`

```json
[
  {
    "id": 1,
    "nome": "Premier League",
    "pais": "Inglaterra",
    "tipo": "LIGA_NACIONAL",
    "temporada": "2026/27"
  },
  {
    "id": 2,
    "nome": "Serie A",
    "pais": "Itália",
    "tipo": "LIGA_NACIONAL",
    "temporada": "2026/27"
  },
  {
    "id": 7,
    "nome": "UEFA Champions League",
    "pais": "Europa",
    "tipo": "CONTINENTAL",
    "temporada": "2026/27"
  }
]
```

`GET /campeonatos/{id}`

`POST /campeonatos`, `PUT /campeonatos/{id}` e `DELETE /campeonatos/{id}` podem compor o CRUD administrativo.

### Classificação

`GET /campeonatos/{id}/classificacao`

```json
[
  {
    "posicao": 1,
    "time": "Arsenal",
    "jogos": 10,
    "vitorias": 8,
    "empates": 1,
    "derrotas": 1,
    "golsPro": 24,
    "golsContra": 8,
    "saldoGols": 16,
    "pontos": 25
  }
]
```

### Times, jogadores e partidas

- `GET /times`
- `GET /times/{id}`
- `GET /times/{id}/jogadores`
- `GET /jogadores/{id}`
- `GET /campeonatos/{id}/partidas`
- `GET /partidas/{id}`
- `POST /partidas` — cria ou agenda uma partida
- `PUT /partidas/{id}/resultado` — registra ou altera o resultado

Exemplo de partida:

```json
{
  "id": 101,
  "campeonatoId": 1,
  "temporada": "2026/27",
  "rodada": 10,
  "dataHora": "2026-10-18T16:00:00Z",
  "mandante": { "id": 10, "nome": "Arsenal" },
  "visitante": { "id": 11, "nome": "Chelsea" },
  "placarMandante": 2,
  "placarVisitante": 1,
  "status": "FINALIZADA"
}
```

### Usuários e favoritos

- `POST /usuarios` e `POST /auth/login` — cadastro e autenticação, quando implementados;
- `GET /usuarios/me/favoritos`;
- `POST /usuarios/me/favoritos`;
- `DELETE /usuarios/me/favoritos/{favoritoId}`.

Um favorito pode referenciar um campeonato, um time ou um jogador.

## 4. Entidades e relacionamentos

| Entidade | Responsabilidade | Relacionamentos principais |
|---|---|---|
| Campeonato | Define a competição (nome, país, tipo e regulamento) | Possui temporadas e participantes |
| Temporada | Representa uma edição de um campeonato | Pertence a um campeonato; possui partidas, classificação e participantes |
| Time | Clube de futebol com dados institucionais | Tem jogadores; participa de várias temporadas |
| Participação | Associa um time a uma temporada | Forma a base da classificação daquela temporada |
| Partida | Jogo entre mandante e visitante | Pertence a uma temporada; referencia dois times |
| Jogador | Atleta vinculado a um time | Possui estatísticas por temporada/campeonato |
| Estatística de Jogador | Gols, assistências, cartões e jogos | Pertence a jogador e temporada |
| Usuário | Conta de acesso à plataforma | Possui favoritos |
| Favorito | Preferência de um usuário | Referencia campeonato, time ou jogador |

Relações centrais:

- Um campeonato possui muitas temporadas.
- Uma temporada possui muitos times por meio de participações.
- Um time pode participar de várias temporadas, inclusive de campeonatos diferentes.
- Uma temporada possui muitas partidas.
- Cada partida possui exatamente um mandante e um visitante.
- Um time possui muitos jogadores; o vínculo do jogador deve considerar período/temporada quando necessário.
- Um jogador pode ter diversas estatísticas, uma para cada recorte de temporada e competição.

## 5. Estrutura inicial do banco de dados

Tabelas iniciais sugeridas:

- `campeonatos`: `id`, `nome`, `pais`, `tipo`, `descricao`, `formato`.
- `temporadas`: `id`, `campeonato_id`, `nome`, `data_inicio`, `data_fim`, `status`.
- `times`: `id`, `nome`, `sigla`, `pais`, `cidade`, `estadio`, `escudo_url`.
- `participacoes_temporada`: `id`, `temporada_id`, `time_id`.
- `partidas`: `id`, `temporada_id`, `rodada`, `data_hora`, `time_mandante_id`, `time_visitante_id`, `gols_mandante`, `gols_visitante`, `status`.
- `jogadores`: `id`, `time_id`, `nome`, `nacionalidade`, `posicao`, `numero_camisa`, `data_nascimento`.
- `estatisticas_jogador`: `id`, `jogador_id`, `temporada_id`, `jogos`, `gols`, `assistencias`, `cartoes_amarelos`, `cartoes_vermelhos`.
- `usuarios`: `id`, `nome`, `email`, `senha_hash`, `papel`, `criado_em`.
- `favoritos`: `id`, `usuario_id`, `tipo`, `campeonato_id`, `time_id`, `jogador_id`.

Restrições importantes: e-mail único; um time não pode ter duas participações na mesma temporada; uma partida não pode ter o mesmo mandante e visitante; e placares só podem ser definidos para partidas concluídas ou em andamento, conforme a regra adotada.

## 6. Regras de negócio: pontuação e classificação

Para competições de pontos corridos, a classificação deve ser calculada a partir das partidas finalizadas:

- vitória: **3 pontos**;
- empate: **1 ponto** para cada time;
- derrota: **0 ponto**.

Para cada time, calcular:

```text
saldo de gols = gols pró - gols contra
```

Critério inicial de ordenação:

1. pontos;
2. número de vitórias;
3. saldo de gols;
4. gols pró;
5. nome do time, apenas como desempate técnico estável.

Os critérios oficiais de desempate variam conforme o campeonato. Por isso, a arquitetura deve deixar espaço para regras configuráveis por campeonato ou temporada, sem acoplar a lógica exclusivamente à Premier League, ao Brasileirão ou a outra liga.

Sempre que um resultado final for criado, alterado ou removido, a classificação e as estatísticas afetadas devem ser recalculadas de forma consistente.

## 7. Partidas e resultados

Uma partida deve suportar, ao menos:

- campeonato e temporada;
- rodada ou fase;
- data, horário e local, quando disponível;
- time mandante e visitante;
- status: `AGENDADA`, `EM_ANDAMENTO`, `FINALIZADA`, `ADIADA` ou `CANCELADA`;
- placar;
- eventos futuros, como gols, cartões e substituições.

Regras iniciais:

- apenas partidas `FINALIZADA` entram no cálculo da tabela;
- uma partida deve relacionar times participantes da mesma temporada;
- o resultado deve atualizar os dados derivados de classificação e estatísticas;
- alterações de resultados precisam recalcular os dados, evitando somar estatísticas em duplicidade.

## 8. Times, jogadores e estatísticas

O cadastro de times deve conter informações de identificação e contexto, como nome, país, cidade, estádio, sigla e imagem do escudo.

O cadastro de jogadores deve incluir nome, posição, nacionalidade, número da camisa, data de nascimento e time atual. Para uma evolução futura, modelar histórico de vínculo permite representar transferências e elencos por temporada.

Estatísticas iniciais por jogador e temporada:

- jogos disputados;
- gols;
- assistências;
- cartões amarelos;
- cartões vermelhos.

Consultas úteis incluem artilharia, líderes de assistência, elenco de um time, partidas de um time e estatísticas filtradas por campeonato e temporada.

## 9. Níveis de implementação

### Nível 1 — CRUD essencial

- CRUD de campeonatos, temporadas, times e jogadores.
- Listagem, busca por identificador e validação básica de dados.
- Persistência relacional e respostas HTTP adequadas.

### Nível 2 — Relacionamentos

- Participação de times em temporadas.
- Associação de jogadores aos times.
- Cadastro e consulta de partidas por temporada, time ou rodada.

### Nível 3 — Regras de negócio

- Registro de resultados.
- Cálculo automático de pontos, saldo de gols e classificação.
- Estatísticas por time e jogador.
- Validações que preservem a integridade dos dados esportivos.

### Nível 4 — Recursos profissionais

- Autenticação e autorização com perfis, por exemplo `ADMIN` e `USUARIO`.
- Favoritos de usuários.
- Paginação, ordenação e filtros (país, temporada, time, status de partida e jogador).
- Documentação OpenAPI/Swagger.
- Tratamento padronizado de erros e validação de requisições.
- Auditoria de alterações e testes automatizados.
- Cache para consultas muito acessadas, como classificação e artilharia.

## 10. Diretrizes arquiteturais

- Usar uma estrutura em camadas, separando controllers, serviços, repositórios, entidades e DTOs.
- Não expor diretamente as entidades de persistência na API; utilizar DTOs para entrada e saída.
- Centralizar regras de classificação e atualização de estatísticas na camada de serviço.
- Modelar temporadas explicitamente: o mesmo campeonato se repete ao longo dos anos, e times, jogadores e resultados podem mudar.
- Projetar para expansão a formatos distintos de competição. As ligas nacionais podem usar pontos corridos; a Champions League pode exigir fases, grupos ou mata-mata.
- Manter a plataforma orientada a dados: novos campeonatos devem ser incluídos por registros administrativos, e não pela criação de novos endpoints ou módulos específicos.

## 11. Escopo inicial recomendado

Começar com campeonatos de liga e pontos corridos, uma temporada por competição, times participantes, partidas e classificação automática. Depois, evoluir para usuários, favoritos, estatísticas avançadas, eventos de jogo e formatos de mata-mata.

Esta especificação define o contexto e as decisões de produto para a implementação futura; ela não determina código, bibliotecas, banco de dados específico ou integração com provedores externos de dados esportivos.
