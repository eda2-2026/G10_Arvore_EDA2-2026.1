package g10.rbtree;

/**
 * Nó da árvore rubro-negra.
 */
public class NoRB<K extends Comparable<K>, V> {

    K chave;
    V valor;
    Cor cor;
    NoRB<K, V> esquerda;
    NoRB<K, V> direita;
    NoRB<K, V> pai;

    NoRB(K chave, V valor) {
        this.chave = chave;
        this.valor = valor;
        this.cor = Cor.VERMELHO;
    }
}
