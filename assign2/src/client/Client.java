package client;
import java.util.Scanner;
import java.io.*;

public class Client {
    


    public static void main(String[] args) {
        if (args.length < 2){
            //System.out.println("Usage: <hostname> <port>");
            return;
        } 
        ClientStub calculatorStub = new ClientStub();
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        

        calculatorStub.createSocket(hostname, port);
        while (true) {
            String input = selectOption();
            if(input.equals("exit")) break;
            if (!input.isEmpty()) {
                calculatorStub.send(input);
                String response = calculatorStub.receive();
                System.out.println("Server response: " + response);
            }

        }

        try { Thread.sleep(100); } 
        catch (Exception e) { System.out.println(e); }
        
        //calculatorStub.send("Biombos indiscretos de alcatrao sujo");
        //System.out.println(calculatorStub.receive());
    }

    public static String selectOption(){
        int option;
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome!!!\n Select an option:");
            System.out.println("----------------------------");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Continue a game");
            System.out.println("0. Exit");
            System.out.print("Option: ");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Login selected");
                    return clientlogin();
                   
                case 2:
                    System.out.println("Register selected");
                    return clientRegister();
                   
                case 3:
                    //Por implementar o hello
                    return "HELLO "; 
                case 0:
                    System.out.println("Exiting...");
                    return "exit";
                default:
                    System.out.println("Invalid option. Please select again.");
                    break;
            }
            

        }while(option < -1 || option > 3);
        return "";
    }

    public static String clientlogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        return "AUTH " + username + " " + password;
    }

    public static String clientRegister() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new username: ");
        String newUsername = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        return "REGISTER " + newUsername + " " + newPassword;
    }
}
