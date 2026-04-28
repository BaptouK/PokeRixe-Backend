package fr.baptouk.pokerixe.backend.game.websocket;

public class UserNotAuthorizedException extends RuntimeException {
    public UserNotAuthorizedException() {
        super("User not authorized with token");
    }
}
