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
    public MessageInput(InputStream in) throws NullPointerException { 
        //constructor
            if (in == null) { //check to see if in is null
                throw new NullPointerException("in is null"); //throw exception
            }
            this.in = in; //set in to in
    }

    public InputStream getIn() { //getter
        try {
            return in; //return in
        } catch (Exception E) {
            throw new NullPointerException("in is null"); //throw exception
        }
    }
    public byte[] readBytes(int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > in.available()) {
            throw new IndexOutOfBoundsException
            ("Offset or length are out of bounds");
        }
        byte[] bytes = new byte[len];
        in.read(bytes, off, len);
        return bytes;
    }
    public byte[] readAllBytes() throws IOException {
        try {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return bytes;
        }
        catch (Exception E) {
            throw new IOException("Invalid Bytes");
        }
    }
}
