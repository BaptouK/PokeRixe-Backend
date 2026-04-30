package fr.baptouk.pokerixe.backend.game.websocket.packets;

import fr.baptouk.pokerixe.backend.game.provider.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class PacketFactory {

    private static PacketSerializer packetSerializer;

    private static GameService gameService;

    @Autowired
    public void init(PacketSerializer serializer, GameService game) {
        packetSerializer = serializer;
        gameService = game;
    }

    private static final Set<WebSocketSession> SESSIONS = new CopyOnWriteArraySet<>();


    public static void sendPacket(final WebSocketSession session,
                                  final PacketData packet) throws Exception {
        session.sendMessage(packetSerializer.serializePacket(packet));
    }

    public static void broadcastPacket(final PacketData packet) throws Exception {
        for (final WebSocketSession session : SESSIONS) {
            sendPacket(session, packet);
        }
    }

    public static void broadcastPacket(final UUID gameId, final PacketData packet) throws Exception {
        for (final WebSocketSession session : SESSIONS) {
            if (gameService.getAvailableGames().stream()
                    .filter(game -> game.getId().equals(gameId))
                    .anyMatch(gamePlay -> gamePlay.getPlayerSessions().containsValue(session.getId()))){
                sendPacket(session, packet);
            }
        }
    }


    public static Set<WebSocketSession> sessions() {
        return SESSIONS;
    }

    public static PacketSerializer serializer() {
        return packetSerializer;
    }

}
