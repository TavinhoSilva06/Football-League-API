# Classificação

**Rota:** `/temporadas/:id/classificacao` (aba do hub da temporada).

## Endpoint
`GET /temporadas/{temporadaId}/classificacao` -> lista ordenada de:
`{ posicao, timeId, timeNome, jogos, vitorias, empates, derrotas, golsPro, golsContra, saldoGols, pontos }`

## Tabela
| Coluna | Campo |
|---|---|
| # | posicao |
| Time | timeNome (link para `/times/:timeId`) |
| Pts | pontos |
| J | jogos |
| V / E / D | vitorias / empates / derrotas |
| GP / GC / SG | golsPro / golsContra / saldoGols |

Destaque visual opcional para as primeiras e últimas posições (a API não informa zonas de classificação/rebaixamento).

## Regras que afetam a UI
- Calculada a cada requisição, só com partidas `FINALIZADA`. Vitória = 3, empate = 1, derrota = 0.
- Todos os times inscritos aparecem, mesmo com 0 jogos.
- Desempate: pontos, vitórias, saldo de gols, gols pró, nome (A-Z). A API já devolve ordenado e com `posicao`; não reordenar no cliente.
- Campeonato `MATA_MATA` (ou sem formato) retorna **400**. Verifique `formato` do campeonato antes de buscar, ou trate o 400 mostrando "Classificação indisponível para este formato".
- Chave de query: `['classificacao', temporadaId]`. Invalidar ao registrar ou corrigir resultados.
- Estados: carregando, sem times inscritos, erro.
