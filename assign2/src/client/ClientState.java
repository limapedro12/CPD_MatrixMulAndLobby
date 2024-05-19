package client;

public class ClientState {

    public enum State {
        AUTH_MENU,
        REGISTER,
        LOGIN,
        MAIN_MENU,
        LOBBY,
        IN_GAME_WAIT,
        IN_GAME_PLAY
    }

    public static State transition(State initial, String input) {

        State ret = initial;

        String[] parts = input.split(" ");

        if (initial == State.AUTH_MENU) {

            ret = switch (input) {
                case "goto login" -> State.LOGIN;
                case "goto register" -> State.REGISTER;
                default -> initial;
            };

        } else if (initial == State.REGISTER) {

            System.out.println(input);

            ret = switch (parts[0]) {
                case "SUCCESS:" -> State.AUTH_MENU;
                default -> initial;
            };

        } else if (initial == State.LOGIN) {

            System.out.println(parts[0] + " " + parts[1] + " " + parts[2]);

            ret = switch (parts[0]) {
                case "SUCCESS:" -> State.MAIN_MENU;
                default -> initial;
            };

            if (parts[0].equals("SUCCESS:"))
                Client.token = parts[5];
            
        } else if (initial == State.MAIN_MENU) {

            System.out.println(input);

            ret = switch (parts[0]) {
                case "SUCCESS:" -> State.LOBBY;
                default -> initial;
            };
            
        } else if (initial == State.LOBBY) {

            System.out.println(input);

            ret = switch (parts[0]) {
                case "STARTING" -> State.IN_GAME_WAIT;
                default -> initial;
            };

        } else if (initial == State.IN_GAME_WAIT) {

            System.out.println(input);

            ret = switch (parts[0]) {
                case "PLAY:" -> State.IN_GAME_PLAY;
                case "GOODBYE:" -> State.MAIN_MENU;
                default -> initial;
            };

        } else if (initial == State.IN_GAME_PLAY) {

            System.out.println(input);

            ret = switch (parts[0]) {
                case "OK:" -> State.IN_GAME_WAIT;
                default -> initial;
            };

        }

        return ret;
    }
}
