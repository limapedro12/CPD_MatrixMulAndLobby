package client;

import java.io.*;

public class Client {
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ClientStub calculatorStub = new ClientStub();

        calculatorStub.createSocket(hostname, port);
        calculatorStub.send("Dunas sao como divans");

        try { Thread.sleep(100); } 
        catch (Exception e) { System.out.println(e); }

        calculatorStub.send("Biombos indiscretos de alcatrao sujo");
        System.out.println(calculatorStub.receive());
    }
}
