package client;

public class ClientState {

    public enum State {
        AUTH_MENU,
        REGISTER,
        LOGIN,
        MAIN_MENU,
        LOBBY,
        IN_GAME
    }

    public static State transition(State initial, String input) {

        String[] words = input.split(" ");

        if (initial == State.AUTH_MENU) {
            switch (words) {
                default:
            }
        } else if (initial == State.REGISTER) {
        } else if (initial == State.LOGIN) {
        } else if (initial == State.MAIN_MENU) {
        } else if (initial == State.LOBBY) {
        } else if (initial == State.IN_GAME) {
        }

        return initial;
    }
}
