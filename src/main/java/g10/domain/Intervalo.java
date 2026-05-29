package g10.domain;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Intervalo de tempo [inicio, fim) no mesmo dia.
 */
public class Intervalo {

    private final LocalTime inicio;
    private final LocalTime fim;

    public Intervalo(LocalTime inicio, LocalTime fim) {
        Objects.requireNonNull(inicio, "inicio");
        Objects.requireNonNull(fim, "fim");
        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao fim.");
        }
        this.inicio = inicio;
        this.fim = fim;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFim() {
        return fim;
    }

    /**
     * Dois intervalos conflitam se há sobreposição no mesmo dia.
     */
    public boolean sobrepoe(Intervalo outro) {
        Objects.requireNonNull(outro, "outro");
        return inicio.isBefore(outro.fim) && outro.inicio.isBefore(fim);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Intervalo intervalo)) {
            return false;
        }
        return inicio.equals(intervalo.inicio) && fim.equals(intervalo.fim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, fim);
    }

    @Override
    public String toString() {
        return inicio + " - " + fim;
    }
}
