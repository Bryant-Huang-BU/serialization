package metanode.app.test;

import java.lang.System.Logger.Level;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import metanode.serialization.*;
public class TestServerBadPacket {
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
        byte[] enc = new byte[] {70,0,0,2,127,0,0,2, 1,-1,(byte) 259, (byte) 259, (byte) 259};
        //Message msg = new Message(enc);
        //System.out.println(msg.toString());
        //enc = msg.encode();
        DatagramPacket packet = 
        new DatagramPacket(enc,
        enc.length, address, port);
        socket.send(packet);
        byte[] response = new byte[1534];
        DatagramPacket responsePacket =
        new DatagramPacket(
        response, response.length);
        socket.receive(responsePacket);
        byte[] trimmed = new byte
        [responsePacket.getLength()];
        System.arraycopy(response, 0, trimmed,
0, trimmed.length);
        Message responseMsg = new Message(trimmed);
        System.out.println(responseMsg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
