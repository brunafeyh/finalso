package prodconsumsemaphore.controller;

import java.util.concurrent.Semaphore;

/**
 * Classe ConsumerProducer - Gerencia a sincronização e o controle de produção e consumo de itens em um buffer FIFO utilizando semáforos.
 */
public class ConsumerProducer {
    private final FIFO fifo;
    private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private volatile boolean running = true;
    private final int producerSpeed; // Velocidade de produção em milissegundos
    private final int consumerSpeed; // Velocidade de consumo em milissegundos
    private final SimulationController controller;

    /**
     * Construtor da classe ConsumerProducer.
     *
     * @param bufferSize     Tamanho do buffer FIFO.
     * @param producerSpeed  Velocidade do produtor (em ms).
     * @param consumerSpeed  Velocidade do consumidor (em ms).
     * @param controller     Controlador para atualizar a interface da simulação.
     */
    public ConsumerProducer(int bufferSize, int producerSpeed, int consumerSpeed, SimulationController controller) {
        this.fifo = new FIFO(bufferSize);
        this.empty = new Semaphore(bufferSize);
        this.full = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.producerSpeed = producerSpeed;
        this.consumerSpeed = consumerSpeed;
        this.controller = controller;
    }

    /**
     * Inicia as threads de produtor e consumidor.
     * Pré-condição: A instância deve estar configurada com os semáforos e o buffer FIFO.
     * Pós-condição: As threads de produção e consumo começam a operar de acordo com a lógica FIFO.
     */
    public void start() {
        Thread producerThread = new Thread(this::produceWithFIFO);
        Thread consumerThread = new Thread(this::consumeWithFIFO);

        producerThread.start();
        consumerThread.start();
    }

    /**
     * Método de produção - Gera itens aleatórios e os insere no buffer FIFO.
     * Pré-condição: Deve haver espaço disponível no buffer (controlado pelo semáforo empty).
     * Pós-condição: Um novo item é adicionado ao buffer, e o semáforo full é incrementado.
     */
    private void produceWithFIFO() {
        while (running) {
            try {
                empty.acquire(); // Aguarda espaço disponível no buffer
                mutex.acquire(); // Garante acesso exclusivo ao buffer

                int item = (int) (Math.random() * 100); // Gera item aleatório
                fifo.enqueue(item); // Insere item no buffer
                controller.logMessage("Produced: " + item); // Log da produção

                mutex.release(); // Libera o acesso ao buffer
                full.release(); // Sinaliza que há um item disponível para consumo

                synchronized (this) {
                    wait(producerSpeed); // Aguarda o tempo de produção configurado
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Método de consumo - Remove itens do buffer FIFO e processa-os.
     * Pré-condição: Deve haver itens disponíveis no buffer (controlado pelo semáforo full).
     * Pós-condição: Um item é removido do buffer e o semáforo empty é incrementado.
     */
    private void consumeWithFIFO() {
        while (running) {
            try {
                full.acquire(); // Aguarda item disponível para consumo
                mutex.acquire(); // Garante acesso exclusivo ao buffer

                int item = fifo.dequeue(); // Remove item do buffer
                controller.logMessage("Consumed: " + item); // Log do consumo

                mutex.release(); // Libera o acesso ao buffer
                empty.release(); // Sinaliza que há espaço disponível no buffer

                synchronized (this) {
                    wait(consumerSpeed); // Aguarda o tempo de consumo configurado
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Retorna o tamanho atual do buffer.
     *
     * @return O tamanho do buffer FIFO.
     */
    public int getBufferSize() {
        return fifo.size();
    }

    /**
     * Retorna o conteúdo atual do buffer.
     *
     * @return Array com os itens atualmente armazenados no buffer.
     */
    public int[] getBufferContents() {
        return fifo.getContents();
    }

    /**
     * Interrompe a execução das threads de produtor e consumidor.
     * Pré-condição: As threads de produção e consumo devem estar em execução.
     * Pós-condição: As threads de produção e consumo são sinalizadas para finalizar.
     */
    public void stop() {
        running = false;
        synchronized (this) {
            notifyAll(); // Notifica todas as threads para encerrar
        }
    }
}
