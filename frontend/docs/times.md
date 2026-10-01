# Times

**Rotas:** `/times` (lista + criar), `/times/:id` (detalhe com abas "Elenco" e "Jogos").

## Endpoints
| Método | Path | Uso |
|---|---|---|
| GET | `/times` | Lista |
| GET | `/times/{id}` | Detalhe |
| POST | `/times` | Criar (201) |
| PUT | `/times/{id}` | Editar |
| DELETE | `/times/{id}` | Excluir (204) |

## Resposta
`{ id, nome, sigla, pais, cidade, estadio, escudoUrl }`

## Formulário
| Campo | Regra |
|---|---|
| nome | obrigatório; único (409) |
| sigla, pais, cidade, estadio | opcionais |
| escudoUrl | opcional; URL de imagem |

## Telas
- **Lista:** grade de cards com escudo, nome, cidade; busca por nome no cliente.
- **Detalhe:** dados do time; abas Elenco (jogadores.md) e Jogos (partidas.md, com filtro de temporada).

## Regras que afetam a UI
- Sem `escudoUrl` (ou imagem quebrada via `onError`), mostrar a sigla ou iniciais num círculo.
- Excluir time com partidas, participações ou jogadores vinculados pode retornar 409 (generico). Confirmar antes e explicar.
- PUT sobrescreve tudo: enviar o objeto completo.
