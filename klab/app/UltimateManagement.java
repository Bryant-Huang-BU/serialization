package klab.app;

import java.net.Socket;

public class UltimateManagement implements Runnable {
    private Socket socket;
    private ResponseManagement rM;
    public UltimateManagement(Socket socket) {
        this.socket = socket;
        this.rM = new ResponseManagement(socket);
    }
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
