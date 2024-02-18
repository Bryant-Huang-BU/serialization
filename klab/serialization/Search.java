/************************************************
* Author: Bryant Huang
* Assignment: Program 1
* Class: CSI4321
************************************************/

package klab.serialization;
/**
 * The Search class represents a search operation in the system.
 * It extends the Message class and encapsulates the search string,
 * time-to-live value, and routing service to be used.
 */
public class Search extends Message {
    private String searchString;
    private RoutingService routingService;
    private int ttl;

    /**
     * Constructs a Search object with the specified parameters.
     *
     * @param msgID the message ID as a byte array
     * @param ttl the time-to-live value
     * @param routingService the routing service to use
     * @param searchString the search string to be used
     * @throws BadAttributeValueException if the\
     * specified search string is invalid
     */
    public Search(byte[] msgID, int ttl, RoutingService routingService, 
    String searchString) throws BadAttributeValueException {
        super(msgID, ttl, routingService); 
        // Add this line to invoke the super class constructor
        setType(0);
        setSearchString(searchString);
    }

    /**
     * Returns the search string.
     *
     * @return the search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Returns a string representation of the Search object.
     * The string includes the ID, TTL, routing service, and search string.
     *
     * @return a string representation of the Search object
     */
    public String toString() {
        return "Search: ID=" + displayBytes() + " TTL=" + getTTL() +
         " Routing=" + routingService + " Search=" + searchString;
    }

    /**
     * Returns a string representation of the bytes in the ID array.
     * If a byte is a single digit, a leading zero is added.
     * The representation is in decimal format.
     *
     * @return the string representation of the bytes in the ID array
     */
    private String displayBytes() {
        StringBuilder sb = new StringBuilder();
        //if double digit, need a 0 in front
        for (byte b : getID()) {
            if (b < 10) {
                sb.append("0");
            }
            //show decimal, not hex
            sb.append(b);
        }
        return sb.toString();
    }


    /**
     * Sets the search string.
     * 
     * @param searchString the search string to be set
     * @return the Search object itself
     * @throws klab.serialization.BadAttributeValueException 
     * if the search string is null or invalid
    */
    public Search setSearchString(String searchString)
    throws klab.serialization.BadAttributeValueException {
        if (searchString == null) {
            throw new klab.serialization.BadAttributeValueException
            ("searchString is null", "searchString");
        }
        //only alphanumeric characters and spaces, only -, _, . are allowed
        if (!searchString.matches("[a-zA-Z0-9._-]+")) {
            throw new BadAttributeValueException(
            "fileName is invalid", "fileName");
            //System.out.println("fileName is invalid");
        }
        this.searchString = searchString;
        return this;
    }

    /**
     * Returns the routing service.
     *
     * @return the routing service
     */
    public RoutingService getRoutingService() {
        return routingService;
    }

        
    /**
     * Sets the routing service for the search operation.
     * 
     * @param routingService the routing service to be set
     * @return the updated Search object
     */
    public Search setRoutingService(RoutingService routingService) {
        this.routingService = routingService;
        return this;
    }

    /*public static String printBytesInHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }*/
}
