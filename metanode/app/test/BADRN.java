package metanode.app.test;

import metanode.serialization.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BADRN {
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
            byte[] enc = new byte[]{64, 0, 0, 2, 127, 0, 0, 2, 1, -1, (byte) 259, (byte) 259, (byte) 259};
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
            enc = new byte[]{65, 0, 0, 2, 127, 0, 0, 2, 1, -1, (byte) 259, (byte) 259, (byte) 259};
            //Message msg = new Message(enc);
            //System.out.println(msg.toString());
            //enc = msg.encode();
            packet =
                    new DatagramPacket(enc,
                            enc.length, address, port);
            socket.send(packet);
            response = new byte[1534];
            responsePacket =
                    new DatagramPacket(
                            response, response.length);
            socket.receive(responsePacket);
            trimmed = new byte
                    [responsePacket.getLength()];
            System.arraycopy(response, 0, trimmed,
                    0, trimmed.length);
            responseMsg = new Message(trimmed);
            System.out.println(responseMsg.toString());
            enc = new byte[]{65, 10, 0, 2, 127, 0, 0, 2, 1, -1, (byte) 259, (byte) 259, (byte) 259};
            //Message msg = new Message(enc);
            //System.out.println(msg.toString());
            //enc = msg.encode();
            packet =
                    new DatagramPacket(enc,
                            enc.length, address, port);
            socket.send(packet);
            response = new byte[1534];
            responsePacket =
                    new DatagramPacket(
                            response, response.length);
            socket.receive(responsePacket);
            trimmed = new byte
                    [responsePacket.getLength()];
            System.arraycopy(response, 0, trimmed,
                    0, trimmed.length);
            responseMsg = new Message(trimmed);
            System.out.println(responseMsg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
