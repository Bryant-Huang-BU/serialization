package klab.app;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import klab.serialization.*;

public class ResponseManagement implements Runnable {
    long id;
    Socket socket;
    String searchDir;
    public ResponseManagement(Socket sock, String searchDir) {
        id = 0;
        this.socket = sock;
        this.searchDir = searchDir;
    }
    @Override
    public void run() {
        Node.LOGGER.info("Response Management thread running");
        while (true) {
            try {
                File currDir = new File(searchDir);
                //List<String> fileNames = new ArrayList<>();
                List<Result> p = new ArrayList<>();
                File[] files = currDir.listFiles();
                MessageInput in = new MessageInput(socket.getInputStream());
                Message msg = Message.decode(in); //if not either,
                if (msg instanceof Response) {
                    Node.LOGGER.info("Received: " + msg.toString());
                    Response r = (Response) msg;
                    processResponse(r);
                }
                else if (msg instanceof Search) {
                    Node.LOGGER.info("Received: " + msg.toString());
                    Search s = (Search) msg;
                    Response r = findFile(s);
                    r.encode(new MessageOutput(socket.getOutputStream()));
                }
            } catch (Exception e) {
                Node.LOGGER.severe("Search Management thread interrupted");
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
        }
    }

    private void processResponse(Response r) {
        //process response
        String searchStr = "";
        //System.out.println(Node.sharedSearchList.size());
        for (Search s : Node.getSList()) {
            //System.out.println(s.toString());
            //check to see if the search ID matches the response ID
            boolean flag = true;
            for (int i = 0; i < s.getID().length; i++) {
                if (s.getID()[i] != r.getID()[i]) {
                    //System.out.println("Search ID does not match response ID");
                    flag = false;
                }
            }
            //System.out.println(s.getSearchString());
            if (flag) {
                //System.out.println(s.getSearchString());
                searchStr = s.getSearchString();
                Node.removeFromSList(s);
                break;
            }
        }
            if (!searchStr.isEmpty()) {
                System.out.println("Search msg for " + searchStr + ": ");
                System.out.println("Download Host: " +
                        r.getResponseHost().getAddress() + ":" +
                        r.getResponseHost().getPort());
                for (Result rt : r.getResultList()) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : rt.getFileID()) {
                        sb.append(String.format("%02X", b));
                    }
                    System.out.print(rt.getFileName() + 
                    ": ID " + sb.toString() + " (" + rt.getFileSize() + ") bytes");
                }
            }
        }
    
    private Response findFile(Search s) throws BadAttributeValueException {
        //find file in local directory
        InetSocketAddress hostaddr = new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
        Response r = new Response(s.getID(), s.getTTL(), s.getRoutingService(), hostaddr);
        return r;
    }
}