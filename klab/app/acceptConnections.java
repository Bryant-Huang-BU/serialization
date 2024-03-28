package klab.app;

import java.io.IOException;
import java.net.Socket;

public class acceptConnections implements Runnable{
    @Override
    public void run() {
        Node.LOGGER.info("Accept Connections thread running");
        while (!Node.serverSocket.isClosed()) {
            try {
                //Node.LOGGER.info("Waiting for connection");
                Socket sock;
                sock = Node.addConnection(Node.serverSocket.accept());
                if (sock == null) {
                    throw new IOException("Error adding connection to list");
                }
                else {
                    Node.addConnection(sock);
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
