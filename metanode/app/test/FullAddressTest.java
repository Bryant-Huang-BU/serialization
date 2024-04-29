package metanode.app.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import metanode.serialization.*;
public class FullAddressTest {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            InetAddress address = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            //while (true) {
            byte[] enc = new byte[] {67,0,0,(byte) 255};
            byte[] ipAddresses = new byte[255 * 6];
int index = 0;
int currentOctet = 1;
for (int i = 0; i < 255; i++) {
    byte[] ipAddress = {(byte) currentOctet, 0, 0, 0};
    int portNumber = 1 + i % 65535;
    byte[] ports = {(byte) (portNumber >> 8), (byte) (portNumber & 0xFF)};

    // Print out the generated IP address and port
    System.out.println("IP address: " + (ipAddress[0] & 0xFF) + "." + ipAddress[1] + "." + ipAddress[2] + "." + ipAddress[3] +
                       ", Port: " + portNumber);

    // Append the IP address and port to the byte array
    System.arraycopy(ipAddress, 0, ipAddresses, index, 4);
    index += 4;
    System.arraycopy(ports, 0, ipAddresses, index, 2);
    index += 2;

    // Increment to the next octet
    currentOctet++;
    if (currentOctet > 223) {
        currentOctet = 1;
    }
}
            int totalLength = enc.length + ipAddresses.length;
            byte[] result = new byte[totalLength];
            System.arraycopy(enc, 0, result, 0, enc.length);
            System.arraycopy(ipAddresses, 0, result, enc.length, ipAddresses.length);
            System.out.println(ipAddresses.length + " " + ipAddresses.length/6 + " " + result.length );
            //Message msg = new Message(result);
            //System.out.println(msg.toString());
            //enc = msg.encode();
            DatagramPacket packet =
            new DatagramPacket(result,
            result.length, address, port);
            //DatagramPacket packet =
            //new DatagramPacket(result,
            //result.length, address, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
