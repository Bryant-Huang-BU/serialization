/************************************************
 * Author: Bryant Huang
 * Assignment: Program 5
 * Class: CSI4321
 ************************************************/

package metanode.app;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.*;

import metanode.serialization.*;
/**
 * The `Client` class represents a client application that
 * communicates with a server using UDP sockets.
 * It provides methods for sending and receiving 
 * messages to and from the server.
 */
public class Client {
    public static final 
    Logger LOGGER = Logger.getLogger("metanodelog.log");
    public static DatagramSocket udpsock;
    //public static Map<Integer, Message> messageMap = new HashMap<>();
    //public static List<InetSocketAddress> metaNodes = new ArrayList<>();
    static {
        FileHandler f;
        ConsoleHandler c;
        try {
            f = new FileHandler(
            System.getProperty("user.dir") +
            "\\metanode.log");
            c = new ConsoleHandler();
            f.setFormatter(new SimpleFormatter());
            LOGGER.setLevel(Level.ALL);
            f.setLevel(Level.ALL);
            c.setLevel(Level.WARNING);
            LOGGER.addHandler(f);
            LOGGER.addHandler(c);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "File LOGGER not working:"
                    , e.getMessage());
        }
    }

    /**
     * The main method is the entry point of the application.
     * It establishes a UDP socket connection with a server
     * based on the provided IP address and port number.
     * It then prompts the user for input commands and 
     * sends them to the server.
     * The server responds with appropriate messages, which 
     * are logged and displayed on the console.
     * The main method handles various error conditions and 
     * provides error messages accordingly.
     *
     * @param args An array of command-line arguments. The 
     * first argument should be the IP address of the server,
     * and the second argument should be the port number.
     */
    public static void main(String[] args) throws IOException {
        InetAddress address;
        int port;
        try {
            udpsock = new DatagramSocket();
        } catch (IOException e) {
            LOGGER.log(Level.INFO,
            "Error creating UDP socket: ", e.getMessage());
            System.err.println(
            "Error creating UDP Socket " + " e.getMessage");
            return;
        }
        if (args.length != 2) {
            LOGGER.log(Level.INFO, "Incorrect Arguments!");
            System.err.println("There needs to be only 2 parameters");
            return;
        }
        else {
        try {
            //if the first argument is a valid IP address
            //and the second argument is a valid port number
            //I need to be able to connect to the server with UDP socket
            if (args[0] == null) {
                LOGGER.log(Level.INFO, "Invalid IP Address!");
                System.err.println("Invalid IP Address");
                return;
            }
            if (args[1] == null) {
                LOGGER.log(Level.INFO, "Invalid Port Number!");
                System.err.println("Invalid Port Number!");
                return;
            }
            //check that the port number is a valid number
            if (Integer.parseInt(args[1]) < 0
                    || Integer.parseInt(args[1]) > 65535) {
                LOGGER.log(Level.INFO, "Invalid Port Number!");
                System.err.println("Invalid Port Number!");
                return;
            }
            try {
                address = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                LOGGER.log(Level.INFO,
            "Invalid message: " + e.getMessage());
                System.err.println("Invalid message: " + e.getMessage());
                return;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        }
        Scanner sc = new Scanner(System.in);
        String input = "";
        int id = -1;
        //System.out.print("> ");
        while (true) {
            System.out.print("> ");
            if (sc.hasNextLine()) {
                //System.out.println("> ");
                input = sc.nextLine();
                String[] command = input.split(" ");
                if (command.length == 0) {
                    LOGGER.log(Level.INFO, "No Command");
                    //System.out.println("> ");
                    System.err.println("No Command");
                    continue;
                }
                if (command[0].equals("exit")) {
                    LOGGER.log(Level.INFO, "Node Exited");
                    break;
                }
                MessageType code = MessageType.getByCmd(command[0]);
                if (code == null || code.getCode() == 2) {
                    LOGGER.log(Level.INFO,
                    "Invalid Command: Bad command " + command[0]);
                    System.err.println(
                    "Invalid Command: Bad command " + command[0]);
                    //System.out.println("\n> ");
                    continue;
                }
                boolean received = false;
                int idprop;
                if (code.getCode() < 2 && command.length != 1) {
                    LOGGER.log(Level.INFO,
                    code.getCmd() + " command expects no arguments");
                    System.err.println(
                    code.getCmd() + " command expects no arguments");
                    //System.out.println("\n> ");
                    continue;
                }
                if (code.getCode() < 2 && command.length == 1) {
                    int count = 0;
                    idprop = new Random().nextInt(256);
                    while (id == idprop) {
                        idprop = new Random().nextInt(256);
                    }
                    id = idprop;
                    //try {
                    long timeleft = 3000;
                    long prevtime;
                    Message msg = new Message(MessageType.getByCmd
                            (command[0]), ErrorType.None, id);
                    byte[] buffer = msg.encode();
                    DatagramPacket packet =
                    new DatagramPacket(buffer,
                    buffer.length, address, port);
                    LOGGER.log(Level.INFO,
                "Message sent: " + msg.toString());
                    udpsock.send(packet);
                    do {
                        try {
                        try {
                        udpsock.setSoTimeout((int) timeleft);
                        DatagramPacket responsePacket;
                        byte[] response;
                        try {
                            //System.out.println(timeleft);
                            response = new byte[1534];
                            responsePacket =
                            new DatagramPacket(
                            response, response.length);
                            prevtime = System.currentTimeMillis();
                            udpsock.receive(responsePacket);
                            timeleft -= (
                            System.currentTimeMillis() - prevtime);
                            //System.out.println(timeleft);
                        } catch (InterruptedIOException e) {
                            count++;
                            LOGGER.log(Level.INFO,
                        "No response received");
                            if (count < 4) {
                                msg = new Message(MessageType.getByCmd
                                (command[0]), ErrorType.None, id);
                                buffer = msg.encode();
                                packet =
                                new DatagramPacket(buffer,
                                buffer.length, address, port);
                                LOGGER.log(Level.INFO,
                                "Message sent: " + msg.toString());
                                udpsock.send(packet);
                            }
                            timeleft = 3000;
                            continue;
                        }
                        udpsock.setSoTimeout(0);
                        //trim the array to the size of the packet
                        byte[] trimmed = new byte
                        [responsePacket.getLength()];
                        System.arraycopy(response, 0, trimmed,
                    0, trimmed.length);
                        if (responsePacket.getData() != null) {
                            //received = true;
                            //System.out.println("DEBUG");
                            Message responseMsg =
                            new Message(trimmed);
                            if (!responseMsg.getType()
                            .getCmd().equals("AR")) {
                            LOGGER.log(Level.INFO,
                            "Unexpected message type "
                                + responseMsg.toString());
                                System.err.println(
                                "Unexpected message type");
                            } else if (responseMsg.
                                getSessionID() != 0 &&
                                responseMsg.getSessionID() != id) {
                                LOGGER.log(Level.INFO,
                            "Unexpected Session ID "
                                + responseMsg.toString());
                                System.err.println(
                                "Unexpected Session ID");
                            } else {
                                LOGGER.log(Level.INFO,
                                "AR Message received: "
                                + responseMsg.toString());
                            System.out.println(responseMsg);
                            if (responseMsg.getError()
                                .getCode() != 0) {
                                received = true;
                                System.out.println("A " +
                                ErrorType.getByCode(responseMsg.
                                getError().getCode()) +
                                " error has occured, code: "
                                + responseMsg.getError().getCode());
                                }
                                break;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        LOGGER.log(Level.INFO,
                                "Invalid Message: " + e.getMessage());
                        System.err.println(
                                "Invalid Message: " + e.getMessage());
                        //continue;
                    }
                }
                catch (IOException e) {
                    LOGGER.log(Level.INFO,
                "Communication problem: " + e.getMessage());
                    System.err.println(
                    "Communication problem: " + e.getMessage());
                    //continue;
                }
                } while (count < 4 && !received);

            }
            else {
                if (command.length < 2) {
                    LOGGER.log(Level.INFO,
                    code.getCmd() +
                    " command expects at least one argument: "
                    + code.getCmd());
                    System.err.println(code.getCmd() +
                    " command expects at least one argument: "
                    + code.getCmd());
                    continue;
                }
                idprop = new Random().nextInt(256);
                while (id == idprop) {
                    idprop = new Random().nextInt(256);
                }
                id = idprop;
                Message msg = new Message(
                MessageType.getByCmd(command[0]), ErrorType.None, id);
                List<InetSocketAddress> addresses;
                try {
                    addresses = collectIP(input);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.INFO,
                    "Invalid Message: " + e.getMessage());
                    System.err.println(
                    "Invalid Message: " + e.getMessage());
                    continue;
                }
                if (addresses.size() == 0) {
                    LOGGER.log(Level.INFO, "Invalid Message");
                    System.err.println("Invalid Message: " + input +
                    " is an invalid IP address");
                    continue;
                }
                for (InetSocketAddress addr : addresses) {
                    try {
                        msg.addAddress(addr);
                    } catch (IllegalArgumentException e) {
                        LOGGER.log(Level.INFO,
                        "Invalid Message: " + e.getMessage());
                        System.err.println(
                        "Invalid Message: " + e.getMessage());
                        continue;
                    }
                }
                byte[] buffer = msg.encode();
                DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, address, port);
                LOGGER.log(Level.INFO,
                "Message sent: " + msg.toString());
                try {
                    udpsock.send(packet);
                } catch (IOException e) {
                    LOGGER.log(Level.INFO,
                    "Communication problem: ", e.getMessage());
                    System.err.println(
                    "Communication problem: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.INFO,
                "Invalid Message: ", e.getMessage());
                    System.err.println(
                    "Invalid Message: " + e.getMessage());
                }
                //id++;
            }
        }
        //System.out.print("> ");
    }
    sc.close();
    //return;
}

/**
 * Collects a list of InetSocketAddress
 * objects from the given input string.
 *
 * @param input the input string containing IP:
 * port pairs separated by spaces
 * @return a list of InetSocketAddress objects
 * representing the IP:port pairs
 * @throws IllegalArgumentException if the
 * input string or any IP:port pair is invalid
 */
public static List<InetSocketAddress>
collectIP(String input) {
    List<InetSocketAddress> addresses = new ArrayList<>();
    String[] ipPortPairs = input.split(" ");
    int count = 0;
    for (String ipPortPair : ipPortPairs) {
        if (count == 0) {
            count++;
            continue;
        }
        String[] parts = ipPortPair.split(":");
        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new
            IllegalArgumentException(e.getMessage());
        }
        if (port < 0 || port > 65535) {
            throw new
            IllegalArgumentException(
            "Invalid port number: " + port);
        }
        if (address == null) {
            throw new
            IllegalArgumentException(
            "Invalid IP address: " + ip);
        }
        if (parts.length != 2) {
            throw new
            IllegalArgumentException(
            "Invalid IP address: " + ip);
        }
        try {
            InetSocketAddress socketAddress =
            new InetSocketAddress(address, port);
            addresses.add(socketAddress);
        } catch (IllegalArgumentException e) {
            throw new
            IllegalArgumentException(e.getMessage());
        }
    }
    return addresses;
}
}
