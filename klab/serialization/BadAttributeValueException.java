/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package klab.serialization;
public class BadAttributeValueException extends Exception {
    private String attribute;
    public BadAttributeValueException(String message, String attribute) 
    throws NullPointerException {
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
    }
    public String getAttribute() {
        return this.attribute;
    }
    public BadAttributeValueException
    (String message, String attribute, Throwable cause) 
    throws NullPointerException {
        super(message, cause);
        try {
            if (message == null || attribute == null || cause == null) {
                throw new NullPointerException();
            }
            //construct object
            this.attribute = attribute;
        } catch (NullPointerException e) {
            throw new NullPointerException
            ("message, attribute, or cause is null");
        }
    }  
}