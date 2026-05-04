package fr.baptouk.pokerixe.backend.game.websocket.dto;

import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.pokemon.GamePokemon;

import java.util.List;

public record OpponentPlayerStateDto(
        String pseudo,
        Integer indexActivePokemon,
        List<PokemonStateDto> pokemons
) {
    public static OpponentPlayerStateDto from(GamePlayer player) {
        return new OpponentPlayerStateDto(
                player.getPseudo(),
                player.getIndexSelectedPokemon(),
                player.getTeam().getPokemons().stream()
                        .map(PokemonStateDto::from)
                        .toList()
        );
    }

    public record PokemonStateDto(
            String name,
            int currentHp,
            int maxHp,
            String urlImageFront
    ) {
        public static PokemonStateDto from(GamePokemon pokemon) {
            return new PokemonStateDto(
                    pokemon.getName(),
                    pokemon.getHp(),
                    pokemon.getStats().getHp(),
                    pokemon.getUrlImageFront()
            );
        }
    }
}