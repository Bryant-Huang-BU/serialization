/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 3
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * The DownloadSender class is responsible for sending a file to a server.
 * It implements the Runnable interface to allow for concurrent execution.
 */
public class DownloadSender implements Runnable {
    private Socket socket;
    private BufferedInputStream input;
    private BufferedOutputStream output;
    private String filename;
    byte[] fileid;
    /**
    * Constructs a new FileDownloader with the given IP
    * address and port number.
    * @param ip the IP address of the server
    * @param port the port number to connect to
    * @param filename the name of the file to download
    */
    public DownloadSender(String ip, int port, byte[] id, String filename) {
        this.filename = filename;
        this.fileid = id;
        try {
            // Connect to the server
            socket = new Socket(ip, port);
            // Get the input stream for the socket
            input = new BufferedInputStream(socket.getInputStream());
            // Create the output file
            output = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (IOException e) {
            // Handle exceptions
        }
    }

    /**
        * Disconnects from the server by closing the input 
        * and output streams and the socket.
        * Any exceptions that occur during the disconnection'
        * process are handled internally.
        */
    public void disconnect() {
        try {
            // Close the input and output streams
            input.close();
            output.close();
            // Close the socket
            socket.close();
        } catch (IOException e) {
            // Handle exceptions
        }
    }

    /**
     * Executes the file transfer process.
     * Sends the file to the server and receives the response.
     * If the response is "ok", the file transfer is successful.
     * Otherwise, the connection is disconnected.
     */
    @Override
    public void run() { //USE TRANSFER TO SEND FILE
        try {
            // Send the file name to the server
            output.write(fileid);
            String newline = "\n"; 
            output.write(newline.getBytes(StandardCharsets.US_ASCII));
            output.flush();
            while (input.available() == 0) {
                // Wait for the server to respond
            }
            byte[] buffer = new byte[4];
            int bytesRead = input.read(buffer);
            if (bytesRead == 4) {
                // Check if the first four bytes are 
                //{'o', 'k', '\n', '\n'}
                if (buffer[0] == 'o' && buffer[1] == 'k'
                 && buffer[2] == '\n' && buffer[3] == '\n') {
                    
                    byte[] buffer2 = new byte[1024];

                    output.close();
                } else {
                    disconnect();
                }
            } else {
                disconnect();
            }
        } catch (IOException e) {
            Node.LOGGER.warning("Error in FileDownloader: " + e.getMessage());
            disconnect();
        }

    }
}