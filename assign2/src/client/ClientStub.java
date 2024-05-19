package client;

import java.net.*;
import java.io.*;
 
public class ClientStub {
    Socket socket;
    
    public void createSocket(String hostname, int port) throws UnknownHostException, IOException {
        try {
            socket = new Socket(hostname, port);
        } catch (UnknownHostException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        }
    }

    public void send(String message) throws IOException {
        try {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
 
        } catch (IOException ex) {
            throw ex;
        }
    }
 
    public String receive() throws IOException {
        String message = "";
 
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            message = reader.readLine();
 
        } catch (IOException ex) {
            throw ex;
        }

        return message;
    }
}
