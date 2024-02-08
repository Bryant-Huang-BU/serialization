/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package klab.serialization;
import java.io.IOException;
import java.lang.Object;
import java.nio.charset.StandardCharsets;

public class Result extends Object {
    private byte[] fileID;
    private long fileSize;
    private String fileName;
    /**
     * Represents a Result object that is used for serialization.
     * This class is responsible for reading and initializing the object
     * from a MessageInput stream.
     *
     * @param in The MessageInput stream to read from.
     * @throws IOException If an I/O error occurs while reading from the stream.
     * @throws BadAttributeValueException If the attribute value is invalid.
     */
    public Result(MessageInput in) throws IOException,
            BadAttributeValueException {
        try{
        if (in == null) {
            throw new IOException("in is null");
        }
        //byte[] bytes = in.readAllBytes();
        /*for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i]);
        }*/
        int off = 0;
        //get starting 4 bytes of byte array
        byte[] buf = in.readFourBytes();
        setFileID(buf);
        //buf = getFileID();
        /*for (int i = 0; i < 4; i++) {
            System.out.println(buf[i]);
        }*/
        //get next 4 bytes of byte array
        setFileSize(in.readUnsignedInt());
        //System.out.println(bytes.length);
        String x = in.readString();
        //System.out.println(x);
        setFileName(x);
        if (this.fileName == null) {
            throw new IOException("Invalid Bytes");
        }
        //System.out.println("made it through no issue");
    } catch(Exception e) {
        e.printStackTrace();
        if (!e.getMessage().isEmpty()) {
            throw new IOException(e.getMessage());
        }
        throw new IOException("Invalid Bytes");
    }
}

    /**
     * Represents a result object that stores file information.
     * 
     * @param fileID the file ID
     * @param fileSize the size of the file
     * @param fileName the name of the file
     * @throws BadAttributeValueException
     */
    public Result(byte[] fileID, long fileSize, String fileName)
        throws BadAttributeValueException {
        setFileID(fileID);
        setFileSize(fileSize);
        setFileName(fileName);
        //grab file information from parameters
        //check to see if the data is good to store
        //if not, do not accept information
        //sort into binary,  store into object

    }
    /**
     * Encodes the file information and writes it into the output stream.
     * 
     * @param out the output stream to write the encoded file information to
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
    public void encode(MessageOutput out) throws IOException {
        try {
            out.writeBytes(fileID);
            out.writeBytes(longToBytes());
            out.writeBytes(fileName.getBytes(StandardCharsets.US_ASCII));
            out.writeBytes("\n".getBytes());
        }
        catch (Exception E) {
            if (E.getMessage() != null) {
                throw new IOException(E.getMessage());
            }
            
        }
        //write file information into output stream
    }
    /**
     * Returns a string representation of the Result object.
     * The string includes the FileID in hexadecimal format, the FileSize in bytes, and the FileName.
     *
     * @return a string representation of the Result object.
     */
    @Override
    public String toString() {
        return "Result: FileID=" + printBytesInHex(getFileID()) + "FileSize=" + getFileSize() + "bytes FileName=" + getFileName();

    }
    private String printBytesInHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    /**
     * Returns the file ID associated with this Result.
     *
     * @return the file ID as a byte array
     */
    public byte[] getFileID() {
        /*for (int i = 0; i < fileID.length; i++) {
          System.out.println(fileID[i]);
        }*/
        return fileID;
    }
    /**
     * Sets the file ID.
     * 
     * @param i the file ID to set
     * @return the updated Result object
     * @throws BadAttributeValueException if the file ID is null or too big
    */
    public final Result setFileID(byte[] i) throws BadAttributeValueException {
        //check to see if fileID isn't empty
        if (i == null) {
            throw new BadAttributeValueException("fileID is null", "fileID");
        }
        if (i.length != 4) {
            throw new BadAttributeValueException("fileID is too big", "fileID");
        }
        //if filled with something valid, set fileID to parameter
        /*for (int j = 0; j < i.length; j++) {
            System.out.println(i[j]);
        }*/
        this.fileID = i;
        return this;
    }
    /**
     * Returns the size of the file.
     *
     * @return the size of the file
     */
    public long getFileSize() {
        return fileSize;
    }
    /**
     * Sets the file size for the result.
     * 
     * @param fileSize the file size to be set
     * @return the updated Result object
     * @throws BadAttributeValueException if the file name is null or invalid
     */
    public final Result setFileSize(long fileSize)
        throws BadAttributeValueException {
        if (fileSize < 0) {
            throw
            new BadAttributeValueException
            ("fileSize is negative", "fileSize");
        }
        if (fileSize > 0xFFFFFFFFL) {
            throw new BadAttributeValueException
            ("fileSize is too large", "fileSize");
        }
        //check to see if fileSize is valid
        //if filled with something valid, set filesize to parameter
        this.fileSize = fileSize;
        return this;
    }
    /**
     * Returns the file name associated with this Result.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
        
    /**
     * Sets the file name for the result.
     * 
     * @param fileName the file name to be set
     * @return the updated Result object
     * @throws BadAttributeValueException if the file name is null or invalid
     */
    public final Result setFileName(String fileName)
        throws BadAttributeValueException {
        //check to see if fileSize exists
        if (fileName == null) {
            throw
            new BadAttributeValueException("fileName is null", "fileName");
        }
        if (!fileName.matches("^[a-zA-Z0-9._-]+$")) { //regex for valid filename
            throw new BadAttributeValueException(
            "fileName is invalid", "fileName");
        }
        //if filled with something valid, set filesize to parameter
        this.fileName = fileName;
        //System.out.println(this.fileName);
        return this;
    }
    /*private int byteToInt(byte[] bytes) {
        int result = 0;
        System.out.println(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            result = result | (bytes[i] << (i * 8));
            System.out.println(result);
        }
        return result;
    }*/
    /**
     * Converts the fileSize to a byte array representation.
     * Throws an IllegalArgumentException if the fileSize is out of range for uint32.
     * 
     * @return The byte array representation of the fileSize.
     */
    private byte[] longToBytes () {
        if (fileSize < 0 || fileSize > 0xFFFFFFFFL) {
            throw new IllegalArgumentException
            ("Value out of range for uint32: " + fileSize);
        }
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (fileSize >> 24);
        bytes[1] = (byte) (fileSize >> 16);
        bytes[2] = (byte) (fileSize >> 8);
        bytes[3] = (byte) (fileSize);
        return bytes;
    }
    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the {@code o} argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Result)) {
            return false;
        }
        Result r = (Result) o;
        return r.getFileID().equals(this.getFileID()) && r.getFileSize() == this.getFileSize() && r.getFileName().equals(this.getFileName());
    }

    /**
        * Returns the hash code value for this Result object.
        * The hash code is calculated based on the fileID, fileSize, and fileName fields.
        *
        * @return the hash code value for this Result object
        */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + fileID.hashCode();
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + fileName.hashCode();
        return result;
    }
}
