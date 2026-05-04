package fr.baptouk.pokerixe.backend.game.websocket.packets.game;

import fr.baptouk.pokerixe.backend.game.websocket.dto.FullStateGameDto;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public record FullStatePacket(
        FullStateGameDto game
) implements SendablePacket {

    private static final Logger logger = LoggerFactory.getLogger(FullStatePacket.class);


    public void send(WebSocketSession session) {
        try {
            PacketFactory.sendPacket(session,this);
            logger.info("FullStatePacket envoyé à {}",session);
        } catch (Exception e) {
            logger.error("Échec de l'envoi du GameStartPacket", e);
        }
    }
}
