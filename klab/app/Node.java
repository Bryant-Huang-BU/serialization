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
import java.net.ServerSocket;
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
    static InetSocketAddress address;
    int id;
    public static Map<String, String> searchMap = new HashMap<>();
    //public static Socket socket;
    public static Socket downSocket = new Socket();
    public static final Logger LOGGER = Logger.getLogger("node.log");
    public static final ExecutorService tS = 
    Executors.newSingleThreadExecutor();
    public static String searchDir = "";
    //ExecutorService threadPool = Executors.newFixedThreadPool(4);
    public static final Map<String, String> dir = new HashMap<>();
    //public static ServerSocket serverSockets;
    public static ServerSocket serverSocket;
    public static ServerSocket downServer;
    public static List<Socket> connectionsList;
    public static int downloadPort;
    public static Object myAddr;
    public static ExecutorService eS = Executors.newCachedThreadPool();
    public static ExecutorService downloadClients = 
    Executors.newCachedThreadPool();

    int connectCount = 0;
    static {
        FileHandler f;
        ConsoleHandler c; 
        try {
            f = new FileHandler(
     System.getProperty("user.dir") + "\\serialization\\klab\\app\\node.log");
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
            "Parameter(s): <local Node port" +
            " <local document directory> " +
            "<local download port>");
            return;
        }
        if (searchDir == null) {
            //LOGGER.log(Level.SEVERE, "Search directory is null");
            System.out.println("Search directory is null");
            return;
        }
        Node.searchDir = args[1];
        File dir = new File(searchDir);
        if (!dir.exists()) {
            Node.LOGGER.log(Level.SEVERE, 
            "Search directory does not exist");
            System.out.println(
            "Search directory does not exist");
            return;
        }
        if (args[0] == null) {
            Node.LOGGER.log(Level.SEVERE, "Invalid local Node port");
            //System.out.println("");
            return;
        }
        if (Integer.parseInt(args[0]) < 0 || Integer.parseInt(args[0]) > 65535) {
            LOGGER.log(Level.SEVERE, "Invalid local port");
            //System.out.println("Invalid Local Port");
            return;
        }
        if (args[2] == null) {
            Node.LOGGER.log(Level.SEVERE, "Invalid Download port");
            //System.out.println("Invalid Download Port");
            return;
        }
        if (Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > 65535) {
            LOGGER.log(Level.SEVERE, "Invalid local port");
            //System.out.println("Invalid Local Port");
            return;
        }
        Node.LOGGER.info("Node connected!");
        //System.out.println(searchDir);
        System.out.print("> ");
        Node.downServer = new ServerSocket(Integer.parseInt(args[2]));
        //Node.socket = new Socket("localhost", Integer.parseInt(args[0]));
        Node.serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        Node.downloadPort= Integer.parseInt(args[2]);
        Thread tD = new Thread(new DownloadService());
        tD.start();
        Thread tC = new Thread(new acceptConnections());
        tC.start();
        Node.connectionsList = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        String input = "";
        while (true) {
            try {
                if (sc.hasNextLine()) {
                    input = sc.nextLine();
                    String[] command = input.split(" ");                    
                    if (command[0].equals("exit")) {
                        //stop the thread;
                        //sc.close();
                        //System.out.println("hello");
                        break;
                    }
                    //connect
                    else if (command[0].equals("connect")) {
                        if (command.length != 3) {
                            System.out.println("Bad connect command: "
                            + "Expect connect <node id> <node port>");
                            Node.LOGGER.info("Bad connect command: "+
                            "Expect connect <node id> <node port>");
                            System.out.print("> ");
                            continue;
                        }
                        else if (Integer.parseInt(command[2]) < 0
                        || Integer.parseInt(command[2]) > 65535) {
                            Node.LOGGER.info
                            ("Bad connect command: invalid port");
                        }
                        else {
                            Node.LOGGER.info("Connecting to node: " + command[1] +
                                    " on port: " + command[2]);
                            if 
                            (Node.addConnection(new Socket
                            (command[1], Integer.parseInt(command[2]))) == null) {
                                Node.LOGGER.log(Level.WARNING,
                                "Connection already exists");
                            }
                            System.out.print("> ");
                        }
                    }
                    //download command
                    else if (command[0].equals("download")) {
                        if (command.length != 5) {
                            System.out.println(
                            "Bad download command: Expect download <download"+
                            " node> <download port> <file ID> <file name>");
                            Node.LOGGER.info
                            ("Bad download command: Expect download"
                            + " <download node>"+
                            " <download port> <file ID> <file name>");
                            System.out.print("> ");
                            continue;
                        }
                        else {
                            Node.LOGGER.info("Downloading file: " + command[4] +
                            " from node: " + command[1] + " on port: " + command[2]);
                            //send download request
                            //string to byte
                            byte[] id = new byte[4];
                            //send download request
                            Thread DownloadSender = new Thread(new DownloadSender(command[1], 
                            Integer.parseInt(command[2]), id,  command[4]));
                            Node.downloadClients.submit(DownloadSender);
                            //Node.LOGGER.info("Downloaded file: " + command[4]);
                            System.out.print("> ");
                        }
                    }
                    //search
                    else {
                        byte[] id = new byte[15];
                        Random rand = new Random();
                        for (int i = 0; i < 15; i++) {
                            id[i] = (byte) rand.nextInt(255);
                        }
                        Search searchObj = new Search(id, 10,
                        RoutingService.DEPTHFIRST, input);
                        Node.tS.submit(new SendManagement(searchObj, null));
                        System.out.print("> ");
                    }
                }
            } catch (IOException e) {
                Node.LOGGER.log(Level.WARNING,  
                "Unable to communicate: "+  e.getMessage());
            } catch (BadAttributeValueException e) {
                Node.LOGGER.log(Level.WARNING,
                "Invalid Message: "
                + e.getMessage());
                System.out.print("> ");
            }
        }
        try {
            Node.closeSocket();
        } catch (IOException e) {
            Node.LOGGER.log(Level.WARNING, "Unable to close socket: "
            + e.getMessage());
        }
        //System.out.println(Node.downServer.isClosed());
        downloadClients.shutdown();
        tS.shutdown();
        tD.interrupt();
        tC.interrupt();
        eS.shutdown();
        sc.close();
        return;
    }
    /**
     * Represents a node in the network.
     * Each node is identified by its address and has an associated ID.
    /
    public Node (InetSocketAddress address) throws IOException {
        this.address = address;
        serverSocket = new ServerSocket(address.getPort());
        //Node.startSocket(address);
        //check if address is valid
        id = 0;
    }
    */
    /**
     * Returns the socket associated with this Node.
     * 
     * @return the socket associated with this Node
     */
    public ServerSocket getSocket() {
        return serverSocket;
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
    public static void closeSocket() throws IOException {
        if (Node.serverSocket != null) {
            Node.serverSocket.close();
        }
        if (Node.downServer != null) {
            Node.downServer.close();
        }
        List<Socket> sockets;
        synchronized (Node.connectionsList) {
            sockets = new ArrayList<>(Node.connectionsList);
        }
        for (Socket sock : sockets) {
            sock.close();
        }
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

    }
    /**
     * Returns a list of connections.
     *
     * @return a list of connections
     */
    public static List<Socket> getConnectionsList() {
        synchronized (connectionsList) {
            return new ArrayList<>(connectionsList);
        }
    }

    /**
     * Represents a network socket connection.
     * 
     * @param s the socket connection
     * @return the socket connection
     */
    public static synchronized Socket addConnection(Socket s) {
        List<Socket> sockets;
        synchronized (Node.connectionsList) {
            sockets = new ArrayList<>(Node.connectionsList);
        }
            for (Socket socket : sockets) {
                if (socket.getRemoteSocketAddress().equals
                (s.getRemoteSocketAddress()) &&
                        socket.getPort() == s.getPort()) {
                    return null;
                }
            }
            connectionsList.add(s);
            Node.eSsubmit(s);
            return s;
    }
    /**
     * Removes the specified socket from the connections list.
     * 
     * @param socket the socket to be removed
     */
    public static synchronized void removeConnection(Socket socket) {
        if (connectionsList.contains(socket)) {
            connectionsList.remove(socket);
        }
    }

    /**
     * Submits a socket to the executor service for processing.
     *
     * @param s the socket to be submitted
     */
    public static synchronized void eSsubmit(Socket s) {
        eS.submit(new UltimateManagement(s));
    }


}
