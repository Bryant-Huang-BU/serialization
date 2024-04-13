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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;


import klab.serialization.*;

/**
 * The SendManagement class implements the Runnable 
 * interface and represents a thread
 * responsible for sending message requests or responses.
 */
public class SendManagement implements Runnable{
    Message message;
    Socket socket;
    /**
     * Constructs a new SendManagement object with
     * the specified message and flag.
     *
     * @param message the message object to be used
     * @param socket   the flag indicating whether to send the management or not
     */
    public SendManagement(Message message, Socket socket) {
        this.message = message;
        this.socket = socket;
        if (socket != null) {
            Node.LOGGER.log(Level.INFO, "Send Management thread created for " +
            socket.getLocalAddress() + ":" + socket.getPort());
        }
        else {
            Node.LOGGER.log(Level.INFO, "Send Management thread created for " +
            "all connections");
        }
    }
    /**
     * Executes the logic of the SendManagement thread.
     * If the flag is false, it sends out a message request
     * by writing the message byte array to Node.socket.
     * If the flag is true, it sends out a response by 
     * finding the file and encoding it to the output stream.
     * Any exceptions that occur during execution are logged as warnings.
     */
    @Override
    public void run() {
        Node.LOGGER.info("Send Management thread running");
        //System.out.println(message.getClass() == Search.class);
        if (message.getClass() == Search.class) {
            Search search = (Search) this.message;
            if (socket != null) { //SEND OUT RESPONSE + FLOOD
                if (socket.isClosed()) {
                    Node.LOGGER.log(Level.INFO, "Closed socket");
                    return;
                }
                //write message byte array to Node.socket
                try {
                    Response r = findFile(search);
                    assert r != null;
                    if (!r.getResultList().isEmpty()) {
                        MessageOutput out = new MessageOutput(socket.getOutputStream());
                        r.encode(out);
                        Node.LOGGER.log(Level.INFO,
                        "Sent: " + r.toString() + " to " +
                        socket.getLocalAddress() + ":" + socket.getPort());
                    }
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        Node.LOGGER.log(Level.WARNING,
                    "Interrupted Due to: " + e.getMessage());
                    }
                    return;
                }
                List<Socket> sockets;
                synchronized (Node.connectionsList) {
                    sockets = new ArrayList<>(Node.connectionsList);
                }
                if (sockets.isEmpty()) {
                    Node.LOGGER.log(Level.INFO, "No connections to send to");
                    return;
                }
                for (Socket sock : sockets) {
                    try {
                        if (sock != socket) {
                        MessageOutput out;
                        out = new MessageOutput
                        (sock.getOutputStream());
                        search.encode(out);
                        Node.LOGGER.log(Level.INFO,
                    "Forwarded: " + search.toString() + " to " +
                        sock.getLocalAddress() + ":" + sock.getPort());
                        }
                    } catch (IOException e) {
                        Node.LOGGER.log(Level.WARNING,
                        "Interrupted Due to: " + e.getMessage());
                    }
                }
            }
            else {
                List<Socket> sockets;
                synchronized (Node.connectionsList) {
                    sockets = new ArrayList<>(Node.connectionsList);
                }
                if (sockets.isEmpty()) {
                    Node.LOGGER.log(Level.INFO, "No connections to send to");
                    return;
                }
                for (Socket sock : sockets) {
                    try {
                        MessageOutput out;
                        out = new MessageOutput
                        (sock.getOutputStream());
                        search.encode(out);
                        Base64.Encoder encoder = Base64.getEncoder();
                        String id = new String(encoder.encode(search.getID()));
                        Node.addToSList(id, search.getSearchString());
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            Node.LOGGER.log(Level.WARNING,
                        "Interrupted Due to: " + e.getMessage());
                        }
                        
                    }
                    Node.LOGGER.log(Level.INFO,
                    "Sent: " + search.toString() + " to " +
                    sock.getLocalAddress() + ":" + sock.getPort());
                }   
            }
        }
        else if (message.getClass() == Response.class) { 
            //forward the response
            Response r = (Response) message;
            List<Socket> sockets;
            synchronized (Node.connectionsList) {
                sockets = new ArrayList<>(Node.connectionsList);
            }
            if (sockets.isEmpty()) {
                Node.LOGGER.log(Level.INFO, "No connections to send to");
                return;
            }
            for (Socket sock : sockets) {
                MessageOutput out;
                try {
                    if (sock != socket) {
                        out = new MessageOutput
                                (sock.getOutputStream());
                        r.encode(out);
                    }
                }
                catch (IOException e) {
                    Node.LOGGER.log(Level.WARNING, 
                    "Interrupted Due to: " + e.getMessage());
                }
                Node.LOGGER.log(Level.INFO,
                "Forwarded: " + r.toString() + " to " +
                sock.getLocalAddress() + ":" + sock.getPort());
            }

        }    
    }
    /**
     * Represents a response object containing message results.
     * @return the response object containing message results
     * @param s the message object to be used in generating the response
     */
    private Response findFile(Search s)
    throws BadAttributeValueException, UnknownHostException {
        //find file in local directory
        InetSocketAddress hostaddr = new
                InetSocketAddress(
                Inet4Address.getLocalHost().getHostAddress()
                , Node.downloadPort);
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
     10, s.getRoutingService(), hostaddr);
        for (File f : files) {
            if (f.getName().contains(s.getSearchString())) {
                byte[] temp = Base64.
                getDecoder().decode(Node.dir.get(f.getName()));
                r.addResult(new Result(temp, f.length(), f.getName()));
            }
        }
        Node.LOGGER.log(Level.INFO, "Found: " + r.toString());
        r.setMatches(r.getResultList().size());
        return r;
    }
}
