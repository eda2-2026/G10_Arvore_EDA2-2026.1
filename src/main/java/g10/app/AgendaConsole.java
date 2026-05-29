package g10.app;

import g10.domain.Intervalo;
import g10.domain.Reserva;
import g10.domain.Sala;
import g10.service.Agenda;
import g10.service.ConflitoDeHorarioException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Menu interativo para testar reservas manualmente no terminal.
 */
public class AgendaConsole {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("H:mm");

    private final Agenda agenda = new Agenda();
    private final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        new AgendaConsole().executar();
    }

    private void executar() {
        System.out.println("=== Agendador de Salas (modo manual) ===\n");

        agenda.cadastrarSala(new Sala("Sala A", 30));
        agenda.cadastrarSala(new Sala("Sala B", 15));
        System.out.println("Salas iniciais: Sala A, Sala B\n");

        boolean ativo = true;
        while (ativo) {
            imprimirMenu();
            System.out.print("Opcao: ");
            String opcao = entrada.nextLine().trim();

            try {
                switch (opcao) {
                    case "1" -> cadastrarSala();
                    case "2" -> listarSalas();
                    case "3" -> novaReserva();
                    case "4" -> listarReservasDia();
                    case "5" -> removerReserva();
                    case "6" -> listarTodas();
                    case "0" -> ativo = false;
                    default -> System.out.println("Opcao invalida.\n");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage() + "\n");
            }
        }

        System.out.println("Encerrado.");
    }

    private void imprimirMenu() {
        System.out.println("1 - Cadastrar sala");
        System.out.println("2 - Listar salas");
        System.out.println("3 - Nova reserva");
        System.out.println("4 - Listar reservas (sala + data)");
        System.out.println("5 - Remover reserva por id");
        System.out.println("6 - Listar todas as reservas");
        System.out.println("0 - Sair");
    }

    private void cadastrarSala() {
        System.out.print("Nome da sala: ");
        String nome = entrada.nextLine().trim();
        System.out.print("Capacidade (0 se nao souber): ");
        int cap = Integer.parseInt(entrada.nextLine().trim());
        agenda.cadastrarSala(new Sala(nome, cap));
        System.out.println("Sala cadastrada.\n");
    }

    private void listarSalas() {
        System.out.println("Salas:");
        for (Sala s : agenda.listarSalas()) {
            System.out.println("  - " + s);
        }
        System.out.println();
    }

    private void novaReserva() {
        Sala sala = lerSala();
        LocalDate data = lerData("Data (dd/MM/yyyy): ");
        LocalTime inicio = lerHora("Horario inicio (ex: 8:00 ou 14:30): ");
        LocalTime fim = lerHora("Horario fim: ");
        System.out.print("Responsavel: ");
        String responsavel = entrada.nextLine().trim();

        try {
            Reserva r = agenda.adicionar(sala, data, new Intervalo(inicio, fim), responsavel);
            System.out.println("Reserva criada: " + r + "\n");
        } catch (ConflitoDeHorarioException e) {
            System.out.println(e.getMessage() + "\n");
        }
    }

    private void listarReservasDia() {
        Sala sala = lerSala();
        LocalDate data = lerData("Data (dd/MM/yyyy): ");
        var lista = agenda.listarPorSalaEData(sala, data);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma reserva nesse dia.\n");
            return;
        }
        System.out.println("Reservas em " + sala.getNome() + " (" + data + "):");
        for (Reserva r : lista) {
            System.out.println("  " + r);
        }
        System.out.println();
    }

    private void removerReserva() {
        System.out.print("Id da reserva: ");
        long id = Long.parseLong(entrada.nextLine().trim());
        if (agenda.removerPorId(id)) {
            System.out.println("Reserva #" + id + " removida.\n");
        } else {
            System.out.println("Reserva nao encontrada.\n");
        }
    }

    private void listarTodas() {
        var lista = agenda.listarTodasReservas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma reserva no sistema.\n");
            return;
        }
        System.out.println("Todas as reservas:");
        for (Reserva r : lista) {
            System.out.println("  " + r);
        }
        System.out.println();
    }

    private Sala lerSala() {
        System.out.print("Nome da sala: ");
        String nome = entrada.nextLine().trim();
        return agenda.buscarSala(nome)
                .orElseThrow(() -> new IllegalArgumentException("Sala nao cadastrada: " + nome));
    }

    private LocalDate lerData(String prompt) {
        System.out.print(prompt);
        String texto = entrada.nextLine().trim();
        try {
            return LocalDate.parse(texto, FMT_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data invalida. Use dd/MM/yyyy (ex: 29/05/2026)");
        }
    }

    private LocalTime lerHora(String prompt) {
        System.out.print(prompt);
        String texto = entrada.nextLine().trim();
        try {
            return LocalTime.parse(texto, FMT_HORA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Horario invalido. Use H:mm (ex: 9:30 ou 14:00)");
        }
    }
}
