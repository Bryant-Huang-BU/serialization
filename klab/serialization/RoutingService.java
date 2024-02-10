package klab.serialization;

import java.lang.constant.ClassDesc;
import java.util.Arrays;
import java.util.Comparator;

public enum RoutingService{
    
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

    public static RoutingService getRoutingService(int code)
    throws BadAttributeValueException {
        System.out.println(code);
        return Arrays.stream(values())
            .filter(r -> code == r.getCode())
            .findFirst()
            .orElseThrow(() -> 
            new BadAttributeValueException("Invalid code", "code"));
    }
}
