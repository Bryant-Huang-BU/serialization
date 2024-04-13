/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 3
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.io.IOException;
import java.net.Socket;

/**
 * The acceptConnections class implements the 
 * Runnable interface and represents a threaddd
 * responsible for accepting incoming connections to the server.
 */
public class acceptConnections implements Runnable{
    /**
=     * It continuously accepts incoming
     * connections until the server socket is closed.
     */
    @Override
    public void run() {
        Node.LOGGER.info("Accept Connections thread running");
        while (!Node.serverSocket.isClosed()) {
            try {
                Socket sock;
                sock = Node.addConnection(Node.serverSocket.accept());
                if (sock == null) {
                    throw new IOException("Error adding connection to list");
                }
                else {
                    Node.LOGGER.info("Connection accepted");
                }
            } catch (Exception e) {
                if (!Node.serverSocket.isClosed()) {
                    Node.LOGGER.warning("Error accepting connection");
                }
                else {
                    Node.LOGGER.info("Server socket closed");
                }
            }
        }    
    }
}
