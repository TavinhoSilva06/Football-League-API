# Participações (times da temporada)

**Rota:** `/temporadas/:id/times` (aba do hub da temporada).

## Endpoints
| Método | Path | Uso |
|---|---|---|
| GET | `/temporadas/{temporadaId}/participacoes` | Lista de participantes |
| POST | `/temporadas/{temporadaId}/participacoes` | Inscrever time (201) |
| DELETE | `/participacoes/{id}` | Remover time da temporada (204) |

## Resposta
`{ id, temporadaId, timeId, timeNome }`

## Formulário
Um único campo: `timeId` (obrigatório). Usar `<select>` alimentado por `GET /times`, ocultando os times já inscritos.

## Regras que afetam a UI
- Inscrever um time repetido retorna 409 ("Este time já está participando desta temporada"), mas o handler troca o texto por uma mensagem genérica. Evite o erro filtrando o select.
- O backend não exige que mandante/visitante de uma partida sejam participantes. A UI de partidas deve oferecer apenas os times inscritos.
- Remover participação pode falhar se o banco tiver dependências (409).
- O `id` a remover é o da participação, não o do time.
