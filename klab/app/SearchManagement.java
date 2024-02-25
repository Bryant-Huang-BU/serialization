package klab.app;

import java.io.IOException;
import java.io.PrintWriter;
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
    @Override
    public void run() {
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
                sc.close();
                System.out.println("Search sent");
            } catch (Exception e) {
                logger.severe("Search Management thread interrupted by " + e.getMessage());
                Thread.currentThread().interrupt();
                return;
            }
        }
        Thread.currentThread().interrupt();
    }

    public void sendSearch(String searchString) throws IOException {
        try {
            //write search byte array to socket
            PrintWriter outp = new PrintWriter(echoSocket.getOutputStream(), true);
            MessageOutput out = new MessageOutput(outp);
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
