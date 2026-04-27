package fr.baptouk.pokerixe.backend.game.provider.exceptions;

public class UserAlreadyInGameException extends RuntimeException {
    public UserAlreadyInGameException() {
        super("User already in game");
    }
}
