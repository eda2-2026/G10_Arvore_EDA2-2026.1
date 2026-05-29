# Roteiro do Trabalho — Agendador de Reservas com Árvore Rubro-Negra

**Disciplina:** Estrutura de Dados 2  
**Linguagem:** Java (orientação a objetos)  
**Integrantes:** Lucas e Mariiana  
**Tema:** Agendador de intervalos — reserva de salas/consultórios com interface gráfica

---

## 1. Visão geral do projeto

Sistema desktop em Java para **gerenciar reservas de salas** em um dia (ou semana simplificada). Cada reserva ocupa um **intervalo de tempo** (início e fim). A estrutura central é uma **árvore rubro-negra** ordenada pelo horário de início, garantindo inserção, busca e remoção em **O(log n)**.

### Objetivos do trabalho

| Objetivo | Como aparece no projeto |
|----------|-------------------------|
| Implementar RB-tree | Classe `ArvoreRubroNegra` com inserir, buscar, remover, rotações e recoloração |
| OOP | Domínio (`Reserva`, `Sala`), serviço (`Agenda`), estrutura (`NoRB`) separados |
| Aplicação real | Conflito de horário, listagem ordenada, cancelamento |
| Interface gráfica | Telas para criar, listar e remover reservas com feedback visual |

### Escopo (o que entra e o que fica de fora)

**Entra (MVP):**
- Cadastro de salas (nome + capacidade opcional)
- Nova reserva: sala, data, hora início, hora fim, responsável
- Validação de **sobreposição** na mesma sala
- Listagem **ordenada** das reservas do dia (ou da sala)
- Remoção de reserva
- Interface gráfica (JavaFX recomendado; Swing aceitável se a turma preferir)

**Fica de fora (para não estourar prazo):**
- Banco de dados / login / rede
- Semana inteira com calendário complexo (pode ser fase extra)
- Persistência em arquivo (opcional como extra da Mariiana)

---

## 2. Arquitetura do código (pacotes sugeridos)

```
trabalho3-eda/
├── src/main/java/
│   ├── domain/          → Reserva, Sala, Intervalo
│   ├── rbtree/          → Cor, NoRB, ArvoreRubroNegra<K,V>
│   ├── service/         → Agenda, ConflitoDeHorarioException
│   ├── ui/              → telas JavaFX, controllers
│   └── app/             → Main.java
├── src/test/java/       → testes da árvore e da agenda (opcional)
├── docs/                → diagramas, prints para relatório
└── ROTEIRO.md           → este arquivo
```

**Regra de ouro:** `rbtree` não conhece “sala” nem “reserva”. Só chaves e valores. `Agenda` usa a árvore; a `ui` só chama `Agenda`.

---

## 3. Divisão de trabalho

O trabalho segue **duas etapas em ordem**: Lucas monta o núcleo (árvore + regras); depois Mariiana monta a interface em cima disso.

| Etapa | Quem | Foco |
|-------|------|------|
| 1 | Lucas | Árvore rubro-negra, classes do problema, `Agenda` |
| 2 | Mariiana | JavaFX, telas, integração, relatório visual, entrega |

---

### Etapa 1 — Lucas (núcleo do sistema)

Tudo que **roda sem tela**: estrutura de dados, modelos e lógica de negócio. Quando fechar, a Mariiana consegue usar a `Agenda` pronta na GUI.

| # | Tarefa | Entregável | Estimativa |
|---|--------|------------|------------|
| L1 | Estrutura do projeto Maven ou Gradle + pacotes | Projeto compila, `Main` vazio | 1–2 h |
| L2 | Enum `Cor` (VERMELHO, PRETO) e classe `NoRB<K,V>` | Nó com filhos, pai, cor, chave, valor | 2 h |
| L3 | Classe `ArvoreRubroNegra<K,V>` — **inserção** + busca | Inserir mantendo propriedades RB; busca por chave | 4–6 h |
| L4 | `ArvoreRubroNegra` — **remoção** + rotações + recoloração | Remover funciona em casos testados manualmente | 4–6 h |
| L5 | Percursos: `emOrdem`, `min`, `max` (para listar reservas) | Lista ordenada de entradas | 2 h |
| L6 | Domínio: `Intervalo`, `Sala`, `Reserva` | Objetos imutáveis ou com validação básica | 2 h |
| L7 | `Agenda` — inserir reserva com checagem de conflito | Método `adicionar(Reserva)` lança exceção se overlap | 3–4 h |
| L8 | `Agenda` — remover, buscar por id/chave, listar do dia | API documentada em comentário JavaDoc | 2 h |
| L9 | `Main` console (provisório) ou classe `AgendaDemo` | Demo: 5 reservas, 1 conflito, listagem | 1–2 h |
| L10 | Testes manuais / JUnit simples da árvore | Arquivo `src/test/...` ou roteiro de teste escrito | 2 h |

**Pronto para a Etapa 2 quando:**

- [ ] `ArvoreRubroNegra` passa em sequência: inserir 10+ chaves, buscar, remover algumas, percurso em ordem correto
- [ ] `Agenda.adicionar` rejeita reserva que sobrepõe outra na **mesma sala**
- [ ] `Agenda.listarReservasOrdenadas()` (ou equivalente) retorna lista ordenada por horário
- [ ] Lucas envia para Mariiana: **lista de métodos públicos de `Agenda`** + exemplo de uso no `AgendaDemo`

**Sugestão de chave na árvore:**  
`ChaveReserva = horário de início (LocalTime ou minutos desde 00:00) + identificador único`  
para desempate quando duas reservas começam no mesmo minuto (raro, mas evita bug).

---

### Etapa 2 — Mariiana (interface e entrega)

Deixar o sistema **usável e apresentável**: telas, integração com a `Agenda` do Lucas, README e demo.

| # | Tarefa | Entregável | Estimativa |
|---|--------|------------|------------|
| M1 | Configurar JavaFX no projeto (module-info se necessário) | App abre janela vazia | 1–2 h |
| M2 | Tela principal — layout (lista + botões) |FXML ou código: lista de reservas, botões Novo / Remover / Atualizar | 3–4 h |
| M3 | Formulário “Nova reserva” | Campos: sala, data, início, fim, responsável; botão Salvar | 3 h |
| M4 | Integração com `Agenda` | Salvar chama `adicionar`; erros mostram alerta (conflito) | 2–3 h |
| M5 | Listagem na tabela ou ListView | Colunas: sala, horário, responsável; ordenação da árvore refletida na UI | 2 h |
| M6 | Remover reserva selecionada | Confirmação + `agenda.remover(...)` | 1–2 h |
| M7 | Cadastro simples de salas (ComboBox) | Lista fixa ou tela “Adicionar sala” | 2 h |
| M8 | Feedback visual | Reserva em conflito = alerta; opcional: cores na lista | 1–2 h |
| M9 | Tela “Sobre” ou legenda RB (para relatório) | Texto explicando que reservas são armazenadas em RB-tree | 1 h |
| M10 | README + prints + roteiro de demo | `README.md` com como executar; 3–5 screenshots | 2 h |
| M11 | Relatório / slides (dividir seções com Lucas) | Seção GUI + casos de teste + conclusão (Mariiana) | 3–4 h |
| M12 | Testes finais integrados + correção de bugs | Checklist da seção 6 abaixo | 2–3 h |

**Critério de “trabalho entregue”:**

- [ ] Usuário cria reserva pela GUI sem editar código
- [ ] Conflito de horário aparece claramente (mensagem, não trava silencioso)
- [ ] Lista sempre ordenada por horário de início
- [ ] README explica como rodar e quem fez o quê

---

## 4. Cronograma sugerido (2–3 semanas)

| Semana | Lucas (Etapa 1) | Mariiana (Etapa 2) | Reunião (30 min) |
|--------|-----------------|-------------------|------------------|
| 1 | L1–L5 (árvore inserção/busca/percurso) | Estudar JavaFX + esboçar mockup no papel/Figma | Alinhar chave da árvore e campos de `Reserva` |
| 2 | L6–L10 (domínio + Agenda + demo console) | M1–M3 (setup GUI + layout + formulário) | Lucas mostra `AgendaDemo` — início da Etapa 2 |
| 3 | Apoio em bugs da árvore; relatório (parte RB) | M4–M12 (integração, polish, README, demo) | Ensaiar apresentação 10 min |

---

## 5. Interface gráfica — wireframe textual

```
┌─────────────────────────────────────────────────────────────┐
│  Agendador de Salas — EDA II                  [Lucas + Mariiana]│
├─────────────────────────────────────────────────────────────┤
│  Sala: [ ComboBox ▼ ]    Data: [ __/__/____ ]                 │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 08:00 - 09:00 │ Sala A │ João Silva                    │   │
│  │ 09:30 - 11:00 │ Sala A │ Maria Santos                  │   │
│  │ 14:00 - 16:00 │ Sala B │ Equipe Projeto                 │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  [ + Nova reserva ]  [ Remover ]  [ Atualizar lista ]        │
└─────────────────────────────────────────────────────────────┘

        ↓ "Nova reserva" abre diálogo ou painel lateral

┌──────────────────────────┐
│ Nova reserva             │
│ Início:  [ 14:00 ]       │
│ Fim:     [ 16:00 ]       │
│ Responsável: [ ______ ]  │
│      [ Cancelar ] [ OK ] │
└──────────────────────────┘
```

**Detalhes da interface:**
- Tabela ordenada por horário (reflete in-order da árvore)
- `Alert` JavaFX em português para conflito
- Título da janela com nome do sistema

---

## 6. Checklist de demonstração (dia da apresentação)

1. Inserir 3 reservas na mesma sala em horários distintos → lista ordenada.
2. Tentar reserva que **sobrepõe** → mensagem de erro.
3. Remover reserva do meio → lista continua correta.
4. (Lucas) Explicar em 2 min: o que é RB-tree, por que não usar `ArrayList` ordenada.
5. (Mariiana) Mostrar fluxo completo só pela GUI.
6. (Opcional) Comparar tempo ou mencionar `TreeMap` do Java como estrutura similar.

---

## 7. Relatório — divisão de seções

| Seção do relatório | Responsável principal |
|--------------------|------------------------|
| Introdução e problema | Ambos (1 parágrafo cada) |
| Fundamentos árvore rubro-negra | Lucas |
| Implementação `ArvoreRubroNegra` (inserção/remoção/rotações) | Lucas |
| Modelagem OOP (`Reserva`, `Agenda`) | Lucas |
| Interface gráfica e casos de uso | Mariiana |
| Testes e resultados | Mariiana (testes GUI); Lucas (testes árvore) |
| Conclusão e referências | Ambos |

---

## 8. Métodos da `Agenda` (para a Mariiana plugar na GUI)

Quando a **Etapa 1** estiver pronta, o Lucas manda (WhatsApp/Drive) a lista de métodos — algo neste formato:

```java
// Exemplo — Agenda.java (métodos que a GUI vai usar)
public class Agenda {
    void adicionar(Reserva r) throws ConflitoDeHorarioException;
    boolean remover(Reserva r);  // ou remover por id
    List<Reserva> listarPorSalaEData(Sala sala, LocalDate data);
    List<Sala> listarSalas();
    void cadastrarSala(Sala sala);
}
```

Mariiana **não altera** `rbtree/` sem combinar com o Lucas (evita conflito no Git).

---

## 9. Git e colaboração (recomendado)

- Repositório único; branches: `lucas/rbtree`, `mariiana/gui`, merge na `main` após a Etapa 1.
- Commits com prefixo: `[LUCAS]` ou `[MARIIANA]` na mensagem.
- Reunião de alinhamento no **fim da semana 2**, quando o núcleo (`Agenda`) estiver pronto.

---

## 10. Próximos passos imediatos

| Quem | Ação |
|------|------|
| **Lucas** | Criar projeto Java + pacotes + começar `NoRB` e inserção na RB-tree |
| **Mariiana** | Instalar JavaFX / ver tutorial; desenhar mockup da tela principal |
| **Ambos** | Ler este roteiro e confirmar: JavaFX ou Swing? Relatório quantas páginas? |

---

## Contato rápido de dúvidas técnicas

- **Conflito de horário:** dois intervalos `[a,b)` e `[c,d)` conflitam se `a < d` e `c < b` (mesma sala).
- **Chave duplicada:** usar desempate (id incremental) se duas reservas começam no mesmo horário.
- **Material de apoio:** Cormenho (cap. RB-tree); documentação Oracle JavaFX.
