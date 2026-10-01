# Temporadas

**Rotas:** `/campeonatos/:id/temporadas` (lista do campeonato), `/temporadas/:id` (hub da temporada com abas: times, partidas, classificação).

## Endpoints
| Método | Path | Uso |
|---|---|---|
| GET | `/temporadas` | Todas |
| GET | `/campeonatos/{campeonatoId}/temporadas` | Por campeonato |
| GET | `/temporadas/{id}` | Detalhe |
| POST | `/campeonatos/{campeonatoId}/temporadas` | Criar (201) |
| PUT | `/temporadas/{id}` | Editar |
| PUT | `/temporadas/{id}/encerrar` | Encerrar (sem body) |
| DELETE | `/temporadas/{id}` | Excluir (204) |

## Resposta
`{ id, campeonatoId, nome, dataInicio, dataFim, numRodadas, status }`

## Formulário
| Campo | Tipo | Regra |
|---|---|---|
| nome | texto | obrigatório; único dentro do campeonato (409) |
| dataInicio / dataFim | date | opcionais; validar fim >= início no cliente |
| numRodadas | número | opcional, mas necessário para encerrar e para validar a rodada das partidas |
| status | select StatusTemporada | **sempre enviar** (o backend não tem default) |

## Telas
- **Lista:** nome, período, rodadas, badge de status; "Nova temporada".
- **Hub:** cabeçalho com dados + botão "Encerrar temporada" + abas (participantes, partidas, classificação).

## Regras que afetam a UI
- **Encerrar** só funciona se `numRodadas` > 0, existe ao menos uma partida, a maior rodada é igual a `numRodadas` e todas as partidas estão `FINALIZADA`. Senão 400 com mensagem: exibir a mensagem.
- O PUT comum consegue mudar `status` direto, sem essa checagem. No formulário de edição, considere desabilitar `ENCERRADA` e deixar só o botão "Encerrar".
- Nada impede editar partidas de temporada encerrada. Opcionalmente, desabilitar edição na UI.
- PUT sobrescreve tudo: enviar o objeto completo.
