/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package klab.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.nio.ByteBuffer;
public class MessageInput extends Object {
    private DataInputStream in;
    public MessageInput(InputStream in) throws NullPointerException { 
        //constructor
            if (in == null) { //check to see if in is null
                throw new NullPointerException("in is null"); //throw exception
            }
            this.in = new DataInputStream(in); //set in to new DataInputStream
    }

    public InputStream getIn() { //getter
        try {
            return in; //return in
        } catch (Exception E) { //the just in case
            throw new NullPointerException("in is null"); //throw exception
        }
    }
    public byte[] readBytes(int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > in.available()) {
            throw new IndexOutOfBoundsException
            ("Offset or length are out of bounds");
        } //input sanitization
        byte[] bytes = new byte[len];
        in.read(bytes, off, len);
        return bytes;
    }

    public long readUnsignedInt() throws NullPointerException{
        try {
            byte[] bytes = new byte[4];
            in.readNBytes(bytes, 0, 4);
            long result = ByteBuffer.wrap(bytes).getInt() & 0xFFFFFFFFL;
            // basically convert the byte array to an long, 
            // but it is removed of sign
            // Convert to unsigned long
            //System.out.println(result);
            return result;
        } catch (IOException e) {
            throw new NullPointerException("Invalid Bytes");
        }
    }

    public byte[] readFourBytes() throws IOException {
        try {
            byte[] bytes = new byte[4];
            in.readNBytes(bytes, 0, 4);
            return bytes;
        } catch (IOException e) {
            throw new IOException("Invalid Bytes");
        }
    }

    public byte readByte() throws IOException {
        try {
            return in.readByte();
        } catch (IOException e) {
            throw new IOException("Invalid Bytes");
        }
    }

    public String readString() throws IOException {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int readBytes;
            boolean flag = false;
            while ((readBytes = in.read()) != -1) {
                if ((char) readBytes == '\n') {
                    flag = true;
                    break;
                }
                bytes.write(readBytes);
            }
            if (bytes.size() == 0) {
                throw new IOException("No bytes to read");
            }
            if (bytes.toByteArray()[bytes.size() - 1] == -1) {
                throw new IOException("Premature EOS");
            }
            if (!flag) {
                throw new IOException("No newline");
            }
            return bytes.toString();
            
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
