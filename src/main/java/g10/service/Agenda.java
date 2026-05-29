package g10.service;

import g10.domain.ChaveReserva;
import g10.domain.Intervalo;
import g10.domain.Reserva;
import g10.domain.Sala;
import g10.rbtree.ArvoreRubroNegra;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gerencia salas e reservas. Para cada par (sala, data) mantém uma
 * {@link ArvoreRubroNegra} de reservas ordenada pelo horário de início.
 */
public class Agenda {

    private final Map<String, Sala> salas = new LinkedHashMap<>();
    private final Map<ChaveDiaSala, ArvoreRubroNegra<ChaveReserva, Reserva>> reservasPorDia = new LinkedHashMap<>();
    private final AtomicLong sequenciaIds = new AtomicLong(1);

    public void cadastrarSala(Sala sala) {
        Objects.requireNonNull(sala, "sala");
        salas.put(chaveSala(sala.getNome()), sala);
    }

    public List<Sala> listarSalas() {
        return List.copyOf(salas.values());
    }

    public Optional<Sala> buscarSala(String nome) {
        return Optional.ofNullable(salas.get(chaveSala(nome)));
    }

    /**
     * Cria e armazena uma nova reserva. Lança exceção se houver sobreposição na mesma sala e data.
     */
    public Reserva adicionar(Sala sala, LocalDate data, Intervalo intervalo, String responsavel)
            throws ConflitoDeHorarioException {
        validarSalaCadastrada(sala);
        Reserva nova = new Reserva(sequenciaIds.getAndIncrement(), sala, data, intervalo, responsavel);
        adicionar(nova);
        return nova;
    }

    public void adicionar(Reserva reserva) throws ConflitoDeHorarioException {
        Objects.requireNonNull(reserva, "reserva");
        validarSalaCadastrada(reserva.getSala());

        ArvoreRubroNegra<ChaveReserva, Reserva> arvore = arvoreDe(reserva.getSala(), reserva.getData());
        for (var entrada : arvore.emOrdem()) {
            if (reserva.getIntervalo().sobrepoe(entrada.getValue().getIntervalo())) {
                throw new ConflitoDeHorarioException(entrada.getValue());
            }
        }

        arvore.inserir(reserva.getChave(), reserva);
    }

    public boolean remover(Reserva reserva) {
        Objects.requireNonNull(reserva, "reserva");
        ArvoreRubroNegra<ChaveReserva, Reserva> arvore =
                reservasPorDia.get(new ChaveDiaSala(reserva.getSala(), reserva.getData()));
        if (arvore == null) {
            return false;
        }
        return arvore.remover(reserva.getChave());
    }

    public boolean removerPorId(long id) {
        for (var arvore : reservasPorDia.values()) {
            for (var entrada : arvore.emOrdem()) {
                if (entrada.getValue().getId() == id) {
                    return arvore.remover(entrada.getKey());
                }
            }
        }
        return false;
    }

    public Optional<Reserva> buscarPorId(long id) {
        for (var arvore : reservasPorDia.values()) {
            for (var entrada : arvore.emOrdem()) {
                if (entrada.getValue().getId() == id) {
                    return Optional.of(entrada.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Reservas de uma sala em uma data, ordenadas por horário de início (percurso em ordem da RB-tree).
     */
    public List<Reserva> listarPorSalaEData(Sala sala, LocalDate data) {
        Objects.requireNonNull(sala, "sala");
        Objects.requireNonNull(data, "data");
        ArvoreRubroNegra<ChaveReserva, Reserva> arvore = reservasPorDia.get(new ChaveDiaSala(sala, data));
        if (arvore == null || arvore.estaVazia()) {
            return List.of();
        }
        List<Reserva> lista = new ArrayList<>();
        for (var entrada : arvore.emOrdem()) {
            lista.add(entrada.getValue());
        }
        return lista;
    }

    /**
     * Todas as reservas cadastradas, ordenadas por data, sala e horário.
     */
    public List<Reserva> listarTodasReservas() {
        List<Reserva> todas = new ArrayList<>();
        for (var arvore : reservasPorDia.values()) {
            for (var entrada : arvore.emOrdem()) {
                todas.add(entrada.getValue());
            }
        }
        todas.sort(Comparator
                .comparing(Reserva::getData)
                .thenComparing(r -> r.getSala().getNome())
                .thenComparing(r -> r.getIntervalo().getInicio()));
        return todas;
    }

    public int totalReservas() {
        return reservasPorDia.values().stream().mapToInt(ArvoreRubroNegra::tamanho).sum();
    }

    private ArvoreRubroNegra<ChaveReserva, Reserva> arvoreDe(Sala sala, LocalDate data) {
        ChaveDiaSala chave = new ChaveDiaSala(sala, data);
        return reservasPorDia.computeIfAbsent(chave, k -> new ArvoreRubroNegra<>());
    }

    private void validarSalaCadastrada(Sala sala) {
        if (!salas.containsKey(chaveSala(sala.getNome()))) {
            throw new IllegalArgumentException("Sala não cadastrada: " + sala.getNome());
        }
    }

    private static String chaveSala(String nome) {
        return nome.trim().toLowerCase();
    }

    private record ChaveDiaSala(Sala sala, LocalDate data) {
        private ChaveDiaSala {
            Objects.requireNonNull(sala, "sala");
            Objects.requireNonNull(data, "data");
        }
    }
}
