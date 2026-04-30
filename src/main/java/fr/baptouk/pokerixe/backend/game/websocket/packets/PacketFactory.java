package fr.baptouk.pokerixe.backend.game.websocket.packets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class PacketFactory {

    private static PacketSerializer packetSerializer;

    @Autowired
    public void init(PacketSerializer serializer) {
        packetSerializer = serializer;
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


    public static Set<WebSocketSession> sessions() {
        return SESSIONS;
    }

    public static PacketSerializer serializer() {
        return packetSerializer;
    }

}
