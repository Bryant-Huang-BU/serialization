/*
 * Name: Bryant Huang
 * Project 0
 */
package serialization;
import java.io.*;
import java.lang.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Result extends Object{
    private byte[] fileID;
    private long fileSize;
    private String fileName;
    public Result(MessageInput in) throws IOException, BadAttributeValueException {
        if (in == null) {
            throw new NullPointerException("in is null");
        }
        if (in.getIn().available() < 9) {
            throw new IOException("Not Big Enough");
        }
        byte[] wholeByte = in.readAllBytes();
        int off = 0;
        int len = 4;
        byte[] fileID = new byte[len];
        System.arraycopy(wholeByte, off, fileID, 0, len);
        setFileID(fileID);
        off += len;
        byte[] fileSize = new byte[len];
        System.arraycopy(wholeByte, off, fileSize, 0, len);
        /*for (byte b : wholeByte) {
            System.out.print(b);
        }
        System.out.print('\n');
        for (byte b : fileSize) {
            System.out.print(b);
        }
        System.out.print('\n');*/
        setFileSize(byteToUnsignedInt(fileSize));
        off += len;
        StringBuilder fileName = new StringBuilder();
        while (off < wholeByte.length) {
            System.out.println((char) wholeByte[off]);
            if ((char) wholeByte[off] == '\n') {
                break;
            }
            fileName.append((char) wholeByte[off]);
            off++;
            if (off == wholeByte.length) {
                throw new IOException("No new line");
            }
        }
        if (fileName.isEmpty()) {
            throw new IOException("No FileName");
        }
        setFileName(fileName.toString());
    }

    public Result(byte[] fileID, long fileSize, String fileName) throws BadAttributeValueException {
        setFileID(fileID);
        setFileSize(fileSize);
        setFileName(fileName);
        //grab file information from parameters
        //check to see if the data is good to store
        //if not, do not accept information
        //sort into binary,  store into object

    }
    public void encode(MessageOutput out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out.getOut());
        dataOut.writeInt(fileID.length);
        dataOut.write(fileID);
        //dataOut.writeInt(fileSize); TODO
        dataOut.write(fileName.getBytes("UTF-8"));
        dataOut.write('\n');
    }
    @Override
    public String toString() {
        String fileID = "";
        for (byte b : this.fileID) {
            fileID += b;
        }
        return "Result{" + "fileID=" + fileID + ", fileSize=" + fileSize +
                ", fileName=" + fileName +
                '}';
    }
    public byte[] getFileID() {
        return fileID;
    }
    public final Result setFileID(byte[] i) throws BadAttributeValueException {
        //check to see if fileID isn't empty
        if (i == null) {
            throw new BadAttributeValueException("fileID is null", "fileID");
        }
        //if filled with something valid, set fileID to parameter
        this.fileID = i;
        return this;
    }
    public long getFileSize() {
        return fileSize;
    }
    public final Result setFileSize(long fileSize) throws BadAttributeValueException {
        if (fileSize < 0) {
            throw new BadAttributeValueException("fileSize is negative", "fileSize");
        }
        //check to see if fileSize is valid
        //if filled with something valid, set filesize to parameter
        this.fileSize = fileSize;
        return this;
    }
    public String getFileName() {
        return fileName;
    }
    public final Result setFileName(String fileName) throws BadAttributeValueException {
        //check to see if fileSize is valid
        if (fileName == null) {
            throw new BadAttributeValueException("fileName is null", "fileName");
        }
        //if filled with something valid, set filesize to parameter
        this.fileName = fileName;
        System.out.println(this.fileName);
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

    private long byteToUnsignedInt(byte[] bytes) {
        long result = ByteBuffer.wrap(bytes).getInt() & 0xFFFFFFFFL; // Convert to unsigned long
        //System.out.println(result);
        return result;
    }
}
