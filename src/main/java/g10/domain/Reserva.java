package g10.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Reserva {

    private final long id;
    private final Sala sala;
    private final LocalDate data;
    private final Intervalo intervalo;
    private final String responsavel;

    public Reserva(long id, Sala sala, LocalDate data, Intervalo intervalo, String responsavel) {
        Objects.requireNonNull(sala, "sala");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(intervalo, "intervalo");
        Objects.requireNonNull(responsavel, "responsavel");
        if (responsavel.isBlank()) {
            throw new IllegalArgumentException("Responsável não pode ser vazio.");
        }
        this.id = id;
        this.sala = sala;
        this.data = data;
        this.intervalo = intervalo;
        this.responsavel = responsavel.trim();
    }

    public long getId() {
        return id;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDate getData() {
        return data;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public ChaveReserva getChave() {
        return new ChaveReserva(intervalo.getInicio(), id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva reserva)) {
            return false;
        }
        return id == reserva.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s",
                id, data, intervalo, sala.getNome(), responsavel);
    }
}
