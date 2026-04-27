package fr.baptouk.pokerixe.backend.user.provider;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }
}
