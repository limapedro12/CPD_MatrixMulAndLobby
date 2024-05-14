package client;

import java.util.*;

public class Client {
    public static void main(String[] args) {

        if (args.length < 2){
            System.out.println("Usage: <hostname> <port>");
            return;
        }

        ClientStub stub = new ClientStub();
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        
        try {
            stub.createSocket(hostname, port);
        } catch (Exception e) {
            System.out.println("Could not connect to server.");
            System.out.println("Exiting...");
            return;
        }

        ClientState.State state = ClientState.State.AUTH_MENU;

        while (true) {

            String command = switch (state) {
                case ClientState.State.AUTH_MENU -> authMenu();
                case ClientState.State.REGISTER -> clientRegister();
                case ClientState.State.LOGIN -> clientLogin();
                case ClientState.State.MAIN_MENU -> "";
                case ClientState.State.LOBBY -> "";
                case ClientState.State.IN_GAME -> "";
                default -> "";
            };

            if (command.equals("exit")) return;

            try {
                stub.send(command);
            } catch (Exception e) {
                continue;
            }

            String answer;

            try {
                answer = stub.receive();
            } catch (Exception e) {
                continue;
            }

            state = ClientState.transition(state, answer);
        }
    }

    public static String authMenu(){
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
                    return clientLogin();
                   
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

    public static String clientLogin() {
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
