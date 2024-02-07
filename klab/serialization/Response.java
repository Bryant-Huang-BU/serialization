package klab.serialization;
import java.net.InetSocketAddress;
import java.util.List;

public class Response {
    byte [] msgID;
    int ttl;
    RoutingService routingService;
    InetSocketAddress responseHost;
    List<Result> resultList;

    Response(byte[] msgID, int ttl, RoutingService routingService, InetSocketAddress responseHost )throws BadAttributeValueException {
        try {
            if (msgID == null || routingService == null || responseHost == null) {
                throw new BadAttributeValueException("msgID, routingService, or responseHost is null", "msgID, routingService, responseHost");
            }
        this.msgID = msgID;
        this.ttl = ttl;
        this.routingService = routingService;
        this.responseHost = responseHost;    
    } catch (BadAttributeValueException e) {
        throw new BadAttributeValueException("msgID, routingService, or responseHost is null", "msgID, routingService, responseHost");
    }
}
    
    public String toString() {
        return "Response: ID=" + msgID + " TTL=" + ttl + " Routing=" + " ResponseHost=" + responseHost;
    }

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
            throw new BadAttributeValueException("responseHost is null", "responseHost");
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
            throw new BadAttributeValueException("resultList is null", "resultList");
        }
    }
}
