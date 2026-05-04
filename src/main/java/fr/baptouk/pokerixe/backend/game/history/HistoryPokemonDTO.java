package fr.baptouk.pokerixe.backend.game.history;

public record HistoryPokemonDTO(
        int pokedexId,
        String name,
        String sprite,
        boolean isFainted
) {}
