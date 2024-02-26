package klab.app;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    Socket socket;
    int id;
    private static List<Search> sharedSearchList = new ArrayList<>();
    public static final Logger LOGGER = Logger.getLogger("beaver.log");
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
    public static void main(String[] args) throws IOException, BadAttributeValueException{
        if (args.length != 3) {
            throw new IllegalArgumentException("Parameter(s): <Search Directory> <Neighbor Node> <Neighbor Port>");
        }
        InetSocketAddress address = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
        Node node = new Node(address);
        String searchDir = args[0];
        //System.out.println(searchDir);
        if (searchDir == null) {
            LOGGER.log(Level.SEVERE, "Search directory is null");
            throw new IllegalArgumentException("Search directory is null");
        }
        //Thread tS = new Thread(new SearchManagement(LOGGER, node.getSocket()));
        ExecutorService tS = Executors.newSingleThreadExecutor();
        tS.execute(new SearchManagement(node.getSocket()));
        ExecutorService tR = Executors.newSingleThreadExecutor();
        tR.execute(new ResponseManagement(node.getSocket(), searchDir));
        //node.getSocket().close();
    }
    public Node (InetSocketAddress address) throws IOException {
        this.address = address;
        this.socket = new Socket(this.address.getAddress(), this.address.getPort());
        id = 0;
    }
    public Socket getSocket() {
        return socket;
    }
    public static byte[] intToBytes(int x, int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (x >> (i * 8));
        }
        return bytes;
    }
    public static synchronized void addToSList(Search s) {
        sharedSearchList.add(s);
    }
    public static synchronized void removeFromSList(Search s) {
        sharedSearchList.remove(s);
    }

    public static synchronized List<Search> getSList() {
        return sharedSearchList;
    }
}
