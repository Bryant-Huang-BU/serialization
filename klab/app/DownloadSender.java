package klab.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This class implements a simple client that downloads a file from a server.
 */
public class DownloadSender implements Runnable {
    private Socket socket;
    private BufferedInputStream input;
    private BufferedOutputStream output;
    private String filename;
    byte[] fileid;
    /**
    * Constructs a new FileDownloader with the given IP address and port number.
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
    * Disconnects from the server and closes the socket.
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

    @Override
    public void run() {
        try {
            // Send the file name to the server
            output.write(fileid);
            String newline = "\n"; 
            output.write(newline.getBytes(StandardCharsets.UTF_8));
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
                    while ((bytesRead = 
                    input.read(buffer2)) != -1) {
                        output.write(buffer2, 0, bytesRead);
                         bytesTransferred = bytesRead;
                    }
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