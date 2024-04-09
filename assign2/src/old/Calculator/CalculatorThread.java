import java.io.*;
import java.net.*;
import java.util.Date;

public class CalculatorThread extends Thread {
    private Socket socket;

    private int localSum = 0;
    private static int globalSum = 0;

    private final ReentrantLock lock = new ReentrantLock();

    public CalculatorThread(Socket socket){
        super();
        this.socket = socket;
        System.out.println("Thread created");
    }

    public void run() {
        try {
            while (true) {
                InputStream input = socket.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String message = reader.readLine();

                if(message != null) {

                    System.out.println("New client connected with message: " + message);

                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    writer.println(new Date().toString());
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}