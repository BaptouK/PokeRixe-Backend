package fr.baptouk.pokerixe.backend.game.websocket.packets;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class PacketFactory {

    private static final PacketSerializer PACKET_SERIALIZER = new PacketSerializer();

    private static final Set<WebSocketSession> SESSIONS = new CopyOnWriteArraySet<>();


    public static void sendPacket(final WebSocketSession session,
                                  final PacketData packet) throws Exception {
        session.sendMessage(PACKET_SERIALIZER.serializePacket(packet));
    }

    public static void broadcastPacket(final PacketData packet) throws Exception {
        for (final WebSocketSession session : SESSIONS) {
            sendPacket(session, packet);
        }
    }

    public static PacketSerializer serializer() {
        return PACKET_SERIALIZER;
    }

    public static Set<WebSocketSession> sessions(){
        return SESSIONS;
    }

}
