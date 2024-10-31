package prodconsum;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueueBuffer {
    private final Queue<String> messageQueue = new LinkedList<>();
    private final Queue<String> ackQueue = new LinkedList<>();
    private final LinkedList<String> log = new LinkedList<>();
    private int producerMessageCount = 0;
    private int consumerMessageCount = 0;

    public BlockingQueueBuffer(int capacity) {
        for (int i = 0; i < capacity; i++) {
            ackQueue.offer("EMPTY");
        }
    }

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

    public synchronized String receiveMessage() throws InterruptedException {
        while (messageQueue.isEmpty()) {
            wait();
        }

        String message = messageQueue.poll();
        consumerMessageCount++;
        log.add("Consumer: Received message -> " + message);
        System.out.println("Consumer: Received message -> " + message);

        notifyAll();
        return message;
    }

    public synchronized void sendAck() {
        ackQueue.offer("EMPTY");
        log.add("Consumer: Sent empty message as acknowledgment.");
        System.out.println("Consumer: Sent empty message as acknowledgment.");
        notifyAll();
    }

    public synchronized boolean isFull() {
        return ackQueue.isEmpty();
    }

    public synchronized Queue<String> getMessageQueue() {
        return new LinkedList<>(messageQueue);
    }

    public synchronized int getProducerMessageCount() {
        return producerMessageCount;
    }

    public synchronized int getConsumerMessageCount() {
        return consumerMessageCount;
    }

    public synchronized String getAllLogs() {
        StringBuilder allLogs = new StringBuilder();
        while (!log.isEmpty()) {
            allLogs.append(log.removeFirst()).append("\n");
        }
        return allLogs.toString();
    }
}

