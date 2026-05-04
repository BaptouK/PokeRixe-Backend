package fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle;

import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record GameStartPacket() implements SendablePacket {

    private static final Logger logger = LoggerFactory.getLogger(GameStartPacket.class);

    public void send() {
        try {
            PacketFactory.broadcastPacket(this);
            logger.info("GameStartPacket envoyé à tous les joueurs");
        } catch (Exception e) {
            logger.error("Échec de l'envoi du GameStartPacket", e);
        }
    }
}