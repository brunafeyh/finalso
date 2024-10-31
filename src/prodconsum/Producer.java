package prodconsum;

public class Producer implements Runnable {
    private final BlockingQueueBuffer buffer;
    private volatile boolean running = true;
    private final Object lock = new Object();

    public Producer(BlockingQueueBuffer buffer) {
        this.buffer = buffer;
    }

    public void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

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
