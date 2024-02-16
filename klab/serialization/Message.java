package klab.serialization;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a message that can be encoded and decoded for communication.
 */
public class Message {
        private int type;
        private byte[] msgID;
        private int ttl;
        private RoutingService routingService;
        private int payloadLength;
        /**
         * Constructs a new Message object with the specified parameters.
         *
         * @param msgID           
         * the unique ID of the message
         * @param ttl             
         * the time-to-live (TTL) of the message
         * @param routingService  
         * the routing service used for message delivery
         * @throws BadAttributeValueException 
         * if the provided parameters are invalid
         */
        public Message (byte[] msgID, int ttl, 
        RoutingService routingService) throws BadAttributeValueException {
            //System.out.println(Arrays.toString(msgID) + " "
            // + ttl + " " + routingService.getCode());
            setID(msgID);
            setTTL(ttl);
            setRoutingService(routingService);
        }

        /**
         * Represents a message that can be decoded from a MessageInput.
         * @param in the MessageInput to decode the message from
         * @return Message the decoded message
         * @throws IOException if an I/O error occurs
         * @throws BadAttributeValueException 
         * if the message has invalid attributes
         */
        public static Message decode(MessageInput in)
            throws IOException, BadAttributeValueException {
            byte[] type = in.readBytes(1);
            int typeint = type[0] & 0xFF;
            byte[] msgID = in.readBytes(15);
            if (msgID.length != 15) {
                throw new BadAttributeValueException(
                "ID not 15 bytes!", "msgID");
            }
            //System.out.print('\n');
            byte[] buf  = in.readBytes(1);
            int ttl = buf[0] & 0xFF;
            //System.out.println(ttl);
            buf  = in.readBytes(1);
            int routingService = buf[0] & 0xFF;
            //System.out.println(routingService);
            if (routingService != 0 && routingService != 1) {
                throw new BadAttributeValueException(
                "RoutingService not 0 or 1", "routingService");
            }
            //System.out.println(routingService);
            buf  = in.readBytes(2);
            int payloadLength = (buf[0] & 0xFF) << 8 | (buf[1] & 0xFF);
            if (typeint == 1) {
                String x = new String( in.readStringWithSize(payloadLength), 
                StandardCharsets.US_ASCII);
                if (payloadLength > x.length()) {
                    throw new BadAttributeValueException(
                    "Search String Mismatch with Size", "Search String");
                }
                return new Search (msgID, ttl,
                RoutingService.getRoutingService(routingService), x);
            }
            if (typeint == 2) { //return Response
                buf  = in.readBytes(1);
                int matches = buf[0] & 0xFF;
                buf  = in.readBytes(2);
                int port = (buf[0] & 0xFF) << 8 | (buf[1] & 0xFF);
                //System.out.println(Arrays.toString(buf));
                //System.out.println("p " + port);
                byte[] buf4 = new byte[4];
                int[] ip = new int[4];
                buf4 = in.readBytes(4);
                for (int i = 0; i < 4; i++) {
                    //System.out.println(buf4[i]);
                    ip[i] = buf4[i] & 0xFF;
                }
                String hostString = ip[0] + "." + 
                ip[1] + "." + ip[2] + "." + ip[3];
                //System.out.println(hostString);
                Inet4Address address = (Inet4Address) 
                Inet4Address.getByName(hostString);
                Response response = new Response (msgID, ttl, 
                RoutingService.getRoutingService(routingService), 
                new InetSocketAddress(address, port));
                response.setMatches(matches);
                List<Result> resultsq = new ArrayList <Result>(matches);
                response.setResultList(resultsq);
                if (matches > 0) {
                    for (int i = 0; i < matches; i++) {
                        Result result = new Result(in);
                        //System.out.println(result.toString());
                        response.addResult(result);
                    }
                }
                response.setPayloadLength(payloadLength);
                return response;
            }
            else {
                //System.out.println(typeint);
                throw new BadAttributeValueException(
                "Type not 1 or 2", "type");
            }
        }
        
        /**
         * Sets the length of the payload in the message.
         *
         * @param payloadLength the length of the payload
         */
        public void setPayloadLength(int payloadLength) {
            this.payloadLength = payloadLength;
        }

        /**
         * Returns the length of the payload.
         *
         * @return the length of the payload
         */
        public int getPayloadLength() {
            return this.payloadLength;
        }
        /**
         * Converts a byte array to an integer.
         *
         * @param x the byte array to be converted
         * @return the integer value converted from the byte array
         */
        public static int byteToInt(byte[] x) {
            int value = 0;
            /*for (byte b : x) {
                System.out.print(b);
            }*/
            //System.out.print('\n');
            /*for (int i = 0; i < 4; i++) {
                value = (value << 8) + (x[i] & 0xff);
            }*/
            return value;
        }

        /**
         * Encodes the message and writes it to
         * the given MessageOutput stream.
         *
         * @param out the MessageOutput 
         * stream to write the encoded message to
         * @throws IOException if there 
         * is an error while writing the encoded message
         */
        public void encode(MessageOutput out)
            throws IOException {
            try {
                if (this instanceof Search) {
                    this.type = 1;
                }
                if (this instanceof Response) {
                    this.type = 2;
                }
                out.writeBytes(intToBytes(this.type, 1));
                out.writeBytes(this.getID());
                out.writeBytes(intToBytes(this.getTTL(), 1));
                out.writeBytes(intToBytes(
                this.getRoutingService().getCode(), 1));
                if (this.type == 1) {
                    byte[] x = ((Search)this).getSearchString()
                    .getBytes(StandardCharsets.US_ASCII);
                    byte[] watchlength = intToBytes(x.length, 2);
                    out.writeBytes(watchlength);
                    out.writeBytes(x);
                }
                if (this.type == 2) {
                    out.writeBytes(intToBytes(getPayloadLength(), 2));
                    out.writeBytes(intToBytes(
                    ((Response)this).getMatches(), 1));
                    out.writeBytes(intToBytes(
                    ((Response)this).getResponseHost().getPort(), 2));
                    InetSocketAddress responseHost = 
                    ((Response)this).getResponseHost();
                    byte[] ip = responseHost.getAddress().getAddress();
                    out.writeBytes(ip);
                    List<Result> resultList = 
                    ((Response)this).getResultList();
                    for (Result r : resultList) {
                        r.encode(out);
                    }
                }
            } catch (IOException e) {
                throw new IOException("Bad Write Function");
            }
        }

        /**
         * Converts an integer to a byte array of specified length.
         *
         * @param x the integer to convert
         * @param n the length of the resulting byte array
         * @return the byte array representation of the integer
         */
        public byte[] intToBytes(int x, int n) {
            byte[] bytes = new byte[n];
            for (int i = 0; i < n; i++) {
                bytes[i] = (byte) (x >> (8 * (n - i - 1)));
                //System.out.println(bytes[i]);
            }
            return bytes;
        }

        /**
         * Returns the ID of the message.
         *
         * @return the ID of the message as a byte array
         */
        public byte[] getID() {
            return msgID;
        }

        /**
         * Sets the ID of the message.
         * 
         * @param msgID the ID of the message
         * @return the updated Message object
         * @throws BadAttributeValueException if the length of msgID is not 15 bytes
         */

        public Message setID(byte[] msgID) 
        throws BadAttributeValueException {      
            if (msgID == null) {
                throw new BadAttributeValueException(
                "msgID is null", "msgID");
            }
            if (msgID.length != 15) {
                throw new BadAttributeValueException(
                "msgID is not 15 bytes", "msgID");
            }
            this.msgID = msgID;
            return this;
        }
        
        /**
         * Returns the Time To Live (TTL) value of the message.
         *
         * @return the TTL value of the message
         */
        public int getTTL() {
            return ttl;
        }
        /**
         * Sets the time-to-live (TTL) value for the message.
         * 
         * @param ttl the time-to-live value to set
         * @return the updated Message object
         * @throws BadAttributeValueException 
         * if the TTL value is less than 0
        */
        public Message setTTL(int ttl) throws BadAttributeValueException {
            if (ttl < 0) {
                throw new BadAttributeValueException(
                "ttl is less than 0", "ttl");
            }
            else if (ttl > 255) {
                throw new BadAttributeValueException(
                "ttl is greater than 255", "ttl");
            }
            this.ttl = ttl;
            return this;
        }
        /**
         * Returns the routing service associated with this message.
         *
         * @return the routing service
         */
        public RoutingService getRoutingService() {
            return routingService;
        }
        /**
             * Sets the routing service for the message.
             *
             * @param routingService the routing service to set
             * @return the updated message object
             */
        public Message setRoutingService(RoutingService routingService) throws BadAttributeValueException {
            if (routingService == null) {
                throw new BadAttributeValueException("routingService is null", "routingService");
            }
            this.routingService = routingService;
            return this;
        }

        /**
         * Returns the type of the message.
         *
         * @return the type of the message
         */
        public int getType() {
            return this.type;
        }

        /**
         * Sets the type of the message.
         *
         * @param type the type of the message
         */
        public void setType(int type) {
            this.type = type;
        }

        /**
         * Indicates whether some other object is 
         * "equal to" this one.
         * 
         * @param o the reference object 
         * with which to compare
         * @return true if this object is the same
         * as the o argument; false otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Message message = (Message) o;

            if (type != message.type) return false;
            if (ttl != message.ttl) return false;
            if (msgID != null ? !msgID.equals(message.msgID)
            : message.msgID != null) return false;
            return routingService == message.routingService;
        }

        /**
         * Returns the hash code value for this Message object.
         * The hash code is calculated based on the
         * type, msgID, ttl, and routingService fields.
         *
         * @return the hash code value for this Message object.
         */
        @Override
        public int hashCode() {
            int result = type;
            result = 31 * result + (msgID != null ? msgID.hashCode() : 0);
            result = 31 * result + ttl;
            result = 31 * result + (routingService != null 
            ? routingService.hashCode() : 0);
            return result;
        }
}