import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        String folder = args[0];
        // get number of threads for every level
        int P = Integer.parseInt(args[1]);

        File file = new File(folder + "/orders.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        // pool for level 2 tasks
        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(P);
        inQueue.incrementAndGet();

        Thread[] t = new Thread[P];
        FileWriter fw = new FileWriter(folder +
                "/../../order_products_out.txt");
        FileWriter fw_order = new FileWriter(folder +
                "/../../orders_out.txt");

        // level 1 threads
        for (int i = 0; i < P; ++i) {
            t[i] = new ThreadLvl1(br, tpe, inQueue, folder, fw, fw_order);
            t[i].start();
        }

        for (int i = 0; i < P; ++i) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tpe.shutdown();
        fw.close();
        fw_order.close();
    }
}
