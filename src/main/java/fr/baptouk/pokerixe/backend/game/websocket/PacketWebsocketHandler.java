package fr.baptouk.pokerixe.backend.game.websocket;

import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketData;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

public final class PacketWebsocketHandler extends BinaryWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(PacketWebsocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        PacketFactory.sessions().add(session);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            final byte[] payload = message.getPayload().array();

            final PacketSerializer.UnserializedPacket unserializedPacket = PacketFactory.serializer().deserialize(session.getId(), payload);

            unserializedPacket.data()
                    .handleRecieve(session,
                            unserializedPacket.user(),
                            unserializedPacket.game());

        } catch (UserNotAuthorizedException e) {
            logger.warn("Unauthorized user attempted to send a packet", e);
            try {
                session.close(CloseStatus.NORMAL.withReason("Unauthorized"));
            } catch (Exception closeException) {
                logger.error("Failed to close unauthorized session", closeException);
            }
        } catch (Exception e) {
            logger.error("Failed to handle binary message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        PacketFactory.sessions().remove(session);
    }
}