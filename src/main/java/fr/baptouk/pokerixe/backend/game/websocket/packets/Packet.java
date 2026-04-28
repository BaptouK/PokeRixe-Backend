package fr.baptouk.pokerixe.backend.game.websocket.packets;

public record Packet<T>(
        String token,
        String type,
        T data
) {

}