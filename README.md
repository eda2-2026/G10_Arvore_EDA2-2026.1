# Agendador de Salas com Árvore Rubro-Negra

Número da Lista: 10 <br>
Conteúdo da Disciplina: Árvore<br>

## Integrantes
| Matrícula | Aluno |
| -- | -- |
| 211063185 | Lucas Ribeiro de Souza |
| 211062796 | Mariiana Siqueira Neris |

## Apresentação
O projeto permite cadastrar salas, criar reservas com horário de início e fim, listar reservas ordenadas por horário e remover reservas existentes. A lógica de armazenamento usa uma `ArvoreRubroNegra` genérica que garante busca, inserção e remoção em tempo O(log n) e preserva a ordenação das reservas por horário.

## Gravação

Link da apresentação: [Árvore - G10]()

## Sobre

### Estrutura do projeto
- `src/main/java/g10/domain`
  - `Sala.java` – modelo de sala com nome e capacidade.
  - `Intervalo.java` – intervalo de tempo com validação de início e fim.
  - `Reserva.java` – reserva com sala, data, intervalo, responsável e id.
  - `ChaveReserva.java` – chave de ordenação para reservas.
- `src/main/java/g10/rbtree`
  - `ArvoreRubroNegra.java` – implementação da árvore rubro-negra genérica.
  - `NoRB.java` – nó da árvore.
  - `Cor.java` – enum de cores (VERMELHO, PRETO).
- `src/main/java/g10/service`
  - `Agenda.java` – lógica de negócio para cadastro de salas, reservas e validação de conflitos.
  - `ConflitoDeHorarioException.java` – exceção lançada quando reservas se sobrepõem.
- `src/main/java/g10/ui`
  - `AgendaApp.java` – aplicação JavaFX com formulário de reserva, cadastro de salas, listagem e remoção.
- `src/main/java/g10/app`
  - `Main.java` – ponto de entrada para a UI JavaFX.
  - `AgendaDemo.java` – demo de console da lógica de agenda.

### Recursos implementados
- Cadastro de salas com nome e capacidade.
- Criação de reservas com validação de conflito na mesma sala e data.
- Listagem ordenada de reservas por horário de início.
- Remoção de reserva selecionada.
- Interface gráfica JavaFX com formulário de reserva e status de exibição.
- Estrutura de dados genérica `ArvoreRubroNegra` isolada da lógica do domínio.

## Screenshots
<img width="1919" height="1019" alt="image" src="https://github.com/user-attachments/assets/d0c566d2-fe59-45e6-a9e7-af79c86fd560" />
<img width="602" height="409" alt="image" src="https://github.com/user-attachments/assets/acf84652-fc51-443f-885a-b4778e1ad82e" />
<img width="505" height="272" alt="image" src="https://github.com/user-attachments/assets/c741aef4-cabb-4b93-a4bb-9c71e7a212e0" />

## Instalação

### Requisitos
- JDK 17 instalado.
- Maven instalado para compilação e execução.
- Sistema Windows (o `pom.xml` usa classifier `win` para JavaFX).

### Como executar
1. Abra um terminal no diretório do projeto.
2. Compile o projeto:
   ```powershell
   mvn compile
   ```
3. Execute a interface JavaFX:
   ```powershell
   mvn -q -Dexec.mainClass=g10.app.Main exec:java
   ```
4. Execute a demo em console (opcional):
   ```powershell
   mvn -q -Dexec.mainClass=g10.app.AgendaDemo exec:java
   ```

> Se o comando acima falhar por falta de Maven, abra o projeto em uma IDE como IntelliJ ou Eclipse e execute a classe `g10.app.Main`.

### Testes
O projeto inclui testes JUnit para a árvore rubro-negra e para a agenda.

Executar testes:
```powershell
mvn -q test
```

## Uso 
1. Selecione uma sala e uma data.
2. Clique em `+ Nova reserva` para abrir o formulário.
3. Informe o horário de início e fim no formato `HH:mm`.
4. Digite o nome do responsável.
5. Clique em `Salvar`.
6. Se houver conflito de horário, a UI exibirá uma mensagem de erro.
7. Para remover, selecione uma reserva e clique em `Remover selecionada`.
