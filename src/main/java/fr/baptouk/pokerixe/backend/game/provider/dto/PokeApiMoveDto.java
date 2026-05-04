package fr.baptouk.pokerixe.backend.game.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PokeApiMoveDto(
        int id,
        String name,
        Integer power,
        Integer accuracy,
        Integer pp,
        @JsonProperty("damage_class") DamageClass damageClass,
        TypeEntry type,
        List<NameEntry> names
) {
    public record DamageClass(String name) {}
    public record TypeEntry(String name) {}

    public record NameEntry(
            String name,
            Language language
    ) {}
    public record Language(String name) {}
}