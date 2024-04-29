package metanode.app.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.lang.model.type.ErrorType;
import metanode.serialization.*;

public class TestServerBadAR {
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
            //System.out.println("TestServerBadAR: main: starting");
            Message message = new Message(MessageType.AnswerRequest, metanode.serialization.ErrorType.None, 0);
            System.out.println("TestServerBadAR: main: message: " + message.toString());
            byte[] enc = message.encode();
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
