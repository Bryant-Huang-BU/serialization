
/************************************************
* Author: Bryant Huang
* Assignment: Program 1
* Class: CSI4321
************************************************/
package klab.serialization;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * The Response class represents a response message
 * in a communication system.
 * It extends the Message class and 
 * contains information about the response host,
 * the list of results, and the number of matches.
 */
public class Response extends Message {
    private InetSocketAddress responseHost;
    private List<Result> resultList;
    private int matches;
    /**
     * Constructs a Response object with the specified parameters.
     *
     * @param msgID           the ID of the message
     * @param ttl             the time-to-live value of the 
     * message
     * @param routingService  the routing service used for 
     * message routing
     * @param responseHost    the host address for the response
     * @throws BadAttributeValueException if any of the input
     *  parameters is null
     */
    public Response(byte[] msgID, int ttl, RoutingService routingService,
    InetSocketAddress responseHost) throws BadAttributeValueException {
        super(msgID, ttl, routingService);
        try {
            if (msgID == null || routingService == null 
            || responseHost == null) {
                throw new BadAttributeValueException
                ("msgID, routingService, or responseHost is null",
                "msgID, routingService, responseHost");
            }
            setResponseHost(responseHost);
            List<Result> resultList = new java.util.ArrayList<>();
            setResultList(resultList);
            setPayloadLength(7);
        } catch (BadAttributeValueException e) {
            throw new BadAttributeValueException
            ("msID, routingService, or responseHost is null", 
            "msgID, routingService, responseHost");
        }
    }

    /**
     * Returns the number of matches.
     *
     * @return the number of matches
     */
    public int getMatches() {
        return matches;
    }

    /**
     * Returns a string representation of the Response object.
     * The string includes the ID, TTL, routing service,
     *  host information, and the list of results.
     *
     * @return a string representation of the Response object
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response: ID=");
        sb.append(displayBytes());
        String hostInfo = String.format("%s:%d", responseHost.getAddress().
        getHostAddress(), responseHost.getPort());
        //System.out.println(getTTL());
        sb.append(" TTL=" + getTTL() + " Routing=" + getRoutingService()
        + " Host=" + hostInfo + " [");
        for (Result r : resultList) {
            sb.append(r.toString());
            if (resultList.indexOf(r) != resultList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
        * Returns the response host address.
        *
        * @return the response host address
        */
    public InetSocketAddress getResponseHost() {
        return this.responseHost;
    }
     /**
     * Sets the response host.
     * 
     * @param responseHost the response host to set
     * @return the updated Response object
     * @throws BadAttributeValueException if the responseHost is null
     */
    public Response setResponseHost(InetSocketAddress responseHost)
        throws BadAttributeValueException {
        try {
            if (responseHost == null) {
                throw new BadAttributeValueException(
                "responseHost is null", "responseHost");
            }
            if (responseHost.getAddress() == null) {
                throw new BadAttributeValueException(
                "responseHost address is null", "responseHost");
            }
            if (responseHost.getPort() < 0 || responseHost.getPort() > 65535) {
                throw new BadAttributeValueException(
                "responseHost port is invalid", "responseHost");
            }
            if (responseHost.getAddress().getHostAddress().isEmpty()) {
                throw new BadAttributeValueException(
                "responseHost address is empty", "responseHost");
            }

            //  JUnit Jupiter:Response:host setter:invalid:host=/224.1.2.3:42
            //get individual octets
            String[] octets = responseHost.getAddress().getHostAddress().
            split("\\.");
            if (octets.length != 4) {
                throw new BadAttributeValueException(
                "responseHost address is invalid", "responseHost");
            }
            //check if multicast
            if (responseHost.getAddress().isMulticastAddress()) {
                throw new BadAttributeValueException(
                "responseHost address is multicast", "responseHost");
            }
            this.responseHost = responseHost;
            return this;
        } catch (BadAttributeValueException e) {
            throw new BadAttributeValueException(e.getMessage(), 
            "responseHost");
        }
    }

    /**
     * Returns the list of results.
     *
     * @return the list of results
     */
    public List<Result> getResultList() {
        return this.resultList;
    }

    /**
     * Sets the result list for the response.
     *
     * @param resultList the list of results to be set
     * @return the updated Response object
     * @throws BadAttributeValueException 
     * if the provided resultList is invalid
     */
    public Response setResultList(List<Result> resultList)
        throws BadAttributeValueException {
            this.resultList = resultList;
            return this;
    }
    
    /**
     * Sets the number of matches in the response.
     * 
     * @param matches the number of matches to set
     * @throws BadAttributeValueException if the number of matches \
     * is less than 0 or greater than 255
     */
    public void setMatches(int matches) throws BadAttributeValueException {
        if (matches < 0 || matches > 255) {
            throw new BadAttributeValueException(
            "matches is less than 0", "matches");
        }
        this.matches = matches;
    }
    /**
     * Adds a result to the response.
     * 
     * @param result the result to be added
     * @return the updated response object
     * @throws BadAttributeValueException if the result is null
     */
    public Response addResult(Result result)
    throws BadAttributeValueException {
        try {
        if (result == null) {
            throw new BadAttributeValueException(
            "result is null", "result");
        }
        if (resultList == null) {
            resultList = new java.util.ArrayList<Result>();
        }
        resultList.add(result);
        this.matches++;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageOutput m = new MessageOutput(outputStream);
        result.encode(m);
        byte[] enc = outputStream.toByteArray();
        setPayloadLength(enc.length + getPayloadLength());
        } catch (Exception e) {
            throw new BadAttributeValueException
            (e.getMessage(), "result");
        }
        return this;
    }

    
    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param o the reference object with which to compare
     * @return true if this object is the same as the o argument; 
     * false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Response)) {
            return false;
        }
        Response r = (Response) o;
        boolean flag = true;
        if (r.hashCode() != this.hashCode()) {
            flag = false;
        }
        for (Result res : r.getResultList()) {
            if (!this.getResultList().contains(res)) {
                flag = false;
            }
        }
        return flag && r.getTTL() == this.getTTL() && this.getResponseHost().equals(r.getResponseHost()) && r.getRoutingService().equals(this.getRoutingService()) && r.getMatches() == this.getMatches();
    }

    /**
        * Returns the hash code value for this Response object.
        * The hash code is calculated based on the responseHost,
         resultList, and matches fields.
        *
        * @return the hash code value for this Response object.
        */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getID().hashCode();
        result = 31 * result + getTTL();
        result = 31 * result + getRoutingService().hashCode();
        result = 31 * result + responseHost.hashCode();
        result = 31 * result + resultList.hashCode();
        result = 31 * result + matches;
        return result;
    }

}
