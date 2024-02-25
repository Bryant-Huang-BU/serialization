package klab.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.*;
import klab.app.*;
import klab.serialization.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/*
 * Implement a node that makes a tcp connection to another Node
 * until termination and completely ascynchronously, prints responses to console and response to searches from other Node
 * Repeatedly until the user types "exit" at the command prompt
 *  Prompts for search string/exit
 *  Send the search to the Neighbor Node
 * 
 */
public class Node {
    private static final String EXIT = "exit";
    private static final String PROMPT = "Enter search string or exit: ";
    InetSocketAddress address;
    Socket socket;
    int id;

    public static void main(String[] args) throws IOException, BadAttributeValueException{
        Logger logger = Logger.getLogger(Node.class.getName());
        logger.setLevel(Level.ALL);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        Formatter formatter = new SimpleFormatter();
        consoleHandler.setFormatter(formatter);
        logger.log(Level.INFO, "Node started");
        InetSocketAddress address = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
        Node node = new Node(address);
        PrintWriter out = new PrintWriter(node.getSocket().getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(node.getSocket().getInputStream()));
        Thread t = new Thread(new SearchManagement(logger, node.getSocket()));
        t.start();
        node.getSocket().close();
        
    }
    public Node (InetSocketAddress address) throws IOException {
        this.address = address;
        this.socket = new Socket(this.address.getAddress(), this.address.getPort());
        id = 0;
    }
    public Socket getSocket() {
        return socket;
    }

    public void incID() {
        this.id++;
    }

    public void sendSearch(String searchString) {
        try {
            MessageOutput out = new MessageOutput(socket.getOutputStream());
            Search searchObj = new Search(genID(searchString), 50, RoutingService.DEPTHFIRST, searchString);
            out.println(searchObj.encode());
            inputLine = in.readLine();
            logger.log(Level.INFO, "Received: " + inputLine);
        } catch (BadAttributeValueException e) {
            logger.log(Level.SEVERE, "BadAttributeValueException: " + e.getMessage());
        }
    }

    private List<Result> searchForFileDepthFirst(Logger logger, String searchString) {
        File currentDirectory = new File(".");
        File[] files = currentDirectory.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().equals("example.txt")) {
                logger.log(Level.INFO, "Found file: " + file.getAbsolutePath());
                files.add(new Result())
            }
            else if (file.isDirectory()) {
                logger.log(Level.INFO, "Searching directory: " + file.getAbsolutePath());
                searchForFileDepthFirst(logger, file.getAbsolutePath());
            }
        }

    }
}
