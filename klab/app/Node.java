/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 2
 * Class: CSI4321
 *
 ************************************************/
package klab.app;
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
 * until termination and completely ascynchronously, prints responses to console and response to searches from other Node
 * Repeatedly until the user types "exit" at the command prompt
 *  Prompts for search string/exit
 *  Send the search to the Neighbor Node
 * 
 */
public class Node {
    InetSocketAddress address;
    int id;
    public static Map<String, String> searchMap = new HashMap<>();
    public static final Logger LOGGER = Logger.getLogger("beaver.log");
    public static final Socket socket = new Socket();
    public static final ExecutorService tS = Executors.newSingleThreadExecutor();
    public static String searchDir = "";

    public static final Map<String, String> dir = new HashMap<>();
    static {
        FileHandler f;
        ConsoleHandler c; 
        try {
            f = new FileHandler(System.getProperty("user.dir") + "\\node.log");
            c = new ConsoleHandler();
            f.setFormatter(new SimpleFormatter());
            LOGGER.setLevel(Level.ALL);
            f.setLevel(Level.ALL);
            c.setLevel(Level.WARNING);
            LOGGER.addHandler(f);
            LOGGER.addHandler(c);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File LOGGER not working", e);
        }   
    }
    public static void main(String[] args) throws IOException, BadAttributeValueException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Parameter(s): <Search Directory> <Neighbor Node> <Neighbor Port>");
        }
        Node.searchDir = args[0];
        InetSocketAddress address = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
        Node node = new Node(address);
        Node.LOGGER.info("Node connected!");
        String searchDir = args[0];
        //System.out.println(searchDir);
        if (searchDir == null) {
            LOGGER.log(Level.SEVERE, "Search directory is null");
            throw new IllegalArgumentException("Search directory is null");
        }
        //Thread tS = new Thread(new SearchManagement(LOGGER, node.getSocket()));

        System.out.print("> ");
        Thread tR = new Thread(new ResponseManagement());
        tR.start();
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {

                if (sc.hasNextLine()) {
                    String input = sc.nextLine();
                    if (input.equals("exit")) {
                        sc.close();
                        Node.closeSocket();
                        break;
                    }
                    byte[] id = new byte[15];
                    Random rand = new Random();
                    for (int i = 0; i < 15; i++) {
                        id[i] = (byte) rand.nextInt(255);
                    }
                    Search searchObj = new Search(id, 50, RoutingService.DEPTHFIRST, input);
                    Node.tS.submit(new SendManagement(searchObj, false));
                    System.out.print("> ");
                }
                //sc.close();
                //System.out.println("Search sent");
            } catch (Exception e) {
                Node.LOGGER.severe(
            "Search Management thread interrupted by "
                + e.getMessage());
                break;
            }
        }
        sc.close();
    }
    public Node (InetSocketAddress address) throws IOException {
        this.address = address;
        Node.startSocket(address);
        id = 0;
    }
    public Socket getSocket() {
        return socket;
    }
    public static synchronized void addToSList(String x, String y) {
        searchMap.put(x, y);
    }

    public static synchronized Map<String, String> getSList() {
        return searchMap;
    }

    public static synchronized void closeSocket() throws IOException {
        socket.close();
    }

    public static synchronized void startSocket(InetSocketAddress i) throws IOException {
        socket.connect(new InetSocketAddress(i.getAddress(),i.getPort()));
    }

}
