/*
 * Name: Bryant Huang
 * Project 0
 */
package serialization;
import java.lang.*;
public class BadAttributeValueException extends Exception{
    private String attribute;
    private String message;
    private Throwable cause;
    public BadAttributeValueException(String message, String attribute) throws NullPointerException {
        if (message == null || attribute == null) {
            throw new NullPointerException();
        }
        //construct object
        this.attribute = attribute;
        this.message = message;
    }
    public String getAttribute() {
        return this.attribute;
    }
    public BadAttributeValueException(String message, String attribute, Throwable cause) throws NullPointerException {
        if (message == null || attribute == null || cause == null) {
            throw new NullPointerException();
        }
        //construct object
        this.attribute = attribute;
        this.message = message;
        this.cause = cause;
    }

}
