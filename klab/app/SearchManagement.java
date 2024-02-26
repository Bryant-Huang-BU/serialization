package klab.app;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import klab.serialization.*;

public class SearchManagement implements Runnable{
    int id;
    Socket socket;
    public SearchManagement(Socket s) {
        id = 0;
        this.socket = s;
    }
    @Override
    public void run() {
        Node.LOGGER.info("Search Management thread running");
        System.out.print("> ");
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                if (sc.hasNextLine()) {
                    String input = sc.nextLine();
                    if (input.equals("exit")) {
                        sc.close();
                        socket.close();
                        return;
                    }
                    sendSearch(input);
                    System.out.print("> ");
                }
                sc.close();
                //System.out.println("Search sent");
            }
            catch (Exception e) {
                Node.LOGGER.severe(
            "Search Management thread interrupted by "
                + e.getMessage());
                return;
            }
        }
    }

    public void sendSearch(String searchString) throws IOException {
        try {
            //write search byte array to socket
            //System.out.println("cry");
            MessageOutput out = new MessageOutput(socket.getOutputStream());
            Search searchObj = new Search(Node.intToBytes(id, 15), 50, RoutingService.DEPTHFIRST, searchString);
            id++;
            searchObj.encode(out);
            Node.addToSList(searchObj);
            Node.LOGGER.log(Level.INFO, "Sent: " + searchObj.toString());
        } catch (BadAttributeValueException e) {
            Node.LOGGER.log(Level.SEVERE, "BadAttributeValueException: " + e.getMessage());
        }
    }
}
