/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package klab.serialization;
<<<<<<< HEAD
public class BadAttributeValueException extends Exception {
    private final String attribute;
=======

import java.io.Serial;
import java.io.Serializable;

public class BadAttributeValueException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = -8352932632237503193L;
    private String attribute;
>>>>>>> b2aedf626751cc0a3bb9f2f307725f90acc1c27e
    /**
     * Constructs a new BadAttributeValueException with the specified message and attribute.
     *
     * @param message   the detail message (which is saved for later retrieval by the getMessage() method)
     * @param attribute the attribute associated with the bad value
     * @throws NullPointerException if either the message or attribute is null
    */
    public BadAttributeValueException(String message, String attribute) throws NullPointerException {
        super(message);
        try {
            if (message == null || attribute == null) {
                throw new NullPointerException();
            }
            // construct object
            this.attribute = attribute;
        } catch (NullPointerException e) {
            throw new NullPointerException("message or attribute is null");
        }
    }
    /**
     * Returns the attribute associated with this exception.
     *
     * @return the attribute associated with this exception
     */
    public String getAttribute() {
        return this.attribute;
    }
    /**
     * This exception is thrown when a bad attribute value is encountered during serialization.
     * It extends the {@link java.lang.Exception} class
     * 
     * @param message the message to be displayed
     * @param attribute the attribute that caused the exception
     * @param cause the cause of the exception
     * @throws NullPointerException if message, attribute, or cause is null
     */
    public BadAttributeValueException
    (String message, String attribute, Throwable cause) 
    throws NullPointerException {
        super(message, cause);
        try {
            if (message == null || attribute == null) {
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