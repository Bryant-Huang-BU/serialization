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
}
