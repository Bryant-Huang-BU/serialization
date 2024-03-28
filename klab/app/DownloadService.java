/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 3
 * Class: CSI4321
 *
 ************************************************/

package klab.app;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The DownloadService class represents a service that handles downloads.
 * It implements the Runnable interface to 
 * allow it to be executed in a separate thread.
 */
public class DownloadService implements Runnable{
    private int max = 4;
    private ExecutorService threadPool = Executors.newFixedThreadPool(max);

    /*public void shutdown() {
        threadPool.shutdown();
    }*/

    /**
     * Adds a new download thread to the thread pool.
     *
     * @param socket the socket representing the connection to the client
     */
    public synchronized void addDownloadThread(Socket socket) {
        threadPool.execute(new DownloadThread(socket));
    }

    /**
     * This method represents the main logic of the DownloadService class.
     * It runs in a separate thread and 
     * continuously listens for incoming socket connections.
     * When a connection is established, it creates a new ]
     * DownloadThread to handle the download process.
     * The method will keep running until the downServer socket is closed.
     * If the downServer socket is closed, the method will log a message, 
     * shut down the thread pool, and exit.
     */
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
