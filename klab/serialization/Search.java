package klab.serialization;
public class Search extends Message {
    String searchString;
    RoutingService routingService;
    int ttl;
    byte[] msgID;

    public Search(byte[] msgID, int ttl, RoutingService routingService, String searchString) {
        this.searchString = searchString;
        this.routingService = routingService;
        this.ttl = ttl;
        this.msgID = msgID;
    }

    public String getSearchString() {
        return searchString;
    }

    public String toString() {
        String fileID = "";
        return "Search: ID=" + msgID + " TTL=" + ttl + " Routing=" + routingService.ValueOf() + " Search=" + searchString;
    }
    public Search setSearchString(String searchString)
    throws klab.serialization.BadAttributeValueException {
        if (searchString == null) {
            throw new klab.serialization.BadAttributeValueException("searchString is null", "searchString");
        }
        this.searchString = searchString;
        return this;
    }
}
