package klab.app;

import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
import klab.serialization.*;

public class SearchManagement implements Runnable{
    int id;
    Logger log;
    Socket x;
    public SearchManagement(Logger log, Socket x) {
        id = 0;
        this.log = log;
        this.x = x;
    }
    public void set(Socket x) {
        log.info("Search Management thread running");
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals("exit")) {
                    sc.close();
                    break;
                }
                Search search = new Search(id, input);
                x.getOutputStream().write(input.getBytes());
            } catch (Exception e) {
                log.severe("Search Management thread interrupted");
            }
        }
    }
    @Override
    public void run() {
        set(x);
    }

    private intToBytes(int len, int id) {
        byte[] bytes = new byte[len];
        for (int i = len - 1; i >= 0; i--) {
            bytes[i] = (byte) (id & 0xff);
            id >>= 8; 
        }
        System.out.println("Bytes: " + bytes.toString());
        return bytes;
    }    
}
