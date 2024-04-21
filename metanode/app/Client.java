package metanode.app;
import metanode.serialization.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
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
    static {
        FileHandler f;
        ConsoleHandler c;
        try {
            f = new FileHandler(
                    System.getProperty("user.dir") +
                            "\\node.log");
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
    public static void main(String[] args) {
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                String input = "";
                if (sc.hasNextLine()) {
                    input = sc.nextLine();
                    String[] command = input.split(" ");
                    if (command.length == 0) {
                        continue;
                    }
                    if (command[0].equals("exit")) {
                        LOGGER.log(Level.INFO, "Node Exited");
                        break;
                    }
                else if (command[0].equals("RN")) {
                    if (command.length > 1) {
                        LOGGER.log(Level.WARNING,
                        "RN command expects no argument");
                        continue;
                    }

                }
                else if (command[0].equals("RM")) {
                    if (command.length > 1) {
                        LOGGER.log(Level.WARNING,
                    "RM command expects no argument");
                        continue;
                    }
                }
                else if (command[0].equals("NA")) {
                    if (command.length < 2) {
                        LOGGER.log(Level.WARNING,
                    "NA commands expects at least one argument: NA");
                    }
                }
                }
            } catch (IOException e) {

            }
        return;
    }
}
