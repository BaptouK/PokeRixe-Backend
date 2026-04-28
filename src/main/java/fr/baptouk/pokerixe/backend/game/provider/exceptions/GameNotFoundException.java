package fr.baptouk.pokerixe.backend.game.provider.exceptions;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException() {
        super("Game not found");
    }

}
