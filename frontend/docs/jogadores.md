# Jogadores

**Rota:** `/times/:id/jogadores` (aba "Elenco" do detalhe do time). Não há tela global de jogadores.

## Endpoints
| Método | Path | Uso |
|---|---|---|
| GET | `/jogadores/time/{timeId}` | Elenco do time |
| GET | `/jogadores/{id}` | Detalhe |
| POST | `/jogadores` | Criar (201) |
| PUT | `/jogadores/{id}` | Editar |
| DELETE | `/jogadores/{id}` | Excluir (204) |

**Não existe `GET /jogadores`** (lista geral) nem endpoint de estatísticas. Uma busca global exigiria alterar o backend.

## Resposta
`{ id, nome, timeId, timeNome, nacionalidade, posicao, numeroCamisa, dataNascimento }`

## Formulário
| Campo | Regra |
|---|---|
| nome | obrigatório |
| timeId | opcional (jogador sem time é permitido); ao criar pelo elenco, pré-preencher com o time atual. Time inexistente = 404 |
| posicao | select PosicaoJogador |
| numeroCamisa | número opcional |
| nacionalidade | texto opcional |
| dataNascimento | date opcional (`yyyy-MM-dd`) |

## Telas
- **Elenco:** tabela agrupada ou filtrável por posição (goleiros, defesa, meio-campo, atacantes), com camisa, nome, nacionalidade e idade (calculada no cliente).
- Modal para criar e editar; confirmação para excluir.
- Estados: elenco vazio, erro.

## Regras que afetam a UI
- Número de camisa não é validado como único pelo backend. Se quiser avisar duplicidade, faça no cliente.
- PUT sobrescreve tudo: enviar o objeto completo.
