/*
 * Name: Bryant Huang
 * Project 0
 */

package serialization;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Object;
public class MessageOutput extends Object{
    private OutputStream out;
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
    
    public OutputStream getOut() {
        try {
            if (out == null) {
                throw new NullPointerException("out is null");
            }
            return out;
        } catch (Exception e) {
            throw new NullPointerException("out is null");
        }
    }


    public void writeBytes(byte[] bytes) throws IOException {
        try {
            if (bytes == null) {
                throw new NullPointerException("bytes is null");
            }
            if (bytes.length == 0) {
                return;
            }
            out.write(bytes);
        } catch (IOException e) {
            throw new IOException("Bad Write Function");
        }

    }
}
