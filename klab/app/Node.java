package klab.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import klab.serialization.*;
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

    public static void main(String[] args) throws IOException, BadAttributeValueException{
        if (args.length != 3) {
            System.err.println("Usage: java Node <search string> <host> <port>");
            System.exit(1);
        }
        if (args[0] == null || args[1] == null || args[2] == null) {
            System.err.println("Usage: java Node <search string> <host> <port>");
            System.exit(1);
        }
        if (args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty()) {
            System.err.println("Usage: java Node <search string> <host> <port>");
            System.exit(1);
        }
        if ((args[1].matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"))) {
            System.err.println("Not a valid ip number!");
            System.exit(1);
        }
        if (Integer.parseInt(args[2]) > 65535 || Integer.parseInt(args[2]) < 0) {
            System.err.println("Not a valid port number!");
            System.exit(1);
        }

        Socket socket = new Socket(args[1], Integer.parseInt(args[2]));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Search search = new Search(new byte[4], 50, RoutingService.DEPTHFIRST, args[0] == null ? "" : args[0]);
        try (Scanner sc = new Scanner(System.in)) {
            String input;

            while (true) {
                System.out.print("Enter search string or 'exit' to close connection: ");
                input = sc.nextLine();
                if (input.equalsIgnoreCase(EXIT)) {
                    out.println(input);
                    break;
                } else {
                    out.println(input);
                }
            }
        }
        socket.close();
    }
    public Node (InetSocketAddress address) {
        this.address = address;
    }

    public byte[] genID(String args) {
        //gen id
        byte[] id = new byte[4];
        for (int i = 0; i < 4; i++) {
            id[i] = (byte) ((Math.random() * 255) + args.hashCode());
        }
        return id;
    }
}
