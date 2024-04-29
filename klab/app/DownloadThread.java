/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 3
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;

import klab.serialization.BadAttributeValueException;
public class DownloadThread implements Runnable {
    private Socket socket;
    private byte[] fileid;

    /**
     * Represents a thread for downloading a file from a socket connection.
     * 
     * @param socket the socket connection to download the file from
     */
    public DownloadThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Executes the download process in a separate thread. 
     * This method is called when
     * the thread is started. It handles the downloading 
     * and sending of files to the
     * client. If an error occurs during the process, an 
     * error message is sent back
     * to the client.
     */
    @Override
    public void run() {
        try {
            Node.LOGGER.log(Level.INFO, "DownloadThread started for " +
            socket.getRemoteSocketAddress());
            //System.out.println("Starting");
            InputStream in = socket.getInputStream();
            // read the fileid
            if (in == null) {
                throw new Exception("Error reading from socket");
            }
            // Read bytes until a newline character is encountered
            //System.out.println("FileID: ");
            // Read until new line
            int bt = 0;
            StringBuilder sb = new StringBuilder();
            while ((bt = in.read()) != '\n') {
                if (bt == -1) {
                    throw new IOException("Error reading from socket");
                }
                sb.append((char) bt);
            }
            //System.out.println("?");
            if (bt == -1) {
                throw new IOException("Error reading from socket");
            }
            // Convert the read bytes to a string
            //System.out.println("Read line: " + line);
            //System.out.println("out: " + bytesRead);
            String s = sb.toString();
            //String s = sb.toString();
            //go from string to byte array
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
            + Character.digit(s.charAt(i+1), 16));
            }
            this.fileid = data;
            /*for (byte b : fileid) {
                System.out.print(String.format("%02X ", b));
            }*/
            
            //System.out.println(id);
        } catch (Exception e) {
            Node.LOGGER.warning("Error in DownloadThread: " + e.getMessage());
        }
        try {
            byte[] payload;
            Node.LOGGER.log(Level.INFO, "Downloading from: " + 
            socket.getInetAddress().getHostAddress());
            // find the file

            if (fileid == null) {
                Node.LOGGER.log(Level.INFO, "No fileid found for: "
                 + socket.getInetAddress().getHostAddress());
                socket.close();
                return;
            }
            /*StringBuilder sb = new StringBuilder();
                for (byte b : fileid) {
                    sb.append(String.format("%02X", b));
                }*/
            //System.out.println(sb.toString());
            File f = findFile(fileid);
            if (f == null) {
                throw new Exception("File not found");
            }
            // send the file
            InputStream in = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int bytesRead;
            payload = new byte[] { "O".getBytes(StandardCharsets.US_ASCII)[0],
            "K".getBytes(StandardCharsets.US_ASCII)[0], 
            "\n".getBytes(StandardCharsets.US_ASCII)[0], 
            "\n".getBytes(StandardCharsets.US_ASCII)[0] };
            socket.getOutputStream().write(payload, 0, 4);
            while ((bytesRead = in.read(buffer)) != -1) {
                /*for (int i = 0 ; i < bytesRead; i++) {
                    System.out.print((char) buffer[i]);
                }*/
                socket.getOutputStream().write(buffer, 0, bytesRead);
            }
            in.close();
            socket.close();
            return;
        } catch (Exception e) {
            Node.LOGGER.warning("Error in DownloadThread: " + e.getMessage());
            Byte[] payload;
            // make payload "Error <Error Message>" and send it
            payload = new Byte[6 + e.getMessage().length()];
            payload[0] = "E".getBytes(StandardCharsets.US_ASCII)[0];
            payload[1] = "R".getBytes(StandardCharsets.US_ASCII)[0];
            payload[2] = "R".getBytes(StandardCharsets.US_ASCII)[0];
            payload[3] = "O".getBytes(StandardCharsets.US_ASCII)[0];
            payload[4] = "R".getBytes(StandardCharsets.US_ASCII)[0];
            payload[5] = " ".getBytes(StandardCharsets.US_ASCII)[0];
            int i = 0;
            while (i < e.getMessage().length()) {
                payload[i + 6] = (byte) e.getMessage().charAt(i);
                i++;
            }
            try {
                for (i = 0; i < payload.length; i++) {
                    socket.getOutputStream().write(payload[i]);
                }
                socket.close();
                return;
            } catch (Exception e2) {
                Node.LOGGER.warning(
                "Error in DownloadThread: " + e2.getMessage());
                return;
            }
        }
    }

    /**
     * Finds a file in the local directory.
     * 
     * @param fileID the file ID to be found
     * @return the file if found, null otherwise
     * @throws BadAttributeValueException if there 
     * is an error in the attribute value
     */
    public File findFile(byte[] fileID) throws BadAttributeValueException {
        // find file in local directory
        File currDir = new File(Node.searchDir);
        File[] files = currDir.listFiles();
        Base64.Encoder encoder = Base64.getEncoder();
        String id = new String(encoder.encode(fileID));
        //iterate through each set in map
        for (Map.Entry<String, String> entry : Node.dir.entrySet()) {
            if (entry.getValue().equals(id)) {
                for (File f : files) {
                    if (f.getName().equals(entry.getKey())) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

}
