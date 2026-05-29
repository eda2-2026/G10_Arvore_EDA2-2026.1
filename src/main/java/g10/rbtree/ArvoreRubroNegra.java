package g10.rbtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Árvore rubro-negra genérica ordenada pela chave {@code K}.
 * Suporta inserção, busca, remoção e percurso em ordem em O(log n).
 */
public class ArvoreRubroNegra<K extends Comparable<K>, V> {

    private final NoRB<K, V> sentinela;
    private NoRB<K, V> raiz;
    private int tamanho;

    public ArvoreRubroNegra() {
        sentinela = new NoRB<>(null, null);
        sentinela.cor = Cor.PRETO;
        raiz = sentinela;
        tamanho = 0;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    public V buscar(K chave) {
        Objects.requireNonNull(chave, "chave");
        NoRB<K, V> no = buscarNo(chave);
        return no == sentinela ? null : no.valor;
    }

    public boolean contem(K chave) {
        return buscar(chave) != null;
    }

    /**
     * Insere ou atualiza o par chave-valor.
     */
    public void inserir(K chave, V valor) {
        Objects.requireNonNull(chave, "chave");
        Objects.requireNonNull(valor, "valor");

        NoRB<K, V> pai = sentinela;
        NoRB<K, V> atual = raiz;

        while (atual != sentinela) {
            pai = atual;
            int cmp = chave.compareTo(atual.chave);
            if (cmp < 0) {
                atual = atual.esquerda;
            } else if (cmp > 0) {
                atual = atual.direita;
            } else {
                atual.valor = valor;
                return;
            }
        }

        NoRB<K, V> novo = new NoRB<>(chave, valor);
        novo.esquerda = sentinela;
        novo.direita = sentinela;
        novo.pai = pai;

        if (pai == sentinela) {
            raiz = novo;
        } else if (chave.compareTo(pai.chave) < 0) {
            pai.esquerda = novo;
        } else {
            pai.direita = novo;
        }

        tamanho++;
        corrigirInsercao(novo);
    }

    /**
     * Remove a entrada pela chave. Retorna true se removeu.
     */
    public boolean remover(K chave) {
        Objects.requireNonNull(chave, "chave");
        NoRB<K, V> no = buscarNo(chave);
        if (no == sentinela) {
            return false;
        }
        removerNo(no);
        tamanho--;
        return true;
    }

    public List<Map.Entry<K, V>> emOrdem() {
        List<Map.Entry<K, V>> lista = new ArrayList<>(tamanho);
        emOrdemRec(raiz, lista);
        return lista;
    }

    public K minimo() {
        if (estaVazia()) {
            return null;
        }
        return minimoNo(raiz).chave;
    }

    public K maximo() {
        if (estaVazia()) {
            return null;
        }
        return maximoNo(raiz).chave;
    }

    private NoRB<K, V> buscarNo(K chave) {
        NoRB<K, V> atual = raiz;
        while (atual != sentinela) {
            int cmp = chave.compareTo(atual.chave);
            if (cmp < 0) {
                atual = atual.esquerda;
            } else if (cmp > 0) {
                atual = atual.direita;
            } else {
                return atual;
            }
        }
        return sentinela;
    }

    private NoRB<K, V> minimoNo(NoRB<K, V> no) {
        while (no.esquerda != sentinela) {
            no = no.esquerda;
        }
        return no;
    }

    private NoRB<K, V> maximoNo(NoRB<K, V> no) {
        while (no.direita != sentinela) {
            no = no.direita;
        }
        return no;
    }

    private void emOrdemRec(NoRB<K, V> no, List<Map.Entry<K, V>> lista) {
        if (no == sentinela) {
            return;
        }
        emOrdemRec(no.esquerda, lista);
        lista.add(Map.entry(no.chave, no.valor));
        emOrdemRec(no.direita, lista);
    }

    private void corrigirInsercao(NoRB<K, V> no) {
        while (no != raiz && no.pai.cor == Cor.VERMELHO) {
            if (no.pai == no.pai.pai.esquerda) {
                NoRB<K, V> tio = no.pai.pai.direita;
                if (tio.cor == Cor.VERMELHO) {
                    no.pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    no.pai.pai.cor = Cor.VERMELHO;
                    no = no.pai.pai;
                } else {
                    if (no == no.pai.direita) {
                        no = no.pai;
                        rotacionarEsquerda(no);
                    }
                    no.pai.cor = Cor.PRETO;
                    no.pai.pai.cor = Cor.VERMELHO;
                    rotacionarDireita(no.pai.pai);
                }
            } else {
                NoRB<K, V> tio = no.pai.pai.esquerda;
                if (tio.cor == Cor.VERMELHO) {
                    no.pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    no.pai.pai.cor = Cor.VERMELHO;
                    no = no.pai.pai;
                } else {
                    if (no == no.pai.esquerda) {
                        no = no.pai;
                        rotacionarDireita(no);
                    }
                    no.pai.cor = Cor.PRETO;
                    no.pai.pai.cor = Cor.VERMELHO;
                    rotacionarEsquerda(no.pai.pai);
                }
            }
        }
        raiz.cor = Cor.PRETO;
    }

    private void removerNo(NoRB<K, V> no) {
        NoRB<K, V> y = no;
        NoRB<K, V> x;
        Cor corOriginalY = y.cor;

        if (no.esquerda == sentinela) {
            x = no.direita;
            transplantar(no, no.direita);
        } else if (no.direita == sentinela) {
            x = no.esquerda;
            transplantar(no, no.esquerda);
        } else {
            y = minimoNo(no.direita);
            corOriginalY = y.cor;
            x = y.direita;
            if (y.pai == no) {
                x.pai = y;
            } else {
                transplantar(y, y.direita);
                y.direita = no.direita;
                y.direita.pai = y;
            }
            transplantar(no, y);
            y.esquerda = no.esquerda;
            y.esquerda.pai = y;
            y.cor = no.cor;
        }

        if (corOriginalY == Cor.PRETO) {
            corrigirRemocao(x);
        }
    }

    private void transplantar(NoRB<K, V> u, NoRB<K, V> v) {
        if (u.pai == sentinela) {
            raiz = v;
        } else if (u == u.pai.esquerda) {
            u.pai.esquerda = v;
        } else {
            u.pai.direita = v;
        }
        v.pai = u.pai;
    }

    private void corrigirRemocao(NoRB<K, V> no) {
        while (no != raiz && no.cor == Cor.PRETO) {
            if (no == no.pai.esquerda) {
                NoRB<K, V> irmao = no.pai.direita;
                if (irmao.cor == Cor.VERMELHO) {
                    irmao.cor = Cor.PRETO;
                    no.pai.cor = Cor.VERMELHO;
                    rotacionarEsquerda(no.pai);
                    irmao = no.pai.direita;
                }
                if (irmao.esquerda.cor == Cor.PRETO && irmao.direita.cor == Cor.PRETO) {
                    irmao.cor = Cor.VERMELHO;
                    no = no.pai;
                } else {
                    if (irmao.direita.cor == Cor.PRETO) {
                        irmao.esquerda.cor = Cor.PRETO;
                        irmao.cor = Cor.VERMELHO;
                        rotacionarDireita(irmao);
                        irmao = no.pai.direita;
                    }
                    irmao.cor = no.pai.cor;
                    no.pai.cor = Cor.PRETO;
                    irmao.direita.cor = Cor.PRETO;
                    rotacionarEsquerda(no.pai);
                    no = raiz;
                }
            } else {
                NoRB<K, V> irmao = no.pai.esquerda;
                if (irmao.cor == Cor.VERMELHO) {
                    irmao.cor = Cor.PRETO;
                    no.pai.cor = Cor.VERMELHO;
                    rotacionarDireita(no.pai);
                    irmao = no.pai.esquerda;
                }
                if (irmao.direita.cor == Cor.PRETO && irmao.esquerda.cor == Cor.PRETO) {
                    irmao.cor = Cor.VERMELHO;
                    no = no.pai;
                } else {
                    if (irmao.esquerda.cor == Cor.PRETO) {
                        irmao.direita.cor = Cor.PRETO;
                        irmao.cor = Cor.VERMELHO;
                        rotacionarEsquerda(irmao);
                        irmao = no.pai.esquerda;
                    }
                    irmao.cor = no.pai.cor;
                    no.pai.cor = Cor.PRETO;
                    irmao.esquerda.cor = Cor.PRETO;
                    rotacionarDireita(no.pai);
                    no = raiz;
                }
            }
        }
        no.cor = Cor.PRETO;
    }

    private void rotacionarEsquerda(NoRB<K, V> no) {
        NoRB<K, V> direita = no.direita;
        no.direita = direita.esquerda;
        if (direita.esquerda != sentinela) {
            direita.esquerda.pai = no;
        }
        direita.pai = no.pai;
        if (no.pai == sentinela) {
            raiz = direita;
        } else if (no == no.pai.esquerda) {
            no.pai.esquerda = direita;
        } else {
            no.pai.direita = direita;
        }
        direita.esquerda = no;
        no.pai = direita;
    }

    private void rotacionarDireita(NoRB<K, V> no) {
        NoRB<K, V> esquerda = no.esquerda;
        no.esquerda = esquerda.direita;
        if (esquerda.direita != sentinela) {
            esquerda.direita.pai = no;
        }
        esquerda.pai = no.pai;
        if (no.pai == sentinela) {
            raiz = esquerda;
        } else if (no == no.pai.direita) {
            no.pai.direita = esquerda;
        } else {
            no.pai.esquerda = esquerda;
        }
        esquerda.direita = no;
        no.pai = esquerda;
    }
}
