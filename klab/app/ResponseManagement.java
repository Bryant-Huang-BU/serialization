/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 2
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

import klab.serialization.*;

/**
 * The ResponseManagement class implements the Runnable interface 
 * and is responsible for managing responses received by the Node.
 * It runs in a separate thread and continuously listens for 
 * incoming messages on the Node's socket.
 * When a response message is received, it processes the response 
 * and performs the necessary actions.
 */
public class ResponseManagement implements Runnable {

    public Socket sock;
    public ResponseManagement (Socket sock) {
        this.sock = sock;
    }
    /**
     * Executes the main logic of the Response Management thread.
     * This method continuously listens for incoming messages on
     * the socket and processes them accordingly.
     * If the message is a Response, it is processed by calling 
     * the processResponse method.
     * If the message is a Search, it is submitted to the \
     * SendManagement thread for further processing.
     * The method runs until the socket is closed.
     */
    @Override
    public void run() {
        Node.LOGGER.info("Response Management thread running");
        MessageInput in = null;
        boolean flag = false;

            try {
            //System.out.println("Hello?");
                in = new MessageInput(sock.getInputStream());
                while (!sock.isClosed()) {
                    if (in.isAvail()) {
                        in = new MessageInput(sock.getInputStream());
                        flag = true;
                        Message msg = Message.decode(in); //if not either,
                        if (msg.getClass() == Response.class) { //IF RESPONSE
                            Response r = (Response) msg;
                            Base64.Encoder encoder = Base64.getEncoder();
                            String id = new String(encoder.encode(r.getID()));
                            if (Node.getSList().containsKey(id)) {
                                processResponse(r);
                                Node.LOGGER.log(Level.INFO, "Processed Response from " 
                                + r.getResponseHost().getAddress() + ":" + 
                                r.getResponseHost().getPort());
                            } else { //forward the response
                                /*List<Socket> socketsr;
                                synchronized (Node.connectionsList) {
                                    socketsr = new ArrayList<>(Node.connectionsList);
                                }
                                for (Socket sockr : socketsr) {*/
                                if (sock != null) {
                                    r.setTTL(r.getTTL() - 1);
                                    if (r.getTTL() > 0){
                                        Node.tS.submit(new SendManagement(
                                                r, sock));
                                        Node.LOGGER.info(
                                    "Received Response: " + r.toString());
                                    }
                                    else {
                                        Node.LOGGER.info("TTL EXPIRED " + r.toString());
                                    }
                                }
                            }

                    } else if (msg.getClass() == Search.class) {
                            Search s = (Search) msg;
                            s.setTTL(s.getTTL() - 1);
                            if (s.getTTL() > 0) {
                                Node.tS.submit(new SendManagement(
                                s, sock));
                                Node.LOGGER.info(
                                        "Received Search: " + s.toString());
                            } else {
                                Node.LOGGER.info(
                                        "TTL EXPIRED " + s.toString());
                            }
                            }
                        }
                        else {
                            flag = false;
                        }
                    }
        } catch (Exception e) {
                if (sock.isClosed()) {
                    Node.LOGGER.info("Socket Closed!");
                    Node.removeConnection(sock);
                } else {
                    if (flag) {
                        Node.LOGGER.warning(
                    "Response Management thread interrupted "
                        + e.getMessage());
                    }
                }
    }
}
    /**
     * Processes the given response.
     * 
     * @param r the response to be processed
     */
    private void processResponse(Response r) {
        //process response
        byte[] id = r.getID();
        Base64.Encoder encoder = Base64.getEncoder();
        String keyString = new String(encoder.encode(id));
        String searchStr = Node.searchMap.get(keyString);
        //System.out.println("Help");
        if (searchStr != null) {
            Node.LOGGER.info(
            "Received Response: " + r.toString());
            System.out.println("Search response for " + searchStr + ": ");
            System.out.println("Download Host: " +
                    r.getResponseHost().getAddress() + ":" +
                    r.getResponseHost().getPort());
            for (Result rt : r.getResultList()) {
                StringBuilder sb = new StringBuilder();
                for (byte b : rt.getFileID()) {
                    sb.append(String.format("%02X", b));
                }
                System.out.print("      " + rt.getFileName() +
                ": ID " + sb.toString() + " (" + rt.getFileSize()
                + " bytes)\n");
            }
        }
    }


}