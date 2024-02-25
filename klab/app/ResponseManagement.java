package klab.app;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import klab.serialization.*;

public class ResponseManagement implements Runnable {
    int id;
    Logger logger;
    Socket socket;
    public ResponseManagement(Logger log, Socket sock) {
        id = 0;
        this.logger = log;
        this.socket = sock;
    }
    public void set() {
        logger.info("Response Management thread running");
        while (true) {
            try {
                //read msg from socket
                
                MessageInput in = new MessageInput(socket.getInputStream());
                Message msg = Message.decode(in); //if not either, 
                //it should throw an exception anyways
                if (msg instanceof Response) {
                    logger.info("Received: " + msg.toString());
                    Response r = (Response) msg;
                    processResponse(r);
                }
                else if (msg instanceof Search) {
                    logger.info("Received: " + msg.toString());
                    Search s = (Search) msg;
                    Response r = findFile(s);
                    r.encode(new MessageOutput(socket.getOutputStream()));
                }

            } catch (Exception e) {
                logger.severe("Search Management thread interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }
    @Override
    public void run() {
        set();
    }

    private void processResponse(Response r) {
        //process response
        System.out.println("Search msg for " + " : "); 
        //TODO: get search string
        System.out.println("Download Host: " + 
        r.getResponseHost().getAddress() + ":" + 
        r.getResponseHost().getPort());
        for(int i = 0; i < r.getResultList().size(); i++) {
            System.out.println(r.getResultList().get(i).getFileName()
             + ": ID " + r.getResultList().get(i).getFileID().toString()
              + " (" + r.getResultList().get(i).getFileSize() + " bytes)");
        }
    }

    private Response findFile(Search s) throws BadAttributeValueException {
        //find file in local directory
        InetSocketAddress hostaddr = new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
        Response r = new Response(s.getID(), s.getTTL(), s.getRoutingService(), hostaddr);
        File currDir = new File(".");
        //List<String> fileNames = new ArrayList<>();
        File[] files = currDir.listFiles((dir, name) -> name.contains(s.getSearchString()));
        if (files != null) {
            for (File file : files) {
                r.addResult(new Result(Node.intToBytes(id, 4), file.length(), file.getName()));
                id++;
            }
        }
        return r;
    }
}