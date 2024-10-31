package prodconsumsemaphore.controller;

import java.util.concurrent.Semaphore;

public class ConsumerProducer {
    private final FIFO fifo;
    private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private volatile boolean running = true;
    private final int producerSpeed;
    private final int consumerSpeed;
    private final SimulationController controller;

    public ConsumerProducer(int bufferSize, int producerSpeed, int consumerSpeed, SimulationController controller) {
        this.fifo = new FIFO(bufferSize);
        this.empty = new Semaphore(bufferSize);
        this.full = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.producerSpeed = producerSpeed;
        this.consumerSpeed = consumerSpeed;
        this.controller = controller;
    }

    public void start() {
        Thread producerThread = new Thread(this::produceWithFIFO);
        Thread consumerThread = new Thread(this::consumeWithFIFO);

        producerThread.start();
        consumerThread.start();
    }

    private void produceWithFIFO() {
        while (running) {
            try {
                empty.acquire();
                mutex.acquire();

                int item = (int) (Math.random() * 100);
                fifo.enqueue(item);
                controller.logMessage("Produced: " + item);

                mutex.release();
                full.release();

                synchronized (this) {
                    wait(producerSpeed);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void consumeWithFIFO() {
        while (running) {
            try {
                full.acquire();
                mutex.acquire();

                int item = fifo.dequeue();
                controller.logMessage("Consumed: " + item);

                mutex.release();
                empty.release();

                synchronized (this) {
                    wait(consumerSpeed);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getBufferSize() {
        return fifo.size();
    }

    public int[] getBufferContents() {
        return fifo.getContents();
    }

    public void stop() {
        running = false;
        synchronized (this) {
            notifyAll();
        }
    }
}
