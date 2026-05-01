package fr.baptouk.pokerixe.backend.game.websocket.packets;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.game.websocket.UserNotAuthorizedException;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.JoinPacket;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle.GameStartPacket;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public final class PacketSerializer {

    @Autowired
    private GameService gameService;

    private final ObjectMapper mapper =
            new ObjectMapper(new MessagePackFactory());

    private static final Map<String, Class<? extends SendablePacket>> REGISTRY = new HashMap<>();

    static {
        register(JoinPacket.class);
    }

    private static void register(Class<? extends SendablePacket> clazz) {
        REGISTRY.put(clazz.getSimpleName(), clazz);
    }


    private <T extends SendablePacket> Packet<T> of(final T data) {
        return new Packet<>(
                null,
                data.getClass().getSimpleName(),
                data
        );
    }

    private byte[] serialize(final SendablePacket data) throws Exception {
        return mapper.writeValueAsBytes(of(data));
    }

    public BinaryMessage serializePacket(final SendablePacket data) throws Exception {
        return new BinaryMessage(serialize(data));
    }

    public UnserializedPacket deserialize(final String sessionId, final byte[] bytes) throws IOException, UserNotAuthorizedException {
        final Packet<?> packet = mapper.readValue(bytes, Packet.class);

        final String token = packet.token();
        final Optional<GamePlay> optionalGame = this.gameService.getGameByToken(token);

        if (optionalGame.isEmpty()) {
            throw new UserNotAuthorizedException();
        }

        final Class<? extends SendablePacket> clazz = REGISTRY.get(packet.type());

        if (clazz == null) {
            throw new RuntimeException("Unknown packet type: " + packet.type());
        }

        final GamePlay game = optionalGame.get();
        final UUID user = game.getUserByToken(token);
        game.applySessionId(user, sessionId);

        return new UnserializedPacket(optionalGame.get(), user, mapper.convertValue(packet.data(), clazz));
    }

    public record UnserializedPacket(
            GamePlay game,
            UUID user,
            SendablePacket data
    ) {}
}