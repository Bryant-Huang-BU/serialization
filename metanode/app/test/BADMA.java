package metanode.app.test;

import metanode.serialization.ErrorType;
import metanode.serialization.Message;
import metanode.serialization.MessageType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BADMA {
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
                //make a message with a bad IP address
            byte[] enc = new byte[] {67,0,0, 2, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, 1,1,1,1,1, 1};
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
            Message msg = new Message (MessageType.RequestMetaNodes, ErrorType.None, 1);
            enc = msg.encode();
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
