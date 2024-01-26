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

    public bytes[] readBytes(byte[] bytes, int off, int len) throws IOException {
            if (bytes == null) { //check to see if bytes is null
                    throw new NullPointerException("bytes is null"); //throw exception
            }
            if (off < 0) { //check to see if off is less than 0
                    throw new IndexOutOfBoundsException("off is less than 0"); //throw exception
            }
            if (len < 0) { //check to see if len is less than 0
                    throw new IndexOutOfBoundsException("len is less than 0"); //throw exception
            }
            if (len > bytes.length - off) { //check to see if len is greater than bytes.length - off
                    throw new IndexOutOfBoundsException("len is greater than bytes.length - off"); //throw exception
            }
            if (len == 0) { //check to see if len is 0
                    return 0; //return 0
            }
            int n = in.read(bytes, off, len); //read bytes
            if (n < 0) { //check to see if n is less than 0
                    return -1; //return -1
            }
            return n; //return n
    }
}
