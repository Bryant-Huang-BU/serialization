/*
 * Name: Bryant Huang
 * Project 0
 */

package serialization;
import java.io.*;
import java.lang.*;
public class Result extends Object{
    private byte[] fileID;
    private long fileSize;
    private String fileName;
    public Result(MessageInput in) throws IOException, BadAttributeValueException {
        byte[] fileID = new byte[4];
        in.readBytes(fileID, 0, 4);
        setFileID(byteToInt(fileID));
        byte[] fileSize = new byte[8];
        in.readBytes(fileSize, 4, 8);
        setFileSize(byteToLong(fileSize));
        int x;
        String fileName = new String("");
        while (x = in.readBytes(fileName, 0, 4) != -1) {
            fileName += (char) x;
        }
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
        dataOut.writeLong(fileSize);
        dataOut.writeInt(fileName.length());
        dataOut.write(fileName.getBytes("UTF-8"));
    }
    @Override
    public String toString() {
        return "Result{" +
                "fileID=" + fileID +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                '}';
    }
    public byte[] getFileID() {
        return fileID;
    }
    public final Result setFileID(byte[] fileID) throws BadAttributeValueException {
        //check to see if fileID isn't empty
        if (fileID == null) {
            throw new BadAttributeValueException("fileID is null", "fileID");
        }
        //if filled with something valid, set fileID to parameter
        this.fileID = fileID;
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
        return this;
    }
    private int byteToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = result | (bytes[i] << (i * 8));
        }
        return result;
    }

    private long byteToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = result | (bytes[i] << (i * 8));
        }
        return result;
    }
}
