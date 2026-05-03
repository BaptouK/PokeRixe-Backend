package fr.baptouk.pokerixe.backend.game.websocket.dto;

import fr.baptouk.pokerixe.backend.game.attack.GameAttack;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.pokemon.GamePokemon;

import java.util.List;

public record CurrentPlayerStateDto(
        String pseudo,
        Integer indexActivePokemon,
        List<PokemonStateDto> pokemons
) {
    public static CurrentPlayerStateDto from(GamePlayer player) {
        return new CurrentPlayerStateDto(
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
            String urlImageBack,
            String urlImageFront,
            List<AttackDto> attacks
    ) {
        public static PokemonStateDto from(GamePokemon pokemon) {
            return new PokemonStateDto(
                    pokemon.getName(),
                    pokemon.getHp(),
                    pokemon.getStats().getHp(),
                    pokemon.getUrlImageBack(),
                    pokemon.getUrlImageFront(),
                    pokemon.getAttacks().stream()
                            .map(AttackDto::from)
                            .toList()
            );
        }

        public record AttackDto(
                String name,
                Integer power,
                Integer accuracy,
                String type
        ) {
            public static AttackDto from(GameAttack attack) {
                return new AttackDto(
                        attack.getName(),
                        attack.getPower(),
                        attack.getAccuracy(),
                        attack.getType().name().toLowerCase()
                );
            }
        }
    }
}
