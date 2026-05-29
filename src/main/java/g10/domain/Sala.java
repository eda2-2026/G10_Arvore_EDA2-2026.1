package g10.domain;

import java.util.Objects;

public class Sala {

    private final String nome;
    private final int capacidade;

    public Sala(String nome) {
        this(nome, 0);
    }

    public Sala(String nome, int capacidade) {
        Objects.requireNonNull(nome, "nome");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome da sala não pode ser vazio.");
        }
        if (capacidade < 0) {
            throw new IllegalArgumentException("Capacidade não pode ser negativa.");
        }
        this.nome = nome.trim();
        this.capacidade = capacidade;
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sala sala)) {
            return false;
        }
        return nome.equalsIgnoreCase(sala.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase());
    }

    @Override
    public String toString() {
        if (capacidade > 0) {
            return nome + " (cap. " + capacidade + ")";
        }
        return nome;
    }
}
