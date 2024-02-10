package klab.serialization;
public class Search extends Message {
    String searchString;
    RoutingService routingService;
    int ttl;
    byte[] msgID;

    public Search(byte[] msgID, int ttl, RoutingService routingService, String searchString) throws BadAttributeValueException {
        super(msgID, ttl, routingService); // Add this line to invoke the super class constructor
        this.searchString = searchString;
        this.routingService = routingService;
        this.ttl = ttl;
        if (msgID.length != 15) {
            throw new BadAttributeValueException("msgID is not 15 bytes", "msgID");
        }
        this.msgID = msgID;
    }

    public String getSearchString() {
        return searchString;
    }

    public String toString() {
        return "Search: ID=" + msgID + " TTL=" + ttl + " Routing=" + routingService.getCode() + " Search=" + searchString;
    }
    public Search setSearchString(String searchString)
    throws klab.serialization.BadAttributeValueException {
        if (searchString == null) {
            throw new klab.serialization.BadAttributeValueException("searchString is null", "searchString");
        }
        this.searchString = searchString;
        return this;
    }

    public RoutingService getRoutingService() {
        return routingService;
    }

    public Search setRoutingService(RoutingService routingService) {
        this.routingService = routingService;
        return this;
    }

    public int getTtl() {
        return ttl;
    }

    public Search setTtl(int ttl) throws BadAttributeValueException {
        if (ttl < 0) {
            throw new BadAttributeValueException("ttl is less than 0", "ttl");
        }
        this.ttl = ttl;
        return this;
    }

    public byte[] getMsgID() {
        return msgID;
    }
    
    /**
     * @param msgID
     * @return
     * @throws BadAttributeValueException
     */
    public Search setMsgID(byte[] msgID) throws BadAttributeValueException {
        if (msgID.length != 15) {
            throw new BadAttributeValueException("msgID is not 15 bytes", "msgID");
        }
        this.msgID = msgID;
        return this;
    }

    public String printBytesInHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
