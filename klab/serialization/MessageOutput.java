/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package klab.serialization;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Object;
public class MessageOutput extends Object{
    private OutputStream out;
        
        /**
         * Constructs a new MessageOutput object with the specified output stream.
         * 
         * @param out the output stream to write messages to
         * @throws NullPointerException if the output stream is null
         */
        public MessageOutput(OutputStream out) throws NullPointerException {
            try {
                if (out == null) {
                    throw new NullPointerException("out is null");
                }
                this.out = out;
            } catch (NullPointerException e) {
                throw new NullPointerException("out is null");    
            }
        }

    /**
     * Writes the specified byte array to the output stream.
     * 
     * @param bytes the byte array to be written
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the byte array is null
     */
    public void writeBytes(byte[] bytes)
     throws IOException, NullPointerException {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        if (bytes.length == 0) {
            return;
        }
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new IOException("Bad Write Function");
        }
    }
}
