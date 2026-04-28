package fr.baptouk.pokerixe.backend.game.play;

import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.user.User;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Objet de la partie en cours
 */
public class GamePlay extends Game {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Setter
    @Getter
    private GameStatus status = GameStatus.WAITING;

    private final int pokemonCount;

    private final Map<String, UUID> playerTokens = new HashMap<>(2);

    public GamePlay(final String description, final int pokemonCount) {
        super(description);
        this.pokemonCount = pokemonCount;
    }

    @Override
    public Game addPlayer(User user) {
        final String token = this.generateToken();
        this.playerTokens.put(token, user.getId());

        return super.addPlayer(user);
    }

    private synchronized String generateToken() {
        final long longToken = Math.abs(RANDOM.nextLong());
        return Long.toString(longToken, 16);
    }

    public UUID getUserByToken(final String token) {
        return this.playerTokens.get(token);
    }

    public String getUserToken(final UUID userId){
        return this.playerTokens.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
