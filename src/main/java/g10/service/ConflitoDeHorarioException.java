package g10.service;

import g10.domain.Reserva;

public class ConflitoDeHorarioException extends Exception {

    private final Reserva reservaConflitante;

    public ConflitoDeHorarioException(Reserva reservaConflitante) {
        super("Conflito de horário com a reserva " + reservaConflitante.getId()
                + " (" + reservaConflitante.getIntervalo() + ").");
        this.reservaConflitante = reservaConflitante;
    }

    public Reserva getReservaConflitante() {
        return reservaConflitante;
    }
}
