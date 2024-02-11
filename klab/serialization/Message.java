package klab.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Message {
        private int type;
        private byte[] msgID;
        private int ttl;
        private RoutingService routingService;
        //private int payloadLength;

        public Message (byte[] msgID, int ttl, RoutingService routingService) throws BadAttributeValueException {
            //System.out.println(Arrays.toString(msgID) + " " + ttl + " " + routingService.getCode());
            setID(msgID);
            setTTL(ttl);
            setRoutingService(routingService);
        }

        public static Message decode(MessageInput in)
            throws IOException, BadAttributeValueException {
            byte[] type = in.readBytes(1);
            int typeint = type[0] & 0xFF;
            System.out.println(typeint);
            byte[] msgID = in.readBytes(15);
            if (msgID.length != 15) {
                throw new BadAttributeValueException("ID not 15 bytes!", "msgID");
            }
            //System.out.print('\n');
            byte[] buf  = in.readBytes(1);
            int ttl = buf[0] & 0xFF;
            //System.out.println(ttl);
            buf  = in.readBytes(1);
            int routingService = buf[0] & 0xFF;
            //System.out.println(routingService);
            buf  = in.readBytes(2);
            int payloadLength = (buf[0] & 0xFF) << 8 | (buf[1] & 0xFF);
            System.out.println(Arrays.toString(buf));
            System.out.println("p " + payloadLength);
            if (typeint == 1) {
                String x = new String( in.readStringWithSize(payloadLength), StandardCharsets.US_ASCII);
                return new Search (msgID, ttl, RoutingService.getRoutingService(routingService), x);
            }
            if (typeint == 2) { //return Response
                buf  = in.readBytes(1);
                int matches = buf[0] & 0xFF;
                buf  = in.readBytes(2);
                int port = (buf[0] & 0xFF) << 8 | (buf[1] & 0xFF);
                System.out.println(Arrays.toString(buf));
                System.out.println("p " + port);
                byte[] buf4 = new byte[4];
                int[] ip = new int[4];
                buf4 = in.readBytes(4);
                for (int i = 0; i < 4; i++) {
                    System.out.println(buf4[i]);
                    ip[i] = buf4[i] & 0xFF;
                }
                String hostString = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
                System.out.println(hostString);
                Inet4Address address = (Inet4Address) Inet4Address.getByName(hostString);
                Response response = new Response (msgID, ttl, RoutingService.getRoutingService(routingService), new InetSocketAddress(address, port));
                response.setMatches(matches);
                List<Result> resultsq = new ArrayList <Result>(matches);
                for (int i = 0; i < matches; i++) {
                    Result result = new Result(in);
                    System.out.println(result.toString());
                    resultsq.add(result);
                }
                response.setResultList(resultsq);
                return response;
            }
            else {
                return new Message(msgID, ttl, RoutingService.getRoutingService(routingService));
            }
        }

        public static int byteToInt(byte[] x) {
            int value = 0;
            for (byte b : x) {
                System.out.print(b);
            }
            System.out.print('\n');
            /*for (int i = 0; i < 4; i++) {
                value = (value << 8) + (x[i] & 0xff);
            }*/
            return value;
        }

        public void encode(MessageOutput out)
            throws IOException {
            out.writeBytes(intToBytes(this.type));
            out.writeBytes(this.msgID);
            out.writeBytes(intToBytes(this.ttl));
            out.writeBytes(intToBytes(this.routingService.getCode()));
        }

        public byte[] intToBytes(int x) {
            byte[] bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = (byte)(x >>> (i * 8));
            }
            return bytes;
        }

        public byte[] getID() {
            return msgID;
        }

        public Message setID(byte[] msgID) throws BadAttributeValueException {
            if (msgID.length != 15) {
                throw new BadAttributeValueException("msgID is not 15 bytes", "msgID");
            }
            this.msgID = msgID;
            return this;
        }

        public int getTTL() {
            return ttl;
        }

        public Message setTTL(int ttl) throws BadAttributeValueException {
            if (ttl < 0) {
                throw new BadAttributeValueException("ttl is less than 0", "ttl");
            }
            this.ttl = ttl;
            return this;
        }

        public RoutingService getRoutingService() {
            return routingService;
        }

        public Message setRoutingService(RoutingService routingService) {
            this.routingService = routingService;
            return this;
        }

        public String toString() {
            return "Message: ID=" + msgID + " TTL=" + ttl + " Routing=" + routingService.getCode();
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Message message = (Message) o;

            if (type != message.type) return false;
            if (ttl != message.ttl) return false;
            if (msgID != null ? !msgID.equals(message.msgID) : message.msgID != null) return false;
            return routingService == message.routingService;
        }

        @Override
        public int hashCode() {
            int result = type;
            result = 31 * result + (msgID != null ? msgID.hashCode() : 0);
            result = 31 * result + ttl;
            result = 31 * result + (routingService != null ? routingService.hashCode() : 0);
            return result;
        }
}