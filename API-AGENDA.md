# API da Agenda — handoff para Etapa 2 (Mariiana)

Pacote principal: `g10`

## Como testar manualmente (console interativo)

```powershell
cd G10_Arvore_EDA2-2026.1
mvn -Pconsole compile exec:java
```

Ou: `mvn compile exec:java "-Dexec.mainClass=g10.app.AgendaConsole"`

No menu, use **3 - Nova reserva** e informe:
- Sala: `Sala A` ou `Sala B` (já cadastradas) ou cadastre outra com **1**
- Data: `29/05/2026` (formato dd/MM/yyyy)
- Horários: `8:00`, `9:30`, `14:00` etc.
- Responsável: seu nome

Demo automática (sem menu):

```powershell
mvn -q exec:java
```

Testes unitários: `mvn -q test`

## Classe `Agenda` (`g10.service.Agenda`)

| Método | Descrição |
|--------|-----------|
| `void cadastrarSala(Sala sala)` | Registra uma sala |
| `List<Sala> listarSalas()` | Salas cadastradas |
| `Optional<Sala> buscarSala(String nome)` | Busca por nome |
| `Reserva adicionar(Sala, LocalDate, Intervalo, String responsavel)` | Cria reserva; lança `ConflitoDeHorarioException` |
| `void adicionar(Reserva reserva)` | Insere reserva já montada |
| `boolean remover(Reserva reserva)` | Remove pela chave interna |
| `boolean removerPorId(long id)` | Remove pelo id |
| `Optional<Reserva> buscarPorId(long id)` | Busca em todo o sistema |
| `List<Reserva> listarPorSalaEData(Sala sala, LocalDate data)` | Lista ordenada (RB-tree em ordem) |
| `List<Reserva> listarTodasReservas()` | Todas, ordenadas |
| `int totalReservas()` | Contagem global |

## Exceção

`ConflitoDeHorarioException` — `getReservaConflitante()` retorna a reserva que já ocupava o horário.

## Modelos (`g10.domain`)

- `Sala(nome)` / `Sala(nome, capacidade)`
- `Intervalo(LocalTime inicio, LocalTime fim)`
- `Reserva` — após `adicionar`, usar `getId()`, `getSala()`, `getData()`, `getIntervalo()`, `getResponsavel()`

## Estrutura de dados

Cada par **(sala, data)** possui uma `ArvoreRubroNegra<ChaveReserva, Reserva>`.

**Não alterar** o pacote `g10.rbtree` sem combinar com o Lucas.
