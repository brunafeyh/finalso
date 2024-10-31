package prodconsum;
public class Consumer implements Runnable {
    private final BlockingQueueBuffer buffer;
    private volatile boolean running = true;
    private final Object lock = new Object();
    private final int initialAckCount;

    public Consumer(BlockingQueueBuffer buffer, int initialAckCount) {
        this.buffer = buffer;
        this.initialAckCount = initialAckCount;
    }

    public void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < initialAckCount; i++) {
                buffer.sendAck();
                System.out.println("Consumer: Sent initial empty message to indicate free slot.");
            }

            while (running) {
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

