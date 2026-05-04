package fr.baptouk.pokerixe.backend.game.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiPokemonDto(
        int id,
        String name,
        List<TypeSlot> types,
        List<StatSlot> stats,
        Sprites sprites,
        List<MoveSlot> moves
) {
    public record TypeSlot(TypeEntry type) {}
    public record TypeEntry(String name) {}

    public record StatSlot(
            @JsonProperty("base_stat") int baseStat,
            StatEntry stat
    ) {}
    public record StatEntry(String name) {}

    public record Sprites(
            @JsonProperty("front_default") String frontDefault,
            @JsonProperty("back_default") String backDefault
    ) {}

    public record MoveSlot(MoveEntry move) {}
    public record MoveEntry(String name, String url) {}
}