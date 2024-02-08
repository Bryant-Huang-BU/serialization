package klab.serialization;

import java.lang.constant.ClassDesc;

public enum RoutingService{
    
    BREADTHFIRST(0), DEPTHFIRST(1);
    public static final class Enum.EnumDesc<E extends Enum<E>>
    extends DynamicConstantDesc<E> {
        static <E extends Enum<E>> Enum.EnumDesc<E> throws NullPointerException{
            return new Enum.EnumDesc<E>(RoutingService.class, name);
    }
}
    

    private int code;
    
    private RoutingService(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
