/*
 * Name: Bryant Huang
 * Project 0
 */
package serialization;
import java.io.Serializable;
import java.lang.*;
public class BadAttributeValueException extends Exception implements Serializable {
    private String attribute;
    public BadAttributeValueException(String message, String attribute) throws NullPointerException {
        super(message);
        try {
            if (message == null || attribute == null) {
                throw new NullPointerException();
            }
            //construct object
            this.attribute = attribute;
        } catch (NullPointerException e) {
            throw new NullPointerException("message or attribute is null");
        }
    public String getAttribute() {
        return this.attribute;
    }
    public BadAttributeValueException(String message, String attribute, Throwable cause) throws NullPointerException {
        super(message, cause);
        try {
            if (message == null || attribute == null || cause == null) {
                throw new NullPointerException();
            }
            //construct object
            this.attribute = attribute;
        } catch (NullPointerException e) {
            throw new NullPointerException("message, attribute, or cause is null");
        }
    }  
}