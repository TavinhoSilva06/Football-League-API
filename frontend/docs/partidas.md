# Partidas

**Rotas:** `/temporadas/:id/partidas` (aba do hub), `/times/:id` (aba "Jogos" do time).

## Endpoints
| Método | Path | Uso |
|---|---|---|
| GET | `/partidas` | Todas |
| GET | `/partidas/{id}` | Detalhe |
| GET | `/temporadas/{temporadaId}/partidas?rodada=&status=` | Por temporada (filtros opcionais, combináveis) |
| GET | `/times/{timeId}/partidas?temporadaId=` | Por time (temporada opcional) |
| POST | `/temporadas/{temporadaId}/partidas` | Criar (201) |
| PUT | `/partidas/{id}` | Editar dados |
| PUT | `/partidas/{id}/resultado` | Registrar ou corrigir resultado |
| DELETE | `/partidas/{id}` | Excluir (204) |

## Resposta
`{ id, temporadaId, rodada, dataHora, timeMandanteId, timeMandanteNome, timeVisitanteId, timeVisitanteNome, golsMandante, golsVisitante, status, local }` (gols são `null` até haver resultado).

## Formulário de partida
| Campo | Regra |
|---|---|
| rodada | obrigatório; entre 1 e `numRodadas` da temporada (400 fora disso) |
| timeMandanteId / timeVisitanteId | obrigatórios e diferentes (400); usar os participantes da temporada |
| dataHora | opcional (`datetime-local`) |
| local | opcional |
| status | opcional; padrão `AGENDADA` no create |

## Formulário de resultado
`golsMandante` e `golsVisitante`: inteiros >= 0, obrigatórios. Rótulo "Registrar resultado" quando `AGENDADA`, "Corrigir resultado" quando `FINALIZADA`.

## Telas
- **Lista por temporada:** agrupada por rodada, filtros de rodada e status, placar ou horário, ação de resultado.
- Estados: sem partidas, erro.

## Regras que afetam a UI
- `PUT /partidas/{id}/resultado` força o status para `FINALIZADA` e pode ser chamado de novo para sobrescrever o placar.
- Após registrar ou corrigir resultado, invalide as queries de partidas **e** de classificação (a classificação é calculada na hora).
- `PUT /partidas/{id}` é parcial (só campos não nulos), mas **não troca os times**, e a validação ainda exige rodada e ids dos times no body. Envie-os mesmo assim, ou desabilite a troca de times no formulário de edição.
- O PUT não revalida o limite de rodadas.
- Filtro `status` inválido causa 500: usar `<select>`.
