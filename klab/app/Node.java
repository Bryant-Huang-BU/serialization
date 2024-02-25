package klab.app;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
    

    public static void main(String[] args) throws IOException, BadAttributeValueException{
        if (args.length != 3) {
            throw new IllegalArgumentException("Parameter(s): <Search Directory> <Neighbor Node> <Neighbor Port>");
        }
        Logger logger = Logger.getLogger(Node.class.getName());
        logger.setLevel(Level.ALL);
        logger.log(Level.INFO, "Node started");
        InetSocketAddress address = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
        Node node = new Node(address);
        String searchDir = args[0];
        System.out.println(searchDir);
        if (searchDir == null) {
            logger.log(Level.SEVERE, "Search directory is null");
            throw new IllegalArgumentException("Search directory is null");
        }
        Thread tS = new Thread(new SearchManagement(logger, node.getSocket()));
        tS.start();
        Thread tR = new Thread(new ResponseManagement(logger, node.getSocket(), searchDir)); 
        //tR.start();
        
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

    public void sendSearch(String searchString, Socket socket, Logger logger) throws IOException {
        try {
            //write search byte array to socket
            MessageOutput out = new MessageOutput(socket.getOutputStream());
            Search searchObj = new Search(intToBytes(id, 15), 50, RoutingService.DEPTHFIRST, searchString);
            searchObj.encode(out);
            logger.log(Level.INFO, "Sent: " + searchObj.toString());
        } catch (BadAttributeValueException e) {
            logger.log(Level.SEVERE, "BadAttributeValueException: " + e.getMessage());
        }
    }

    public static byte[] intToBytes(int len, int id) {
        byte[] bytes = new byte[len];
        for (int i = len - 1; i >= 0; i--) {
            bytes[i] = (byte) (id & 0xff);
            id >>= 8; 
        }
        System.out.println("Bytes: " + bytes.toString());
        return bytes;
    }    
}
