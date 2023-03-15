import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLvl1 extends Thread {

    private final BufferedReader br_order;
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;
    private final String folder;
    private final FileWriter fw;
    private final FileWriter fw_order;

    public ThreadLvl1(BufferedReader br_order, ExecutorService tpe, AtomicInteger inQueue, String folder,
                      FileWriter fw, FileWriter fw_order) {
        this.br_order = br_order;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.folder = folder;
        this.fw = fw;
        this.fw_order = fw_order;
    }

    public synchronized void run() {
        String line = null;
        try {
            // every thread reads one line from file
            synchronized (this) {
                line = br_order.readLine();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        if (line != null) {
            // get number of products and order's name
            int products = Integer.parseInt(line.substring(line.lastIndexOf(",") + 1));
            String order = line.substring(0, line.lastIndexOf(",") - 1);

            File file = new File(folder + "/order_products.txt");
            FileReader fr = null;
            try {
                fr = new FileReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert fr != null;
            BufferedReader br = new BufferedReader(fr);

            CountDownLatch latch = null;
            if (products > 0)
                latch = new CountDownLatch(products);
            if (products > 0) {
                // add a level 2 task for each product in each order
                for (int i = 1; i <= products; i++) {
                    tpe.submit(new TaskLvl2(order, inQueue, fw, br, latch));
                }
                try {
                    // thread waits until all the products in its order are shipped
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    fw_order.write(line + ",shipped\n");
                    fr.close();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        assert line != null;
        try {
            if (br_order.ready())
                this.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
