package klab.app;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import klab.serialization.*;

public class SearchManagement implements Runnable{
    int id;
    Logger logger;
    Socket socket;
    public SearchManagement(Logger log, Socket s) {
        id = 0;
        this.logger = log;
        this.socket = s;
    }
    public void set() {
        logger.info("Search Management thread running");
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals("exit")) {
                    sc.close();
                    break;
                }
                sendSearch(input);
            } catch (Exception e) {
                logger.severe("Search Management thread interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }
    @Override
    public void run() {
        set();
    }

    public void sendSearch(String searchString) throws IOException {
        try {
            //write search byte array to socket
            MessageOutput out = new MessageOutput(socket.getOutputStream());
            Search searchObj = new Search(Node.intToBytes(id, 15), 50, RoutingService.DEPTHFIRST, searchString);
            id++;
            searchObj.encode(out);
            logger.log(Level.INFO, "Sent: " + searchObj.toString());
        } catch (BadAttributeValueException e) {
            logger.log(Level.SEVERE, "BadAttributeValueException: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
}
