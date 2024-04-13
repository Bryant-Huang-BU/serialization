/************************************************
 *
 * Author: Bryant Huang
 * Assignment: Program 3
 * Class: CSI4321
 *
 ************************************************/
package klab.app;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * The DownloadSender class is responsible for sending a file to a server.
 * It implements the Runnable interface to allow for concurrent execution.
 */
public class DownloadSender implements Runnable {
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private String filename;
    byte[] fileid;

    /**
     * Constructs a new DownloadSender object.
     * 
     * @param s the socket representing the connection to the server
     * @param id the id of the file to be downloaded
     * @param filename the name of the file to be downloaded
     */
    public DownloadSender(Socket s, byte[] id, String filename) {
        this.filename = filename;
        this.fileid = id;
        try {
            // Connect to the server
            socket = s;
            if (socket.isClosed()) {
                throw new IOException("Socket is closed"); 
            }
            // Get the input stream for the socket
            input = socket.getInputStream();
            // Get the output stream for the socket
            output = socket.getOutputStream();
            
        } catch (IOException e) {
            Node.LOGGER.log(Level.WARNING, 
            "Download Socket failed to open" + e.getMessage());
        }
    }


    /* Disconnects from the server by closing the input 
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
            //encode fileid to us_ascii
            //make byte[] a string
            StringBuilder Strin = new StringBuilder();
            for (int i = 0; i < fileid.length; i++) {
                Strin.append(String.format("%02X", fileid[i]));
            }
            Strin.append('\n');
            //output.flush();
            byte[] fileidascii = Strin.toString().getBytes(
            StandardCharsets.US_ASCII);
            //System.out.println(fileidascii.length);
            output.write(fileidascii);
            while (input.available() == 0) {
                // Wait for the server to respond
            }
            byte[] buffer = new byte[4];
            int bytesRead = 0;
            //System.out.println(filename + " sending...");
            //wait for response from server
            InputStream input = socket.getInputStream();
            while (bytesRead < 4) {
                int count = input.read(buffer, bytesRead, 4 - bytesRead);
                if (count == -1) {
                    throw new IOException("Error reading from socket");
                }
                bytesRead += count;
            }
            //System.out.println("Response: " + new String(buffer));
            /*or (int i = 0; i < buffer.length; i++) {
                System.out.print((char) buffer[i] + " ");
            }*/
            if (bytesRead == 4) {
                // Check if the first four bytes are 
                //{'o', 'k', '\n', '\n'}
                if (buffer[0] == 'O' && buffer[1] == 'K'
                    && buffer[2] == '\n' && buffer[3] == '\n') {
                    String destinationPath = Node.searchDir + "\\" + filename;
                    File file = new File(destinationPath);
                    synchronized(file) {
                        //FileOutputStream fO = new FileOutputStream(file);
                        if (file.exists()) {
                            Node.LOGGER.log(Level.WARNING, "File " +
                            "already exists, overwriting");
                        }
                        try (FileOutputStream fOS =
                        new FileOutputStream(destinationPath)) {
                        byte[] buf = new byte[1024];
                        while ((bytesRead = input.read(buf)) != -1) {
                            fOS.write(buf, 0, bytesRead);
                        }
                    }
                    }
                    Node.LOGGER.log(Level.INFO, 
                    "File downloaded successfully.");
                    disconnect();
                } else if (buffer[0] == 'E' && buffer[1] == 'R'
                        && buffer[2] == 'R' && buffer[3] == 'O') {
                    // If the response is "ERROR\n\n", disconnect
                    buffer = new byte[2];
                    input.read(buffer);
                    if (buffer[0] == 'R' && buffer[1] == ' ') {
                        //read the error message
                        byte[] buf = new byte[1024];
                        while ((bytesRead = input.read(buffer)) != -1) {
                            //System.out.println(new String
                            //(buf, 0, bytesRead));
                            // Clear the buffer
                            for (int i = 0; i < buf.length; i++) {
                                buf[i] = 0;
                            }
                        }
                        disconnect();
                    }
                }
                else {
                    throw new IOException("Invalid response");
                }
            }
        } catch (IOException e) {
            Node.LOGGER.warning("Error in FileDownloader: " + e.getMessage());
            disconnect();
        } catch (Exception e) {
            Node.LOGGER.warning("CLOSED THREAD" + e.getMessage());
            disconnect();
        }

    }
}