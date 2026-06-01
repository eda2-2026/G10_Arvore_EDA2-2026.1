package g10.ui;

import g10.domain.Reserva;
import g10.domain.Sala;
import g10.domain.Intervalo;
import g10.service.Agenda;
import g10.service.ConflitoDeHorarioException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AgendaApp extends Application {

    private final Agenda agenda = new Agenda();
    private ComboBox<Sala> salaCombo;
    private DatePicker dataPicker;
    private TableView<Reserva> tabelaReservas;
    private final ObservableList<Reserva> reservasVisiveis = FXCollections.observableArrayList();

    @Override
    public void init() {
        agenda.cadastrarSala(new Sala("Sala A", 30));
        agenda.cadastrarSala(new Sala("Sala B", 20));
        agenda.cadastrarSala(new Sala("Sala C", 15));
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Agendador de Salas - EDA II");

        salaCombo = new ComboBox<>(FXCollections.observableArrayList(agenda.listarSalas()));
        salaCombo.getSelectionModel().selectFirst();
        salaCombo.setPrefWidth(160);
        salaCombo.setOnAction(e -> carregarReservas());

        dataPicker = new DatePicker(LocalDate.now());
        dataPicker.setPrefWidth(150);
        dataPicker.setOnAction(e -> carregarReservas());

        Button botaoNova = new Button("+ Nova reserva");
        botaoNova.setOnAction(e -> abrirDialogoNovaReserva());

        Button botaoNovaSala = new Button("+ Nova sala");
        botaoNovaSala.setOnAction(e -> abrirDialogoCadastrarSala());

        Button botaoRemover = new Button("Remover selecionada");
        botaoRemover.setOnAction(e -> removerReservaSelecionada());

        Button botaoAtualizar = new Button("Atualizar lista");
        botaoAtualizar.setOnAction(e -> carregarReservas());

        HBox topo = new HBox(10,
                new Label("Sala:"), salaCombo,
                new Label("Data:"), dataPicker,
                botaoNova, botaoNovaSala, botaoRemover, botaoAtualizar);
        topo.setPadding(new Insets(10));

        tabelaReservas = new TableView<>(reservasVisiveis);
        tabelaReservas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaReservas.setPlaceholder(new Label("Nenhuma reserva para esta sala/data."));
        tabelaReservas.getColumns().addAll(
                criarColuna("ID", "id", 60),
                criarColuna("Horário", "intervalo", 140),
                criarColuna("Sala", "sala", 120),
                criarColuna("Responsável", "responsavel", 200)
        );

        BorderPane raiz = new BorderPane();
        raiz.setTop(topo);
        raiz.setCenter(tabelaReservas);

        Scene cena = new Scene(raiz, 760, 440);
        stage.setScene(cena);
        stage.show();

        carregarReservas();
    }

    private TableColumn<Reserva, ?> criarColuna(String titulo, String propriedade, int largura) {
        TableColumn<Reserva, ?> coluna = new TableColumn<>(titulo);
        coluna.setCellValueFactory(new PropertyValueFactory<>(propriedade));
        coluna.setMinWidth(largura);
        return coluna;
    }

    private void abrirDialogoNovaReserva() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nova reserva");
        dialogo.setHeaderText("Preencha os dados da reserva");

        ButtonType botaoSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(botaoSalvar, ButtonType.CANCEL);

        ComboBox<Sala> salaDialog = new ComboBox<>(FXCollections.observableArrayList(agenda.listarSalas()));
        salaDialog.getSelectionModel().select(salaCombo.getSelectionModel().getSelectedItem());
        salaDialog.setPrefWidth(220);

        DatePicker dataDialog = new DatePicker(dataPicker.getValue());
        TextField inicioField = new TextField("08:00");
        TextField fimField = new TextField("09:00");
        TextField responsavelField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Sala:"), 0, 0);
        grid.add(salaDialog, 1, 0);
        grid.add(new Label("Data:"), 0, 1);
        grid.add(dataDialog, 1, 1);
        grid.add(new Label("Início (HH:mm):"), 0, 2);
        grid.add(inicioField, 1, 2);
        grid.add(new Label("Fim (HH:mm):"), 0, 3);
        grid.add(fimField, 1, 3);
        grid.add(new Label("Responsável:"), 0, 4);
        grid.add(responsavelField, 1, 4);

        dialogo.getDialogPane().setContent(grid);

        dialogo.setResultConverter(dialogButton -> dialogButton);
        dialogo.showAndWait().ifPresent(result -> {
            if (result == botaoSalvar) {
                try {
                    Sala sala = salaDialog.getValue();
                    LocalDate data = dataDialog.getValue();
                    LocalTime inicio = parseHorario(inicioField.getText());
                    LocalTime fim = parseHorario(fimField.getText());
                    String responsavel = responsavelField.getText();

                    if (sala == null || data == null || responsavel == null || responsavel.isBlank()) {
                        throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
                    }

                    agenda.adicionar(sala, data, new Intervalo(inicio, fim), responsavel);
                    carregarReservas();
                } catch (ConflitoDeHorarioException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Conflito de horário", ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Dados inválidos", ex.getMessage());
                }
            }
        });
    }

    private LocalTime parseHorario(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Horário não pode ser vazio.");
        }
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de horário inválido. Use HH:mm.");
        }
    }

    private void removerReservaSelecionada() {
        Reserva selecionada = tabelaReservas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Selecionar reserva", "Escolha uma reserva para remover.");
            return;
        }
        boolean removida = agenda.remover(selecionada);
        if (removida) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva removida", "Reserva removida com sucesso.");
            carregarReservas();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível remover a reserva selecionada.");
        }
    }

    private void abrirDialogoCadastrarSala() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Cadastrar sala");
        dialogo.setHeaderText("Informe o nome e a capacidade da nova sala");

        ButtonType botaoSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(botaoSalvar, ButtonType.CANCEL);

        TextField nomeField = new TextField();
        TextField capacidadeField = new TextField();
        capacidadeField.setPromptText("Opcional");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Nome da sala:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Capacidade:"), 0, 1);
        grid.add(capacidadeField, 1, 1);

        dialogo.getDialogPane().setContent(grid);

        dialogo.setResultConverter(dialogButton -> dialogButton);
        dialogo.showAndWait().ifPresent(result -> {
            if (result == botaoSalvar) {
                try {
                    String nome = nomeField.getText();
                    int capacidade = 0;
                    if (capacidadeField.getText() != null && !capacidadeField.getText().isBlank()) {
                        capacidade = Integer.parseInt(capacidadeField.getText().trim());
                    }
                    Sala sala = new Sala(nome, capacidade);
                    agenda.cadastrarSala(sala);
                    salaCombo.getItems().add(sala);
                    salaCombo.getSelectionModel().select(sala);
                    carregarReservas();
                } catch (NumberFormatException ex) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidade inválida", "Digite um número inteiro para a capacidade.");
                } catch (IllegalArgumentException ex) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Dados inválidos", ex.getMessage());
                }
            }
        });
    }

    private void carregarReservas() {
        Sala sala = salaCombo.getSelectionModel().getSelectedItem();
        LocalDate data = dataPicker.getValue();
        if (sala == null || data == null) {
            reservasVisiveis.setAll(List.of());
            return;
        }
        reservasVisiveis.setAll(agenda.listarPorSalaEData(sala, data));
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }
}
