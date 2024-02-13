/************************************************
* Author: Bryant Huang
* Assignment: Program 1
* Class: CSI4321
************************************************/

package klab.serialization;
import java.util.Arrays;
/**
 * Represents a routing service.
 * 
 * This enum class defines different types of routing services,
 * such as BREADTHFIRST and DEPTHFIRST.
 * Each routing service has an associated code.
 */
public enum RoutingService {
    
    /**
     * Represents a routing service.
     * 
     * This enum class defines different types of routing services,
     * such as BREADTHFIRST and DEPTHFIRST.
     * Each routing service has an associated code.
     */
    BREADTHFIRST(0), DEPTHFIRST(1);

    private int code;

    /**
     * Represents a routing service.
     * @param code the code for the routing service3
     */
    private RoutingService(int code) {
        this.code = code;
    }
    
    /**
     * Returns the code associated with this RoutingService.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }
    /*
     * Returns the RoutingService associated with the given code.
     * @param code the code
     * @return RoutingService
     * @throws BadAttributeValueException if the code is invalid
     */
    public static RoutingService getRoutingService(int code)
    throws BadAttributeValueException {
        //System.out.println(code);
        return Arrays.stream(values())
            .filter(r -> code == r.getCode())
            .findFirst()
            .orElseThrow(() -> 
            new BadAttributeValueException(
                "Invalid code", "code"));
    }
}
