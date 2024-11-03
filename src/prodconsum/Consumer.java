package prodconsum;

/**
 * Classe Consumer que representa o consumidor no problema Produtor-Consumidor.
 * O consumidor recebe mensagens do buffer, processa-as e envia confirmações de recebimento.
 */
public class Consumer implements Runnable {
    private final BlockingQueueBuffer buffer;
    private volatile boolean running = true;
    private final Object lock = new Object();
    private final int initialAckCount;

    /**
     * Construtor da classe Consumer.
     * @param buffer Buffer de mensagens de onde o consumidor irá retirar itens.
     * @param initialAckCount Número inicial de confirmações enviadas para indicar slots livres no buffer.
     */
    public Consumer(BlockingQueueBuffer buffer, int initialAckCount) {
        this.buffer = buffer;
        this.initialAckCount = initialAckCount;
    }

    /**
     * Método para parar a execução do consumidor.
     * Define o estado de execução para false e notifica quaisquer threads em espera.
     */
    public void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * Método principal do consumidor, que recebe e processa mensagens do buffer.
     * Envia uma quantidade inicial de confirmações e depois processa cada mensagem recebida.
     */
    @Override
    public void run() {
        try {
            // Envia mensagens iniciais para indicar espaços livres no buffer
            for (int i = 0; i < initialAckCount; i++) {
                buffer.sendAck();
                System.out.println("Consumer: Sent initial empty message to indicate free slot.");
            }

            while (running) {
                // Recebe e processa mensagem do buffer
                String message = buffer.receiveMessage();
                String item = message.replace("Message containing ", "");

                buffer.sendAck();
                System.out.println("Consumer: Sent empty message as acknowledgment.");

                System.out.println("Consumer (Server): Processed item -> " + item);

                synchronized (lock) {
                    lock.wait(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
