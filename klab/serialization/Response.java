package klab.serialization;
import java.net.InetSocketAddress;
import java.util.List;

public class Response extends Message {
    byte[] msgID;
    int ttl;
    RoutingService routingService;
    InetSocketAddress responseHost;
    List<Result> resultList;

    Response(byte[] msgID, int ttl, RoutingService routingService, InetSocketAddress responseHost) throws BadAttributeValueException {
        super(msgID, ttl, routingService);
        try {
            if (msgID == null || routingService == null || responseHost == null) {
                throw new BadAttributeValueException("msgID, routingService, or responseHost is null", "msgID, routingService, responseHost");
            }
            setMsgID(msgID);
            setTtl(ttl);
            setRoutingService(routingService);
            setResponseHost(responseHost);
    } catch (BadAttributeValueException e) {
        throw new BadAttributeValueException("msID, routingService, or responseHost is null", "msgID, routingService, responseHost");
    }
}
    
    public int getTtl() {
        return ttl;
    }

    public byte[] getMsgID() {
        return msgID;
    }

    public RoutingService getRoutingService() {
        return routingService;
    }

    public Response setMsgID(byte[] msgID) throws BadAttributeValueException {
        try {
            if (msgID == null) {
                throw new BadAttributeValueException("msgID is null", "msgID");
            }
            this.msgID = msgID;
            return this;
        } catch (BadAttributeValueException e) {
            throw new BadAttributeValueException("msgID is null", "msgID");
        }
    }

    public Response setTtl(int ttl) throws BadAttributeValueException {
        try {
            if (ttl < 0) {
                throw new BadAttributeValueException("ttl is less than 0", "ttl");
            }
            this.ttl = ttl;
            return this;
        } catch (BadAttributeValueException e) {
            throw new BadAttributeValueException(e.getMessage(), "ttl");
        }
    }

    public Response setRoutingService(RoutingService routingService) {
        this.routingService = routingService;
        return this;
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
}
