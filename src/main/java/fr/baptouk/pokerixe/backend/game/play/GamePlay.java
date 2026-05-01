package fr.baptouk.pokerixe.backend.game.play;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.play.lifecycle.GameLifecycle;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle.GameStartPacket;
import fr.baptouk.pokerixe.backend.user.User;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Objet de la partie en cours
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GamePlay extends Game {

    private static final SecureRandom RANDOM = new SecureRandom();
    private transient final Logger logger = LoggerFactory.getLogger(GamePlay.class);

    @Setter
    @Getter
    private GameStatus status = GameStatus.WAITING;

    private final int pokemonCount;

    private transient final Map<String, UUID> playerTokens = new HashMap<>(2);
    private transient final Map<UUID, String> playerSessions = new HashMap<>(2);

    private transient final GameLifecycle lifecycle = new GameLifecycle(this);

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


    public Game addPlayer(User user, int selectedPokemon) {
        final String token = this.generateToken();
        this.playerTokens.put(token, user.getId());

        return super.addPlayer(user, selectedPokemon);
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

    public void init() {

        this.lifecycle.fetchPokemons();

    }

    public void start() {
        this.status = GameStatus.PLAYING;

        try {
            PacketFactory.broadcastPacket(this.getId(), new GameStartPacket());
        } catch (Exception e) {
            logger.error("Failed to broadcast GameStartPacket for game {}", this.getId(), e);
        }

        this.lifecycle.startGame();
    }

    public void applySessionId(UUID user, String sessionId) {
        this.playerSessions.put(user, sessionId);
    }

    public Map<UUID, String> getPlayerSessions() {
        return playerSessions;
    }
}
