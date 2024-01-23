/*
 * Name: Bryant Huang
 * Project 0
 */

package serialization;

import java.io.InputStream;
import java.lang.Object;
public class MessageInput extends Object {
    private InputStream in;
    public MessageInput(InputStream in) throws NullPointerException { //constructo
            if (in == null) { //check to see if in is null
                    throw new NullPointerException("in is null"); //throw exception
            }
            this.in = in; //set in to in
    }



}
