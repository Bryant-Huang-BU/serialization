/************************************************
* Author: Bryant Huang
* Assignment: Program 4
* Class: CSI4321
************************************************/

package metanode.serialization;

/**
 * Represents the type of error.
 */
public enum ErrorType {
    None(0), System(10), 
    IncorrectPacket(20);
    private final int code;

    /**
     * Constructs a new ErrorType with 
     * the specified code.
     * @param code the error code
     */
    ErrorType(int code) {
        this.code = code;
    }

    /**
     * Returns the code associated 
     * with this ErrorType.
     * @return the error code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Returns the ErrorType associated with 
     * the specified code.
     * @param code the error code
     * @return the ErrorType, or null
     * if no matching ErrorType is found
     */
    public static ErrorType getByCode(int code) {
        switch (code) {
            case 0:
                return None;
            case 10:
                return System;
            case 20:
                return IncorrectPacket;
            default:
               return null;
        }
    }
}
