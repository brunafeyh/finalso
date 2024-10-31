package prodconsumsemaphore.controller;

public class FIFO {
    private int head, tail, count;
    private final int[] buffer;

    public FIFO(int size) {
        buffer = new int[size];
        head = 0;
        tail = 0;
        count = 0;
    }

    public synchronized void enqueue(int value) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        buffer[tail] = value;
        tail = (tail + 1) % buffer.length;
        count++;
        notifyAll();
    }

    public synchronized int dequeue() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        int value = buffer[head];
        head = (head + 1) % buffer.length;
        count--;
        notifyAll();
        return value;
    }

    public synchronized boolean isFull() {
        return count == buffer.length;
    }

    public synchronized boolean isEmpty() {
        return count == 0;
    }

    public synchronized int size() {
        return count;
    }

    public synchronized int[] getContents() {
        int[] contents = new int[count];
        for (int i = 0; i < count; i++) {
            contents[i] = buffer[(head + i) % buffer.length];
        }
        return contents;
    }
}
