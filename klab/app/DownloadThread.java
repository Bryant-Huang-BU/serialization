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
import java.io.InputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
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
        try {
            this.socket = socket;
            InputStream in = socket.getInputStream();
            // read the fileid
            this.fileid = new byte[4];
            for (int i = 0; i < 4; i++) {
                fileid[i] = (byte) in.read();
            }
            Base64.Encoder encoder = Base64.getEncoder();
            String id = new String(encoder.encode(fileid));
            System.out.println(id);
        } catch (Exception e) {
            Node.LOGGER.warning("Error in DownloadThread: " + e.getMessage());
        }
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
            File f = findFile(fileid);
            if (f == null) {
                throw new Exception("File not found");
            }
            // send the file
            InputStream in = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int bytesRead;
            payload = new byte[] { (byte) 'O', 
            (byte) 'K', (byte) '\n', (byte) '\n' };
            socket.getOutputStream().write(payload, 0, 4);
            while ((bytesRead = in.read(buffer)) != -1) {
                socket.getOutputStream().write(buffer, 0, bytesRead);
            }
            in.close();
            socket.close();
            return;
        } catch (Exception e) {
            Node.LOGGER.warning("Error in DownloadThread: " + e.getMessage());
            Node.LOGGER.log(Level.INFO, "No file found for: " + 
            socket.getInetAddress().getHostAddress());
            Byte[] payload;
            // make payload "Error <Error Message>" and send it
            payload = new Byte[6 + e.getMessage().length()];
            payload[0] = (byte) 'E';
            payload[1] = (byte) 'R';
            payload[2] = (byte) 'R';
            payload[3] = (byte) 'O';
            payload[4] = (byte) 'R';
            payload[5] = (byte) ' ';
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
        byte[] idbyte = new byte[4];
        Base64.Encoder encoder = Base64.getEncoder();
        String id = new String(encoder.encode(idbyte));
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            idbyte[i] = (byte) rand.nextInt(255);
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(id)) {
                return files[i];
            }
        }
        return null;
    }

}
