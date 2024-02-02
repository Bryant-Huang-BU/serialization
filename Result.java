/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
*
************************************************/

package serialization;
import java.io.IOException;
import java.lang.Object;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Result extends Object {
    private byte[] fileID;
    private long fileSize;
    private String fileName;
    public Result(MessageInput in) throws IOException,
            BadAttributeValueException {
        try{
        if (in == null) {
            throw new IOException("in is null");
        }
        byte[] wholeByte = in.readAllBytes();
        for (byte b : wholeByte) {
            if (b < 0) {
                throw new IOException("Invalid Bytes");
            }
            System.out.print(b + ", ");
        }
        System.out.print("\n");
        int off = 0;
        byte[] fileID = new byte[4];
        System.arraycopy(wholeByte, off, fileID, 0, 4);
        setFileID(fileID);
        off += 4;
        byte[] fileSize = new byte[4];
        System.arraycopy(wholeByte, off, fileSize, 0, 4);
        setFileSize(byteToUnsignedInt(fileSize));
        off += 4;
        StringBuilder fileName = new StringBuilder();
        System.out.println("off: " + off + " " + " length: " + wholeByte.length);
        while (off < wholeByte.length) {
            //System.out.println((char) wholeByte[off]);
            if ((char) wholeByte[off] == '\n') {
                //System.out.println("found new line");
                break;
            }
            System.out.println("off: " + off + " " + (char) wholeByte[off]);
            fileName.append((char) wholeByte[off]);
            off++;
            if (off == wholeByte.length) {
                throw new BadAttributeValueException("No new line", "fileName");
            }
        }
        if (fileName.isEmpty()) {
            throw new BadAttributeValueException("No FileName", "fileName");
        }
        //System.out.println("made it through no issue");
        setFileName(fileName.toString());
    } catch(Exception e) {
        e.printStackTrace();
        if (!e.getMessage().isEmpty()) {
            throw new IOException(e.getMessage());
        }
        throw new IOException("Invalid Bytes");
    }
    }

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
    public void encode(MessageOutput out) throws IOException {
        try {
            out.getOut().write(fileID, 0, 4);
            out.getOut().write(longToBytes(), 0, 4);
            out.getOut().write(fileName.getBytes(StandardCharsets.UTF_8),0, 
            fileName.length());
            out.getOut().write('\n');
        }
        catch (Exception E) {
            if (E.getMessage() != null) {
                throw new IOException(E.getMessage());
            }
            
        }
        //write file information into output stream
    }
    @Override
    public String toString() {
        String fileID = "";
        
        return "Result: FileID=" + printBytesInHex(getFileID()) + "FileSize=" + getFileSize() + "bytes FileName=" + getFileName();

    }
    private String printBytesInHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    public byte[] getFileID() {
        return fileID;
    }
    public final Result setFileID(byte[] i) throws BadAttributeValueException {
        //check to see if fileID isn't empty
        if (i == null) {
            throw new BadAttributeValueException("fileID is null", "fileID");
        }
        if (i.length != 4) {
            throw new BadAttributeValueException("fileID is too big", "fileID");
        }
        //if filled with something valid, set fileID to parameter
        this.fileID = i;
        return this;
    }
    public long getFileSize() {
        return fileSize;
    }
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
    public String getFileName() {
        return fileName;
    }
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

    private long byteToUnsignedInt(byte[] bytes) throws NullPointerException {
        if (bytes == null) {
            throw new NullPointerException("byte array is null");
        }
        long result = ByteBuffer.wrap(bytes).getInt() & 0xFFFFFFFFL;
        // basically convert the byte array to an long, 
        // but it is removed of sign
        // Convert to unsigned long
        //System.out.println(result);
        return result;
    }
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

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + fileID.hashCode();
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + fileName.hashCode();
        return result;
    }
}
