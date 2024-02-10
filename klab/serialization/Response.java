package klab.serialization;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Response extends Message {
    InetSocketAddress responseHost;
    List<Result> resultList;
    int matches;
    Response(byte[] msgID, int ttl, RoutingService routingService, InetSocketAddress responseHost) throws BadAttributeValueException {
        super(msgID, ttl, routingService);
        try {
            if (msgID == null || routingService == null || responseHost == null) {
                throw new BadAttributeValueException("msgID, routingService, or responseHost is null", "msgID, routingService, responseHost");
            }
            setResponseHost(responseHost);
    } catch (BadAttributeValueException e) {
        throw new BadAttributeValueException("msID, routingService, or responseHost is null", "msgID, routingService, responseHost");
    }
}

    public int getMatches() {
        return matches;
    }
    /*public String toString() {
        return "Response: ID=" + msgID + " TTL=" + ttl + " Routing=" + " ResponseHost=" + responseHost;
    }*/

    public InetSocketAddress getResponseHost() {
        return this.responseHost;
    }

    public Response setResponseHost(InetSocketAddress responseHost)
        throws BadAttributeValueException {
        try {
            if (responseHost == null) {
                throw new BadAttributeValueException("responseHost is null", "responseHost");
            }
            this.responseHost = responseHost;
            return this;
        } catch (BadAttributeValueException e) {
            throw new BadAttributeValueException(e.getMessage(), "responseHost");
        }
    }

    public List<Result> getResultList() {
        return this.resultList;
    }

    public Response setResultList(List<Result> resultList)
        throws BadAttributeValueException {
        try {
            if (resultList == null) {
                throw new BadAttributeValueException("resultList is null", "resultList");
            }
            this.resultList = resultList;
            return this;
        } catch (BadAttributeValueException e) {
            throw new BadAttributeValueException(e.getMessage(), "resultList");
        }
    }

    public void setMatches(int matches2) throws BadAttributeValueException {
        if (matches2 < 0) {
            throw new BadAttributeValueException("matches is less than 0", "matches");
        }
        this.matches = matches2;
    }
}
