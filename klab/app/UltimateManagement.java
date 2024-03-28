/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 3
 * Class: CSI4321
 *
 ************************************************/

package klab.app;

import java.net.Socket;

/**
 * The UltimateManagement class represents a thread
 * that manages the ultimate node.
 * It implements the Runnable interface to allow it 
 * to be executed in a separate thread.
 */
public class UltimateManagement implements Runnable {
    private Socket socket;
    private ResponseManagement rM;

    /**
     * Constructs a new UltimateManagement object with the specified socket.
     * 
     * @param socket the socket to be managed by this UltimateManagement object
     */
    public UltimateManagement(Socket socket) {
        this.socket = socket;
        this.rM = new ResponseManagement(socket);
    }

    /**
     * Runs the ultimate node thread.
     * It starts a new thread for response management
     * and logs the socket information.
     */
    @Override
    public void run() {
        Node.LOGGER.info("Ultimate Node thread running for socket: " 
        + socket.getInetAddress() + ":" + socket.getPort());
        try {
            Thread rMT = new Thread(rM);
            rMT.start();
        } catch (Exception e) {
            Node.LOGGER.warning("Error in Ultimate Node thread");
        }
    }
    
}
