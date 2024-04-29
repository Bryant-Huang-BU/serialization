/************************************************
 * Author: Bryant Huang
 * Assignment: Program 6
 * Class: CSI4321
 ************************************************/
package metanode.app;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.*;
import metanode.serialization.*;
/**
 * The Server class represents the server application for handling 
 * communication with nodes and metanodes.
 * It provides methods for starting the server, receiving and processing
 * messages, and maintaining a list of nodes and metanodes.
 */
public class Server {
    public static final
    Logger LOGGER = Logger.getLogger("metanodelog.log");
    public static DatagramSocket udpsock;
    public static List<Map.Entry
    <InetSocketAddress, Integer>> map = new ArrayList<>();
    public static int nodesize;
    public static int metasize;
    static {
        FileHandler f;
        try {
            f = new FileHandler(
                    System.getProperty("user.dir") +
                            "\\meta.log");
            f.setFormatter(new SimpleFormatter());
            LOGGER.setLevel(Level.ALL);
            f.setLevel(Level.ALL);
            LOGGER.addHandler(f);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "File LOGGER not working:"
                    , e.getMessage());
        }
    }
    /**
     * The main method is the entry point of the application.
     * It starts the server and listens for incoming messages.
     * 
     * @param args The command line arguments. 
     * Expects a single argument representing the port number.
     * @throws IOException If there is an error in the I/O operations.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) { // 1
            LOGGER.log(Level.SEVERE,
                    "Startup problem: Incorrect number of arguments");
            return;
        }
        if (Integer.parseInt(args[0]) < 0 ||
                Integer.parseInt(args[0]) > 65535) {
            LOGGER.log(Level.SEVERE,
                    "Startup problem: Invalid Port Number");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            udpsock = new DatagramSocket(port);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
        "Startup problem: Unable to bind to port");
            return;
        }
        metasize = 0;
        nodesize = 0;
        try {
            while (true) {
                byte[] response = new byte[1534];
                DatagramPacket responsePacket =
                        new DatagramPacket(
                                response, response.length);
                try {
                    udpsock.receive(responsePacket);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Communication Problem: " //2
                            + e.getMessage());
                    Message msg = new Message(MessageType.AnswerRequest,
                            ErrorType.System, 0);
                    byte[] buffer = msg.encode();
                    DatagramPacket packet =
                            new DatagramPacket(buffer,
                                    buffer.length, responsePacket.getAddress(),
                                    responsePacket.getPort());
                    LOGGER.log(Level.INFO,
                            "Sending: " + msg.toString());
                    udpsock.send(packet);
                    continue;
                }
                Message message;
                try {
                    byte[] trimmed = new byte
                            [responsePacket.getLength()];
                    System.arraycopy(response, 0, trimmed,
                            0, trimmed.length);
                    message = new Message(trimmed);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Communication Problem: "
                    + e.getMessage()); //3
                    Message msg = new Message(MessageType.AnswerRequest,
                    ErrorType.System, 0);
                    byte[] buffer = msg.encode();
                    DatagramPacket packet =
                    new DatagramPacket(buffer,
                    buffer.length, responsePacket.getAddress(),
                    responsePacket.getPort());
                    LOGGER.log(Level.INFO,
                "Sending: " + msg.toString());
                    udpsock.send(packet);
                    continue;
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Invalid Message: "
                            + e.getMessage()); //3
                    Message msg = new Message(MessageType.AnswerRequest,
                            ErrorType.IncorrectPacket, 0);
                    byte[] buffer = msg.encode();
                    DatagramPacket packet =
                            new DatagramPacket(buffer,
                                    buffer.length, responsePacket.getAddress(),
                                    responsePacket.getPort());
                    LOGGER.log(Level.INFO,
                            "Sending: " + msg.toString());
                    udpsock.send(packet);
                    continue;
                }
                if (message.getType() == MessageType.AnswerRequest) {
                    LOGGER.log(Level.WARNING,
                            "Unexpected message type: " + message.toString());
                    Message msg = new Message(MessageType.AnswerRequest,
                            ErrorType.IncorrectPacket, message.getSessionID());
                    byte[] buffer = msg.encode();
                    DatagramPacket packet =
                            new DatagramPacket(buffer,
                                    buffer.length, responsePacket.getAddress(),
                                    responsePacket.getPort());
                    LOGGER.log(Level.INFO,
                            "Sending: " + msg.toString());
                    udpsock.send(packet);
                    continue;
                }
                LOGGER.log(Level.INFO, "Received: "
                        + message.toString());
                if (message.getType().getCode() < 2) {
                    Message msg = new Message(MessageType.AnswerRequest,
                            ErrorType.None, message.getSessionID());
                    // 0 for Node, 1 for MetaNode
                    for (Map.Entry
                            <InetSocketAddress, Integer> entry : map) {
                        if (entry.getValue() == message.getType().getCode()) {
                            msg.addAddress(entry.getKey()); //IF issues, FIXME
                        }
                    }
                    /*if (msg.getAddresses().size() != nodesize &&
                        msg.getAddresses().size() != metasize) {
                        LOGGER.log(Level.SEVERE, "DESYNCED LIST");
                        return;
                    }*/
                    byte[] buffer = msg.encode();
                    DatagramPacket packet =
                    new DatagramPacket(buffer,
                    buffer.length, responsePacket.getAddress(),
                    responsePacket.getPort());
                    LOGGER.log(Level.INFO,
                            "Sending: " + msg.toString());
                    udpsock.send(packet);
                    continue;
                }
                if (message.getType() == MessageType.MetaNodeAdditions) {
                    for (InetSocketAddress address : message.getAddresses()) {
                        //check if each address is valid
                        //if valid and not already in map, add to list
                    if (address.getAddress() instanceof Inet4Address) {
                        if (address.getPort() > 0 &&
                        address.getPort() < 65535) {
                            if (metasize < 255) {
                                Map.Entry<InetSocketAddress, Integer> entry =
                                new AbstractMap.SimpleEntry<>(address, 1);
                                if (!dupe(entry)) { //test for duplicates
                                    // TESTME
                                    map.add(entry);
                                    metasize++;
                                }
                            }
                        }
                    }
                    }
                    //System.out.println(metasize);

                    continue;
                }
                if (message.getType() == MessageType.NodeAdditions) {
                    for (InetSocketAddress address : message.getAddresses()) {
                        //check if each address is valid
                        //if valid and not already in map, add to list
                    if (address.getAddress() instanceof Inet4Address) {
                        if (address.getPort() > 0 &&
                        address.getPort() < 65535) {
                            if (nodesize < 255) {
                                Map.Entry<InetSocketAddress, Integer> entry =
                                new AbstractMap.SimpleEntry<>(address, 0);
                                if (!dupe(entry)) {
                                //test for duplicates //TESTME
                                    map.add(entry);
                                    nodesize++;
                                }
                            }
                        }
                    }
                    }
                    //System.out.println(nodesize);
                    continue;
                }
                if (message.getType() == MessageType.NodeDeletions) {
                    for (InetSocketAddress address : message.getAddresses()) {
                        //check if each address is valid
                        //if valid and in map, remove from list
                        if (address.getAddress() instanceof Inet4Address) {
                            if (address.getPort() > 0 && address.getPort() < 65535) {
                                if (nodesize != 0) {
                                    Map.Entry<InetSocketAddress, Integer> entry =
                                            new AbstractMap.SimpleEntry<>(address, 0);
                                    if (dupe(entry)) { //test for existence
                                        map.remove(entry);
                                        nodesize--;
                                    }
                                }
                            }
                        }
                    }
                    continue;
                }
                if (message.getType() == MessageType.MetaNodeDeletions) {
                    for (InetSocketAddress address : message.getAddresses()) {
                        //check if each address is valid
                        //if valid and in map, remove from list
                        if (address.getAddress() instanceof Inet4Address) {
                            if (address.getPort() > 0 && address.getPort() < 65535) {
                                if (metasize != 0) {
                                    Map.Entry<InetSocketAddress, Integer> entry =
                                    new AbstractMap.SimpleEntry<>(address, 1);
                                    if (dupe(entry)) { //test for duplicates //TESTME
                                        map.remove(entry);
                                        metasize--;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Problem sending out error");
        }
    }
    /**
     * Checks if the given entry already exists in the map.
     * 
     * @param entry the entry to check for duplication
     * @return true if the entry is a duplicate, false otherwise
     */
    public static boolean dupe (Map.Entry
        <InetSocketAddress, Integer> entry) {
        for (Map.Entry<InetSocketAddress, Integer> existingEntry : map) {
            if (existingEntry.getKey().equals(entry.getKey())) {
                return true;
            }
        }
        return false;
    }
}