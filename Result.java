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
        int fileIDLength = in.readInt();
        if (fileIDLength < 0) {
            throw new BadAttributeValueException("fileIDLength is negative", "fileIDLength");
        }
        fileID = new byte[fileIDLength];
        in.readFully(fileID);
        fileSize = in.readLong();
        if (fileSize < 0) {
            throw new BadAttributeValueException("fileSize is negative", "fileSize");
        }
        int fileNameLength = in.readInt();
        if (fileNameLength < 0) {
            throw new BadAttributeValueException("fileNameLength is negative", "fileNameLength");
        }
        byte[] fileNameBytes = new byte[fileNameLength];
        in.readFully(fileNameBytes);
        fileName = new String(fileNameBytes, "UTF-8");
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
        //check to see if output stream is valid
        //if valid, output the attributes of the class in header to the output
        //stream
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



}
