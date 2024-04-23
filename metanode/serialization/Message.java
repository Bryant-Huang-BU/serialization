/************************************************
* Author: Bryant Huang
* Assignment: Program 4
* Class: CSI4321
************************************************/
package metanode.serialization;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message in the system.
 */
public class Message {
    private final int version;
    private final MessageType type;
    private final ErrorType error;
    private int sessionID;
    private final int count;
    private final List<InetSocketAddress> addresses;

    /**
     * Represents a message in the system.
     * @param type The type of the message
     * @param error The error type of the message
     * @param sessionID The session ID of the message
     * @throws IllegalArgumentException If the type is null, 
     * the error is null, or the session ID is negative
     */
    public Message(MessageType type, ErrorType error, int sessionID)
    throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException
            ("Message type cannot be null.");
        }
        if (error == null) {
            throw new IllegalArgumentException("Error type cannot be null.");
        }
        if (sessionID < 0 || sessionID > 255) {
            throw new IllegalArgumentException("sessionID out of bounds");
        }
        if (type != MessageType.AnswerRequest && error != ErrorType.None) {
            throw new IllegalArgumentException(
            "No error allowed for this Message type!");
        }
        this.version = 4;
        this.type = type;
        this.error = error;
        this.sessionID = sessionID;
        this.count = 0;
        this.addresses = new ArrayList<InetSocketAddress>();
    }

    /**
     * Constructs a new Message object from the given byte array.
     *
     * @param buf the byte array containing the message data
     * @throws IOException if there is an error reading the buffer
     * @throws IllegalArgumentException if the buffer is null,
     * the buffer length is less than 4,
     * the version is invalid, the message type is not valid,
     * the error code is not valid, the error code is not allowed
     * for the message type, the count is invalid, or the number
     * of addresses in the message does not match the buffer length
     */
    public Message(byte[] buf) throws IOException,
        IllegalArgumentException {
        if (buf == null) {
            throw new
            IOException("Buffer cannot be null.");
        }
        if (buf.length < 4) {
            throw new
            IOException("Buffer length must be at least 4.");
        }
        //version is 4 bits
        int typeint = (buf[0] & 0x0F);
        version = (buf[0] & 0xF0) >> 4;
        if (version != 4) {
            throw new IllegalArgumentException("Invalid version: " + version);
        }
        //System.out.println(MessageType.getByCode(typeint) + " " + typeint);
        type = MessageType.getByCode(typeint);
        if (type == null) {
            throw new IllegalArgumentException("Not a valid message type");
        }
        //error is an unsigned integer
        sessionID = (int) (buf[2] & 0xFF);
        error = ErrorType.getByCode((int) (buf[1] & 0xFF));
        if (error == null) {
            throw new IllegalArgumentException("Error code not valid!");
        }
        if (error != ErrorType.None &&
            type != MessageType.getByCmd("AR")) {
            throw new IllegalArgumentException(
            "No error allowed for anything but Answer Request!");
        }
        count = (int) (buf[3] & 0xFF);
        if (type.getCode() <= 1 && count > 1) {
            throw new IllegalArgumentException("There " +
            "should be no addresses for this message type.");
        }
        //data is a byte array of addresses and ports
        int countdata = 0;
        int[] ip = new int[5];
        addresses = new ArrayList<InetSocketAddress>();
        //System.out.println(count);
        if ((count * 6) + 4 != buf.length) {
            throw new IOException("The number of addresses " +
            "in the message does not match the buffer length.");
        }
        for (int i = 0; i < count; i++) {
            //System.out.println(i + " " + count);
            for (int j = 0; j < 5; j++) {
                if (j < 4) {
                    //System.out.println(4 + (6 * i) + j);
                    ip[j] = (buf[4 + (6 * i) + j] & 0xFF);
                    if (ip[j] < 0 || ip[j] > 255) {
                        throw new IllegalArgumentException(
                        "Invalid address: " + ip[j]);
                    }
                } else {
                    ip[j] = ((buf[4 + (6 * i) + j] & 0xFF) << 8) 
                    | ((buf[4 + (6 * i) + j + 1] & 0xFF));
                    // combine the two bytes
                    if (ip[j] < 0 || ip[j] > 65535) {
                        throw new IllegalArgumentException(
                        "Invalid port: " + ip[j]);
                    }

                }
            }
            InetSocketAddress addr = new InetSocketAddress(
            Inet4Address.getByAddress(new byte[] {
            (byte) ip[0], (byte) ip[1], (byte) ip[2], (byte) ip[3]
            }), ip[4]);
            addAddress(addr);
            countdata++;
        }
        if (countdata != count) {
            throw new IOException("The number of addresses " +
            "in the message does not match the count.");
        }
        if ((countdata * 6) + 4 != buf.length) {
            throw new IOException("The number of read addresses " +
                    "in the message does not match the buffer length.");
        }
    }

    /**
     * Encodes the message into a byte array.
     *
     * @return The encoded byte array.
     */
    public byte[] encode() {
        byte[] buf = new byte[4 + (6 * addresses.size())];
        buf[0] = (byte) ((version << 4) | type.getCode());
        buf[1] = (byte) error.getCode();
        buf[2] = (byte) sessionID;
        buf[3] = (byte) addresses.size();
        for (int i = 0; i < addresses.size(); i++) {
            InetSocketAddress addr = addresses.get(i);
            byte[] ip = addr.getAddress().getAddress();
            for (int j = 0; j < 4; j++) {
                buf[4 + (6 * i) + j] = ip[j];
            }
            int port = addr.getPort();
            buf[4 + (6 * i) + 4] = (byte) ((port & 0xFF00) >> 8);
            buf[4 + (6 * i) + 5] = (byte) (port & 0xFF);
        }
        /* for (int i = 0; i < buf.length; i++) {
            System.out.println((int) buf[i]);
        }*/
        //System.out.println(buf.length);
        return buf;
    }

    /**
     * Returns a string representation of the Message object.
     *
     * @return a string representation of the Message object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type=" + type.toString() + " Error=" +
        error.toString() + " Session ID=" + sessionID + " Addrs=");
        for (int i = 0; i < addresses.size(); i++) {
            String ph = addresses.get(i).toString();
            for (int j = 0; j < ph.length(); j++) {
                if (ph.charAt(j) == '/') {
                    ph = ph.substring(j + 1);
                    break;
                }
            }
            sb.append(ph);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Represents the type of a message.
     * @return MessageType The type of the message
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns the error type associated with this message.
     *
     * @return the error type
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * Returns the session ID associated with this message.
     *
     * @return the session ID
     */
    public int getSessionID() {
        return sessionID;
    }

    public Message setSessionID(int sessionID)
    throws IllegalArgumentException {
        if (sessionID < 0 || sessionID > 255) {
            throw new IllegalArgumentException
            ("Session ID must be non-negative.");
        }
        this.sessionID = sessionID;
        return this;
    }

    /**
     * Sets the session ID for the message.
     * 
     * @param sessionID the session ID to set
     * @return the updated Message object
     * @throws IllegalArgumentException if the session ID is invalid
     */
    public List<InetSocketAddress> getAddresses() {
        return addresses;
    }
    /**
         * Adds a new address to the message.
         * 
         * @param newAddress The new address to add.
         * @return The updated Message object.
         * @throws IllegalArgumentException 
         * If the new address is null, multicast,
         * has an invalid port number, 
         * has an invalid address length, or 
         * if the message type code is less than 2.
         */
    public Message addAddress(InetSocketAddress newAddress)
    throws IllegalArgumentException {
        if (newAddress == null) {
            throw new IllegalArgumentException("Address cannot be null.");
        }
        if (newAddress.getAddress().isMulticastAddress()) {
            throw new IllegalArgumentException("Can't be multicast.");
        }
        if (newAddress.getPort() < 0 || newAddress.getPort() > 65535) {
            throw new IllegalArgumentException("Invalid port number.");
        }
        if (newAddress.getAddress().getAddress().length != 4) {
            throw new IllegalArgumentException("Invalid address length.");
        }
        if (this.type.getCode() < 2) {
            throw new IllegalArgumentException("No address");
        }
        if (!addresses.contains(newAddress)) {
            if (addresses.size() >= 255) {
                throw new IllegalArgumentException("Too many addresses.");
            }
            addresses.add(newAddress);
            //this.count++;
        }
        else {
            throw new IllegalArgumentException("Address already exists in list");
        }
        return this;
    }

    /**
        * Returns a hash code value for the object. 
        * This method is used by the hashing-based
        * data structures, such as HashMap, HashSet, etc.
        *
        * @return the hash code value for the object.
        */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + 
        ((addresses == null) ? 0 : addresses.hashCode());
        result = prime * result + 
        ((error == null) ? 0 : error.hashCode());
        result = prime * result + sessionID;
        result = prime * result + 
        ((type == null) ? 0 : type.hashCode());
        result = prime * result + version;
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument;
     * false otherwise
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Message other = (Message) obj;
        if (addresses == null) {
            if (other.addresses != null) {
                return false;
            }
        } else if (!addresses.equals(other.addresses)) {
            return false;
        }
        if (error != other.error) {
            return false;
        }
        if (sessionID != other.sessionID) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        return true;
    }

    /**
     * Returns the version of the message.
     *
     * @return the version of the message
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the list of InetSocketAddress 
     * objects representing the addresses.
     *
     * @return the list of InetSocketAddress 
     * objects representing the addresses
     */
    public List<InetSocketAddress> getAddrList() {
        return addresses;
    }
}
