package fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle;

import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public record MustSwichPacket() implements SendablePacket {

    private static final Logger logger = LoggerFactory.getLogger(MustSwichPacket.class);

    public void send(WebSocketSession session) {
        try {
            PacketFactory.sendPacket(session,this);
            logger.info("MustSwichPacket envoyé à {}",session);
        } catch (Exception e) {
            logger.error("Échec de l'envoi du MustSwichPacket", e);
        }
    }
}
