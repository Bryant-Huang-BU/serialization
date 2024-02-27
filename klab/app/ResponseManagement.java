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

public class ResponseManagement implements Runnable {
    public ResponseManagement() {
    }
    @Override
    public void run() {
        Node.LOGGER.info("Response Management thread running");
        while (!Node.socket.isClosed()) {
            try {
                MessageInput in = new MessageInput(Node.socket.getInputStream());
                Message msg = Message.decode(in); //if not either,
                if (msg instanceof Response) {
                    Response r = (Response) msg;
                    processResponse(r);
                } else if (msg instanceof Search) {
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

    private void processResponse(Response r) {
        //process response
        byte[] id = r.getID();
        Base64.Encoder encoder = Base64.getEncoder();
        String keyString = new String(encoder.encode(id));
        String searchStr = Node.searchMap.get(keyString);
        if (searchStr != null) {
            Node.LOGGER.info(
            "Received Response: " + r.toString());
            System.out.println("Search msg for " + searchStr + ": ");
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
                + ") bytes\n");
            }
        }
    }


}