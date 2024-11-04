package prodconsum;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Classe BlockingQueueBuffer que implementa um buffer bloqueante para armazenar mensagens.
 * Permite a troca de mensagens entre o produtor e o consumidor, garantindo que o consumidor
 * só consuma mensagens quando houver disponibilidade no buffer.
 */
public class BlockingQueueBuffer {
    private final Queue<String> messageQueue = new LinkedList<>();
    private final Queue<String> ackQueue = new LinkedList<>();
    private final LinkedList<String> log = new LinkedList<>();
    private int producerMessageCount = 0;
    private int consumerMessageCount = 0;
    private final int capacity; // Campo para armazenar a capacidade do buffer

    /**
     * Construtor da classe BlockingQueueBuffer.
     * @param capacity Capacidade do buffer. Inicializa a fila de confirmações (ackQueue) com mensagens vazias.
     */
    public BlockingQueueBuffer(int capacity) {
        this.capacity = capacity; // Armazena a capacidade no campo
        for (int i = 0; i < capacity; i++) {
            ackQueue.offer("EMPTY");
        }
    }

    /**
     * Envia uma mensagem para o buffer. Aguarda se o buffer está cheio (ackQueue vazia).
     * Incrementa o contador de mensagens produzidas e adiciona o log da operação.
     * @param message Mensagem a ser enviada ao buffer.
     * @throws InterruptedException se a thread for interrompida enquanto aguarda espaço no buffer.
     */
    public synchronized void sendMessage(String message) throws InterruptedException {
        while (ackQueue.isEmpty()) {
            wait();
        }

        ackQueue.poll();
        messageQueue.offer(message);
        producerMessageCount++;
        log.add("Producer: Sent message -> " + message);
        System.out.println("Producer: Sent message -> " + message);

        notifyAll();
    }

    /**
     * Recebe uma mensagem do buffer. Aguarda se não houver mensagens disponíveis.
     * Incrementa o contador de mensagens consumidas e adiciona o log da operação.
     * @return A mensagem recebida.
     * @throws InterruptedException se a thread for interrompida enquanto aguarda uma mensagem.
     */
    public synchronized String receiveMessage() throws InterruptedException {
        while (messageQueue.isEmpty()) {
            wait();
        }

        String message = messageQueue.poll();
        consumerMessageCount++;
        log.add("Consumer: Received message -> " + message);
        System.out.println("Consumer: Received message -> " + message);
        sendAck();
        notifyAll();
        return message;
    }

    /**
     * Envia uma confirmação de que uma mensagem foi consumida, liberando um espaço no buffer.
     * Adiciona um log da confirmação enviada.
     */
    public synchronized void sendAck() {
        ackQueue.offer("EMPTY");
        log.add("Consumer: Sent empty message as acknowledgment.");
        System.out.println("Consumer: Sent empty message as acknowledgment.");
        notifyAll();
    }

    /**
     * Verifica se o buffer está cheio (ackQueue vazia).
     * @return true se o buffer está cheio; caso contrário, false.
     */
    public synchronized boolean isFull() {
        return ackQueue.isEmpty();
    }

    /**
     * Retorna uma cópia da fila de mensagens para visualização.
     * @return Uma nova fila contendo as mensagens atuais no buffer.
     */
    public synchronized Queue<String> getMessageQueue() {
        return new LinkedList<>(messageQueue);
    }

    /**
     * Retorna o número total de mensagens produzidas.
     * @return Contador de mensagens produzidas.
     */
    public synchronized int getProducerMessageCount() {
        return producerMessageCount;
    }

    /**
     * Retorna o número total de mensagens consumidas.
     * @return Contador de mensagens consumidas.
     */
    public synchronized int getConsumerMessageCount() {
        return consumerMessageCount;
    }

    /**
     * Retorna todos os logs de atividades e esvazia o log.
     * @return Uma string contendo todas as mensagens de log.
     */
    public synchronized String getAllLogs() {
        StringBuilder allLogs = new StringBuilder();
        while (!log.isEmpty()) {
            allLogs.append(log.removeFirst()).append("\n");
        }
        return allLogs.toString();
    }

    /**
     * Limpa o buffer, resetando as filas e reiniciando as mensagens de confirmação.
     */
    public synchronized void clearBuffer() {
        messageQueue.clear();
        ackQueue.clear();
        log.clear();
        for (int i = 0; i < capacity; i++) {
            ackQueue.offer("EMPTY");
        }
        System.out.println("Buffer has been cleared.");
    }

    /**
     * Retorna a capacidade do buffer.
     * @return Capacidade do buffer.
     */
    public int getCapacity() {
        return capacity;
    }
}
