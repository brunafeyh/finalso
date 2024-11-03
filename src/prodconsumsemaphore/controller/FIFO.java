package prodconsumsemaphore.controller;

/**
 * Classe FIFO que implementa um buffer circular para armazenar itens produzidos e consumidos.
 * Esta classe é usada para armazenar dados no estilo First-In-First-Out (FIFO) com controle de sincronização.
 */
public class FIFO {
    private int head, tail, count;
    private final int[] buffer;

    /**
     * Construtor da classe FIFO.
     * @param size Tamanho do buffer circular.
     */
    public FIFO(int size) {
        buffer = new int[size];
        head = 0;
        tail = 0;
        count = 0;
    }

    /**
     * Insere um valor no buffer.
     * Pré-condição: o buffer não deve estar cheio.
     * Pós-condição: o valor é adicionado na posição indicada por tail e o contador é incrementado.
     * @param value Valor a ser inserido no buffer.
     * @throws InterruptedException se a thread for interrompida enquanto espera espaço no buffer.
     */
    public synchronized void enqueue(int value) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        buffer[tail] = value;
        tail = (tail + 1) % buffer.length;
        count++;
        notifyAll();
    }

    /**
     * Remove e retorna um valor do buffer.
     * Pré-condição: o buffer não deve estar vazio.
     * Pós-condição: o valor é removido da posição indicada por head e o contador é decrementado.
     * @return Valor removido do buffer.
     * @throws InterruptedException se a thread for interrompida enquanto espera um item no buffer.
     */
    public synchronized int dequeue() throws InterruptedException {
        while (isEmpty()) {  // Aguarda se o buffer está vazio
            wait();
        }
        int value = buffer[head];
        head = (head + 1) % buffer.length;  // Atualiza head de forma circular
        count--;
        notifyAll();
        return value;
    }

    /**
     * Verifica se o buffer está cheio.
     * @return true se o buffer estiver cheio; caso contrário, false.
     */
    public synchronized boolean isFull() {
        return count == buffer.length;
    }

    /**
     * Verifica se o buffer está vazio.
     * @return true se o buffer estiver vazio; caso contrário, false.
     */
    public synchronized boolean isEmpty() {
        return count == 0;
    }

    /**
     * Retorna o número de itens atualmente no buffer.
     * @return Quantidade de itens no buffer.
     */
    public synchronized int size() {
        return count;
    }

    /**
     * Retorna uma cópia dos itens atualmente no buffer.
     * @return Array contendo os itens do buffer, na ordem de entrada.
     */
    public synchronized int[] getContents() {
        int[] contents = new int[count];
        for (int i = 0; i < count; i++) {
            contents[i] = buffer[(head + i) % buffer.length];
        }
        return contents;
    }
}
