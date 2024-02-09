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
/**
 * The MessageInput class represents an input stream for reading messages.
 * It provides methods for reading bytes, unsigned integers, four bytes, bytes, and strings.
 */
public class MessageInput extends Object {
    private DataInputStream in; 
    /**
     * Constructs a new MessageInput object with the specified input stream.
     * 
     * @param in the input stream to read messages from
     * @throws NullPointerException if the input stream is null
     */
    public MessageInput(InputStream in) throws NullPointerException { 
        //constructor
            if (in == null) { //check to see if in is null
                throw new NullPointerException("in is null"); //throw exception
            }
            this.in = new DataInputStream(in); //set in to new DataInputStream
    }

    /**
     * Reads a specified number of bytes from the input stream starting at the given offset.
     * 
     * @param off the starting offset in the input stream
     * @param len the number of bytes to read
     * @return an array of bytes containing the read data
     * @throws IOException if an I/O error occurs
     * @throws IndexOutOfBoundsException if the offset or length are out of bounds
     */
    public byte[] readBytes(int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > in.available()) {
            throw new IndexOutOfBoundsException
            ("Offset or length are out of bounds");
        } //input sanitization
        byte[] bytes = new byte[len];
        in.read(bytes, off, len);
        return bytes;
    }

    /**
     * Reads an unsigned integer from the input stream.
     * 
     * @return the unsigned integer value read from the input stream
     * @throws NullPointerException if there are invalid bytes or an I/O error occurs
     */
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

    /**
     * Reads four bytes from the input stream.
     *
     * @return an array of four bytes read from the input stream.
     * @throws IOException if an I/O error occurs.
     */
    public byte[] readFourBytes() throws IOException {
            byte[] bytes = new byte[4];
            in.readNBytes(bytes, 0, 4);
            if (bytes.length != 4) {
                throw new IOException("Invalid Bytes");
            }
            return bytes;
    }
    

    /**
     * Reads a string from the input stream.
     *
     * @return the string read from the input stream
     * @throws IOException if an I/O error occurs
     */
    public byte[] readString() throws IOException {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int readBytes;
            boolean flag = false;
            while ((readBytes = in.read()) != -1) {
                if ((char) readBytes == '\n') {
                    flag = true;
                    break;
                }
                bytes.write((char) readBytes);
                //System.out.println((char) readBytes);
            }
            if (bytes.size() == 0) {
                throw new IOException("No bytes to read");
            }
            /*if (bytes.toByteArray()[bytes.size() - 1] == -1) {
                throw new IOException("Premature EOS");
            }*/
            if (!flag) {
                throw new IOException("No newline");
            }
            //System.out.println(bytes.toString());
            return bytes.toByteArray();
            
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
