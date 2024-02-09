package klab.serialization;

import java.lang.constant.ClassDesc;

public enum RoutingService{
    
    BREADTHFIRST(0), DEPTHFIRST(1);

    private int code;

    private RoutingService(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }

    public static RoutingService getRoutingService(int code) throws BadAttributeValueException {
        if (code == 0) {
            return BREADTHFIRST;
        } else if (code == 1) {
            return DEPTHFIRST;
        }
        throw new BadAttributeValueException("Invalid code", "code");
    }
}
