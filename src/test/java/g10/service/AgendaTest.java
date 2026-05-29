package g10.service;

import g10.domain.Intervalo;
import g10.domain.Reserva;
import g10.domain.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendaTest {

    private Agenda agenda;
    private Sala salaA;
    private LocalDate dia;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        salaA = new Sala("Sala A", 20);
        dia = LocalDate.of(2026, 5, 29);
        agenda.cadastrarSala(salaA);
    }

    @Test
    void listagemOrdenadaPorHorario() throws ConflitoDeHorarioException {
        agenda.adicionar(salaA, dia, new Intervalo(LocalTime.of(14, 0), LocalTime.of(15, 0)), "C");
        agenda.adicionar(salaA, dia, new Intervalo(LocalTime.of(8, 0), LocalTime.of(9, 0)), "A");
        agenda.adicionar(salaA, dia, new Intervalo(LocalTime.of(10, 0), LocalTime.of(11, 0)), "B");

        List<Reserva> lista = agenda.listarPorSalaEData(salaA, dia);
        assertEquals(3, lista.size());
        assertEquals(LocalTime.of(8, 0), lista.get(0).getIntervalo().getInicio());
        assertEquals(LocalTime.of(10, 0), lista.get(1).getIntervalo().getInicio());
        assertEquals(LocalTime.of(14, 0), lista.get(2).getIntervalo().getInicio());
    }

    @Test
    void rejeitaConflitoNaMesmaSala() throws ConflitoDeHorarioException {
        agenda.adicionar(salaA, dia, new Intervalo(LocalTime.of(9, 0), LocalTime.of(11, 0)), "Maria");

        assertThrows(ConflitoDeHorarioException.class, () ->
                agenda.adicionar(salaA, dia, new Intervalo(LocalTime.of(10, 0), LocalTime.of(10, 30)), "Pedro"));
    }

    @Test
    void permiteMesmoHorarioEmSalasDiferentes() throws ConflitoDeHorarioException {
        Sala salaB = new Sala("Sala B");
        agenda.cadastrarSala(salaB);

        agenda.adicionar(salaA, dia, new Intervalo(LocalTime.of(9, 0), LocalTime.of(10, 0)), "A");
        agenda.adicionar(salaB, dia, new Intervalo(LocalTime.of(9, 0), LocalTime.of(10, 0)), "B");

        assertEquals(1, agenda.listarPorSalaEData(salaA, dia).size());
        assertEquals(1, agenda.listarPorSalaEData(salaB, dia).size());
    }

    @Test
    void removerPorId() throws ConflitoDeHorarioException {
        Reserva r = agenda.adicionar(salaA, dia,
                new Intervalo(LocalTime.of(8, 0), LocalTime.of(9, 0)), "João");

        assertTrue(agenda.removerPorId(r.getId()));
        assertTrue(agenda.listarPorSalaEData(salaA, dia).isEmpty());
        assertFalse(agenda.removerPorId(999));
    }
}
