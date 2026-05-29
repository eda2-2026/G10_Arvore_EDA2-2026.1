package g10.domain;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Chave de ordenação na árvore rubro-negra: horário de início + id (desempate).
 */
public class ChaveReserva implements Comparable<ChaveReserva> {

    private final LocalTime inicio;
    private final long id;

    public ChaveReserva(LocalTime inicio, long id) {
        Objects.requireNonNull(inicio, "inicio");
        this.inicio = inicio;
        this.id = id;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public long getId() {
        return id;
    }

    @Override
    public int compareTo(ChaveReserva outra) {
        int cmp = inicio.compareTo(outra.inicio);
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(id, outra.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChaveReserva that)) {
            return false;
        }
        return id == that.id && inicio.equals(that.inicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, id);
    }
}
