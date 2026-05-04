package fr.baptouk.pokerixe.backend.game.websocket.packets;

import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public interface ReceivablePacket extends PacketData{

    void handleRecieve(WebSocketSession session, UUID user, GamePlay game);

}
