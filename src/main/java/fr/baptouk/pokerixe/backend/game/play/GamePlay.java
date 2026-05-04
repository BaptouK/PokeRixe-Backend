package fr.baptouk.pokerixe.backend.game.play;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.play.lifecycle.GameLifecycle;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.game.websocket.dto.CurrentPlayerStateDto;
import fr.baptouk.pokerixe.backend.game.websocket.dto.FullStateGameDto;
import fr.baptouk.pokerixe.backend.game.websocket.dto.OpponentPlayerStateDto;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.FullStatePacket;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle.GameStartPacket;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Objet de la partie en cours
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GamePlay extends Game {

    private static final SecureRandom RANDOM = new SecureRandom();
    @Transient private transient final Logger logger = LoggerFactory.getLogger(GamePlay.class);

    @Setter
    @Getter
    private GameStatus status = GameStatus.WAITING;

    @Transient private transient final Map<String, UUID> playerTokens = new HashMap<>(2);
    @Transient @Getter private transient final Map<UUID, String> playerSessions = new HashMap<>(2);
    @Transient @Getter private transient final Set<UUID> playersWhoActed = new HashSet<>(2);

    @Transient public transient final GameLifecycle lifecycle = new GameLifecycle(this);
    @Transient @Setter private transient GameService gameService;
    @Transient @Setter private transient WebClient pokeApiClient;

    public GamePlay(final String description) {
        super(description);
    }

    @Override
    public Game addPlayer(final GamePlayer player) {
        final String token = this.generateToken();
        this.playerTokens.put(token, player.getId());
        return super.addPlayer(player);
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
        this.lifecycle.fetchPokemons(pokeApiClient)
                .doOnSuccess(v -> gameService.saveGame(this))
                .subscribe();
    }

    public void start() {
        this.status = GameStatus.PLAYING;

        new GameStartPacket().send();

        sendFullState("waiting_actions", "");

    }

    public void sendFullState(String fightPhase, String winner) {

        GamePlayer p1 = null;
        GamePlayer p2 = null;
        String p1SessionId = null;
        String p2SessionId = null;

        for (Map.Entry<UUID, String> entry : playerSessions.entrySet()) {
            GamePlayer player = this.getPlayers().stream()
                    .filter(p -> p.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);

            if (p1 == null) {
                p1 = player;
                p1SessionId = entry.getValue();
            } else {
                p2 = player;
                p2SessionId = entry.getValue();
            }
        }

        WebSocketSession p1Session = PacketFactory.getSession(p1SessionId);
        WebSocketSession p2Session = PacketFactory.getSession(p2SessionId);

        if (p1Session == null || p2Session == null) {
            logger.warn("Session introuvable pour l'envoi du FullState");
            return;
        }

        new FullStatePacket(
                FullStateGameDto.from(this, p1, p2, CurrentPlayerStateDto.from(p1), OpponentPlayerStateDto.from(p2), fightPhase, winner)
        ).send(p1Session);

        new FullStatePacket(
                FullStateGameDto.from(this, p2, p1, CurrentPlayerStateDto.from(p2), OpponentPlayerStateDto.from(p1), fightPhase, winner)
        ).send(p2Session);
    }

    public void finishGame() {
        if (gameService != null) {
            gameService.saveGame(this);
            gameService.removeGame(this.getId());
        }
    }

    public void applySessionId(UUID user, String sessionId) {
        this.playerSessions.put(user, sessionId);
    }

}
