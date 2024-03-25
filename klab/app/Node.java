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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import klab.serialization.*;
/*
 * Implement a node that makes a tcp connection to another Node
 * until termination and completely ascynchronously, prints 
 * responses to console and response to searches from other Node
 * Repeatedly until the user types "exit" at the command prompt
 * Prompts for search string/exit
 * Send the search to the Neighbor Node
 * 
 */
public class Node {
    InetSocketAddress address;
    int id;
    public static Map<String, String> searchMap = new HashMap<>();
    public static final Logger LOGGER = Logger.getLogger("node.log");
    public static final Socket socket = new Socket();
    public static final ExecutorService tS = 
    Executors.newSingleThreadExecutor();
    public static String searchDir = "";

    public static final Map<String, String> dir = new HashMap<>();
    
    static {
        FileHandler f;
        ConsoleHandler c; 
        try {
            f = new FileHandler(
            System.getProperty("user.dir") + "\\node.log");
            c = new ConsoleHandler();
            f.setFormatter(new SimpleFormatter());
            LOGGER.setLevel(Level.ALL);
            f.setLevel(Level.ALL);
            c.setLevel(Level.WARNING);
            LOGGER.addHandler(f);
            LOGGER.addHandler(c);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File LOGGER not working:"
            , e.getMessage());
        }   
    }
    /**
     * The main method is the entry point of the program.
     * It takes command line arguments and initializes a Node object.
     * It then prompts the user for input and sends search requests 
     * to other nodes.
     * The program can be terminated by entering "exit".
     *
     * @param args The command line arguments: <Search Directory> 
     * <Neighbor Node> <Neighbor Port>
     * @throws IOException                If an I/O error occurs.
     * @throws BadAttributeValueException If the attribute value is invalid.
     */
    public static void main(String[] args) 
    throws IOException, BadAttributeValueException {
        if (args.length != 3) {
            System.out.println(
            "Illegal Arguments! " + 
            "Parameter(s): <Search Directory> "
            + "<Neighbor Node> <Neighbor Port>"
            );
            return;
        }
        Node.searchDir = args[0];
        File dir = new File(searchDir);
        if (!dir.exists()) {
            //LOGGER.log(Level.SEVERE, 
            //+ "Search directory does not exist");
            System.out.println(
            "Search directory does not exist");
            return;
        }
        if (args[1] == null) {
            //LOGGER.log(Level.SEVERE, "Invalid IP address");
            System.out.println("Invalid IP address");
            return;
        }
        if (Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > 65535) {
            //LOGGER.log(Level.SEVERE, "Invalid port number");
            System.out.println("Invalid port number");
            return;
        }
        if (searchDir == null) {
            //LOGGER.log(Level.SEVERE, "Search directory is null");
            System.out.println("Search directory is null");
            return;
        }
        
        InetSocketAddress address = new InetSocketAddress(
        args[1], Integer.parseInt(args[2]));
        try {
            Node node = new Node(address); //init node
        } catch (IOException e) {
            //LOGGER.log(Level.SEVERE, "Unable to connect to node");
            System.out.println("Unable to connect to node");
            return;
        }
        Node.LOGGER.info("Node connected!");
        //System.out.println(searchDir);
        
        System.out.print("> ");
        Thread tR = new Thread(new ResponseManagement());
        tR.start();
        Scanner sc = new Scanner(System.in);
        String input = "";
        while (true) {
            try {
                if (sc.hasNextLine()) {
                    input = sc.nextLine();
                    if (input.equals("exit")) {
                        //stop the thread;
                        sc.close();
                        Node.closeSocket();
                        break;
                    }
                    byte[] id = new byte[15];
                    Random rand = new Random();
                    for (int i = 0; i < 15; i++) {
                        id[i] = (byte) rand.nextInt(255);
                    }
                    Search searchObj = new Search(id, 50,
                    RoutingService.DEPTHFIRST, input);
                    Node.tS.submit(new SendManagement(searchObj, false));
                    System.out.print("> ");
                }
            } catch (IOException e) {
                Node.LOGGER.log(Level.WARNING,  
                "Unable to communicate:"+  e.getMessage());
                break;
            } catch (BadAttributeValueException e) {
                Node.LOGGER.log(Level.WARNING,
                "Invalid Message: "
                + e.getMessage());
            }
        }
        tR.interrupt();
        tS.shutdown();
        sc.close();
        return;
    }
    /**
     * Represents a node in the network.
     * Each node is identified by its address and has an associated ID.
     */
    public Node (InetSocketAddress address) throws IOException {
        this.address = address;
        Node.startSocket(address);
        //check if address is valid
        id = 0;
    }
    /**
     * Returns the socket associated with this Node.
     * 
     * @return the socket associated with this Node
     */
    public Socket getSocket() {
        return socket;
    }
    /**
     * Adds a key-value pair to the searchMap.
     * 
     * @param x the key to be added
     * @param y the value to be added
     */
    public static synchronized void addToSList(String x, String y) {
        searchMap.put(x, y);
    }

    /**
     * Returns the synchronized map of strings.
     *
     * @return the synchronized map of strings
     */
    public static synchronized Map<String, String> getSList() {
        return searchMap;
    }

    /**
     * Closes the socket connection.
     * 
     * @throws IOException if an I/O error occurs while closing the socket
     */
    public static synchronized void closeSocket() throws IOException {
        socket.close();
    }

    /**
     * Starts a socket connection to the specified InetSocketAddress.
     * This method is synchronized to ensure thread safety.
     *
     * @param i the InetSocketAddress to connect to
     * @throws IOException if an I/O error occurs while connecting
     */
    public static synchronized void startSocket(InetSocketAddress i) 
    throws IOException {
        try {
            socket.connect(new InetSocketAddress(i.getAddress(), i.getPort()));
        } catch (IOException e) {
            throw new IOException("Socket connection failed");
        }
    }

}
