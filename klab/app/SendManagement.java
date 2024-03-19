/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 2
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;


import klab.serialization.*;

/**
 * The SendManagement class implements the Runnable 
 * interface and represents a thread
 * responsible for sending search requests or responses.
 */
public class SendManagement implements Runnable{
    Search search;
    Boolean flag;
    /**
     * Constructs a new SendManagement object with
     * the specified search and flag.
     *
     * @param search the Search object to be used
     * @param flag   the flag indicating whether to send the management or not
     */
    public SendManagement(Search search, Boolean flag) {
        this.search = search;
        this.flag = flag;
    }
    /**
     * Executes the logic of the SendManagement thread.
     * If the flag is false, it sends out a search request
     * by writing the search byte array to Node.socket.
     * If the flag is true, it sends out a response by 
     * finding the file and encoding it to the output stream.
     * Any exceptions that occur during execution are logged as warnings.
     */
    @Override
    public void run() {
        Node.LOGGER.info("Send Management thread running");
        try {
            if (!flag) { //SEND OUT SEARCH
                //write search byte array to Node.socket
                MessageOutput out = new MessageOutput
                (Node.socket.getOutputStream());
                Base64.Encoder encoder = Base64.getEncoder();
                String keyString = new String
                (encoder.encode(search.getID()));
                search.encode(out);
                Node.addToSList(keyString, search.getSearchString());
                Node.LOGGER.log(Level.INFO, "Sent: " + search.toString());
            }
            if (flag) { //SEND OUT RESPONSE
                Response r = findFile(search);
                r.encode(new MessageOutput(Node.socket.getOutputStream()));
                Node.LOGGER.log(Level.INFO, "Sent: " + r.toString());
            }
        } catch (IOException e) {
            Node.LOGGER.log(Level.WARNING, 
            "Interrupted Due to : " + e.getMessage());
        } catch (BadAttributeValueException e) {
            Node.LOGGER.log(Level.WARNING, 
            "Interrupted Due to : " + e.getMessage());
        } 
    }
    /**
     * Represents a response object containing search results.
     * @return the response object containing search results
     * @param s the search object to be used in generating the response
     */
    private Response findFile(Search s)
    throws BadAttributeValueException, UnknownHostException {
        //find file in local directory
        InetSocketAddress hostaddr = new
                InetSocketAddress(
                Inet4Address.getLocalHost().getHostAddress()
                , Node.socket.getLocalPort());
        if (s.getSearchString() == null) {
            return null;
        }
        if (s.getSearchString().isEmpty()) {
            Response r = new Response(s.getID(),
            s.getTTL(), s.getRoutingService(), hostaddr);
            r.setMatches(0);
            return r;
        }
        File currDir = new File(Node.searchDir);
        File[] files = currDir.listFiles();
        byte[] idbyte = new byte[4];
        Base64.Encoder encoder = Base64.getEncoder();
        String id = new String(encoder.encode(idbyte));
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            idbyte[i] = (byte) rand.nextInt(255);
        }
        for (File f : files) {
            if (!Node.dir.containsKey(f.getName())) {
                while (Node.dir.containsValue(id)) {
                    for (int i = 0; i < 4; i++) {
                        idbyte[i] = (byte) rand.nextInt(255);
                        id = new String(encoder.encode(idbyte));
                    }
                }
                Node.dir.put(f.getName(), id);
            }
        }
        Response r = new Response(s.getID(), 
        s.getTTL(), s.getRoutingService(), hostaddr);
        for (File f : files) {
            if (f.getName().contains(s.getSearchString())) {
                byte[] temp = Base64.
                getDecoder().decode(Node.dir.get(f.getName()));
                r.addResult(new Result(temp, f.length(), f.getName()));
                Node.LOGGER.log(Level.INFO, "Found: " + r.toString());
            }
        }
        r.setMatches(r.getResultList().size());
        return r;
    }
}
