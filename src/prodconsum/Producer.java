package prodconsum;

/**
 * Classe Producer que representa o produtor no problema Produtor-Consumidor.
 * O produtor gera mensagens e as adiciona ao buffer, respeitando o limite de capacidade.
 */
public class Producer implements Runnable {
    private final BlockingQueueBuffer buffer;
    private volatile boolean running = true;
    private final Object lock = new Object();

    /**
     * Construtor da classe Producer.
     * @param buffer Buffer de mensagens onde o produtor irá adicionar itens.
     */
    public Producer(BlockingQueueBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Método para parar a execução do produtor.
     * Define o estado de execução para false e notifica quaisquer threads em espera.
     */
    public void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * Método principal do produtor, que gera mensagens e as envia ao buffer.
     * Se o buffer estiver cheio, o produtor aguarda até que haja espaço disponível.
     */
    @Override
    public void run() {
        int messageId = 0;
        try {
            while (running) {
                String item = "Item " + messageId++;
                System.out.println("Producer: Generated item -> " + item);

                synchronized (lock) {
                    while (buffer.isFull()) {
                        lock.wait();
                    }
                }

                String message = "Message containing " + item;
                buffer.sendMessage(message);

                synchronized (lock) {
                    lock.wait(500);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
