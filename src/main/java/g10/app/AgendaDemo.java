package g10.app;

import g10.domain.Intervalo;
import g10.domain.Reserva;
import g10.domain.Sala;
import g10.service.Agenda;
import g10.service.ConflitoDeHorarioException;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Demonstração em console da Etapa 1 (Lucas).
 */
public class AgendaDemo {

    public static void main(String[] args) {
        Agenda agenda = new Agenda();
        LocalDate hoje = LocalDate.now();

        agenda.cadastrarSala(new Sala("Sala A", 30));
        agenda.cadastrarSala(new Sala("Sala B", 15));

        System.out.println("=== Agendador de Salas (demo console) ===\n");
        System.out.println("Salas: " + agenda.listarSalas());
        System.out.println();

        try {
            agenda.adicionar(new Sala("Sala A"), hoje,
                    new Intervalo(LocalTime.of(8, 0), LocalTime.of(9, 0)), "João");
            agenda.adicionar(new Sala("Sala A"), hoje,
                    new Intervalo(LocalTime.of(9, 30), LocalTime.of(11, 0)), "Maria");
            agenda.adicionar(new Sala("Sala A"), hoje,
                    new Intervalo(LocalTime.of(14, 0), LocalTime.of(16, 0)), "Equipe Projeto");
            agenda.adicionar(new Sala("Sala B"), hoje,
                    new Intervalo(LocalTime.of(10, 0), LocalTime.of(12, 0)), "Ana");

            System.out.println("Reservas inseridas com sucesso.\n");
        } catch (ConflitoDeHorarioException e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }

        imprimirDia(agenda, new Sala("Sala A"), hoje);

        System.out.println("--- Tentativa de conflito (10:00 - 10:30 na Sala A) ---");
        try {
            agenda.adicionar(new Sala("Sala A"), hoje,
                    new Intervalo(LocalTime.of(10, 0), LocalTime.of(10, 30)), "Pedro");
            System.out.println("Inserido (não deveria).");
        } catch (ConflitoDeHorarioException e) {
            System.out.println("Bloqueado: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Remoção da reserva #2 ---");
        agenda.removerPorId(2);
        imprimirDia(agenda, new Sala("Sala A"), hoje);

        System.out.println("Total de reservas no sistema: " + agenda.totalReservas());
    }

    private static void imprimirDia(Agenda agenda, Sala sala, LocalDate data) {
        System.out.println("Reservas ordenadas — " + sala.getNome() + " em " + data + ":");
        for (Reserva r : agenda.listarPorSalaEData(sala, data)) {
            System.out.println("  " + r);
        }
        System.out.println();
    }
}
