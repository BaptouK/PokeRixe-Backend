package fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle;

import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketData;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public record GameStartPacket() implements PacketData {

    @Override
    public void handleRecieve(WebSocketSession session, UUID user, GamePlay game) {

    }

}
