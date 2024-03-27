package klab.app;

public class acceptConnections implements Runnable{

    @Override
    public void run() {
        Node.LOGGER.info("Accept Connections thread running");
        while (!Node.serverSocket.isClosed()) {
            try {
                //Node.LOGGER.info("Waiting for connection");
                Node.connectionsList.add(Node.serverSocket.accept());
                Node.LOGGER.info("Connection accepted");
            } catch (Exception e) {
                Node.LOGGER.warning("Error accepting connection");
            }
        }    
    }
    
}
