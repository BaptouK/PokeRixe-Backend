package fr.baptouk.pokerixe.backend.game.mapper;

import fr.baptouk.pokerixe.backend.game.attack.GameAttack;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.pokemon.GamePokemon;
import fr.baptouk.pokerixe.backend.game.team.GameTeam;
import fr.baptouk.pokerixe.backend.user.User;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GamePlayer toGamePlayer(User user, int selectedPokemon) {
        GameTeam gameTeam = new GameTeam();
        gameTeam.setPokemons(user.getTeam().getPokemons().stream()
                .map(pokemon -> {
                    GamePokemon gamePokemon = new GamePokemon();
                    gamePokemon.setPokemonId(pokemon.getId());
                    gamePokemon.setAttacks(pokemon.getAttacks().stream()
                            .map(attack -> {
                                GameAttack gameAttack = new GameAttack();
                                gameAttack.setApiUrl(attack.getApiUrl());
                                return gameAttack;
                            })
                            .toList());
                    return gamePokemon;
                })
                .toList());

        return GamePlayer.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .team(gameTeam)
                .indexSelectedPokemon(selectedPokemon)
                .build();
    }
}
