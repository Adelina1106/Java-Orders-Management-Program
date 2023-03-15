import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class TaskLvl2 implements Runnable {
    String order;
    AtomicInteger inQueue;
    CountDownLatch latch;
    final BufferedReader br;
    FileWriter fw;

    public TaskLvl2(String order, AtomicInteger inQueue,
                    FileWriter fw, BufferedReader br, CountDownLatch latch) {
        this.order = order;
        this.inQueue = inQueue;
        this.fw = fw;
        this.br = br;
        this.latch = latch;

    }

    @Override
    public void run() {
        String line = null;
        while (latch.getCount() > 0) {
            // reads from file until it finds new product from desired order
            try {
                synchronized (this) {
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert line != null;
            if (line.contains(order))
                try {
                    latch.countDown();
                    fw.write(line + ",shipped\n");
                    inQueue.decrementAndGet();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}