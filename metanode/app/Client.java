package metanode.app;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.*;

import metanode.serialization.*;
public class Client {
    public static final Logger LOGGER = Logger.getLogger("node.log");
    public static DatagramSocket udpsock;
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
        InetAddress address;
        int port;
        try {
            udpsock = new DatagramSocket();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, 
            "Error creating UDP socket: ", e.getMessage());
            return;
        }
        if (args.length != 2) {
            LOGGER.log(Level.SEVERE, "Incorrect Arguments!");
            return;
        }
        else {
            //if the first argument is a valid IP address
            //and the second argument is a valid port number
            //I need to be able to connect to the server with UDP socket
            if (args[0] == null) {
                LOGGER.log(Level.SEVERE, "Invalid IP Address!");
                return;
            }
            if (args[1] == null) {
                LOGGER.log(Level.SEVERE, "Invalid Port Number!");
                return;
            }
            //check that the port number is a valid number
            if (Integer.parseInt(args[1]) < 0 
            || Integer.parseInt(args[1]) > 65535) {
                LOGGER.log(Level.SEVERE, "Invalid Port Number!");
                return;
            }
            try {
                address = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Invalid Arguments!");
                return;
            }
        }
        int id = 0;
        while (true) {
            Scanner sc = new Scanner(System.in);
            String input = "";
            System.out.print("> ");
            if (sc.hasNextLine()) {
                input = sc.nextLine();
                String[] command = input.split(" ");
                if (command.length == 0) {
                    continue;
                }
                if (command[0].equals("exit")) {
                    LOGGER.log(Level.INFO, "Node Exited");
                    break;
                } else if (command[0].equals("RN")) {
                    if (command.length > 1) {
                        LOGGER.log(Level.WARNING,
                    "Invalid Message: RN command expects no argument");
                        continue;
                    }
                } else if (command[0].equals("RM")) {
                    if (command.length > 1) {
                        LOGGER.log(Level.WARNING,
                    "Invalid Message: RM command expects no argument");
                        continue;
                    }
                } else if (command[0].equals("NA")) {
                    if (command.length < 2) {
                        LOGGER.log(Level.WARNING,
                                "Invalid Message: NA commands expects at " +
                                "least one argument: NA");
                        continue;
                    }
                    Message msg = new Message(MessageType.NodeAdditions, ErrorType.None, id);
                    List<InetSocketAddress> addresses = new ArrayList<>();
                    addresses = collectIP(input);
                    for (InetSocketAddress addr : addresses) {
                        msg.addAddress(addr);
                    }
                    LOGGER.log(Level.INFO, "Sending NA message " + msg.toString());
                    try {
                        if (!sendAndWait(msg, address, port)) {
                            LOGGER.log(Level.WARNING, "No response received");
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Invalid IP address");
                        continue;
                    }
                } else if (command[0].equals("MA")) {
                    if (command.length < 2) {
                        LOGGER.log(Level.WARNING,
                                "Invalid Message: MA commands expects at " +
                                "least one argument: MA");
                        continue;
                    }
                    Message msg = new Message(MessageType.MetaNodeAdditions, ErrorType.None, id);
                    List<InetSocketAddress> addresses = new ArrayList<>();
                    addresses = collectIP(input);
                    for (InetSocketAddress addr : addresses) {
                        msg.addAddress(addr);
                    }
                    LOGGER.log(Level.INFO, "Sending MA message " + msg.toString());
                    try {
                        if (!sendAndWait(msg, address, port)) {
                            LOGGER.log(Level.WARNING, "No response received");
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Invalid IP address");
                        continue;
                    }
                } else if (command[0].equals("ND")) {
                    if (command.length < 2) {
                        LOGGER.log(Level.WARNING,
                                "Invalid Message: ND commands expects at " +
                                "least one argument: ND");
                        continue;
                    }
                    Message msg = new Message(MessageType.NodeDeletions, ErrorType.None, id);
                    List<InetSocketAddress> addresses = new ArrayList<>();
                    addresses = collectIP(input);
                    for (InetSocketAddress addr : addresses) {
                        msg.addAddress(addr);
                    }
                    LOGGER.log(Level.INFO, "Sending ND message " + msg.toString());
                    try {
                        if (!sendAndWait(msg, address, port)) {
                            LOGGER.log(Level.WARNING, "No response received");
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Invalid IP address");
                        continue;
                    }
                } else if (command[0].equals("MD")) {
                    if (command.length < 2) {
                        LOGGER.log(Level.WARNING,
                                "Invalid Message: MD commands expects at " +
                                        "least one argument: MD");
                        continue;
                    }
                    Message msg = new Message(MessageType.MetaNodeDeletions, ErrorType.None, id);
                    List<InetSocketAddress> addresses = new ArrayList<>();
                    addresses = collectIP(input);
                    for (InetSocketAddress addr : addresses) {
                        msg.addAddress(addr);
                    }
                    LOGGER.log(Level.INFO, "Sending MD message " + msg.toString());
                    try {
                        if (!sendAndWait(msg, address, port)) {
                            LOGGER.log(Level.WARNING, "No response received");
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Invalid IP address");
                        continue;
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Invalid Message: Not a valid command");
                    continue;
                }
            }
            sc.close();
            return;
        }
    }
//send is blocking
    public static boolean sendAndWait(Message msg, InetAddress ip, int port) throws IOException {
        try {
            int count = 0;
            //create a UDP socket  
            byte[] buffer = msg.encode();  
            boolean received = false;
            while (count < 3 && !received) {
                long start = System.currentTimeMillis();
                DatagramPacket packet = new DatagramPacket(buffer,
                buffer.length, ip, port);
                udpsock.send(packet);
                while (System.currentTimeMillis() - start < 3000 && !received) {
                    byte[] response = new byte[1534];
                    DatagramPacket responsePacket = new DatagramPacket(response,
                    1534);
                    udpsock.receive(responsePacket);
                    if (responsePacket.getLength() == 0) {
                        continue;
                    }
                    else {
                        received = true;
                    }
                    Message responseMsg = new Message(responsePacket.getData());
                }
                count++;
            }
            if (!received) {
                return true;
            }
            return false;    
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message: ", e.getMessage());
            throw new IOException("Error sending message");
        }
    }

    public static List<InetSocketAddress> collectIP(String input) throws IllegalArgumentException {
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
                throw new IllegalArgumentException("Invalid IP address");
            }
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            addresses.add(socketAddress);
        }
        return addresses;

    }
}
