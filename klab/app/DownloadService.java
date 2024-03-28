package klab.app;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService implements Runnable{
    private int max = 4;
    private ExecutorService threadPool = Executors.newFixedThreadPool(max);

    /*public void shutdown() {
        threadPool.shutdown();
    }*/

    public synchronized void addDownloadThread(Socket socket) {
        threadPool.execute(new DownloadThread(socket));
    }

    @Override
    public void run() {
        while (!Node.downServer.isClosed()) {
            Node.LOGGER.info("Download Service Running!");
            try {
                if (Node.downServer != null) {
                    Socket socket = Node.downServer.accept();
                    if (socket != null) {
                        addDownloadThread(socket);
                        }
                    }
            }
            catch (Exception e){
                //System.out.println("RUN");
                if (Node.downServer.isClosed()) {
                    Node.LOGGER.info("Download Server Socket Closed!");
                    threadPool.shutdown();
                    break;
                }
            }
        }
    }
}
