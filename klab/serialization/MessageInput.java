/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package klab.serialization;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.nio.ByteBuffer;
/**
 * The MessageInput class represents an input stream for reading messages.
 * It provides methods for reading bytes, unsigned integers, 
 * four bytes, bytes, and strings.
 */
public class MessageInput extends Object {
    private BufferedInputStream in; 
    /**
     * Constructs a new MessageInput object with the specified input stream.
     * 
     * @param in the input stream to read messages from
     * @throws IOException if the input stream is null
     */
    public MessageInput(InputStream in) throws NullPointerException { 
        //constructor
            if (in == null) { //check to see if in is null
                throw new NullPointerException("in is null"); //throw exception
            }
            this.in = new BufferedInputStream(in); //set in to new DataInputStream
    }
    

    /**
     * Checks if there is data available to be read from the input stream.
     *
     * @return {@code true} if there is data available, {@code false} otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean isAvail() throws IOException {
        return in.available() > 0;
    }
    /**
     * Reads a specified number of bytes from the input 
     * stream starting at the given offset.
     * 
     * @param len the number of bytes to read
     * @return an array of bytes containing the read data
     * @throws IOException if an I/O error occurs
     * @throws IndexOutOfBoundsException idf the offset 
     * or length are out of bounds
     */
    public byte[] readBytes(int len) throws IOException {
        try {
            //check if stream ends unexpectdly
            if (len < 0) {
                throw new IOException("Length is less than 0");
            } 
            byte[] bytes = new byte[len];
            int readBytes = 0;
            int chunkSize = 1024;
            int remainingBytes = len;
            while (remainingBytes > 0) {
                int chunkSizeToRead = Math.min(remainingBytes, chunkSize);
                int bytesRead = in.read(bytes, readBytes, chunkSizeToRead);
                if (bytesRead < 0) {
                    throw new IOException("End of stream reached");
                }
                readBytes += bytesRead;
                remainingBytes -= bytesRead;
            }
            if (remainingBytes != 0) {
                throw new IOException("Invalid Bytes");
            }
            //in.flush();
            return bytes;
        }
            //input sanitization
            /*
            byte[] bytes = in.readNBytes(len);
            //System.out.println(len);
            /*for (byte b : bytes) {
                System.out.print(b);
            }
            if (bytes.length != len) {
                throw new IOException("Invalid Bytes");
            } //input sanitization
            re
            turn bytes;
        }*/
        catch (IOException e) {
            throw new IOException("Invalid Bytes");
        }
    }

    /**
     * Reads an unsigned integer from the input stream.
     * 
     * @return the unsigned integer value read from the input stream
     * @throws IOException if there are invalid bytes 
     * or an I/O error occurs
     */
    public long readUnsignedInt() throws IOException{
        try {
            byte[] bytes = new byte[4];
            in.readNBytes(bytes, 0, 4);
            return(ByteBuffer.wrap(bytes).getInt() & 0xFFFFFFFFL);
            //input sanitization
            // basically convert the byte array to an long, 
            // but it is removed of sign
            // Convert to unsigned long
            //System.out.println(result);
        } catch (IOException e) {
            throw new IOException("Invalid Bytes");
        }
    }

    /**
     * Reads a string from the input stream.
     *
     * @return the string read from the input stream
     * @throws IOException if an I/O error occurs
    */
    public byte[] readString(char delim) throws IOException {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int readBytes;
            boolean flag = false;
            while ((readBytes = in.read()) != -1) {
                if ((char) readBytes == delim) {
                    flag = true;
                    break;
                }
                bytes.write((char) readBytes);
                //System.out.println((char) readBytes);
            }
            if (bytes.size() == 0) {
                throw new IOException("No bytes to read");
            }  
            if (!flag) {
                throw new IOException("No delimiter found");
            }
            
            //System.out.println(bytes.toString());
            return bytes.toByteArray();
            
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
     }
}
