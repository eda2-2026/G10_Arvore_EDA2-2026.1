package g10.rbtree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArvoreRubroNegraTest {

    private ArvoreRubroNegra<Integer, String> arvore;

    @BeforeEach
    void setUp() {
        arvore = new ArvoreRubroNegra<>();
    }

    @Test
    void inserirEBuscar() {
        arvore.inserir(10, "dez");
        arvore.inserir(5, "cinco");
        arvore.inserir(15, "quinze");

        assertEquals("dez", arvore.buscar(10));
        assertEquals("cinco", arvore.buscar(5));
        assertNull(arvore.buscar(99));
        assertEquals(3, arvore.tamanho());
    }

    @Test
    void emOrdemOrdenado() {
        int[] valores = {41, 38, 31, 12, 19, 8, 7, 25, 27, 22, 39, 47};
        for (int v : valores) {
            arvore.inserir(v, "n" + v);
        }

        List<Integer> chaves = new ArrayList<>();
        for (Map.Entry<Integer, String> e : arvore.emOrdem()) {
            chaves.add(e.getKey());
        }

        List<Integer> esperado = List.of(7, 8, 12, 19, 22, 25, 27, 31, 38, 39, 41, 47);
        assertEquals(esperado, chaves);
    }

    @Test
    void removerVariasChaves() {
        for (int i = 1; i <= 15; i++) {
            arvore.inserir(i, "v" + i);
        }

        assertTrue(arvore.remover(4));
        assertTrue(arvore.remover(8));
        assertTrue(arvore.remover(15));
        assertFalse(arvore.remover(99));

        assertEquals(12, arvore.tamanho());
        assertNull(arvore.buscar(4));

        List<Integer> chaves = arvore.emOrdem().stream().map(Map.Entry::getKey).toList();
        for (int i = 0; i < chaves.size() - 1; i++) {
            assertTrue(chaves.get(i) < chaves.get(i + 1));
        }
    }

    @Test
    void atualizaValorExistente() {
        arvore.inserir(1, "a");
        arvore.inserir(1, "b");
        assertEquals("b", arvore.buscar(1));
        assertEquals(1, arvore.tamanho());
    }

    @Test
    void minimoEMaximo() {
        arvore.inserir(30, "a");
        arvore.inserir(10, "b");
        arvore.inserir(50, "c");

        assertEquals(10, arvore.minimo());
        assertEquals(50, arvore.maximo());
    }
}
