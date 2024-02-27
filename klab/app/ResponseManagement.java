/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 2
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.util.*;

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
        while (!Node.socket.isClosed()) {
            try {
                MessageInput in = new MessageInput(Node.socket.getInputStream());
                Message msg = Message.decode(in); //if not either,
                if (msg.getClass() == Response.class) {
                    Response r = (Response) msg;
                    processResponse(r);
                } else if (msg.getClass() == Search.class) {
                    Search s = (Search) msg;
                    //Response r = findFile(s);
                    Node.tS.submit(new SendManagement(
                    s, true));
                    Node.LOGGER.info(
                    "Received Search: " + s.toString());
                    //r.encode(new MessageOutput(Node.socket.getOutputStream()));
                }
            } catch (Exception e) {
                if (Node.socket.isClosed()) {
                    Node.LOGGER.info("Socket Closed!");
                    break;
                }
                else {
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