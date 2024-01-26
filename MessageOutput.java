/*
 * Name: Bryant Huang
 * Project 0
 */

package serialization;
import java.io.OutputStream;
import java.lang.Object;
public class MessageOutput extends Object{
    private OutputStream out;
    public MessageOutput(OutputStream out) throws NullPointerException {
        if (out == null) {
            throw new NullPointerException("out is null");
        }
        this.out = out;
    }
    
    public getOut() {
        return out;
    }

    public void writeBytes(byte[] bytes, int off, int len) throws IOException {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        if (off < 0) {
            throw new IndexOutOfBoundsException("off is less than 0");
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("len is less than 0");
        }
        if (len > bytes.length - off) {
            throw new IndexOutOfBoundsException("len is greater than bytes.length - off");
        }
        if (len == 0) {
            return;
        }
        out.write(bytes, off, len);
    }
}
