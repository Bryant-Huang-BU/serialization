package klab.app;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import klab.serialization.*;

public class SearchManagement implements Runnable{
    String search;
    public SearchManagement(String search) {
        this.search = search;
    }
    @Override
    public void run() {
        Node.LOGGER.info("Search Management thread running");
        try {
            //write search byte array to Node.socket
            //System.out.println("cry");
            MessageOutput out = new MessageOutput(Node.socket.getOutputStream());
            byte[] id = new byte[15];
            Random rand = new Random();
            for (int i = 0; i < 15; i++) {
                id[i] = (byte) rand.nextInt(255);
            }
            Search searchObj = new Search(id, 50, RoutingService.DEPTHFIRST, search);
            searchObj.encode(out);
            Node.addToSList(searchObj);
            Node.LOGGER.log(Level.INFO, "Sent: " + searchObj.toString());
        } catch (BadAttributeValueException | IOException e) {
            Node.LOGGER.log(Level.SEVERE, "BadAttributeValueException: " + e.getMessage());
        }
    }
}
