/************************************************
* Author: Bryant Huang
* Assignment: Program 1
* Class: CSI4321
************************************************/

package klab.serialization;

import java.util.Arrays;
import java.util.Objects;

/**
 * The Search class represents a search operation in the system.
 * It extends the Message class and encapsulates the search string,
 * time-to-live value, and routing service to be used.
 */
public class Search extends Message {
    private String searchString;

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
        this.searchString = searchString; //avoid error
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
        return "Search: ID=" + this.displayBytes() + " TTL=" + getTTL() +
         " Routing=" + getRoutingService() + " Search=" + getSearchString();
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
        if (searchString.length() > 65535) {
            throw new klab.serialization.BadAttributeValueException
            ("searchString is too big", "searchString");
        }
        //only alphanumeric characters and spaces, only -, _, . are allowed
        if (!searchString.matches("[a-zA-Z0-9._-]*")){
            throw new BadAttributeValueException(
                "fileName is invalid", "fileName");
        }
        this.searchString = searchString;
        return this;
    }
    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Search)) return false;
    Search search = (Search) o;
    return getTTL() == search.getTTL() &&
        getRoutingService() == search.getRoutingService() &&
        Arrays.equals(getID(), search.getID()) &&
        Objects.equals(getSearchString(), search.getSearchString());
}

@Override
public int hashCode() {
    int result = Objects.hash(getID(), getSearchString(), getRoutingService());
    result = 31 * result + getTTL();
    return result;
    }
}
