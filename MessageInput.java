/*
 * Name: Bryant Huang
 * Project 0
 */

package serialization;

import java.io.IOException;
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

    public InputStream getIn() { //getter
            return in; //return in
    }

    /*public Long readLong(int off) throws IOException {
            Byte[] bytes = new Byte[4]; //create byte array
            in.read(bytes, off, sizeof(bytes));
            //convert bytes to long

            if (bytes == null) { //check to see if bytes is null
                    throw new NullPointerException("bytes is null"); //throw exception
            }
            return bytes; //return bytes
    }*/

}
