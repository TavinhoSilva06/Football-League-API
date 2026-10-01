# Campeonatos

**Rotas:** `/campeonatos` (lista + criar), `/campeonatos/:id` (detalhe, editar, excluir).

## Endpoints
| Método | Path | Uso |
|---|---|---|
| GET | `/campeonatos` | Lista |
| GET | `/campeonatos/{id}` | Detalhe |
| GET | `/campeonatos/{id}/times` | Times do campeonato (aba no detalhe) |
| POST | `/campeonatos` | Criar (201) |
| PUT | `/campeonatos/{id}` | Editar (200) |
| DELETE | `/campeonatos/{id}` | Excluir (204) |

## Resposta
`{ id, nome, pais, tipo, descricao, formato }`

## Formulário
| Campo | Tipo | Regra |
|---|---|---|
| nome | texto | obrigatório; único (409 se repetido) |
| pais | texto | opcional |
| tipo | select TipoCampeonato | opcional |
| formato | select FormatoCampeonato | opcional, mas necessário para a classificação funcionar |
| descricao | textarea | opcional |

## Telas
- **Lista:** cards ou tabela com nome, país, tipo e formato; botão "Novo campeonato".
- **Detalhe:** dados, abas "Temporadas" (link para temporadas.md) e "Times".
- Estados: carregando, lista vazia, erro.

## Regras que afetam a UI
- O PUT sobrescreve tudo: envie sempre o objeto completo, senão campos omitidos viram `null`.
- Excluir pode falhar com 409 se houver temporadas. Confirmar antes e explicar: "remova as temporadas primeiro".
- Formato `null` faz a classificação retornar 400.
