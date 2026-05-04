package fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle;

import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.play.GameStatus;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.ReceivablePacket;
import fr.baptouk.pokerixe.backend.game.websocket.packets.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public record JoinPacket(UUID userId) implements ReceivablePacket, SendablePacket {

    private static final Logger logger = LoggerFactory.getLogger(JoinPacket.class);

    @Override
    public void handleRecieve(WebSocketSession session, UUID user, GamePlay game) {
        if (game.getStatus() == GameStatus.WAITING) {

            this.send();

            logger.info("User {} joined the game {}", userId, game.getId());
            if (game.getPlayers().size() == 2) {
                logger.info("Game {} is now full and will start", game.getId());
                game.init();
            }
        } else {
            logger.warn("User {} attempted to join a game that is not waiting or already has 2 players", userId);
        }
    }

    public void send() {
        try {
            PacketFactory.broadcastPacket(this);
        } catch (Exception e) {
            logger.error("Failed to broadcast JoinPacket", e);
        }
    }
}
