package klab.app;

import klab.serialization.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Level;

public class ResponseThread implements Runnable {

    Message msg;
    Socket sock;
    ResponseThread (Message msg, Socket sock) {
        this.msg = msg;
        this.sock = sock;
    }

    @Override
    public void run() {
        boolean flag = true;
        try {
            if (msg.getClass() == Response.class) { //IF RESPONSE
                Response r = (Response) msg;
                Base64.Encoder encoder = Base64.getEncoder();
                String id = new String(encoder.encode(r.getID()));
                if (Node.getSList().containsKey(id)) {
                    processResponse(r);
                    Node.LOGGER.log(
                    Level.INFO, "Processed Response from "
                    + r.getResponseHost().getAddress() + ":" +
                    r.getResponseHost().getPort());
                } else { //forward the response
                    if (sock != null) {
                        r.setTTL(r.getTTL() - 1);
                        if (r.getTTL() > 0) {
                            Node.tS.submit(new SendManagement(
                            r, sock));
                            Node.LOGGER.info(
                        "Received Response: " + r.toString());
                        } else {
                            Node.LOGGER.info
                            ("TTL EXPIRED " + r.toString());
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
            } else {
                flag = false;
            }
        } catch (Exception e) {
            if (flag) {
                Node.LOGGER.warning(
            "Response Management thread interrupted "
                + e.getMessage());
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
